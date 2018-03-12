package SignalProc;

//import com.sattvamedtech.fetallite.FLApplication;
//import com.sattvamedtech.fetallite.enums.FileLoggerType;
//import com.sattvamedtech.fetallite.enums.TaskEnum;
//import com.sattvamedtech.fetallite.helper.FileLoggerHelper;
//import com.sattvamedtech.fetallite.utils.ApplicationUtils;

import Wrapper.Filename;

import java.util.LinkedList;

//import timber.log.Timber;

public class AlgorithmMain {

    /**
     * Object initialization of  {@link MatrixFunctions}
     */
    MatrixFunctions mMatrixFunctions = new MatrixFunctions();

    public Object[] algoStart(double[][] iInput, int iCurrentIteration) throws Exception {


        long aST = System.currentTimeMillis();
        int[] aFinalQRSM;
        float[] aFinalHRM;
        int[] aFinalQrsmHrPlot = new int[SignalProcConstants.NO_OF_PRINT_VALUES];
        String[] aHRmPrint = new String[SignalProcConstants.NO_OF_PRINT_VALUES];

        int[] aFinalQRSF;
        float[] aFinalHRF;
        int[] aFinalQrsfHrPlot = new int[SignalProcConstants.NO_OF_PRINT_VALUES];
        String[] aHRfPrint = new String[SignalProcConstants.NO_OF_PRINT_VALUES];


        for (int i = 0; i < SignalProcConstants.NO_OF_PRINT_VALUES; i++) {

            aFinalQrsfHrPlot[i] = 1;
            aHRfPrint[i] = "0001";

            aFinalQrsmHrPlot[i] = 1;
            aHRmPrint[i] = "0001";
        }


        for (int i = 0; i < SignalProcConstants.NO_OF_PRINT_VALUES; i++) {

            SignalProcUtils.mhrPlot[i] = 1;
            SignalProcUtils.mhrPrint[i] = "0001";

            SignalProcUtils.fhrPlot[i] = 1;
            SignalProcUtils.fhrPrint[i] = "0001";
        }


        SignalProcUtils.currentIteration = iCurrentIteration;
        Filename.ExecutionLogs.append(iCurrentIteration+",");
        SignalProcUtils.qrsCurrentShift = SignalProcConstants.QRS_SHIFT * SignalProcUtils.currentIteration + SignalProcUtils.dataLossCounter;
        Filename.ExecutionLogs.append(SignalProcUtils.qrsCurrentShift+",");

        if (iCurrentIteration == 0) {
            SignalProcUtils.qrsFetalLocation.add(0);
            SignalProcUtils.hrFetal.add(0f);
            SignalProcUtils.qrsMaternalLocation.add(0);
            SignalProcUtils.hrMaternal.add(0f);
        }

        LinkedList<Integer> aQrsF = new LinkedList<Integer>();
        LinkedList<Integer> aQrsM = new LinkedList<Integer>();

        /**
         * NUll input check
         */
        double[][] aInput = new double[SignalProcConstants.NO_OF_SAMPLES][SignalProcConstants.NO_OF_CHANNELS];
        int aInputZeroCount = 0;
        for (int i = 0; i < SignalProcConstants.NO_OF_SAMPLES; i++) {
            for (int j = 0; j < SignalProcConstants.NO_OF_CHANNELS; j++) {
                aInput[i][j] = iInput[i][j];
                if (iInput[i][j] == 0) {
                    aInputZeroCount++;
                }
            }
        }
        if (aInputZeroCount > (15000 * 4 * 0.2)) {
            throw new Exception("Invalid input");
        }

//        ApplicationUtils.getTaskList().add(TaskEnum.IMPULSE_FILTER.getTaskName() + " started at : " + ApplicationUtils.getCurrentTime());
        /**
         * Impulse filtering
         */
//        ImpulseFilter aImpulseFilter = new ImpulseFilter();
//        double[][] aEcgImpulse = aImpulseFilter.impulseFilterParallel(aInput);

//        Timber.i("AlgorithmMain : Time for Impulse Filter : "+(System.currentTimeMillis()-aST)+" ms");
        long aET = System.currentTimeMillis();

        /**
         * NUll input check
         */
//        int aImpulseZeroCount = 0;
//        for (int i = 0; i < SignalProcConstants.NO_OF_SAMPLES; i++) {
//            for (int j = 0; j < SignalProcConstants.NO_OF_CHANNELS; j++) {
//                if (aInput[i][j] == 0) {
//                    aImpulseZeroCount++;
//                }
//            }
//        }
//        if (aImpulseZeroCount > (15000 * 4 * 0.2)) {
//            throw new Exception("Invalid Impulse Filter output");
//        }

//        ApplicationUtils.getTaskList().add(TaskEnum.LOW_HI_NOTCH_FILTER.getTaskName() + " started at : " + ApplicationUtils.getCurrentTime());

        /**
         * /** Filtering : Low, high, notch
         */
        FilterSerial aFilter = new FilterSerial();
//        FilterLowHiNotch aFilter = new FilterLowHiNotch();
        double[][] aEcgFilter = aFilter.filterParallel(aInput);

        if (SignalProcUtils.MA_FLAG){
            Filename.CHF_Ind.append(SignalProcUtils.currentIteration+", , , , , , , , , ,\n ");
            Filename.RRMeanFetal.append(",\n");
            Filename.FqrsSelectionType.append(" MA \n");
            Filename.ExecutionLogs.append(SignalProcUtils.MA_Shift+",,,,,\n");
            return null;
        }
        else {
            Filename.ExecutionLogs.append(SignalProcUtils.MA_Shift+",");
        }
//        Timber.i("AlgorithmMain : Time for Filtering : "+(System.currentTimeMillis()-aET)+" ms");
//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : Algorithm Main : Time for filtering : "+(System.currentTimeMillis()-aET)+" msec.", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

        aET = System.currentTimeMillis();
        /**
         * Perform ICA on filtered data
         */
        JadeMainFuction aJade = new JadeMainFuction();
        double[][] aEcgIca1 = aJade.jade(aEcgFilter);


//        ApplicationUtils.getTaskList().add(TaskEnum.MQRS_DETECTION.getTaskName() + " started at : " + ApplicationUtils.getCurrentTime());
//
//        Timber.i("AlgorithmMain : Time for ICA1 : "+(System.currentTimeMillis()-aET)+" ms");
//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : Algorithm Main : Time for ICA1 : "+(System.currentTimeMillis()-aET)+" msec.", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

        aET = System.currentTimeMillis();
        /**
         * Estimate Maternal QRS
         */
        MQRSDetection mqrsDetection = new MQRSDetection();
        int[] aQRSM = mqrsDetection.mQRS(aEcgIca1);

//        Timber.i("AlgorithmMain : Time for MQRS detection : "+(System.currentTimeMillis()-aET)+" ms");
//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : Algorithm Main : Time for mQRS : "+(System.currentTimeMillis()-aET)+" msec.", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
        aET = System.currentTimeMillis();


        int aQRSF[];
        if (aQRSM.length >= SignalProcConstants.MQRS_MIN_SIZE && aQRSM.length <= SignalProcConstants.MQRS_MAX_SIZE) {
//            ApplicationUtils.getTaskList().add(TaskEnum.MQRS_CANCELLATION_PARALLEL.getTaskName() + " started at : " + ApplicationUtils.getCurrentTime());

            /**
             * Maternal HR calculating
             */
//            int aNoQrsMRemoved = 0;
//            for (int j = 0; j < aQRSM.length; j++) {
//                if (aQRSM[j] >= SignalProcConstants.QRS_START_VALUE ) { // (&& aQRSM[j] < SignalProcConstants.QRS_END_VALUE)
//                    aQrsM.add(aQRSM[j] + SignalProcConstants.QRS_SHIFT * SignalProcUtils.currentIteration + SignalProcUtils.dataLossCounter);
//                }
//                else {
//                    aNoQrsMRemoved++;
//                }
//            }

            HeartRateMaternal aMHR = new HeartRateMaternal();
            aMHR.heartRate(aQRSM);
            int aSizeMaternalHR = SignalProcUtils.qrsmLocTemp.size();
//            if (SignalProcUtils.maternalHrEnd == 1){
//                aSizeMaternalHR = aSizeMaternalHR + 4;
//            }

//            FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : Length of MQRS selected, length of MHR detected : %d, %d", aQRSM.length, aSizeMaternalHR), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

//            if ( aSizeMaternalHR > SignalProcConstants.MQRS_MIN_SIZE - 2) {


//                if ( SignalProcUtils.maternalHrNew == 0) {
//                    aFinalHRM[0] = SignalProcUtils.hrMaternal.get(SignalProcUtils.lastMaternalPlotIndex - 1);
//                    aFinalQRSM[0] = SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex - 1);
//                } else {
//                    aFinalHRM[0] = 0;
//                    aFinalQRSM[0] = SignalProcConstants.QRS_SHIFT * SignalProcUtils.currentIteration + 2000 + SignalProcUtils.dataLossCounter;
//                }


                if ( SignalProcUtils.qrsmLocTemp.getFirst() > SignalProcConstants.QRS_START_VALUE) {
                    aFinalQRSM = new int[aSizeMaternalHR+1];
                    aFinalHRM = new float[aSizeMaternalHR+1];

                    aFinalHRM[0] = 0;
                    aFinalQRSM[0] = SignalProcConstants.QRS_START_VALUE + SignalProcUtils.qrsCurrentShift;
                    for (int i = 0; i < aSizeMaternalHR; i++) {
                        aFinalHRM[i+1] = SignalProcUtils.hrmTemp.get(i);
                        aFinalQRSM[i+1] = SignalProcUtils.qrsmLocTemp.get(i) + SignalProcUtils.qrsCurrentShift;
                        if (SignalProcUtils.qrsmLocTemp.get(i) < SignalProcConstants.QRS_END_VALUE && SignalProcUtils.qrsmLocTemp.get(i) >= SignalProcConstants.QRS_START_VALUE ){
                            SignalProcUtils.qrsMaternalLocation.add(SignalProcUtils.qrsmLocTemp.get(i) + SignalProcUtils.qrsCurrentShift);
                            SignalProcUtils.hrMaternal.add(SignalProcUtils.hrmTemp.get(i));
                        }
                    }
                }
                else {
                    aFinalQRSM = new int[aSizeMaternalHR];
                    aFinalHRM = new float[aSizeMaternalHR];
                    for (int i = 0; i < aSizeMaternalHR; i++) {
                        aFinalHRM[i] = SignalProcUtils.hrmTemp.get(i);
                        aFinalQRSM[i] = SignalProcUtils.qrsmLocTemp.get(i) + SignalProcUtils.qrsCurrentShift;
                        if (SignalProcUtils.qrsmLocTemp.get(i) < SignalProcConstants.QRS_END_VALUE && SignalProcUtils.qrsmLocTemp.get(i) >= SignalProcConstants.QRS_START_VALUE){
                            SignalProcUtils.qrsMaternalLocation.add(SignalProcUtils.qrsmLocTemp.get(i) + SignalProcUtils.qrsCurrentShift);
                            SignalProcUtils.hrMaternal.add(SignalProcUtils.hrmTemp.get(i));
                        }
                    }


                }
//                for (int i = 0; i<SignalProcUtils.qrsmLocTemp.size() ; i++){
//                    aFinalHRM[i+1] = SignalProcUtils.hrmTemp.get(i);
//                    aFinalQRSM[i+1] = SignalProcUtils.qrsmLocTemp.get(i);
//                    if ( i == SignalProcUtils.qrsmLocTemp.size() -1){
//                        if (SignalProcUtils.maternalHrEnd == 0){
//                            SignalProcUtils.qrsMaternalLocation.add(SignalProcUtils.qrsmLocTemp.get(i));
//                            SignalProcUtils.hrMaternal.add(SignalProcUtils.hrmTemp.get(i));
//
////                            FileLoggerHelper.getInstance().sendLogData(String.valueOf(SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.qrsMaternalLocation.size() - 1)) + "," + String.valueOf(SignalProcUtils.hrMaternal.get(SignalProcUtils.hrMaternal.size() - 1)), FileLoggerType.MQRS, FLApplication.mFileTimeStamp);
//                        }
//                    }
//                    else {
//                        SignalProcUtils.qrsMaternalLocation.add(SignalProcUtils.qrsmLocTemp.get(i));
//                        SignalProcUtils.hrMaternal.add(SignalProcUtils.hrmTemp.get(i));
//
////                        FileLoggerHelper.getInstance().sendLogData(String.valueOf(SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.qrsMaternalLocation.size() - 1)) + "," + String.valueOf(SignalProcUtils.hrMaternal.get(SignalProcUtils.hrMaternal.size() - 1)), FileLoggerType.MQRS, FLApplication.mFileTimeStamp);
//                    }
//                }


                SignalProcUtils.lastMaternalPlotIndex = SignalProcUtils.qrsMaternalLocation.size();

                aHRmPrint = mMatrixFunctions.convertHR2MilliSec(aFinalQrsmHrPlot, aFinalHRM, aFinalQRSM);
                SignalProcUtils.lastQRSMIteration = iCurrentIteration;
                SignalProcUtils.mhrComputed = true;

//            } else {
//                SignalProcUtils.mhrComputed = false;
//                SignalProcUtils.lastQRSFetal = 0;
//                SignalProcUtils.lastRRMeanFetal = 0;
//
//                SignalProcUtils.lastQRSMaternal = 0;
//                SignalProcUtils.lastRRMeanMaternal = 0;
//            }
        } else {
            SignalProcUtils.mhrComputed = false;

            SignalProcUtils.lastQRSFetal = 0;
            SignalProcUtils.lastRRMeanFetal = 0;

            SignalProcUtils.lastQRSMaternal = 0;
            SignalProcUtils.lastRRMeanMaternal = 0;
        }
        Filename.ExecutionLogs.append(SignalProcUtils.mhrComputed+",");

        if (SignalProcUtils.mhrComputed){

            /**
             * MQRS cancellation
             */


            MQRSCancelSerial aCancelParallel = new MQRSCancelSerial();
            double[][] aFetalSig = aCancelParallel.cancel(aEcgFilter, aQRSM);
//            Timber.i("AlgorithmMain : Time for MQRS Cancellation : "+(System.currentTimeMillis()-aET)+" ms");
//            FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : Algorithm Main : Time for mQRS Cancel : "+(System.currentTimeMillis()-aET)+" msec.", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
            aET = System.currentTimeMillis();
            /**
             * ICA 2
             */


            double[][] aEcgIca2 = aJade.jade(aFetalSig);

//            Timber.i("AlgorithmMain : Time for ICA2 : "+(System.currentTimeMillis()-aET)+" ms");
//            FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : Algorithm Main : Time for ICA2 : "+(System.currentTimeMillis()-aET)+" msec.", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
            aET = System.currentTimeMillis();

//            ApplicationUtils.getTaskList().add(TaskEnum.FQRS_DETECTION.getTaskName() + " started at : " + ApplicationUtils.getCurrentTime());
            /**
             * FQRS detection
             */


            FQRSDetection aFqrsDetection = new FQRSDetection();
            Object[] aQrsfSelected = aFqrsDetection.fQRS(aEcgIca2, aQRSM, SignalProcUtils.interpolatedLengthFetal,
                    SignalProcUtils.lastQRSFetal, SignalProcUtils.lastRRMeanFetal, SignalProcUtils.noDetectionFlagFetal);
            aQRSF = (int[]) aQrsfSelected[0];

            // Added by Aravind Prasad 9th March 2018
//            Object[] aStdNoise = mMatrixFunctions.CDQC_fetal(aFetalSig,aQrsfSelected);



//            Timber.i("AlgorithmMain : Time for FQRS detection : "+(System.currentTimeMillis()-aET)+" ms");
//            FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : Algorithm Main : Time for fQRS : "+(System.currentTimeMillis()-aET)+" msec.", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
            aET = System.currentTimeMillis();

            SignalProcUtils.interpolatedLengthFetal = (int) aQrsfSelected[1];
            SignalProcUtils.noDetectionFlagFetal = (int) aQrsfSelected[2];

            Filename.ExecutionLogs.append(aQRSF.length+",");

            if (aQRSF.length >= SignalProcConstants.FQRS_MIN_SIZE && aQRSF.length <= SignalProcConstants.FQRS_MAX_SIZE) {


                int i = aQRSF.length - 1;
                while (aQRSF[i] > SignalProcConstants.QRS_END_VALUE) {
                    i = i - 1;
                }
                SignalProcUtils.lastQRSFetal = aQRSF[i] - SignalProcConstants.QRS_SHIFT;
                SignalProcUtils.lastRRMeanFetal = (aQRSF[i] - aQRSF[(int) (i - SignalProcConstants.QRS_NO_RR_MEAN)])
                        / SignalProcConstants.QRS_NO_RR_MEAN;

                /**
                 * Fetal HR calculating
                 */
//                int aNoQrsRemoved = 0;
//                for (int j = 0; j < aQRSF.length; j++) {
//                    if (aQRSF[j] >= SignalProcConstants.QRS_START_VALUE ) { // (&& aQRSF[j] < SignalProcConstants.QRS_END_VALUE)
//                        aQrsF.add(aQRSF[j] + SignalProcConstants.QRS_SHIFT * SignalProcUtils.currentIteration + SignalProcUtils.dataLossCounter);
//                    }
//                    else {
//                        aNoQrsRemoved++;
//                    }
//                }
                Filename.QRSF_Selected.append("\n");
                for (int j = 0; j < aQRSF.length; j++) {
                        Filename.QRSF_Selected.append( (aQRSF[j] + SignalProcUtils.qrsCurrentShift)+",");
                }
                Filename.QRSF_Selected.append("\n");

                HeartRateFetal aFHR = new HeartRateFetal();
                aFHR.heartRate(aQRSF);
                int aSizeFetalHR = SignalProcUtils.qrsfLocTemp.size();
                Filename.ExecutionLogs.append(aSizeFetalHR+",");

//                if (SignalProcUtils.fetalHrNew == 1){
//                    aSizeFetalHR = aSizeFetalHR + 4;
//                }

//                FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : Length of FQRS selected, length of FHR detected : %d, %d", aQRSF.length, aSizeFetalHR), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

//                if ( aSizeFetalHR > SignalProcConstants.FQRS_MIN_SIZE - 4) {

                    if ( SignalProcUtils.qrsfLocTemp.getFirst() > SignalProcConstants.QRS_START_VALUE) {
                        aFinalQRSF = new int[aSizeFetalHR+1];
                        aFinalHRF = new float[aSizeFetalHR+1];

                        aFinalHRF[0] = 0;
                        aFinalQRSF[0] = SignalProcConstants.QRS_START_VALUE + SignalProcUtils.qrsCurrentShift;
                        for (i = 0; i < aSizeFetalHR; i++) {
                            aFinalHRF[i+1] = SignalProcUtils.hrfTemp.get(i);
                            aFinalQRSF[i+1] = SignalProcUtils.qrsfLocTemp.get(i) + SignalProcUtils.qrsCurrentShift;
                            if (SignalProcUtils.qrsfLocTemp.get(i) < SignalProcConstants.QRS_END_VALUE && SignalProcUtils.qrsfLocTemp.get(i) >= SignalProcConstants.QRS_START_VALUE){
                                SignalProcUtils.qrsFetalLocation.add(SignalProcUtils.qrsfLocTemp.get(i) + SignalProcUtils.qrsCurrentShift);
                                SignalProcUtils.hrFetal.add(SignalProcUtils.hrfTemp.get(i));
                                SignalProcUtils.lastQRSFetal = SignalProcUtils.qrsfLocTemp.get(i) - SignalProcConstants.QRS_SHIFT;
                            }
                        }
                    }
                    else {
                        aFinalQRSF = new int[aSizeFetalHR];
                        aFinalHRF = new float[aSizeFetalHR];
                        for (i = 0; i < aSizeFetalHR; i++) {
                            aFinalHRF[i] = SignalProcUtils.hrfTemp.get(i);
                            aFinalQRSF[i] = SignalProcUtils.qrsfLocTemp.get(i) + SignalProcUtils.qrsCurrentShift;
                            if (SignalProcUtils.qrsfLocTemp.get(i) < SignalProcConstants.QRS_END_VALUE && SignalProcUtils.qrsfLocTemp.get(i) >= SignalProcConstants.QRS_START_VALUE){
                                SignalProcUtils.qrsFetalLocation.add(SignalProcUtils.qrsfLocTemp.get(i) + SignalProcUtils.qrsCurrentShift);
                                SignalProcUtils.hrFetal.add(SignalProcUtils.hrfTemp.get(i));
                                SignalProcUtils.lastQRSFetal = SignalProcUtils.qrsfLocTemp.get(i) - SignalProcConstants.QRS_SHIFT;
                            }
                        }


                    }

//                    for (i = 0; i<SignalProcUtils.qrsfLocTemp.size() ; i++){
//                        aFinalHRF[i+1] = SignalProcUtils.hrfTemp.get(i);
//                        aFinalQRSF[i+1] = SignalProcUtils.qrsfLocTemp.get(i);
//                        if ( i == SignalProcUtils.qrsfLocTemp.size() -1){
//                            if (SignalProcUtils.fetalHrEnd == 0){
//                                SignalProcUtils.qrsFetalLocation.add(SignalProcUtils.qrsfLocTemp.get(i));
//                                SignalProcUtils.hrFetal.add(SignalProcUtils.hrfTemp.get(i));
//
////                                FileLoggerHelper.getInstance().sendLogData(String.valueOf(SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.qrsFetalLocation.size() - 1)) + "," + String.valueOf(SignalProcUtils.hrFetal.get(SignalProcUtils.hrFetal.size() - 1)), FileLoggerType.FQRS, FLApplication.mFileTimeStamp);
//                            }
//                        }
//                        else {
//                            SignalProcUtils.qrsFetalLocation.add(SignalProcUtils.qrsfLocTemp.get(i));
//                            SignalProcUtils.hrFetal.add(SignalProcUtils.hrfTemp.get(i));
//
////                            FileLoggerHelper.getInstance().sendLogData(String.valueOf(SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.qrsFetalLocation.size() - 1)) + "," + String.valueOf(SignalProcUtils.hrFetal.get(SignalProcUtils.hrFetal.size() - 1)), FileLoggerType.FQRS, FLApplication.mFileTimeStamp);
//                        }
//                    }


                    SignalProcUtils.lastFetalPlotIndex = SignalProcUtils.qrsFetalLocation.size();

                    aHRfPrint = mMatrixFunctions.convertHR2MilliSec(aFinalQrsfHrPlot, aFinalHRF, aFinalQRSF);
                    SignalProcUtils.lastQRSFIteration = iCurrentIteration;
//                }
//                else {
//
//                    SignalProcUtils.lastQRSFetal = 0;
//                    SignalProcUtils.lastRRMeanFetal = 0;
////                    if (iCurrentIteration - SignalProcUtils.lastQRSFIteration >= 3) {
//////    						throw new Exception("No Fetal Heart Rate Detected");
////                    }
//                }
            } else {
                Filename.ExecutionLogs.append(0+",");

                SignalProcUtils.lastQRSFetal = 0;
                SignalProcUtils.lastRRMeanFetal = 0;
//                if (iCurrentIteration - SignalProcUtils.lastQRSFIteration >= 3) {
////    					throw new Exception("No Fetal Heart Rate Detected");
//                }
            }

        }
        String aHrPrint = "";

        for (int i = 0; i < SignalProcConstants.NO_OF_PRINT_VALUES; i++) {
            aHrPrint = aHrPrint + aHRfPrint[i];
            aHrPrint = aHrPrint + aHRmPrint[i];
        }

        int[] aLocation = new int[SignalProcConstants.NO_OF_PRINT_VALUES];

        for (int i = 0; i < SignalProcConstants.NO_OF_PRINT_VALUES; i++) {
            aLocation[i] = 2000 + SignalProcConstants.DIFFERENCE_SAMPLES * i + SignalProcConstants.QRS_SHIFT * SignalProcUtils.currentIteration + SignalProcUtils.dataLossCounter;
            Filename.FHR.append(aLocation[i]+",");
            Filename.FHR.append(aFinalQrsfHrPlot[i]+"\n");

        }

        System.out.println("AlgorithmMain : Time for Algorithm : " + (System.currentTimeMillis() - aST) + " ms");
//        Timber.i("AlgorithmMain : Time for Algorithm : "+(System.currentTimeMillis()-aST)+" ms");
        Filename.ExecutionLogs.append(SignalProcUtils.lastRRMeanFetal+"\n");

        Filename.RRMeanFetal.append(SignalProcUtils.lastRRMeanFetal+",\n");
        return new Object[]{aLocation, aFinalQrsmHrPlot, aFinalQrsfHrPlot};
    }

}
