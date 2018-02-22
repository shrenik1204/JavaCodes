package Wrapper;

import SignalProc.SignalProcConstants;
import SignalProc.SignalProcUtils;


public class Find_impedence {
    private static boolean mLeadOff = true;


    private static double[] mLeadResistance = new double[SignalProcConstants.NO_OF_CHANNELS];
    private static double[][] mDoubleArrayBuffer = new double[5000][4];

    public static void main (String[] args){



        DataTextFileReader aReadFile = new DataTextFileReader();
        String aInputFilePath = ""+"/Users/kishoresubramanian/Downloads/input-normal.txt";

        CubicInterpolate15 aCubic = new CubicInterpolate15();
		double[][] aInput = aCubic.convert(aInputFilePath);

        for (int i = 9000; i < 9000 + 5000; i++) {
            for (int j = 0; j < 4; j++) {
                mDoubleArrayBuffer[i-9000][j] = aInput[i][j];
            }
        }



        findLeadResistance(mLeadOff, 1000);
//        findLeadResistance(mLeadOff2, 1);
//        findLeadResistance(mLeadOff3, 2);
//        findLeadResistance(mLeadOff4, 3);

//        Timber.i("Channel 1 Resistance : "+ mChannelResistance[0]);
//        Timber.i("Channel 2 Resistance : "+ mChannelResistance[1]);
//        Timber.i("Channel 3 Resistance : "+ mChannelResistance[2]);
//        Timber.i("Channel 4 Resistance : "+ mChannelResistance[3]);

//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Channel 1 Resistance : " + mChannelResistance[0], FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Channel 2 Resistance : " + mChannelResistance[1], FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Channel 3 Resistance : " + mChannelResistance[2], FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Channel 4 Resistance : " + mChannelResistance[3], FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

        int j = 0;
        j++;



    }

    private static void findLeadResistance(boolean iFlag, int iStartLoc) {
        if (iFlag){

            double[] aSumCosine = new double[SignalProcConstants.NO_OF_CHANNELS];
            double[] aSumSine = new double[SignalProcConstants.NO_OF_CHANNELS];

            for (int j = 0; j < SignalProcConstants.NO_OF_CHANNELS; j++) {
                for (int i = 0; i < SignalProcConstants.LEADOFF_DETECTION_LENGTH; i++) {
                    aSumCosine[j] += mDoubleArrayBuffer[iStartLoc + i][j] * SignalProcUtils.findCosine(i);
                    aSumSine[j] += mDoubleArrayBuffer[iStartLoc + i][j] * SignalProcUtils.findSine(i);
                }
                aSumCosine[j] = aSumCosine[j] * aSumCosine[j];
                aSumSine[j] = aSumSine[j] * aSumSine[j];
            }


            for (int i = 0; i < SignalProcConstants.NO_OF_CHANNELS; i++) {
                mLeadResistance[i] = ( 2 * (Math.sqrt(aSumCosine[i] + aSumSine[i]) * 2/ SignalProcConstants.LEADOFF_DETECTION_LENGTH) / SignalProcConstants.LEADOFF_CURRENT - 11000 )/5;
            }

        }
    }


}
