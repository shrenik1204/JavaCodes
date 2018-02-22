package SignalProc;

public class FilterSerial {
    MatrixFunctions mMatrixFunctions = new MatrixFunctions();

    public double[][] filterParallel(double[][] iInput) throws Exception {

        int aLength = iInput.length;
        SignalProcUtils.ma_amplitudeFlag = new double[9][5];
        SignalProcUtils.ma_psdFlag = new double[9][5];

        if (iInput.length > 0) {
            double[][] aFinalInput = iInput;

            double[][] aFinalOutput = new double[aLength][SignalProcConstants.NO_OF_CHANNELS];

            if (iInput[0].length == SignalProcConstants.NO_OF_CHANNELS) {

                double[] aFinalChannel = new double[aLength];
                for (int i = 0; i < aLength; i++) {
                    aFinalChannel[i] = iInput[i][0];
                }

                filterLowHiNotch(aFinalChannel);
                mMatrixFunctions.findAmplitudeMA(aFinalChannel, SignalProcUtils.ma_amplitude1, 0);
                for (int i = 0; i < aLength; i++) {
                    aFinalOutput[i][0] = aFinalChannel[i];
                }

                for (int i = 0; i < aLength; i++) {
                    aFinalChannel[i] = iInput[i][1];
                }

                filterLowHiNotch(aFinalChannel);
                mMatrixFunctions.findAmplitudeMA(aFinalChannel, SignalProcUtils.ma_amplitude2, 1);

                for (int i = 0; i < aLength; i++) {
                    aFinalOutput[i][1] = aFinalChannel[i];
                }

                for (int i = 0; i < aLength; i++) {
                    aFinalChannel[i] = aFinalInput[i][2];
                }

                filterLowHiNotch(aFinalChannel);
                mMatrixFunctions.findAmplitudeMA(aFinalChannel, SignalProcUtils.ma_amplitude3, 2);
                for (int i = 0; i < aLength; i++) {
                    aFinalOutput[i][2] = aFinalChannel[i];
                }

                for (int i = 0; i < aLength; i++) {
                    aFinalChannel[i] = aFinalInput[i][3];
                }

                filterLowHiNotch(aFinalChannel);
                mMatrixFunctions.findAmplitudeMA(aFinalChannel, SignalProcUtils.ma_amplitude4, 3);
                for (int i = 0; i < aLength; i++) {
                    aFinalOutput[i][3] = aFinalChannel[i];
                }

                int[] aOverlap = new int[10];
                double aEndLocation = 0;
                aEndLocation = mMatrixFunctions.checkMA(aOverlap,0, aEndLocation);
                aEndLocation = mMatrixFunctions.checkMA(aOverlap,1, aEndLocation);
                aEndLocation = mMatrixFunctions.checkMA(aOverlap,2, aEndLocation);
                aEndLocation = mMatrixFunctions.checkMA(aOverlap,3, aEndLocation);

                aEndLocation = mMatrixFunctions.checkOverlapMA(aOverlap, aEndLocation);

                if (aEndLocation > 0){
                    SignalProcUtils.MA_FLAG = true;
                    SignalProcUtils.MA_Shift = (int) aEndLocation;
                }
                else {
                    SignalProcUtils.MA_FLAG = false;
                    SignalProcUtils.MA_Shift = (int) aEndLocation;
                }
                System.out.println("Error Location in Iteration : "+SignalProcUtils.currentIteration+" is : "+aEndLocation);
                return aFinalOutput;

            }
            else {
                throw new Exception("Input column size must be 4 : filterParallel");
            }
        } else {
            throw new Exception("Input length must be 15000 : filterParallel");
        }



    }

    public void filterLowHiNotch(double[] iChannel) throws Exception {

//        mMatrixFunctions.filtfilt_Sos(iChannel, SignalProcConstants.FILTER_HIGH_SOS, SignalProcConstants.FILTER_HIGH_GAIN, SignalProcConstants.FILTER_HIGH_Z, SignalProcConstants.FILTER_HIGH_ORDER);
//        mMatrixFunctions.filtfilt_Sos(iChannel, SignalProcConstants.FILTER_LOW_SOS, SignalProcConstants.FILTER_LOW_GAIN, SignalProcConstants.FILTER_LOW_Z, SignalProcConstants.FILTER_LOW_ORDER);
        mMatrixFunctions.filtfilt(iChannel,SignalProcConstants.FILTER_AHIGH, SignalProcConstants.FILTER_BHIGH,SignalProcConstants.FILTER_ZHIGH);
        mMatrixFunctions.filtfilt(iChannel,SignalProcConstants.FILTER_ALOW, SignalProcConstants.FILTER_BLOW,SignalProcConstants.FILTER_ZLOW);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_50, SignalProcConstants.FILTER_BNOTCH_50, SignalProcConstants.FILTER_ZNOTCH_50);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_100, SignalProcConstants.FILTER_BNOTCH_100, SignalProcConstants.FILTER_ZNOTCH_100);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_150, SignalProcConstants.FILTER_BNOTCH_150, SignalProcConstants.FILTER_ZNOTCH_150);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_200, SignalProcConstants.FILTER_BNOTCH_200, SignalProcConstants.FILTER_ZNOTCH_200);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_250, SignalProcConstants.FILTER_BNOTCH_250, SignalProcConstants.FILTER_ZNOTCH_250);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_3125, SignalProcConstants.FILTER_BNOTCH_3125, SignalProcConstants.FILTER_ZNOTCH_3125);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_9375, SignalProcConstants.FILTER_BNOTCH_9375, SignalProcConstants.FILTER_ZNOTCH_9375);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_15625, SignalProcConstants.FILTER_BNOTCH_15625, SignalProcConstants.FILTER_ZNOTCH_15625);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_21875, SignalProcConstants.FILTER_BNOTCH_21875, SignalProcConstants.FILTER_ZNOTCH_21875);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_28125, SignalProcConstants.FILTER_BNOTCH_28125, SignalProcConstants.FILTER_ZNOTCH_28125);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_34375, SignalProcConstants.FILTER_BNOTCH_34375, SignalProcConstants.FILTER_ZNOTCH_34375);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_40625, SignalProcConstants.FILTER_BNOTCH_40625, SignalProcConstants.FILTER_ZNOTCH_40625);
        mMatrixFunctions.filtfilt(iChannel, SignalProcConstants.FILTER_ANOTCH_46875, SignalProcConstants.FILTER_BNOTCH_46875, SignalProcConstants.FILTER_ZNOTCH_46875);
//		} else {
//			throw new Exception("Input must be of size greater than 6 : filterLowHiNotchParallel");
//		}
    }
}