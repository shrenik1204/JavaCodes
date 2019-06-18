package SignalProc;

//import com.sattvamedtech.fetallite.FLApplication;
//import com.sattvamedtech.fetallite.enums.FileLoggerType;

import helper.ExecutorServiceHelper;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

//import com.sattvamedtech.fetallite.helper.FileLoggerHelper;
//import com.sattvamedtech.fetallite.utils.ApplicationUtils;

/**
 * <p> Maternal QRS peak detection and selection from ICA-2 output.</p>
 * <p> Change Logs :</p>
 * <ul>
 *     <li> 28th August, 2017
 *         <ol>
 *             <li> Changed QRS selection to QRS Selection Robust.</li>
 *         </ol>
 *     </li>
 *     <li> 5th June, 2017
 *         <ol>
 *             <li> Parallized computation of QRS for each channel.</li>
 *         </ol>
 *     </li>
 *     <li> 24th May, 2017
 *     		<ol>
 *     		 	<li> First commit.</li>
 *     		</ol>
 *     </li>
 * </ul>
 * @author Kishore Subramanian (kishore@sattvamedtech.com)
 *
 */
public class MQRSDetection {
	/**
	 * Object initialization of {@link MatrixFunctions}
	 */
	MatrixFunctions mMatrixFunctions = new MatrixFunctions();

	/**
	 * Column 1 extracted from iInput
	 */
	double[] mChannel1;
	/**
	 * Column 2 extracted from iInput
	 */
	double[] mChannel2;
	/**
	 * Column 3 extracted from iInput
	 */
	double[] mChannel3;
	/**
	 * Column 4 extracted from iInput
	 */
	double[] mChannel4;
	/**
	 * Column 1 extracted from iFilterInput
	 */
	double[] mFiltChannel1;
	/**
	 * Column 2 extracted from iFilterInput
	 */
	double[] mFiltChannel2;
	/**
	 * Column 3 extracted from iFilterInput
	 */
	double[] mFiltChannel3;
	/**
	 * Column 4 extracted from iFilterInput
	 */
	double[] mFiltChannel4;

	/**
	 * QRS detected array for each channel.
	 */
	double[] mQRS1, mQRS2, mQRS3, mQRS4;

	/**
	 * Determines and selects the maternal QRS in ICA-1 output.
	 * @param iInput {@literal 15000 x 4} ICA-1 output.
	 * @return Final maternal QRS selected.
	 * @throws Exception If (iInput[0].length not equal to 4) .
	 */
	public int[] mQRS(double[][] iInput, double[][] iFilterInput) throws Exception {
		int aLength = iInput.length;

		if (aLength > SignalProcConstants.MQRS_DERIVATIVE.length) {
			if (iInput[0].length == 4) {
				ExecutorService executorService = ExecutorServiceHelper.getInstance().getExecutorService();

				mChannel1 = new double[aLength];
				mChannel2 = new double[aLength];
				mChannel3 = new double[aLength];
				mChannel4 = new double[aLength];

				mFiltChannel1 = new double[aLength];
				mFiltChannel2 = new double[aLength];
				mFiltChannel3 = new double[aLength];
				mFiltChannel4 = new double[aLength];

				Future<Boolean> channelOneTask = executorService.submit(() -> {
                    Thread.currentThread().setName(SignalProcUtils.currentIteration + " 1MQRS");

					for (int i = 0; i < aLength; i++) {
						mChannel1[i] = iInput[i][0];
						mFiltChannel1[i] = iFilterInput[i][1];
					}

					try {
						mQRS1 = mMatrixFunctions.mqrsDetection(mChannel1,mFiltChannel1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, true);

				Future<Boolean> channelTwoTask = executorService.submit(() -> {
                    Thread.currentThread().setName(SignalProcUtils.currentIteration + " 2MQRS");

					for (int i = 0; i < aLength; i++) {
						mChannel2[i] = iInput[i][1];
						mFiltChannel2[i] = iFilterInput[i][2];
					}

					try {
						mQRS2 = mMatrixFunctions.mqrsDetection(mChannel2,mFiltChannel2);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, true);

				Future<Boolean> channelThreeTask = executorService.submit(() -> {
                    Thread.currentThread().setName(SignalProcUtils.currentIteration + " 3MQRS");

					for (int i = 0; i < aLength; i++) {
						mChannel3[i] = iInput[i][2];
						mFiltChannel3[i] = iFilterInput[i][3];
					}

					try {
						mQRS3 = mMatrixFunctions.mqrsDetection(mChannel3,mFiltChannel3);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, true);

				Future<Boolean> channelFourTask = executorService.submit(() -> {
                    Thread.currentThread().setName(SignalProcUtils.currentIteration + " 4MQRS");

					for (int i = 0; i < aLength; i++) {
						mChannel4[i] = iInput[i][3];
						mFiltChannel4[i] = iFilterInput[i][4];
					}

					try {
						mQRS4 = mMatrixFunctions.mqrsDetection(mChannel4,mFiltChannel4);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}, true);

				if (channelOneTask.get() && channelTwoTask.get() && channelThreeTask.get() && channelFourTask.get()) {
					Object[] qrsSelectionInputs = mMatrixFunctions.channelSelection_Mqrs(mQRS1, mQRS2, mQRS3, mQRS4,
							SignalProcConstants.MQRS_VARIANCE_THRESHOLD, SignalProcConstants.MQRS_RR_LOW_TH, SignalProcConstants.MQRS_RR_HIGH_TH,SignalProcUtils.lastRRMeanMaternal,iFilterInput);
//					FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : Mean Value : %f, %f, %f, %f", mQRS1[1], mQRS2[1], mQRS3[1], mQRS4[1]), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//					FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : Median Value : %f, %f, %f, %f", mQRS1[0], mQRS2[0], mQRS3[0], mQRS4[0]), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

//					double aMaxMedian, aMaxMean;
//					aMaxMedian = mQRS1[0];
//					aMaxMean = mQRS1[1];
//					int aChMedian, aChMean;
//					aChMean = -1;
//					if (aMaxMean > 0) {
//						aChMean = 0;
//					}
//					aChMedian = 0;
//					if (aMaxMedian < mQRS2[0]) {
//						aMaxMedian = mQRS2[0];
//						aChMedian = 1;
//					}
//					if (aMaxMedian < mQRS3[0]) {
//						aMaxMedian = mQRS3[0];
//						aChMedian = 2;
//					}
//					if (aMaxMedian < mQRS4[0]) {
//						aMaxMedian = mQRS4[0];
//						aChMedian = 3;
//					}
//					if (aMaxMean < mQRS2[1] && mQRS2[1] > 0) {
//						aMaxMean = mQRS2[1];
//						aChMean = 1;
//					}
//					if (aMaxMean < mQRS3[1] && mQRS3[1] > 0) {
//						aMaxMean = mQRS3[1];
//						aChMean = 2;
//					}
//					if (aMaxMean < mQRS4[1] && mQRS4[1] > 0) {
////						aMaxMean = mQRS4[1];
//						aChMean = 3;
//					}
//
//					int aCh;
//					if (aMaxMedian > 0.1) {
//						aCh = aChMedian;
//					} else if (aChMean > -1) {
//						aCh = aChMean;
//					} else {
//						aCh = -1;
//					}
//
////                    FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : Channel selected : %d", aCh), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//
					int[] aQrsSelected;
					int aQrsLength;
//					if (aCh > -1) {
//						if (aCh == 0) {
//							aQrsLength = mQRS1.length - 2;
//							aQrsSelected = new int[aQrsLength];
//							for (int i = 0; i < aQrsSelected.length; i++) {
//								aQrsSelected[i] = (int) mQRS1[i + 2];
//							}
//						} else if (aCh == 1) {
//							aQrsLength = mQRS2.length - 2;
//							aQrsSelected = new int[aQrsLength];
//							for (int i = 0; i < aQrsSelected.length; i++) {
//								aQrsSelected[i] = (int) mQRS2[i + 2];
//							}
//						} else if (aCh == 2) {
//							aQrsLength = mQRS3.length - 2;
//							aQrsSelected = new int[aQrsLength];
//							for (int i = 0; i < aQrsSelected.length; i++) {
//								aQrsSelected[i] = (int) mQRS3[i + 2];
//							}
//						} else {
//							aQrsLength = mQRS4.length - 2;
//							aQrsSelected = new int[aQrsLength];
//							for (int i = 0; i < aQrsSelected.length; i++) {
//								aQrsSelected[i] = (int) mQRS4[i + 2];
//							}
//						}
//					} else {
//						aQrsLength = 0;
//						aQrsSelected = new int[]{};
//					}
					aQrsSelected = (int[]) qrsSelectionInputs[0];
					aQrsLength = ((int[])qrsSelectionInputs[0]).length;
					// Find RR mean and Std in the iteration and check if the Qrs spans the enter signal range.
					if (aQrsLength > SignalProcConstants.MQRS_MIN_SIZE) {
						int[] aRR = new int[aQrsLength - 1];
						for (int i = 0; i < aQrsLength - 1; i++) {
							aRR[i] = aQrsSelected[i + 1] - aQrsSelected[i];
						}
						Arrays.sort(aRR);

						double aSum = 0;
						for (int i = 0; i < aRR.length; i++) {
							aSum += aRR[i];
						}
						double aRRMean = aSum / aRR.length;

						aSum = 0;
						for (int i = 0; i < aRR.length; i++) {
							aSum += Math.pow((aRR[i] - aRRMean), 2);
						}
						double aRRstd = Math.sqrt(aSum) / aRR.length;

						if ((aRRstd > 0.4 * aRRMean) || (aRRMean * aQrsLength < 12000)) {
//                            FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : RR standard deviation, mean : %f, %f", aRRstd, aRRMean), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//
//                            FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : Average length : %f", aRRMean * aQrsLength), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

//							aCh = -1;
							qrsSelectionInputs[2] = -1;
							aQrsSelected = new int[]{};
						}
					}

					return aQrsSelected;
				} else {
				    return new int[]{};
                }
			} else {
				throw new Exception("Input must have 4 channels : mQRS");
			}
		} else {
			throw new Exception("Input array must have size greater filter : mQRS");
		}
	}
}