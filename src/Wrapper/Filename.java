package Wrapper;

import java.io.File;

public class Filename {

	private static Filename filename;
	static String aFilePath = ""+"/Users/kishoresubramanian/Sattva_Aravind/Java Generated Data/APR_MAY_Newelectrode";
	static String aFilePath_FHR = ""+"/Users/kishoresubramanian/Sattva_Aravind/Java Generated Data/FHR/";
	static String aFilePath_EXE = ""+"/Users/kishoresubramanian/Sattva_Aravind/Java Generated Data/EXE/";
	static String aFilePath_UA = ""+"/Users/kishoresubramanian/Sattva_Aravind/Java Generated Data/UA/";

	static private String aInputFilePath = "";
	
	 public Filename(String aInputFilePath) {
		 Filename.aInputFilePath = aInputFilePath;
				 createFilePath();
	}

	public static String aFilePathMALogs;
	public static String aFilePathPSDLogs;

	public static String aFilePathMHR;
	public static String aFilePathMQRS;

	public static String aFilePathFHR_new;
	public static String aFilePathExecutionLogs_new;
	public static String aFilePathUA_new;


	public static String aFilePathFHR;
	public static String aFilePathFQRS;
	public static String aFilePathFHR_FQRS;

	public static String aFilePathStddeviation;

	public static String aFilePathUA;

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
	public static String aFilePathExecutionLogs_maternal;
	public static StringBuilder ExecutionLogs = new StringBuilder();
	public static StringBuilder ExecutionLogs_Maternal = new StringBuilder();

	public static StringBuilder MAlogs = new StringBuilder();

	public static StringBuilder PSDlogs = new StringBuilder();


	public static StringBuilder QRSM_Detected = new StringBuilder();
	public static StringBuilder ICA1 = new StringBuilder();
	public static StringBuilder CHM = new StringBuilder();
	public static StringBuilder QRSM_Selected = new StringBuilder();
	public static StringBuilder MHR = new StringBuilder();

	public static StringBuilder RESIDUE = new StringBuilder();

	public static StringBuilder QRSF_Detected = new StringBuilder();
	public static StringBuilder ICA2 = new StringBuilder();
	public static StringBuilder CHF = new StringBuilder();
	public static StringBuilder QRSF_Selected = new StringBuilder();
	public static StringBuilder Stddeviation = new StringBuilder();
	public static StringBuilder UA = new StringBuilder();
	public static StringBuilder CHF_Ind = new StringBuilder();
	public static StringBuilder RRMeanFetal = new StringBuilder();
	public static StringBuilder FqrsSelectionType = new StringBuilder();
	public static StringBuilder FHR = new StringBuilder();
	public static StringBuilder FHR_FQRS = new StringBuilder();

	public static int CH_counter = 0;
	
	private static void createFilePath() {
		File afn = new File(aFilePath+File.separator+aInputFilePath);
		File afnfhr = new File(aFilePath_FHR+File.separator);
		File afnexe = new File(aFilePath_EXE+File.separator);
		File afnua = new File(aFilePath_UA+File.separator);

		if (!afn.exists()||!afnexe.exists()||!afnfhr.exists()||!afnua.exists()) {
			afn.mkdir();
//			afnfhr.mkdir();
//			afnexe.mkdir();
//			afnua.mkdir();
		}
		aFilePathExecutionLogs_new = afnexe.getPath() +File.separator + aInputFilePath + "-Execution"+".csv";
		aFilePathFHR_new = afnfhr.getPath() +File.separator+aInputFilePath+"-fhr"+".csv";
		aFilePathUA_new = afnua.getPath() +File.separator+ aInputFilePath+"-ua"+".csv";

		aFilePathMALogs = afn.getPath() +File.separator + aInputFilePath + "-MA_Logs"+".csv";
		aFilePathPSDLogs = afn.getPath() +File.separator + aInputFilePath + "-PSD_Logs"+".csv";

		aFilePathExecutionLogs = afn.getPath() +File.separator + aInputFilePath + "-Execution"+".csv";
		aFilePathExecutionLogs_maternal = afn.getPath() +File.separator + aInputFilePath + "-Execution_maternal"+".csv";
		aFilePathMHR = afn.getPath() +File.separator+ aInputFilePath+"-mhr"+".csv";
				
		aFilePathMQRS = afn.getPath() +File.separator+ aInputFilePath+"-mqrs"+".csv";

		aFilePathFHR = afn.getPath() +File.separator+aInputFilePath+"-fhr"+".csv";
		aFilePathFQRS = afn.getPath() +File.separator+ aInputFilePath+"-fqrs"+".csv";
		aFilePathFHR_FQRS = afn.getPath() +File.separator+ aInputFilePath+"-fhr_fqrs"+".csv";

		aFilePathStddeviation = afn.getPath() +File.separator+aInputFilePath+"-stddeviation"+".csv";

		aFilePathUA = afn.getPath() +File.separator+ aInputFilePath+"-ua"+".csv";

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

	public static void reset() {
		aFilePathMALogs = null;
		aFilePathPSDLogs = null;
		aFilePathMHR = null;
		aFilePathMQRS = null;

		aFilePathFHR_new = null;
		aFilePathExecutionLogs_new = null;
		aFilePathUA_new = null;


		aFilePathFHR = null;
		aFilePathFQRS = null;
		aFilePathFHR_FQRS = null;

		aFilePathStddeviation = null;

		aFilePathUA = null;


		aFilePathICA1 = null;
		aFilePathICA2 = null;

		aFilePathQRSMDet = null;
		aFilePathQRSMSel = null;
		aFilePathCHM = null;

		aFilePathResidue = null;

		aFilePathQRSFDet = null;
		aFilePathQRSFSel = null;
		aFilePathCHF = null;
		aFilePathChfInd = null;
		aFilePathRRMeanFetal = null;
		aFilePathFqrsSelectionType = null;

		aFilePathExecutionLogs = null;
		aFilePathExecutionLogs_maternal = null;
		ExecutionLogs = new StringBuilder();
		ExecutionLogs_Maternal = new StringBuilder();

		MAlogs = new StringBuilder();
		PSDlogs = new StringBuilder();

		QRSM_Detected = new StringBuilder();
		ICA1 = new StringBuilder();
		CHM = new StringBuilder();
		QRSM_Selected = new StringBuilder();
		MHR = new StringBuilder();

		RESIDUE = new StringBuilder();

		QRSF_Detected = new StringBuilder();
		ICA2 = new StringBuilder();
		CHF = new StringBuilder();
		QRSF_Selected = new StringBuilder();
		Stddeviation = new StringBuilder();
		UA = new StringBuilder();
		CHF_Ind = new StringBuilder();
		RRMeanFetal = new StringBuilder();
		FqrsSelectionType = new StringBuilder();
		FHR = new StringBuilder();
		FHR_FQRS = new StringBuilder();

		CH_counter = 0;
	}
}
