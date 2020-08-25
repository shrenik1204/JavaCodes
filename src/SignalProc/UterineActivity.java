package SignalProc;

//import SignalProc.MatrixFunctions;
//import SignalProc.SignalProcConstants;
//import com.sattvamedtech.fetallite.FLApplication;
//import com.sattvamedtech.fetallite.foundation.enums.FileLoggerType;
//import com.sattvamedtech.fetallite.foundation.helper.FileLoggerHelper;
//import com.sattvamedtech.fetallite.foundation.utils.ApplicationUtils;

import java.util.ArrayList;
import java.util.List;

public class UterineActivity {

    /**
     * Object initialization of  {@link MatrixFunctions}
     */

    /**
     * <p> Determine UA using DWT.</p>
     * @param iInput Channel 4 ECG data.
     * @return UA values for every 500 ms.
     * @throws Exception Message containing the exception.
     */
    public static List<Double> uaAlgoDwt(double[] iInput) throws Exception {

        MatrixFunctions mMatrixFunctions = new MatrixFunctions();
        List<Double> aUA_Energy = new ArrayList<>(SignalProcConstants.NO_OF_PRINT_VALUES);
        List<Double> aUA_Energy_plotdata = new ArrayList<>();

        for (int i = 0; i < SignalProcConstants.NO_OF_PRINT_VALUES; i++) {
            aUA_Energy.add(1.0);
        }
        double[] aInput = new double[SignalProcConstants.NO_OF_SAMPLES];
        for (int i = 0; i < SignalProcConstants.NO_OF_SAMPLES; i++) {
            aInput[i] = iInput[i];
        }
        /**
         * Input is 15000 array
         */

        // Re-scaling of Input to 12-bit
        for (int i = 0; i< SignalProcConstants.NO_OF_SAMPLES; i++){
            aInput[i] = (aInput[i] * 24 * (Math.pow(2,23)-1) )/ (Math.pow(2,12) * 4.5);
        }

        int aLengthInput = aInput.length;

        // Filtering between 0.3 - 3 Hz
        mMatrixFunctions.filtfilt_Sos(aInput, SignalProcConstants.UA_HIGH_SOS, SignalProcConstants.UA_HIGH_GAIN,
                SignalProcConstants.UA_HIGH_Z, SignalProcConstants.UA_HIGH_ORDER);

        mMatrixFunctions.filtfilt_Sos(aInput, SignalProcConstants.UA_LOW_SOS, SignalProcConstants.UA_LOW_GAIN,
                SignalProcConstants.UA_LOW_Z, SignalProcConstants.UA_LOW_ORDER);

        // Decimation
        int aDecimateFactor = 100;

        int aDecimatedLength = aLengthInput/aDecimateFactor;

        double[] aDecimatedInput = new double[aDecimatedLength];

        for (int i =0; i<aDecimatedLength; i++) {
            //Change Aravind
            aDecimatedInput[i] = Math.abs(aInput[i*aDecimateFactor]);
        }

        //  StepSize and Shift
        int aStepSize = 5000/aDecimateFactor; // 5 sec

        int aShift = 500/aDecimateFactor;    // 500 milli sec

        double[] aSignalExtract = new double[aStepSize];
        double[] aPsdSignalExtract = new double[2048];
        double aTotalEnergy = 0;
        double aMidEnergy = 0;
        double aMedianEnergy = 0;
        int aFindMedian = 0;
        int k = 0;

        double sumE =0.0;
        if(SignalProcUtils.iter == 6 || SignalProcUtils.UArecheck){
            if(SignalProcUtils.UArecheck){
//                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : UA Algo : UA recheck initiated", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
            }
            SignalProcUtils.UArecheck = true;
            for (int z = 0; z< SignalProcUtils.aUA_Energy_iter.size(); z++) {
                sumE = sumE + SignalProcUtils.aUA_Energy_iter.get(z);
            }

            SignalProcUtils.uaAvgEnergy = sumE/SignalProcUtils.aUA_Energy_iter.size();

            if(SignalProcUtils.uaAvgEnergy > 1.0 && SignalProcUtils.uaAvgEnergy <= 10.0){
//                if(SignalProcUtils.UArecheck){
//                    SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy)))) + 2;
//                }
//                else{
                    SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy)))) + 1;
//                }
            }
            else if(SignalProcUtils.uaAvgEnergy < 0.10){
//                if(SignalProcUtils.UArecheck){
//                    SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy)))) + 2;
//                }
//                else{
                    SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy))));
//                }
            }
            else if(SignalProcUtils.uaAvgEnergy > 10.0 && SignalProcUtils.uaAvgEnergy <= 100.0){
//                if(SignalProcUtils.UArecheck){
//                    SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy)))) + 1;
//                }
//                else{
                    SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy)))) - 1;
//                }
            }
            else if(SignalProcUtils.uaAvgEnergy > 100.0 && SignalProcUtils.uaAvgEnergy <= 1000.0) {
//                if(SignalProcUtils.UArecheck){
//                    SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy)))) - 1;
//                }
//                else{
                    SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy)))) - 2;
//                }
            }
            else {
                SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy)))) + 1;
            }

//            FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : UA Algo : Average Energy =" + SignalProcUtils.uaAvgEnergy, FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//            FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : UA Algo : UA Scale =" + SignalProcUtils.uaScale, FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
        }


        for (int i = 0; i< SignalProcConstants.NO_OF_PRINT_VALUES; i++ ) {
            for (int j = 0; j<aStepSize; j++) {
                aSignalExtract[j] = aDecimatedInput[j+aShift*i];
            }
            aSignalExtract=mMatrixFunctions.stftwindow(aSignalExtract);
            aPsdSignalExtract = mMatrixFunctions.fastfouriertransform_UA(aSignalExtract);
            aTotalEnergy = 0;
            // Energy from 0.1 - 5 Hz
            for (int j = 1045; j<2048; j++) {
                aTotalEnergy = aTotalEnergy + aPsdSignalExtract[j];
            }
            aFindMedian = 0;
            aMidEnergy = aTotalEnergy/2;

            k = 1046;

            while (aFindMedian == 0 && k < 2048) {
                aMedianEnergy = 0;
                for (int j = 1045; j<=k; j++) {
                    aMedianEnergy = aMedianEnergy + aPsdSignalExtract[j];
                }
                if (aMedianEnergy > aMidEnergy) {
                    aFindMedian = 1;
                    k = k-1;
                }
                else {
                    k = k+1;
                }
            }

            if ( (-1/2 + k/2048)*10 < 0.8 ) {
                aMedianEnergy = 0;
                // energy from 0.4 - 1.5 Hz
                for (int j = 1105; j<=1331; j++) {
                    aMedianEnergy = aMedianEnergy + aPsdSignalExtract[j];
                }
                aMedianEnergy = aMedianEnergy / (227);// / aScale;
            }
            else {
                aMedianEnergy = 0;
            }
            if (SignalProcUtils.uaCounter < SignalProcConstants.UA_WINDOW) {
                SignalProcUtils.uaEnergyTemp.add(aMedianEnergy);
                SignalProcUtils.uaCounter++;
                double sum = 0;
                for (int z = 0; z< SignalProcUtils.uaCounter; z++) {
                    sum = sum + SignalProcUtils.uaEnergyTemp.get(z);
                }
                aUA_Energy.set(i,((sum / SignalProcUtils.uaCounter) / 3));//* Math.pow(10, SignalProcUtils.uaScale)));
//                aUA_Energy_plotdata.add(aUA_Energy.get(i));
            }
            else {
                SignalProcUtils.uaEnergyTemp.removeFirst();
                SignalProcUtils.uaEnergyTemp.add(aMedianEnergy);
                SignalProcUtils.uaCounter++;
                double sum = 0;
                for (int z = 0; z< SignalProcConstants.UA_WINDOW; z++) {
                    sum = sum + SignalProcUtils.uaEnergyTemp.get(z);
                }
                aUA_Energy.set(i,((sum / SignalProcConstants.UA_WINDOW) / 3));//* Math.pow(10, SignalProcUtils.uaScale));
//                aUA_Energy_plotdata.add(aUA_Energy.get(i));
            }
            SignalProcUtils.aUA_Energy_iter.add(aMedianEnergy);
            SignalProcUtils.aUA_Energy_iter_test.add((aUA_Energy.get(i)));
        }

        System.out.println("UA array scale for iteration "+SignalProcUtils.currentIteration+ " is "+SignalProcUtils.uaScale);
        for (int i = 0; i < SignalProcUtils.aUA_Energy_iter_test.size() ; i++) {
            aUA_Energy_plotdata.add(SignalProcUtils.aUA_Energy_iter_test.get(i)* Math.pow(10,SignalProcUtils.uaScale));
        }
//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : UA Algo : UA List size " + aUA_Energy_plotdata.size() + " Iteration :" + SignalProcUtils.iter, FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
        SignalProcUtils.iter++;
        if (SignalProcUtils.UArecheck) {
//            SignalProcUtils.UArecheck = false;
            SignalProcUtils.UArecheck_global = false;
        }

        List<Double> new1aUA_Energy_plotdatat=slopeThresolding(aUA_Energy_plotdata);
        return new1aUA_Energy_plotdatat;
    }

    /**
     * Slope thresholding
     * @param aUA_Energy_plotdata
     * @return
     */
    public static List<Double> slopeThresolding(List<Double> aUA_Energy_plotdata){
        double diff;
        List<Double> new1aUA_Energy_plotdata = new ArrayList<Double>();
        new1aUA_Energy_plotdata.add(aUA_Energy_plotdata.get(0));
        for (int i=1; i<aUA_Energy_plotdata.size(); i++){
             diff=(aUA_Energy_plotdata.get(i)-aUA_Energy_plotdata.get(i-1));
             if(Math.abs(diff)>SignalProcConstants.UA_PLOT_DIFF_THRESHOLD & SignalProcUtils.currentIteration>1){
                 diff=0;
             }
            new1aUA_Energy_plotdata.add(new1aUA_Energy_plotdata.get(i-1)+diff);
             if (new1aUA_Energy_plotdata.get(i)<0){
                 new1aUA_Energy_plotdata.set(i,0.0);
             }
        }
        return new1aUA_Energy_plotdata;
    }
}
