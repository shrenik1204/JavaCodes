package Wrapper;

import java.io.File;

public class Filename {

	private static Filename filename;
	static String aFilePath = ""+"/Users/kishoresubramanian/Desktop/Sattva work/Java Results Demo/";
	
	static private String aInputFilePath = "";
	
	 public Filename(String aInputFilePath) {
		 Filename.aInputFilePath = aInputFilePath;
				 createFilePath();
	}
	
	public static String aFilePathMHR;
	public static String aFilePathMQRS;

	
	
	public static String aFilePathFHR;
	public static String aFilePathFQRS;
	public static String aFilePathFHR_FQRS;

	public static String aFilePathStddeviation;

	public static String aFilePathUC;
	
	
	public static String aFilePathICA1;
	public static String aFilePathICA2;

	public static String aFilePathQRSMDet;
	public static String aFilePathQRSMSel;
	public static String aFilePathCHM;
	
	public static String aFilePathResidue;
	
	public static String aFilePathQRSFDet;
	public static String aFilePathQRSFSel;
	public static String aFilePathCHF;
	public static String aFilePathChfInd;
	public static String aFilePathRRMeanFetal;
	public static String aFilePathFqrsSelectionType;

	public static String aFilePathExecutionLogs;
	public static StringBuilder ExecutionLogs = new StringBuilder();
	
	public static StringBuilder QRSM_Detected = new StringBuilder();
	public static StringBuilder ICA1 = new StringBuilder();
	public static StringBuilder CHM = new StringBuilder();
	public static StringBuilder QRSM_Selected = new StringBuilder();
	
	public static StringBuilder RESIDUE = new StringBuilder();

	public static StringBuilder QRSF_Detected = new StringBuilder();
	public static StringBuilder ICA2 = new StringBuilder();
	public static StringBuilder CHF = new StringBuilder();
	public static StringBuilder QRSF_Selected = new StringBuilder();
	public static StringBuilder Stddeviation = new StringBuilder();
	public static StringBuilder UC = new StringBuilder();
	public static StringBuilder CHF_Ind = new StringBuilder();
	public static StringBuilder RRMeanFetal = new StringBuilder();
	public static StringBuilder FqrsSelectionType = new StringBuilder();
	public static StringBuilder FHR = new StringBuilder();
	public static StringBuilder FHR_FQRS = new StringBuilder();



	
	public static int CH_counter = 0;
	
	private static void createFilePath() {
		File afn = new File(aFilePath+File.separator+aInputFilePath);
		if (!afn.exists())
			afn.mkdir();
		aFilePathExecutionLogs = afn.getPath() +File.separator + aInputFilePath + "-Execution"+".csv";
		aFilePathMHR = afn.getPath() +File.separator+ aInputFilePath+"-mhr"+".csv";
				
		aFilePathMQRS = afn.getPath() +File.separator+ aInputFilePath+"-mqrs"+".csv";
		
		
		aFilePathFHR = afn.getPath() +File.separator+aInputFilePath+"-fhr"+".csv";
		aFilePathFQRS = afn.getPath() +File.separator+ aInputFilePath+"-fqrs"+".csv";
		aFilePathFHR_FQRS = afn.getPath() +File.separator+ aInputFilePath+"-fhr_fqrs"+".csv";

		aFilePathStddeviation = afn.getPath() +File.separator+aInputFilePath+"-stddeviation"+".csv";

		aFilePathUC = afn.getPath() +File.separator+ aInputFilePath+"-uc"+".csv";
		
		
		aFilePathICA1 = afn.getPath()+File.separator+ aInputFilePath+"-ica1"+".csv";
		aFilePathICA2 = afn.getPath() +File.separator+ aInputFilePath+"-ica2"+".csv";

		aFilePathQRSMDet = afn.getPath() +File.separator+ aInputFilePath+"-qrsmD"+".csv";
		aFilePathQRSMSel = afn.getPath() +File.separator+ aInputFilePath+"-qrsmS"+".csv";
		aFilePathCHM = afn.getPath() +File.separator+ aInputFilePath+"-chm"+".csv";
		
		aFilePathQRSFDet = afn.getPath() +File.separator+ aInputFilePath+"-qrsfD"+".csv";
		aFilePathQRSFSel = afn.getPath() +File.separator+ aInputFilePath+"-qrsfS"+".csv";
		aFilePathCHF = afn.getPath() +File.separator+ aInputFilePath+"-chf"+".csv";
		aFilePathChfInd = afn.getPath() +File.separator+ aInputFilePath+"-chf Ind"+".csv";
		aFilePathResidue = afn.getPath() +File.separator+ aInputFilePath+"-residue"+".csv";

        aFilePathRRMeanFetal = afn.getPath() +File.separator+ aInputFilePath+"-rrMeanFetal"+".csv";
        aFilePathFqrsSelectionType = afn.getPath() +File.separator+ aInputFilePath+"-FqrsSelectionType"+".csv";
	}
	
	
	
}
