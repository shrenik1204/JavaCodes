package Wrapper;

public class Convert2Csv {

	public static void main(String[] args) {
		
		String aInputFilePath = ""+"/Users/kishoresubramanian/SattvaMedT Drive/AUF_Pune_2018/Khanade Hospital/K1/sattva-01-30-10-33-33/input.txt";
		
		DataTextFileReader aReadFile =  new DataTextFileReader();
		
		double[][] aOutput = aReadFile.readFile(aInputFilePath,1);
		
		
		
	}
	

}
