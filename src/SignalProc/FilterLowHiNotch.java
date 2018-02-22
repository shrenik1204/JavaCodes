package SignalProc;

import helper.ExecutorServiceHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

//import com.sattvamedtech.fetallite.utils.ApplicationUtils;

/**
 * <p>Performs Low-pass, High-pass and Notch Filtering.</p>
 * <p> Change Logs :</p>
 * <ul>
 *     <li> 24th May, 2017
 *         <ol>
 *             <li> First commit.</li>
 *         </ol>
 *     </li>
 * </ul>
 *
 * @author Kishore Subramanian (kishore@sattvamedtech.com)
 *
 */

public class FilterLowHiNotch {

    /**
     * Object initialization of  {@link MatrixFunctions}
     */
    MatrixFunctions mMatrixFunctions = new MatrixFunctions();

    /**
     * @param iInput
     *            : (double[][]) Input data (length greater than FILTERNFACT3)
     * @return : (double[][]) Filtered data
     * @exception :
     *                Throws appropriate exception if the input arguments do not
     *                meet the requirements
     */

    /**
     * <p> Performs filtering of each channel by parallelizing into 4 threads.</p>
     * @param iInput {@literal 15000 x 4} array.
     * @return {@literal 15000 x 4} filtered output.
     * @throws Exception Message containing the exception.
     */
    public double[][] filterParallel(double[][] iInput) throws Exception {
        int aLength = iInput.length;
        SignalProcUtils.ma_amplitudeFlag = new double[9][5];
        SignalProcUtils.ma_psdFlag = new double[9][5];

        if (iInput.length > 0) {
            if (iInput[0].length == SignalProcConstants.NO_OF_CHANNELS) {
                double[][] aFinalInput = iInput;

                double[][] aFinalOutput = new double[aLength][SignalProcConstants.NO_OF_CHANNELS];

                ExecutorService executorService = ExecutorServiceHelper.getInstance().getExecutorService();

                try {
                    Future<Boolean> channelOneTask = executorService.submit(() -> {
                        Thread.currentThread().setName(SignalProcUtils.currentIteration + " 1HLN");

                        double[] aFinalChannel = new double[aLength];

                        try {

                            for (int i = 0; i < aLength; i++) {
                                aFinalChannel[i] = aFinalInput[i][0];
                            }

                            filterLowHiNotch(aFinalChannel);
                            mMatrixFunctions.findAmplitudeMA(aFinalChannel, SignalProcUtils.ma_amplitude1, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e.getMessage());
                        }

                        for (int i = 0; i < aLength; i++) {
                            aFinalOutput[i][0] = aFinalChannel[i];
                        }
                    }, true);

                    Future<Boolean> channelTwoTask = executorService.submit(() -> {
                        Thread.currentThread().setName(SignalProcUtils.currentIteration + " 2HLN");

                        double[] aFinalChannel = new double[aLength];

                        try {
                            for (int i = 0; i < aLength; i++) {
                                aFinalChannel[i] = aFinalInput[i][1];
                            }

                            filterLowHiNotch(aFinalChannel);
                            mMatrixFunctions.findAmplitudeMA(aFinalChannel, SignalProcUtils.ma_amplitude2, 1);

                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e.getMessage());
                        }

                        for (int i = 0; i < aLength; i++) {
                            aFinalOutput[i][1] = aFinalChannel[i];
                        }
                    }, true);

                    Future<Boolean> channelThreeTask = executorService.submit(() -> {
                        Thread.currentThread().setName(SignalProcUtils.currentIteration + " 3HLN");

                        double[] aFinalChannel = new double[aLength];

                        try {
                            for (int i = 0; i < aLength; i++) {
                                aFinalChannel[i] = aFinalInput[i][2];
                            }

                            filterLowHiNotch(aFinalChannel);
                            mMatrixFunctions.findAmplitudeMA(aFinalChannel, SignalProcUtils.ma_amplitude3, 2);

                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e.getMessage());
                        }

                        for (int i = 0; i < aLength; i++) {
                            aFinalOutput[i][2] = aFinalChannel[i];
                        }
                    }, true);

                    Future<Boolean> channelFourTask = executorService.submit(() -> {
                        Thread.currentThread().setName(SignalProcUtils.currentIteration + " 4HLN");

                        double[] aFinalChannel = new double[aLength];

                        try {
                            for (int i = 0; i < aLength; i++) {
                                aFinalChannel[i] = aFinalInput[i][3];
                            }

                            filterLowHiNotch(aFinalChannel);
                            mMatrixFunctions.findAmplitudeMA(aFinalChannel, SignalProcUtils.ma_amplitude4, 3);

                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e.getMessage());
                        }

                        for (int i = 0; i < aLength; i++) {
                            aFinalOutput[i][3] = aFinalChannel[i];
                        }
                    }, true);

                    if (channelOneTask.get() && channelTwoTask.get() && channelThreeTask.get() && channelFourTask.get()) {
                        // fucntion : throw exception

                        int[] aOverlap = new int[10];
                        double aEndLocation = 0;
                        aEndLocation = mMatrixFunctions.checkMA(aOverlap,0, aEndLocation);
                        aEndLocation = mMatrixFunctions.checkMA(aOverlap,1, aEndLocation);
                        aEndLocation = mMatrixFunctions.checkMA(aOverlap,2, aEndLocation);
                        aEndLocation = mMatrixFunctions.checkMA(aOverlap,3, aEndLocation);

                        aEndLocation = mMatrixFunctions.checkOverlapMA(aOverlap, aEndLocation);
                        System.out.println("Error Location in Iteration : "+SignalProcUtils.currentIteration+" is : "+aEndLocation);
                        aEndLocation = 0;
                        return aFinalOutput;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            } else {
                throw new Exception("Input column size must be 4 : filterParallel");
            }
        } else {
            throw new Exception("Input length must be 15000 : filterParallel");
        }

        return new double[][]{};
    }

    /**
     * filterLowHiNotch : Filtering of each channel. This method is
     * executed parallel for each channel
     *
     * @param iChannel
     *            : (double[]) Input data (length greater than FILTERNFACT3)
     * @throws Exception
     *             : Throws appropriate exception if the input arguments do not
     *             meet the requirements
     */
    /**
     * <p> Filtering of each channel. This method is executed in parallel for each channel.</p>
     * @param iChannel {@literal 15000 x 1} array.
     * @throws Exception Message containing the exception.
     */
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