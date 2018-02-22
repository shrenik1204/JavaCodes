package Wrapper;

import SignalProc.MatrixFunctions;

public class Test_filter {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		DataTextFileReader aReadFile = new DataTextFileReader();
		String aInputFilePath = ""+"/Users/kishoresubramanian/SattvaMedT Drive/CER_QMS/DMH/August - 28/797050/new1sattva-mayuriubhe08-28-13-35-08.txt";
		
		double[][] aInput  = aReadFile.readFile(aInputFilePath, 0);
		
		MatrixFunctions aMF = new MatrixFunctions();
		double aInput1[] = new double[2000];
		for (int i = 0; i<2000; i++) {
			aInput1[i] = aInput[i][0];
		}

		try {
			double[] aFFT = aMF.fastfouriertransform_MA(aInput1);
			int k = 0;
			k++;
		} catch (Exception e) {
			e.printStackTrace();
		}


		
		
		
	}

}
