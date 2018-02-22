package Wrapper;

import SignalProc.SignalProcConstants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.LinkedList;

public class CubicInterpolate {

	private static double mCheck = Math.pow(2, 23);
	private static double mVref = 4.5;
	private static double mGain = 24;
	private static double mCheckDivide = 2 * mCheck;
	private static LinkedList<String> mStringArray = new LinkedList<String>();
	private static double mInput[][] = new double[1][1];
	private static LinkedList<Double> mDoubleArray1 = new LinkedList<>();
	private static LinkedList<Double> mDoubleArray2 = new LinkedList<>();
	private static LinkedList<Double> mDoubleArray3 = new LinkedList<>();
	private static LinkedList<Double> mDoubleArray4 = new LinkedList<>();
	private static LinkedList<Integer> mMissLocations = new LinkedList<>();
	private static LinkedList<Integer> mSamplesMissed = new LinkedList<>();
	private static int mDoubleArrCounter = -1;
	private static int mNextValidSampleCounter =-1;
	private static final int mSampleLength = 26;
	private static final int mAsciiStart  = 34;
	private static final int mAsciiEnd = 126;
	private static final int mAsciiDiff = mAsciiEnd - mAsciiStart + 1;
	
	public static void main(String[] args) {

		String iFilePath = "/Users/kishoresubramanian/Downloads/new1input-sattva-12-14-14-15-44.txt";
		// Add data to mString Array
		try {

			BufferedReader br = new BufferedReader(new FileReader(iFilePath));
//			BufferedReader br = new BufferedReader(new FileReader("/Users/kishoresubramanian/Downloads/index_test_new1Test_simulator.txt"));
			String line;

			int counter = 0;
			while ((line = br.readLine()) != null) {
				String[] aSplitLine = line.split("\\+");
				if (aSplitLine.length >0){
					mStringArray.addAll(Arrays.asList(aSplitLine).subList(1, aSplitLine.length));
				}
				counter++;
			}
			System.out.println("count = " + counter);

			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Convert valid samples and store the double values in mDoubleArray[1-4]. 
		// Store miss Index in mMissLocations, mSamplesMissed
		int aLengthStrArr = mStringArray.size();
		int count = 0;
		String aValidSample = "";
		int aLastSampleIndex = -1;
		int aLastSampleIndex1 , aLastSampleIndex2;
		while (aValidSample.length() != mSampleLength && aLengthStrArr > 0) {
			aValidSample = mStringArray.removeFirst();
			count++;
		}
		if (aValidSample.length() == mSampleLength) {
			feedInputArray(aValidSample);
			aLastSampleIndex1 = (int) (aValidSample.charAt(0)) - mAsciiStart;
			aLastSampleIndex2 = (int) (aValidSample.charAt(1)) - mAsciiStart;
			aLastSampleIndex = aLastSampleIndex1 * mAsciiDiff + aLastSampleIndex2;
			if (mNextValidSampleCounter > 0) {
				mMissLocations.add(mDoubleArrCounter-1);
				mSamplesMissed.add(mNextValidSampleCounter);
			}
		}
		int aCurrentSampleIndex, aDiffSampleIndex;
		int aCurrentSampleIndex1, aCurrentSampleIndex2;
		
		if ( aLastSampleIndex != -1) { 
			for (int i = count; i< SignalProcConstants.NO_OF_SAMPLES; i++) { // should be while loop
				aValidSample = mStringArray.remove();
				if (aValidSample.length() == mSampleLength ) {

					aCurrentSampleIndex1 = (int) (aValidSample.charAt(0)) - mAsciiStart;
					aCurrentSampleIndex2 = (int) (aValidSample.charAt(1)) - mAsciiStart;
					aCurrentSampleIndex = aCurrentSampleIndex1 * mAsciiDiff + aCurrentSampleIndex2;
					aDiffSampleIndex = aCurrentSampleIndex - aLastSampleIndex;
					feedInputArray(aValidSample);
					aLastSampleIndex = aCurrentSampleIndex;
					if (aDiffSampleIndex != 1 && aDiffSampleIndex != -(mAsciiDiff*mAsciiDiff - 1)) {

						if (aDiffSampleIndex < 0) {
							aDiffSampleIndex = mAsciiDiff*mAsciiDiff + aDiffSampleIndex;
							if (aDiffSampleIndex > mNextValidSampleCounter) {
								mMissLocations.add(mDoubleArrCounter-1);
								mSamplesMissed.add(aDiffSampleIndex-1);
							}
							else {
								mMissLocations.add(mDoubleArrCounter-1);
								mSamplesMissed.add(mNextValidSampleCounter);
							}
						}
						else {
							if (aDiffSampleIndex > mNextValidSampleCounter) {
								mMissLocations.add(mDoubleArrCounter-1);
								mSamplesMissed.add(aDiffSampleIndex-1);
							}
							else {
								mMissLocations.add(mDoubleArrCounter-1);
								mSamplesMissed.add(mNextValidSampleCounter);
							}
						}
					}
					else {
						if (mNextValidSampleCounter > 0) {
							mMissLocations.add(mDoubleArrCounter-1);
							mSamplesMissed.add(mNextValidSampleCounter);
						}
					}
					
				}	
			}
		}
		
		//Perform Interpolation
		LinkedList<Integer> aMissedSamples;
		LinkedList<Integer> aMissedLocation;
		LinkedList<Double> aDataPoints;
		int aLastLeftIndex, aFirstLeftIndex, aLastRightIndex, aFirstRightIndex;
		long aStartTime = System.currentTimeMillis();
		int aMissSamplesStart = 0;
		if (mMissLocations.size() > 0) {
			
			while (mMissLocations.getFirst() < 0) {
				mMissLocations.removeFirst();
				aMissSamplesStart = mSamplesMissed.removeFirst();
			}
		}
		
		for (int i = mMissLocations.size()-1; i>=0 ; i--) {
			aFirstLeftIndex = 0;
			aFirstRightIndex = 1;
			
			aMissedLocation = new LinkedList<>();
			aMissedSamples = new LinkedList<>();
			
			
			aMissedLocation.add(mMissLocations.get(i));
			aMissedSamples.add(mSamplesMissed.get(i));
			count = i;
			if (count >0) {
				while (aMissedLocation.getLast() - mMissLocations.get(count-1) < 10) {
					aMissedLocation.addFirst(10 - (aMissedLocation.getLast() - mMissLocations.get(count-1)) );
					aMissedSamples.addFirst(mSamplesMissed.get(count-1));
					count--;
					if (count == 0)
						break;
				}
			}
			int aLastTempIndex = aMissedLocation.removeLast();
			if (aLastTempIndex >= SignalProcConstants.NO_OF_SAMPLES - 10 ) { // 14990 - 14999 miss index
				int aDiff = (SignalProcConstants.NO_OF_SAMPLES - 1) - aLastTempIndex;
				
				aLastLeftIndex = 9 + (10- aDiff);
				aLastRightIndex = aDiff;
				
			}
			else if (aLastTempIndex < (10-1)) { // lastempIndex is included
				
				aLastLeftIndex = aLastTempIndex;
				aLastRightIndex = 10+ ((10-1)-aLastTempIndex);
				
			}
			else {
				aLastLeftIndex = 9;
				aLastRightIndex = 10;
				
			}
			aMissedLocation.add(aLastLeftIndex);
			// Array 1
			aDataPoints = new LinkedList<>();
			for (int k = aLastLeftIndex; k >=aFirstLeftIndex ; k--) {
				aDataPoints.add(mDoubleArray1.get(aLastTempIndex - k));
			}
			for (int k = aFirstRightIndex; k<=aLastRightIndex ; k++ ) {
				aDataPoints.add(mDoubleArray1.get(aLastTempIndex + k));
			}
			
			LinkedList<Double> aInterpolatedSamples = cubicInterpolateDynamic(aDataPoints, aMissedLocation, aMissedSamples);
			for (int k = aMissedSamples.getLast() -1; k>=0; k--) {
				mDoubleArray1.add(aLastTempIndex+1, aInterpolatedSamples.get(k));
			}
			
			// Array 2
			aDataPoints = new LinkedList<>();
			for (int k = aLastLeftIndex; k >=aFirstLeftIndex ; k--) {
				aDataPoints.add(mDoubleArray2.get(aLastTempIndex - k));
			}
			for (int k = aFirstRightIndex; k<=aLastRightIndex ; k++ ) {
				aDataPoints.add(mDoubleArray2.get(aLastTempIndex + k));
			}

			aInterpolatedSamples = cubicInterpolateDynamic(aDataPoints, aMissedLocation, aMissedSamples);
			for (int k = aMissedSamples.getLast() -1; k>=0; k--) {
				mDoubleArray2.add(aLastTempIndex+1, aInterpolatedSamples.get(k));
			}

			// Array 3
			aDataPoints = new LinkedList<>();
			for (int k = aLastLeftIndex; k >=aFirstLeftIndex ; k--) {
				aDataPoints.add(mDoubleArray3.get(aLastTempIndex - k));
			}
			for (int k = aFirstRightIndex; k<=aLastRightIndex ; k++ ) {
				aDataPoints.add(mDoubleArray3.get(aLastTempIndex + k));
			}

			aInterpolatedSamples = cubicInterpolateDynamic(aDataPoints, aMissedLocation, aMissedSamples);
			for (int k = aMissedSamples.getLast() -1; k>=0; k--) {
				mDoubleArray3.add(aLastTempIndex+1, aInterpolatedSamples.get(k));
			}

			// Array 4
			aDataPoints = new LinkedList<>();
			for (int k = aLastLeftIndex; k >=aFirstLeftIndex ; k--) {
				aDataPoints.add(mDoubleArray4.get(aLastTempIndex - k));
			}
			for (int k = aFirstRightIndex; k<=aLastRightIndex ; k++ ) {
				aDataPoints.add(mDoubleArray4.get(aLastTempIndex + k));
			}

			aInterpolatedSamples = cubicInterpolateDynamic(aDataPoints, aMissedLocation, aMissedSamples);
			for (int k = aMissedSamples.getLast() -1; k>=0; k--) {
				mDoubleArray4.add(aLastTempIndex+1, aInterpolatedSamples.get(k));
			}

			
		}
		
		System.out.println("Time for Interpolation : "+(System.currentTimeMillis() - aStartTime));


		try {
			BufferedWriter br = new BufferedWriter(new FileWriter( iFilePath+".csv"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < mDoubleArray4.size(); i++) {
				sb.append(mDoubleArray1.get(i));
				sb.append(",");
				sb.append(mDoubleArray2.get(i));
				sb.append(",");
				sb.append(mDoubleArray3.get(i));
				sb.append(",");
				sb.append(mDoubleArray4.get(i));
				sb.append("\n");
			}
			br.write(sb.toString());
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		int k =0;
		k = k+1;
	}
	/**
	 * 
	 * @param iDataPoints
	 * @param iMissedLocation Location takes values from 1-10 not 0-9
	 * @param iMissedSamples
	 */
	private static LinkedList<Double> cubicInterpolateDynamic(LinkedList<Double> iDataPoints, LinkedList<Integer> iMissedLocation,
			LinkedList<Integer> iMissedSamples) {
		long st = System.currentTimeMillis();
		int aNoOfDataPoints = iDataPoints.size();
		int aNoOfRegions = aNoOfDataPoints-1;
		int aMissRegion = iMissedLocation.getLast();
		
		double[] aStepSize = new double[aNoOfRegions];
		for (int i =0; i<aNoOfRegions ; i++) {
			aStepSize[i] = 1;
		}
		for (int i =0; i<iMissedLocation.size(); i++) {
			aStepSize[iMissedLocation.get(i)] =  iMissedSamples.get(i)+1;
		}
//		aStepSize[aMissRegion] = iNoOfSamplesMissed+1;
		double[] aSlope = new double[aNoOfRegions];
		for (int i =0; i<aNoOfRegions; i++) {
			aSlope[i] = (iDataPoints.get(i+1) - iDataPoints.get(i))/aStepSize[i];
		}
		
		int aSizeOfTriDiagonalMatrix = aNoOfRegions-1;
		double[][] aTriDiagonalMatrix = new double[aSizeOfTriDiagonalMatrix][aSizeOfTriDiagonalMatrix];
		for (int i = 0; i<aSizeOfTriDiagonalMatrix; i++) {
			aTriDiagonalMatrix[i][i] = 2 * (aStepSize[i] + aStepSize[i+1]);
		}
		for (int i =1; i<aSizeOfTriDiagonalMatrix; i++) {
			aTriDiagonalMatrix[i-1][i] = aStepSize[i];
			aTriDiagonalMatrix[i][i-1] = aStepSize[i];
		}
		
		double[] aB = new double[aSizeOfTriDiagonalMatrix];
		for (int i = 0;i < aSizeOfTriDiagonalMatrix; i++) {
			aB[i] = 6 * (aSlope[i+1] - aSlope[i]);
		}
		
		solve_TriDiagonal(aTriDiagonalMatrix, aB);
		
		double[] aCoiefficients = new double[4];
		
		aCoiefficients[0] = iDataPoints.get(aMissRegion);
		aCoiefficients[1] = aSlope[aMissRegion] - aStepSize[aMissRegion] * (2 * aB[aMissRegion-1] + aB[aMissRegion]) / 6;
		aCoiefficients[2] = aB[aMissRegion-1]/2;
		aCoiefficients[3] = ( aB[aMissRegion] - aB[aMissRegion-1] )/ (6 * aStepSize[aMissRegion]) ;
		LinkedList<Double> aInterpolatedSamples = new LinkedList<>();
		
		for (int i = iMissedSamples.getLast(); i>0; i--) {
			iDataPoints.add(aMissRegion+1, aCoiefficients[0] + aCoiefficients[1] * i + aCoiefficients[2] * Math.pow(i,2) + aCoiefficients[3] * Math.pow(i,3) );
			aInterpolatedSamples.addFirst( aCoiefficients[0] + aCoiefficients[1] * i + aCoiefficients[2] * Math.pow(i,2) + aCoiefficients[3] * Math.pow(i,3) );

		}
		
		System.out.println("Time for cubic interpolation "+(System.currentTimeMillis() - st));

		return aInterpolatedSamples;
		
		
		
	}

	private static void cubicInterpolate(LinkedList<Double> iDataPoints, int iNoOfSamplesMissed) {
		// Hardcode to 10 points either side.
		
		int aNoOfDataPoints = iDataPoints.size();
		int aNoOfRegions = aNoOfDataPoints-1;
		int aMissRegion = aNoOfDataPoints/2 - 1;
		
		double[] aStepSize = new double[aNoOfRegions];
		for (int i =0; i<aNoOfRegions ; i++) {
			aStepSize[i] = 1;
		}
		aStepSize[aMissRegion] = iNoOfSamplesMissed+1;
		
		double[] aSlope = new double[aNoOfRegions];
		for (int i =0; i<aNoOfRegions; i++) {
			aSlope[i] = (iDataPoints.get(i+1) - iDataPoints.get(i))/aStepSize[i];
		}
		
		int aSizeOfTriDiagonalMatrix = aNoOfRegions-1;
		double[][] aTriDiagonalMatrix = new double[aSizeOfTriDiagonalMatrix][aSizeOfTriDiagonalMatrix];
		for (int i = 0; i<aSizeOfTriDiagonalMatrix; i++) {
			aTriDiagonalMatrix[i][i] = 2 * (aStepSize[i] + aStepSize[i+1]);
		}
		for (int i =1; i<aSizeOfTriDiagonalMatrix; i++) {
			aTriDiagonalMatrix[i-1][i] = aStepSize[i];
			aTriDiagonalMatrix[i][i-1] = aStepSize[i];
		}
		
		double[] aB = new double[aSizeOfTriDiagonalMatrix];
		for (int i = 0;i < aSizeOfTriDiagonalMatrix; i++) {
			aB[i] = 6 * (aSlope[i+1] - aSlope[i]);
		}
		
		solve_TriDiagonal(aTriDiagonalMatrix, aB);
		
		double[] aCoiefficients = new double[4];
		
		aCoiefficients[0] = iDataPoints.get(aMissRegion);
		aCoiefficients[1] = aSlope[aMissRegion] - aStepSize[aMissRegion] * (2 * aB[aMissRegion-1] + aB[aMissRegion]) / 6;
		aCoiefficients[2] = aB[aMissRegion-1]/2;
		aCoiefficients[3] = ( aB[aMissRegion] - aB[aMissRegion-1] )/ (6 * aStepSize[aMissRegion]) ;
		
		for (int i =iNoOfSamplesMissed; i>0; i--) {
			iDataPoints.add(aMissRegion+1, aCoiefficients[0] + aCoiefficients[1] * i + aCoiefficients[2] * Math.pow(i,2) + aCoiefficients[3] * Math.pow(i,3) );
		}
		
		
	}
	
	
	private static void solve_TriDiagonal(double[][] iTriDiagonalMatrix, double[] iRHS) {
		long st = System.currentTimeMillis();
		int aLength = iTriDiagonalMatrix.length;
		
		double[] aAlpha = new double[aLength];
		double[] aBeta = new double[aLength-1];
		
		aAlpha[0] = iTriDiagonalMatrix[0][0];
		for (int i =1; i< aLength; i++) {
			aAlpha[i] = iTriDiagonalMatrix[i][i];
			aBeta[i-1] = iTriDiagonalMatrix[i][i-1];
		}
		double aTemp;
		for (int i = 1; i<aLength; i++) {
			aTemp = aBeta[i-1];
			aBeta[i-1] = aTemp / aAlpha[i-1];
			aAlpha[i] = aAlpha[i] - aTemp * aBeta[i-1];
		}
		
		iRHS[0] = iRHS[0] / aAlpha[0];
		for (int i = 1 ; i<aLength; i++) {
			iRHS[i] = ( iRHS[i] - (aBeta[i-1] * aAlpha[i-1] * iRHS[i-1]) ) / aAlpha[i];
		}
		
		for (int i = aLength-2; i>=0; i--) {
			iRHS[i] = iRHS[i] - aBeta[i] * iRHS[i+1];
		}
		
		System.out.println("Time for tri-diagonal "+(System.currentTimeMillis() - st));
	}

	
	private static String getNextValidSample() {
		String aValidSample = "";
		if (mStringArray.size() > 0) {
			mNextValidSampleCounter = -1;
			do { 
				aValidSample = mStringArray.remove();
				mNextValidSampleCounter++;
			} while (aValidSample.length() != mSampleLength && mStringArray.size() > 0);
		}
		return aValidSample;
	}

	private static void feedInputArray(String iInputString) {
	   mDoubleArray1.add(stringToDouble(iInputString.substring(6 * 0 + 2, 6 * 0 + 8)));
	   mDoubleArray2.add(stringToDouble(iInputString.substring(6 * 1 + 2, 6 * 1 + 8)));
	   mDoubleArray3.add(stringToDouble(iInputString.substring(6 * 2 + 2, 6 * 2 + 8)));
	   mDoubleArray4.add(stringToDouble(iInputString.substring(6 * 3 + 2, 6 * 3 + 8)));
	   mDoubleArrCounter++;
	}
	private static double stringToDouble(String iChannelInput) {
		return doubleConv(new BigInteger(iChannelInput, 16).doubleValue());
	}

	private static double doubleConv(double iDoubleValue) {
		double aOut;
		if (iDoubleValue >= mCheck) {
			aOut = (iDoubleValue - mCheckDivide) * mVref / (mCheck - 1) / mGain;
		} else {
			aOut = iDoubleValue / (mCheck - 1) / mGain * mVref;
		}
		return aOut;
	}


}
