package Wrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class DataTextFileReader {
	private static double mCheck = Math.pow(2, 23);
	private static double mVref = 4.5;
	private static double mGain = 24;
	private static double mCheckDivide = 2 * mCheck;
	private Queue<String> mStringArray = new LinkedList<String>();
	private double mInput[][];
	private List<String> mStringCheck;
	StringBuilder sb_dataLoss = new StringBuilder();

	public double[][] readFile(String iFilePath, int iSaveCsv) {


		try {
			iFilePath = iFilePath.substring(0, iFilePath.length() - 4);
			BufferedReader br = new BufferedReader(new FileReader(iFilePath+".txt"));
			String line;

			int counter = 0;
			while ((line = br.readLine()) != null) {
				String[] aSplitLine = line.split("\\+");
				if (aSplitLine.length >0){
					mStringArray.addAll(Arrays.asList(aSplitLine).subList(1, aSplitLine.length));
					mStringCheck = Arrays.asList(aSplitLine).subList(1, aSplitLine.length);
				}
				
				if (mStringCheck == null)
				{
					System.out.println("count = " + counter);
					break;
				}
				counter++;
			}
			System.out.println("count = " + counter);

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mInput = new double[mStringArray.size()][4];
		
		populateInputArray();
		/**
		 * To write into a file
		 */

		if (iSaveCsv == 1) {
			try {
				BufferedWriter br = new BufferedWriter(new FileWriter( iFilePath+".csv"));
				BufferedWriter br_dataloss = new BufferedWriter(new FileWriter( iFilePath+"_Data_loss.csv"));
				StringBuilder sb = new StringBuilder();
				for (int i = 0; i < mInput.length; i++) {
					sb.append(mInput[i][0]);
					sb.append(",");
					sb.append(mInput[i][1]);
					sb.append(",");
					sb.append(mInput[i][2]);
					sb.append(",");
					sb.append(mInput[i][3]);
					sb.append("\n");
				}
				br.write(sb.toString());
				br.close();
				br_dataloss.write(sb_dataLoss.toString());
				br_dataloss.close();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return mInput;

	}
	
	private void feedInputArray(String iInputString, int iInputIndex) {
//		System.out.println("Index : "+iInputString);
	    for (int aInputChannelIndex = 0; aInputChannelIndex < 4; aInputChannelIndex++) {
	        mInput[iInputIndex][aInputChannelIndex] = stringToDouble(iInputString.substring(6 * aInputChannelIndex + 1, 6 * aInputChannelIndex + 7));
	    }
	}

	private void populateInputArray() {
		int aInputArrayCounter = 0;
		String aSample = getNextValidSample();
		System.out.println(""+aInputArrayCounter);
		int aLastIndex = Character.getNumericValue(aSample.charAt(0));
		feedInputArray(aSample, aInputArrayCounter);
		for (aInputArrayCounter++; aInputArrayCounter < mInput.length; aInputArrayCounter++) {
			aSample = getNextValidSample();
			int aCurrentIndex = Character.getNumericValue(aSample.charAt(0));
			int aIndexDiff = aCurrentIndex - aLastIndex;
			if (aIndexDiff <= 0)
				aIndexDiff += 10;
			if (aIndexDiff == 1 || aIndexDiff == -9) {
				feedInputArray(aSample, aInputArrayCounter);
			} else {
				sb_dataLoss.append(aInputArrayCounter);
				sb_dataLoss.append(",");
				sb_dataLoss.append(aIndexDiff-1);
				sb_dataLoss.append(",");
				sb_dataLoss.append(aSample);
				sb_dataLoss.append("\n");
				aInputArrayCounter += aIndexDiff-1;
				feedInputArray(aSample, aInputArrayCounter);


				interpolate(aInputArrayCounter, aInputArrayCounter - aIndexDiff, aLastIndex);
			}
			aLastIndex = Character.getNumericValue(aSample.charAt(0));
		}

	}

	private String getNextValidSample() {
		String aValidSample = "";
		if (mStringArray.size() > 0) {
			do { 
				aValidSample = mStringArray.remove();
			} while (aValidSample.length() != 25 && mStringArray.size() > 0);
		}
		return aValidSample;
	}

	private void interpolate(int iCurrentInputIndex, int iStartIndex, int iEndIndex) {
		System.out.println("iCurrentInputIndex: " + iCurrentInputIndex);
		System.out.println("iStartIndex: " + iStartIndex);
		System.out.println("iEndIndex: " + iEndIndex);
		for (int k = iStartIndex + 1; k < iCurrentInputIndex; k++) {
			for (int aInputChannelIndex = 0; aInputChannelIndex < 4; aInputChannelIndex++) {
				mInput[k][aInputChannelIndex] = mInput[iStartIndex][aInputChannelIndex] + (mInput[iCurrentInputIndex][aInputChannelIndex] - mInput[iStartIndex][aInputChannelIndex]) / (iCurrentInputIndex - iStartIndex) * (k-iStartIndex);
			}
		}
	}

	private double stringToDouble(String iChannelInput) {
		return doubleConv(new BigInteger(iChannelInput, 16).doubleValue());
	}

	private double doubleConv(double iDoubleValue) {
		double aOut;
		if (iDoubleValue >= mCheck) {
			aOut = (iDoubleValue - mCheckDivide) * mVref / (mCheck - 1) / mGain;
		} else {
			aOut = iDoubleValue / (mCheck - 1) / mGain * mVref;
		}
		return aOut;
	}
}

