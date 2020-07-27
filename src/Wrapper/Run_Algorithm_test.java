package Wrapper;

import SignalProc.AlgorithmMain;
import SignalProc.SignalProcConstants;
import SignalProc.SignalProcUtils;
import SignalProc.UterineActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Run_Algorithm_test {

    private static Object[] output;

    private static int it = 0;

    private static boolean algoFinished;

    public static void main(String[] args) {
        DataTextFileReader aReadFile = new DataTextFileReader();
        String aInputFilePath = ""+"/Users/kishoresubramanian/Sattva_Aravind/Tests_Aravind/sattva-03-07-22-23-50/algo-new1input-sattva-03-07-22-23-50.txt";

        int aInputPathLength = aInputFilePath.length();
        Filename nFilw = new Filename(aInputFilePath.substring(aInputPathLength-18, aInputPathLength-4));

        String aFilePath = ""+"/Users/kishoresubramanian/Desktop/Sattva work/FHR_Results/";

        String aFilePath_QRSM = aFilePath + "mhr-"+aInputFilePath.substring(aInputPathLength-18, aInputPathLength-4)+".txt";
        String aFilePath_QRSF = aFilePath + "fhr-"+aInputFilePath.substring(aInputPathLength-18, aInputPathLength-4)+".txt";
        String aFilePathUC = aFilePath + "uc-"+aInputFilePath.substring(aInputPathLength-18, aInputPathLength-4)+".txt";
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
        ArrayList<Double> UC = new ArrayList<>();
        ArrayList<Integer> UCLoc = new ArrayList<>();
//        UcAlgo aUcAlgo = new UcAlgo();

//		Filename.summarizedData.append("Iteration, Start Location, MA , QRSM Detection , QRSF Selection Type, Last Fetal QRS, No of QRSF Selected, No of FHR computed, Last RR mean Fetal \n");
        while (aInput.length - (SignalProcConstants.QRS_SHIFT*it+MA_shift) >= SignalProcConstants.NO_OF_SAMPLES)
        {
            double[][] input1 = new double[15000][4];
            double[] input2 = new double[15000];

            for (int i = 0; i<15000; i++)
            {
                for (int j = 0; j<4; j++)
                {
                    input1[i][j] = aInput[i+ SignalProcConstants.QRS_SHIFT*it+MA_shift][j];
                    if (j == 0) {
                        input2[i] = aInput[i+ SignalProcConstants.QRS_SHIFT*it+MA_shift][j];
                    }
                }
            }

            long T1 = System.currentTimeMillis();



            Thread algoThread = new Thread(() -> {
                Thread.currentThread().setName(ApplicationUtils.algoProcessStartCount+ " Algo Main");

                try {
                    output = AlgorithmMain.algoStart(input1, it);

                    algoFinished = true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            algoThread.start();

            Thread ucThread = new Thread(() -> {
                Thread.currentThread().setName(ApplicationUtils.algoProcessStartCount+ " UC Algo");

                try {
                    List<Double> aUc = UterineActivity.uaAlgoDwt(input2);

                    for (int i = 0; i < aUc.size(); i++) {
                        UC.add(aUc.get(i));
                        UCLoc.add(2000 + SignalProcConstants.DIFFERENCE_SAMPLES*i + SignalProcConstants.QRS_SHIFT*SignalProcUtils.currentIteration+SignalProcUtils.dataLossCounter);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            ucThread.start();

            while (!algoFinished) {
                //Wait for the execution to be completed
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (SignalProcUtils.MA_FLAG){
                it--;
                MA_shift += SignalProcUtils.MA_Shift;
                SignalProcUtils.dataLossCounter += SignalProcUtils.MA_Shift;

                SignalProcUtils.lastQRSFetal = 0;
                SignalProcUtils.lastRRMeanFetal = 0;

            } else {
                int[] Loc = (int[]) output[0];
                int[] HRM = (int[]) output[1];
                int[] HRF = (int[]) output[2];
            }
//					for (int z =0; z<20; z++){
//						FHR_plot.add(HRF[z]);
//						HRLocations.add(Loc[z]);
//						MHR_plot.add(HRM[z]);
//					}
            long T2 = System.currentTimeMillis();

            System.out.println("Time for Algo to complete:"+it +" iteration  : "+ (T2 - T1) + " ms");

            System.out.println("***Completed At : "+(new java.text.SimpleDateFormat("H:mm:ss:SSS")).format(java.util.Calendar.getInstance().getTime()));

            System.gc();

            it++;

            algoFinished = false;
        }
//
        SignalProcUtils.qrsFetalLocation.remove(0);
        SignalProcUtils.hrFetal.remove(0);
        SignalProcUtils.qrsMaternalLocation.remove(0);
        SignalProcUtils.hrMaternal.remove(0);

        ////////////Write to file //////////////////////
//		for (int i = 0; i<SignalProcUtils.qrsFetalLocation.size(); i++) {
//			Filename.FHR_FQRS.append(SignalProcUtils.qrsFetalLocation.get(i));
//			Filename.FHR_FQRS.append(",");
//			Filename.FHR_FQRS.append(SignalProcUtils.hrFetal.get(i));
//			Filename.FHR_FQRS.append("\n");
//		}


//		write2file(Filename.FHR_FQRS, Filename.aFilePathFHR_FQRS);
//		write2file(Filename.QRSF_Selected, Filename.aFilePathFQRS);
//        write2file(Filename.CHF_Ind, Filename.aFilePathChfInd);
//        write2file(Filename.RRMeanFetal, Filename.aFilePathRRMeanFetal);
//        write2file(Filename.FqrsSelectionType, Filename.aFilePathFqrsSelectionType);
//        write2file(Filename.FHR, Filename.aFilePathFHR);
//        write2file(Filename.summarizedData, Filename.aFilePathExecutionLogs);
        ////////////Write to file //////////////////////

////////////Write to file //////////////////////
//	for (int i = 0; i<SignalProcUtils.qrsMaternalLocation.size(); i++) {
//		Filename.QRSM_Selected.append(SignalProcUtils.qrsMaternalLocation.get(i));
//		Filename.QRSM_Selected.append(",");
//		Filename.QRSM_Selected.append(SignalProcUtils.hrMaternal.get(i));
//		Filename.QRSM_Selected.append("\n");
//	}
//
//	write2file(Filename.QRSM_Selected, Filename.aFilePathMQRS);
        ////////////Write to file //////////////////////

////////////Write to file //////////////////////
//		for (int i = 0; i<UC.size(); i++) {
//			Filename.UC.append(UCLoc.get(i));
//			Filename.UC.append(",");
//			Filename.UC.append(UC.get(i));
//			Filename.UC.append("\n");
//		}
//
//		write2file(Filename.UC, Filename.aFilePathUC);
        ////////////Write to file //////////////////////



//		fileWrite(aFilePath_QRSF_plot, FHR_plot, HRLocations);
//		fileWrite(aFilePath_QRSM_plot, MHR_plot, HRLocations);
//		fileWrite1(aFilePath_QRSF, SignalProcUtils.HR_FETAL, SignalProcUtils.QRS_FETAL_LOCATION );
//		fileWrite1(aFilePath_QRSM, SignalProcUtils.HR_MATERNAL, SignalProcUtils.QRS_MATERNAL_LOCATION);
//



    }

//	private static void fileWrite1(String iFilePath, LinkedList<Float> iHR,
//			LinkedList<Integer> iQRS) {
//		// TODO Auto-generated method stub
//		try {
//			BufferedWriter br = new BufferedWriter(new FileWriter(iFilePath));
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < iHR.size(); i++) {
//			 sb.append(iHR.get(i));
//			 sb.append(",");
//			 sb.append(iQRS.get(i));
//			 sb.append("\n");
//			}
////			sb.append("\n");
//
//
//			br.write(sb.toString());
//			br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//	}

//	public static void write2file(StringBuilder sb, String iFilename) {
//		try {
//			BufferedWriter br = new BufferedWriter(new FileWriter(iFilename));
//			br.write(sb.toString());
//			br.close();
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//	}


//	private static void fileWrite(String iFilePath, LinkedList<Integer> iHR,
//			LinkedList<Integer> iQRS) {
//		// TODO Auto-generated method stub
//		try {
//			BufferedWriter br = new BufferedWriter(new FileWriter(iFilePath));
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < iHR.size(); i++) {
//			 sb.append(iHR.get(i));
//			 sb.append(",");
//			 sb.append(iQRS.get(i));
//			 sb.append("\n");
//			}
////			sb.append("\n");
//
//
//			br.write(sb.toString());
//			br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//
//	}

//	private static void fileWrite2(String iFilePath, int[] iQRS) {
//		// TODO Auto-generated method stub
//		try {
//			BufferedWriter br = new BufferedWriter(new FileWriter(iFilePath));
//			StringBuilder sb = new StringBuilder();
//			for (int i = 0; i < iQRS.length; i++) {
//			 sb.append(iQRS[i]);
//
//			 sb.append("\n");
//			}
////			sb.append("\n");
//
//
//			br.write(sb.toString());
//			br.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//
//
//	}

}
