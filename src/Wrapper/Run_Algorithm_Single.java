package Wrapper;

import SignalProc.AlgorithmMain;
import SignalProc.SignalProcConstants;
import SignalProc.SignalProcUtils;
import SignalProc.UterineActivity_New;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Run_Algorithm_Single {

    public static void main(String[] args) {
        SignalProcUtils.reset();

        DataTextFileReader aReadFile = new DataTextFileReader();
        String aInputFilePath = ""+"/Users/kishoresubramanian/Sattva_Aravind/DATA/Manipal Data/ManipalDay3 - 19th Sept/sattva-2018-Sep-19-10-13-19/algo-new1input-sattva-2018-Sep-19-10-13-19.txt";

        int aInputPathLength = aInputFilePath.length();
        Filename nFilw = new Filename(aInputFilePath.substring(aInputPathLength-18, aInputPathLength-4));

        String aFilePath = ""+"/Users/kishoresubramanian/Desktop/Sattva work/Java Results Demo/";

        String aFilePath_QRSM = aFilePath + "mhr-"+aInputFilePath.substring(aInputPathLength-18, aInputPathLength-4)+".txt";
        String aFilePath_QRSF = aFilePath + "fhr-"+aInputFilePath.substring(aInputPathLength-18, aInputPathLength-4)+".txt";
        String aFilePathUA = aFilePath + "uc-"+aInputFilePath.substring(aInputPathLength-18, aInputPathLength-4)+".txt";
        String aFilePath_QRSM_plot = aFilePath + "mhr-plot-"+aInputFilePath.substring(aInputPathLength-18, aInputPathLength-4)+".txt";
        String aFilePath_QRSF_plot = aFilePath + "fhr-plot-"+aInputFilePath.substring(aInputPathLength-18, aInputPathLength-4)+".txt";


        /**
         * For old 1-bit sample count
         */
//		double[][] aInput  = aReadFile.readFile(aInputFilePath, 0);

        /**
         * Forcubic interpolate and 2 bit sample count.
         */
//		String iFilePath = "/Users/kishoresubramanian/Desktop/sattva-11-23-17-58-24/new1input-sattva-11-23-17-58-24.txt";

        CubicInterpolate15 aCubic = new CubicInterpolate15();
        double[][] aInput = aCubic.convert(aInputFilePath);
        AlgorithmMain aAlgo = new AlgorithmMain();


        LinkedList<Integer> FHR_plot = new LinkedList<>();
        LinkedList<Integer> HRLocations = new LinkedList<>();
        LinkedList<Integer> MHR_plot = new LinkedList<>();
        int MA_shift = 0;
        int aNit = aInput.length/10000 - 1;
//		ArrayList<Double> UA = new ArrayList<>();
//		ArrayList<Integer> UALoc = new ArrayList<>();

        int it = 0;
        Filename.ExecutionLogs.append("Iteration, Start Location, MA , QRSM Detection, ch , StartIndex  , Overlap, QRSF Selection Type, Last Fetal QRS, No of QRSF Selected, No of FHR computed, Last RR mean Fetal, Last Valid RRMean Fetal \n");
        Filename.ExecutionLogs_Maternal.append("Iteration, Start Location, MA , IndCh1, IndCh2, IndCh3, IndCh4,Maxind-nextmax, ch , QRSM Detection, No of QRSM Selected,  No of MHR computed ,Last RR mean Maternal\n");
        Filename.MAlogs.append("Iteration, Chnk 1, Chnk 2, Chnk 3, Chnk 4, Chnk 5, Chnk 6, Chnk 7, Chnk 8, Chnk 9, Chnk 10, Chnk 11, Chnk 12 \n");
        Filename.PSDlogs.append("Iteration, Values \n");
        while (aInput.length - (SignalProcConstants.QRS_SHIFT*it+MA_shift) >= SignalProcConstants.NO_OF_SAMPLES)
        {
            double[][] input1 = new double[15000][4];
            double[] input2 = new double[15000];

            for (int i = 0; i<15000; i++)
            {
                for (int j = 0; j<4; j++)
                {
                    input1[i][j] = aInput[i+ SignalProcConstants.QRS_SHIFT*it+MA_shift][j];
                    if (j == 1) {
                        input2[i] = aInput[i+ SignalProcConstants.QRS_SHIFT*it+MA_shift][j];
                    }
                }
            }

            Object[] Final;
            try {
                long T1 = System.currentTimeMillis();

                Final = AlgorithmMain.algoStart(input1, it);
//                int[] fhrData = (int[])Final[2];
//                int count = 0;
//                for (int j = 0; j < SignalProcConstants.NO_OF_PRINT_VALUES; j++) {
//                    if(fhrData[j] == 1){
//                        count++;
//                    }
//                }
//                if(ApplicationUtils.algoProcessStartCount == 0){
//                    if (count >= 15) {
////                        ApplicationUtils.mConversionFlag = ApplicationUtils.IDLE;
//                        ApplicationUtils.algoProcessStartCount--;
//                        SignalProcUtils.currentIteration--;
//                        ApplicationUtils.HANDLE_DATA_SIZE = 15000;
//                        SignalProcUtils.dataLossCounter = 0;
//                        SignalProcUtils.lastIteration = -1;
////                        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Iteration 0 restarted, FHR Counter :" + count, FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//                        SignalProcUtils.aLocation_UA_Batch.clear();
//                        return;
//                    }
//                }

//					double[] aUc = aUcAlgo.ucAlgoDwt(input2);
                //Change by Aravind
//                List<Double> aUa = UterineActivity.uaAlgoDwt(input2);
                List<Double> aUa = UterineActivity_New.uaAlgoDwt(input1);

//					double[] aUc = UterineActivity.ucAlgoDwt(input2);
                List<Integer> aLocation = new ArrayList<>(SignalProcConstants.NO_OF_PRINT_VALUES);

                for (int i = 0; i < SignalProcConstants.NO_OF_PRINT_VALUES; i++) {
                    aLocation.add(1);
                }
                for (int i = 0; i < SignalProcConstants.NO_OF_PRINT_VALUES; i++) {
                    if(SignalProcUtils.currentIteration == 0){
                        SignalProcUtils.dataLossCounter = 0;
                    }
//                    if(SignalProcUtils.currentIteration == 1 && SignalProcUtils.independentCount == 2) {
//                        SignalProcUtils.locationIteration = SignalProcUtils.currentIteration - 1;
//                    }else if(SignalProcUtils.currentIteration == 2 && SignalProcUtils.independentCount == 2) {
//                        SignalProcUtils.locationIteration = SignalProcUtils.currentIteration - 2;
//                    }else if(SignalProcUtils.currentIteration == 3 && SignalProcUtils.independentCount == 2) {
//                        SignalProcUtils.locationIteration = SignalProcUtils.currentIteration - 3;
//                    }
                    aLocation.set(i, 2000 + SignalProcConstants.DIFFERENCE_SAMPLES * i + SignalProcConstants.QRS_SHIFT * SignalProcUtils.currentIteration + SignalProcUtils.dataLossCounter);
                    SignalProcUtils.UA.add(aUa.get(i));
                    SignalProcUtils.UALoc.add(aLocation.get(i));
                }
//                if(SignalProcUtils.currentIteration == 0){
//                    aLocation.clear();
//                    SignalProcUtils.aLocation_UA_Batch.clear();
//                }

//					for (int i = 0; i < aUa.size(); i++) {
//
//					}
                if (SignalProcUtils.MA_FLAG){
                    it--;
                    MA_shift += SignalProcUtils.MA_Shift;
                    SignalProcUtils.dataLossCounter += SignalProcUtils.MA_Shift;

                    SignalProcUtils.lastQRSFetal = 0;
                    SignalProcUtils.lastRRMeanFetal = 0;
                    SignalProcUtils.lastRRMeanMaternal = 0;

                }
                else {
                    List<Integer> Loc = (List<Integer>) Final[0];
                    int[] HRM = (int[]) Final[1];
                    int[] HRF = (int[]) Final[2];
                }
//					for (int z =0; z<20; z++){
//						FHR_plot.add(HRF[z]);
//						HRLocations.add(Loc[z]);
//						MHR_plot.add(HRM[z]);
//					}
                long T2 = System.currentTimeMillis();
                System.out.println("Time for Algo to complete:"+it +" iteration  : "+ (T2 - T1) + " ms");


            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("***Completed At : "+(new java.text.SimpleDateFormat("H:mm:ss:SSS")).format(java.util.Calendar.getInstance().getTime()));

            System.gc();
            it++;
        }
//
        SignalProcUtils.qrsFetalLocation.remove(0);
        SignalProcUtils.hrFetal.remove(0);
        SignalProcUtils.qrsMaternalLocation.remove(0);
        SignalProcUtils.hrMaternal.remove(0);

        ////////////Write to file //////////////////////
        for (int i = 0; i < SignalProcUtils.qrsFetalLocation.size(); i++) {
            Filename.FHR_FQRS.append(SignalProcUtils.qrsFetalLocation.get(i));
            Filename.FHR_FQRS.append(",");
            Filename.FHR_FQRS.append(SignalProcUtils.hrFetal.get(i));
            Filename.FHR_FQRS.append("\n");
        }


        write2file(Filename.FHR_FQRS, Filename.aFilePathFHR_FQRS);
        write2file(Filename.QRSF_Selected, Filename.aFilePathFQRS);
        write2file(Filename.CHF_Ind, Filename.aFilePathChfInd);
        write2file(Filename.RRMeanFetal, Filename.aFilePathRRMeanFetal);
        write2file(Filename.FqrsSelectionType, Filename.aFilePathFqrsSelectionType);
        write2file(Filename.FHR, Filename.aFilePathFHR);
        write2file(Filename.ExecutionLogs, Filename.aFilePathExecutionLogs);
        write2file(Filename.ExecutionLogs_Maternal, Filename.aFilePathExecutionLogs_maternal);

        ////////////Write to file //////////////////////

////////////Write to file //////////////////////
        for (int i = 0; i < SignalProcUtils.qrsMaternalLocation.size(); i++) {
            Filename.QRSM_Selected.append(SignalProcUtils.qrsMaternalLocation.get(i));
            Filename.QRSM_Selected.append(",");
            Filename.QRSM_Selected.append(SignalProcUtils.hrMaternal.get(i));
            Filename.QRSM_Selected.append("\n");
        }

        write2file(Filename.QRSM_Selected, Filename.aFilePathMQRS);
        write2file(Filename.MHR, Filename.aFilePathMHR);

        ////////////Write to file /////////////////////

        write2file(Filename.Stddeviation, Filename.aFilePathStddeviation);


////////////Write to file //////////////////////
//        Filename.UA.delete(0,UA.size());
        write2file(Filename.MAlogs, Filename.aFilePathMALogs);
        write2file(Filename.PSDlogs, Filename.aFilePathPSDLogs);

        for (int i = 0; i < SignalProcUtils.UALoc.size(); i++) {
            Filename.UA.append(SignalProcUtils.UALoc.get(i));
            Filename.UA.append(",");
            Filename.UA.append(SignalProcUtils.UA.get(i));
            Filename.UA.append("\n");
        }

        write2file(Filename.UA, Filename.aFilePathUA);
        write2file(Filename.ICA1, Filename.aFilePathICA1);
        write2file(Filename.ICA2, Filename.aFilePathICA2);

        ////////////Write to file //////////////////////




//		fileWrite(aFilePath_QRSF_plot, FHR_plot, HRLocations);
//		fileWrite(aFilePath_QRSM_plot, MHR_plot, HRLocations);
//		fileWrite1(aFilePath_QRSF, SignalProcUtils.HR_FETAL, SignalProcUtils.QRS_FETAL_LOCATION );
//		fileWrite1(aFilePath_QRSM, SignalProcUtils.HR_MATERNAL, SignalProcUtils.QRS_MATERNAL_LOCATION);
//



    }

    private static void fileWrite1(String iFilePath, LinkedList<Float> iHR,
                                   LinkedList<Integer> iQRS) {
        // TODO Auto-generated method stub
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(iFilePath));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < iHR.size(); i++) {
                sb.append(iHR.get(i));
                sb.append(",");
                sb.append(iQRS.get(i));
                sb.append("\n");
            }
//			sb.append("\n");
            br.write(sb.toString());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void write2file(StringBuilder sb, String iFilename) {
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(iFilename));
            br.write(sb.toString());
            br.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static void fileWrite(String iFilePath, LinkedList<Integer> iHR,
                                  LinkedList<Integer> iQRS) {
        // TODO Auto-generated method stub
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(iFilePath));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < iHR.size(); i++) {
                sb.append(iHR.get(i));
                sb.append(",");
                sb.append(iQRS.get(i));
                sb.append("\n");
            }
//			sb.append("\n");
            br.write(sb.toString());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void fileWrite2(String iFilePath, int[] iQRS) {
        // TODO Auto-generated method stub
        try {
            BufferedWriter br = new BufferedWriter(new FileWriter(iFilePath));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < iQRS.length; i++) {
                sb.append(iQRS[i]);

                sb.append("\n");
            }
//			sb.append("\n");
            br.write(sb.toString());
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

