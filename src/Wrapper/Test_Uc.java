package Wrapper;

import SignalProc.UterineActivity;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.List;

public class Test_Uc {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		DataTextFileReader aReadFile = new DataTextFileReader();
		String aInputFilePath = ""+"/Users/kishoresubramanian/SattvaMedT Drive/CER_QMS/DMH/July - 7/824579/new1input-sattva-07-07-12-34-59";
		
		double[][] aInput  = aReadFile.readFile(aInputFilePath, 0);
		
		double[] aInput1 = new double[15000];
		int aNit = aInput.length/10000 - 2;
//		UcAlgo aUc = new UcAlgo();
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter("UC.txt"));
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i<aNit; i++) {
				for (int j = 0; j<15000; j++) {
					aInput1[j] = aInput[10000*i + j][0];
				}

				List<Double> aUA_Output= UterineActivity.uaAlgoDwt(aInput1);
				for (int j = 0; j < 20; j++) {
					 sb.append(aUA_Output.get(j));
					 sb.append("\n");
					}

			}
			br.write(sb.toString());
			br.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		
		
	}

}
