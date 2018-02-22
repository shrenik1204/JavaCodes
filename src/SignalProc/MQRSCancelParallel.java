package SignalProc;

//import com.sattvamedtech.fetallite.utils.ApplicationUtils;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Subtract MECG components from filtered AECG
 * <p> Change Logs :</p>
 * <ul>
 *     <li> 24th May, 2017
 *         <ol>
 *             <li> First commit.</li>
 *         </ol>
 *     </li>
 * </ul>
 * @author Kishore Subramanian (kishore@sattvamedtech.com)
 *
 */
public class MQRSCancelParallel {
    /**
     * Object initialization of {@link MatrixFunctions}
     */
    MatrixFunctions mMatrixFunctions = new MatrixFunctions();

    /**
     * Rank-3 approximation of MECG.
     */
    static double[][] mApproxSignal = null;
    /**
     * Length of maternal QRS complex.
     */
    static int mNoSamplesQRS;
    /**
     * Number of maternal QRS.
     */
    static int mNoQrs;

    /**
     * Perform PCA on each channel in separate threads.
     * @param iNoQrs Number of ECG signals.
     * @param iQrsIndexArrI Start index of each ECG wave.
     * @param iQrsIndexArrF End index of each ECG wave.
     * @param iInputPCA Signal extended around the boundary.
     * @param iNoSamplesQRS Length of each maternal ECG wave.
     * @param iApproxmSignal Store the rank-3 approximation of maternal ECG in each channel.
     * @return mApproxSignal.
     */
    private double[][] pcaParallized(int iNoQrs, int[] iQrsIndexArrI, int[] iQrsIndexArrF, int[] iQrsM, double[][] iInputPCA, int iNoSamplesQRS, double[][] iApproxmSignal) {

        mNoSamplesQRS = iNoSamplesQRS;
        mNoQrs = iNoQrs;
        final int[] aQrsM = iQrsM;

        final int[] aQrsIndexArrI = iQrsIndexArrI;
        final int[] aQrsIndexArrF = iQrsIndexArrF;

        final double[][] aInputPCA = iInputPCA;
        mApproxSignal = iApproxmSignal;

        try {
            ExecutorService executorService = Executors.newFixedThreadPool(SignalProcConstants.NO_OF_CHANNELS);

            Future<Boolean> channelOneTask = executorService.submit(() -> {
                Thread.currentThread().setName(SignalProcUtils.currentIteration + " 1MQRSCancel");

                doTask(aQrsIndexArrF, aQrsIndexArrI, aInputPCA, aQrsM, 0);
            }, true);

            Future<Boolean> channelTwoTask = executorService.submit(() -> {
                Thread.currentThread().setName(SignalProcUtils.currentIteration + " 2MQRSCancel");

                doTask(aQrsIndexArrF, aQrsIndexArrI, aInputPCA, aQrsM, 1);
            }, true);

            Future<Boolean> channelThreeTask = executorService.submit(() -> {
                Thread.currentThread().setName(SignalProcUtils.currentIteration + " 3MQRSCancel");

                doTask(aQrsIndexArrF, aQrsIndexArrI, aInputPCA, aQrsM, 2);
            }, true);

            Future<Boolean> channelFourTask = executorService.submit(() -> {
                Thread.currentThread().setName(SignalProcUtils.currentIteration + " 4MQRSCancel");

                doTask(aQrsIndexArrF, aQrsIndexArrI, aInputPCA, aQrsM, 3);
            }, true);

            if (channelOneTask.get() && channelTwoTask.get() && channelThreeTask.get() && channelFourTask.get()) {
                //Do Nothing
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mApproxSignal;
    }

    private void doTask(int[] aQrsIndexArrF, int[] aQrsIndexArrI, double[][] aInputPCA, int[] aQrsM, int channel) {
        try{
            double[][] aPcaExtract = new double[mNoSamplesQRS][mNoQrs];
            double[][] aRowExtract = new double[1][mNoSamplesQRS];
            double[][] aRowWeighted = new double[1][mNoSamplesQRS];
            double[][] aWeightWindowFunction;
            for (int i = 0; i < mNoQrs; i++) {
                int aQrsIndexI = aQrsIndexArrI[i];

                int aQrsIndexF = aQrsIndexArrF[i];

                aRowExtract = mMatrixFunctions.subMatrix(aInputPCA, aQrsIndexI, aQrsIndexF, channel, channel);

                if (i == 0){
                    aWeightWindowFunction = mMatrixFunctions.weightFunctionDynamic(0, aQrsM[i], aQrsM[i+1], i, mNoQrs, channel);
                } else if (i == mNoQrs-1){
                    aWeightWindowFunction = mMatrixFunctions.weightFunctionDynamic(aQrsM[i-1],aQrsM[i],0, i, mNoQrs, channel);
                } else {
                    aWeightWindowFunction = mMatrixFunctions.weightFunctionDynamic(aQrsM[i-1],aQrsM[i], aQrsM[i+1], i, mNoQrs, channel);
                }

                aRowWeighted = mMatrixFunctions.elementWiseMultiply(aRowExtract, aWeightWindowFunction);

                for (int j = 0; j < mNoSamplesQRS; j++) {
                    aPcaExtract[j][i] = aRowWeighted[j][0];
                }

            }

            PCARank3 aPCA = new PCARank3();

            double[][] aApproxSignal;

            aApproxSignal = aPCA.pca(aPcaExtract);

            // putting back the approximation into a single
            // channel
            double[][] aApproxSignalTemp = new double[mNoSamplesQRS][1];

            double[][] aApproxSignalTemp1 = new double[mNoSamplesQRS][1];

            for (int iq = 0; iq < mNoQrs; iq++) {
                int aIwq = aQrsIndexArrI[iq];
                int aFwq = aQrsIndexArrF[iq];

                aApproxSignalTemp = mMatrixFunctions.subMatrix(aApproxSignal, 0, mNoSamplesQRS - 1, iq, iq);

                if (iq == 0){
                    aWeightWindowFunction = mMatrixFunctions.weightFunctionDynamic(0, aQrsM[iq], aQrsM[iq+1], iq, mNoQrs, channel);
                } else if (iq == mNoQrs-1) {
                    aWeightWindowFunction = mMatrixFunctions.weightFunctionDynamic(aQrsM[iq-1],aQrsM[iq],0, iq, mNoQrs, channel);
                } else {
                    aWeightWindowFunction = mMatrixFunctions.weightFunctionDynamic(aQrsM[iq-1],aQrsM[iq], aQrsM[iq+1], iq, mNoQrs, channel);
                }

                aApproxSignalTemp1 = mMatrixFunctions.elementWiseDivide(aApproxSignalTemp, aWeightWindowFunction);

                for (int k = aIwq; k <= aFwq; k++) {
                    mApproxSignal[k][channel] = aApproxSignalTemp1[k - aIwq][0];
                }
            } // end approx single channel
        } catch (Exception e) {
            e.printStackTrace();
        }

        double aDifferenceValue = 0;
        double aPercentValue = 0;

        for (int iq = 1; iq < mNoQrs; iq++) {
            int aQrsIndexF = aQrsIndexArrF[iq - 1];
            int aQrsIndexI = aQrsIndexArrI[iq];
            if (aQrsIndexI > aQrsIndexF) {
                aDifferenceValue = mApproxSignal[aQrsIndexI][channel] - mApproxSignal[aQrsIndexF][channel];
                aPercentValue = aDifferenceValue / (aQrsIndexI - aQrsIndexF);

                for (int it = aQrsIndexF + 1; it < aQrsIndexI; it++) {
                    mApproxSignal[it][channel] = mApproxSignal[aQrsIndexF][channel] + aPercentValue * (it - aQrsIndexF);
                }
            }
        }
    }

    /**
     * <p> Performs a rank-3 PCA of maternal ECG for each channel of filtered signal by extracting same length maternal
     * ECG using the maternal QRS location obtained.</p>
     * @param iInput {@literal 15000 x 4} Filtered ECG.
     * @param iQRSm Maternal QRS locations.
     * @return Maternal ECG cancelled signal.
     * @throws Exception If {@literal iQRSm.length <= 5}.
     */
    public double[][] cancel(double[][] iInput, int[] iQRSm) throws Exception {
        // input = Nx4

        int aNoQRSm = iQRSm.length;
        if (aNoQRSm > 5 ) {
            if (iQRSm[0] > 0 && iInput.length > iQRSm[aNoQRSm-1] ) {

                double[] aRRms = new double[aNoQRSm - 1]; // RR in milli seconds
                for (int i = 1; i < aNoQRSm; i++) {
                    aRRms[i - 1] = (iQRSm[i] - iQRSm[i - 1]) / (double) SignalProcConstants.FS;
                }

                double[] aRRms_sort = aRRms.clone();
                Arrays.sort(aRRms_sort);
                double aRRmean = 0;
                for (int i = 1; i < aRRms_sort.length-1; i++) {
                    aRRmean += aRRms_sort[i];
                }
                aRRmean = aRRmean / (aRRms_sort.length - 2);
//                double aRRmean = mMatrixFunctions.findMeanBetweenDistributionTails(aRRms, SignalProcConstants.CANCEL_PERCI, SignalProcConstants.CANCEL_PERCF);

                /**
                 * Initialize the no of points before and after QRS
                 */
                int aNoSamplesBeforeQRS = (int) Math.floor(SignalProcConstants.CANCEL_QRS_BEFORE_PERC * SignalProcConstants.FS);



//                if (Math.ceil(SignalProcConstants.CANCEL_QRS_BEFORE_PERC * SignalProcConstants.FS) == SignalProcConstants.CANCEL_QRS_BEFORE_PERC
//                        * SignalProcConstants.FS) {
//                    aNoSamplesBeforeQRS = (int) Math.ceil(SignalProcConstants.CANCEL_QRS_BEFORE_PERC * SignalProcConstants.FS);
//                } else {
//                    aNoSamplesBeforeQRS = (int) Math.ceil(SignalProcConstants.CANCEL_QRS_BEFORE_PERC * SignalProcConstants.FS) - 1;
//                }

                double aNoSamplesTemp = SignalProcConstants.CANCEL_QRS_AFTER_PERC * (aRRmean - 0.1);

                if (aNoSamplesTemp > SignalProcConstants.CANCEL_QRS_AFTER_TH) {
                    aNoSamplesTemp = SignalProcConstants.CANCEL_QRS_AFTER_TH;
                }
                int aNoSamplesAfterQRS = (int) Math.floor(aNoSamplesTemp * SignalProcConstants.FS);
//                if (Math.ceil(aNoSamplesTemp * SignalProcConstants.FS) == aNoSamplesTemp * SignalProcConstants.FS) {
//                    aNoSamplesAfterQRS = (int) Math.ceil(aNoSamplesTemp * SignalProcConstants.FS);
//                } else {
//                    aNoSamplesAfterQRS = (int) Math.ceil(aNoSamplesTemp * SignalProcConstants.FS) - 1;
//                }

                int aNoSamplesQRS = 1 + aNoSamplesBeforeQRS + aNoSamplesAfterQRS;

                /**
                 * Extend signals to manage first QRS
                 * Extend the signal in case of first QRS within 200 ms.
                 */
                int aInitialQrsIndexArr = 0;
                int aNoSamplesToLeft = 0;

                if (aNoSamplesBeforeQRS - iQRSm[0] > 0) {
                    aNoSamplesToLeft = aNoSamplesBeforeQRS - iQRSm[0];
                }

//                double[][] aRowExtract = mMatrixFunctions.subMatrix(iInput, 0, 0, 0, SignalProcConstants.NO_OF_CHANNELS - 1);
//
//                double[][] aRowExtension = mMatrixFunctions.repmat(aRowExtract, aNoSamplesToLeft);

//                double[][] aInputExtension = mMatrixFunctions.verticalConcat(aRowExtension, iInput);
                double[][] aInputExtension = new double[iInput.length + aNoSamplesToLeft][SignalProcConstants.NO_OF_CHANNELS];
                for (int i = 0; i < aNoSamplesToLeft; i++) {
                    for (int j = 0; j < SignalProcConstants.NO_OF_CHANNELS; j++) {
                        aInputExtension[i][j] = iInput[0][j];
                    }
                }

                for (int i = 0; i < iInput.length; i++) {
                    for (int j = 0; j < SignalProcConstants.NO_OF_CHANNELS; j++) {
                        aInputExtension[i+aNoSamplesToLeft][j] = iInput[i][j];
                    }
                }

                /**
                 * Extend signals to manage last QRS
                 *
                 */

                int aFinalQrsIndexArr = aNoQRSm - 1;
//                double[] aRRMedTemp = new double[SignalProcConstants.CANCEL_NO_SAMPLES_END];
//                double aRRMeanSum = 0;
//                for (int i = 0; i < SignalProcConstants.CANCEL_NO_SAMPLES_END; i++) {
//                    aRRMedTemp[i] = aRRms[aRRms.length - 1 - i];
//                    aRRMeanSum = aRRMedTemp[i] + aRRMeanSum;
//                }
//                double aRRmsMean = aRRMeanSum / SignalProcConstants.CANCEL_NO_SAMPLES_END;
//                double aRRmsMedian = mMatrixFunctions.findMedian(aRRMedTemp);
//
//                double aTempD = (1 - SignalProcConstants.CANCEL_LASTQRS_TH_HIGH_PERC) * SignalProcConstants.FS * aRRmsMedian;
//                int aTempI = 0;
//                if (Math.ceil(aTempD) == aTempD) {
//                    aTempI = (int) Math.ceil(aTempD);
//                } else {
//                    aTempI = (int) Math.ceil(aTempD) - 1;
//                }
//                double aNoSamplesAddedEndTemp = 0;
//                int aNoSamplesAddedEnd = 0;
//
//                if (iQRSm[aFinalQrsIndexArr - 1] + aTempI < SignalProcConstants.NO_OF_SAMPLES) {
//                    // find max
//                    if (SignalProcConstants.CANCEL_LASTQRS_TH_LOW_PERC * SignalProcConstants.FS > SignalProcConstants.CANCEL_LASTQRS_TH_HIGH_PERC
//                            * SignalProcConstants.FS * aRRmsMean) {
//                        aNoSamplesAddedEndTemp = SignalProcConstants.CANCEL_LASTQRS_TH_LOW_PERC * SignalProcConstants.FS;
//                    } else {
//                        aNoSamplesAddedEndTemp = SignalProcConstants.CANCEL_LASTQRS_TH_HIGH_PERC * SignalProcConstants.FS * aRRmsMean;
//                    }
//
//                    if (Math.ceil(aNoSamplesAddedEndTemp) == aNoSamplesAddedEndTemp) {
//                        aNoSamplesAddedEnd = (int) Math.ceil(aNoSamplesAddedEndTemp);
//                    } else {
//                        aNoSamplesAddedEnd = (int) Math.ceil(aNoSamplesAddedEndTemp) - 1;
//                    }
//
//                    int aRowToExtend = aInputExtension.length - 1 - aNoSamplesAddedEnd - 1;
//
//                    // Do replicate the row and add it to the input extension
//                    double[][] aRowExtended = mMatrixFunctions.subMatrix(aInputExtension, aRowToExtend, aRowToExtend, 0,
//                            aInputExtension[0].length - 1);
//
//                    double[][] aInputExtendFinal = mMatrixFunctions.repmat(aRowExtended, aNoSamplesAddedEnd);
//                    for (int i = 0; i < aNoSamplesAddedEnd; i++) {
//                        for (int j = 0; j < aInputExtension[0].length; j++) {
//                            aInputExtension[i + aRowToExtend + 2][j] = aInputExtendFinal[i][j];
//                        }
//                    }
//
//                } // end if qrsm[qf]
                /**
                 * no of samples to add to right of the signal
                 */
                int aNoSamplesToRight = 0;

                if (iQRSm[aFinalQrsIndexArr] + aNoSamplesAfterQRS - SignalProcConstants.NO_OF_SAMPLES > -1) {
                    aNoSamplesToRight = iQRSm[aFinalQrsIndexArr] + aNoSamplesAfterQRS - SignalProcConstants.NO_OF_SAMPLES + 1;
                }
                double[][] aInputSVD = new double[aInputExtension.length + aNoSamplesToRight][aInputExtension[0].length];
                for (int i = 0; i < aInputExtension.length; i++) {
                    for (int j = 0; j < aInputExtension[0].length; j++) {
                        aInputSVD[i][j] = aInputExtension[i][j];
                    }
                }
                // Do extension if required.
                if (aNoSamplesToRight > 0) {
//                    double[][] aRowExtractRight = mMatrixFunctions.subMatrix(iInput, SignalProcConstants.NO_OF_SAMPLES - 1,
//                            SignalProcConstants.NO_OF_SAMPLES - 1, 0, SignalProcConstants.NO_OF_CHANNELS - 1);
//                    double[][] aReplicateSamplesRight = mMatrixFunctions.repmat(aRowExtractRight, aNoSamplesToRight);

                    for (int i = 0; i < aNoSamplesToRight; i++) {
                        for (int j = 0; j < SignalProcConstants.NO_OF_CHANNELS; j++) {
                            aInputSVD[i + aInputExtension.length][j] = iInput[SignalProcConstants.NO_OF_SAMPLES-1][j];
                        }
                    }
                }

                /**
                 * Added Samples to right :: ALL extensions of signal is done.
                 */

                int aNoSamplesExtend = aInputSVD.length;
                int aNoQrs = aFinalQrsIndexArr - aInitialQrsIndexArr + 1;

                int[] aInitQrsLocations = new int[aNoQrs];
                for (int i = aInitialQrsIndexArr; i <= aFinalQrsIndexArr; i++) {
                    aInitQrsLocations[i - aInitialQrsIndexArr] = iQRSm[i];
                }

                /**
                 * Start and end of QRS window
                 */
                int[] aQrsIndexArrI = new int[aNoQrs];
                int[] aQrsIndexArrF = new int[aNoQrs];

                for (int i = 0; i < aNoQrs; i++) {
                    aQrsIndexArrI[i] = aNoSamplesToLeft + aInitQrsLocations[i] - aNoSamplesBeforeQRS;
                    aQrsIndexArrF[i] = aNoSamplesToLeft + aInitQrsLocations[i] + aNoSamplesAfterQRS;
                }

                double aSvdExtract[][] = new double[aNoSamplesQRS][aNoQrs];

                // Add weight function
//				double[][] aWeightWindowFunction = mMatrixFunctions.weightFunction(aNoSamplesBeforeQRS,
//						aNoSamplesAfterQRS, SignalProcConstants.FS);
                SignalProcUtils.noSamplesBeforeQRS = aNoSamplesBeforeQRS;
                SignalProcUtils.noSamplesAfterQRS = aNoSamplesAfterQRS;
                SignalProcUtils.trapezodialWindow = mMatrixFunctions.weightFunction(aNoSamplesBeforeQRS,
                        aNoSamplesAfterQRS, SignalProcConstants.FS);
                SignalProcUtils.slope1 = ( SignalProcUtils.trapezodialWindow[SignalProcUtils.trapezodialWindowRegion[3][0]][0] - SignalProcUtils.trapezodialWindow[SignalProcUtils.trapezodialWindowRegion[3][1]][0])/
                        (SignalProcUtils.trapezodialWindowRegion[3][0] - SignalProcUtils.trapezodialWindowRegion[3][1]);
                SignalProcUtils.slope2 = ( SignalProcUtils.trapezodialWindow[SignalProcUtils.trapezodialWindowRegion[1][0]][0] - SignalProcUtils.trapezodialWindow[SignalProcUtils.trapezodialWindowRegion[1][1]][0])/
                        (SignalProcUtils.trapezodialWindowRegion[1][0] - SignalProcUtils.trapezodialWindowRegion[1][1]);


                // double[][] wwg = Matrix.transpose(wwgT);
                double[][] aApproxmSignal = new double[aNoSamplesExtend][SignalProcConstants.NO_OF_CHANNELS];

                /**
                 * Start loop for doing SVD and substraction
                 */
                double[][] aRowextract = new double[1][aNoSamplesQRS];
                double[][] aRowWeighted = new double[1][aNoSamplesQRS];

                aApproxmSignal = pcaParallized(aNoQrs, aQrsIndexArrI, aQrsIndexArrF, iQRSm, aInputSVD, aNoSamplesQRS, aApproxmSignal);

                double[][] aResidueOutput = new double[SignalProcConstants.NO_OF_SAMPLES][SignalProcConstants.NO_OF_CHANNELS];

                for (int i = 0; i < SignalProcConstants.NO_OF_SAMPLES; i++) {
                    for (int j = 0; j < SignalProcConstants.NO_OF_CHANNELS; j++) {
                        aResidueOutput[i][j] = aInputSVD[i + aNoSamplesToLeft][j] - aApproxmSignal[i + aNoSamplesToLeft][j];
                    }
                }

                return aResidueOutput;
            } else {
                throw new Exception("Invalid mQRS locations : mqrs cancel");
            }
        } else {
            throw new Exception("No of mQRS must be atleast 6 : mqrs cancel");
        }
    } // end main

} // end class