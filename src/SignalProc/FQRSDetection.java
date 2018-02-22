package SignalProc;

import Wrapper.Filename;
import helper.ExecutorServiceHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

//import com.sattvamedtech.fetallite.utils.ApplicationUtils;

/**
 * <p> Fetal QRS peak detection and selection from ICA-2 output.</p>
 * <p> Change Logs :</p>
 * <ul>
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
 *
 * @author Kishore Subramanian (kishore@sattvamedtech.com)
 */
public class FQRSDetection {
	
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
	 * QRS detected array for each channel.
	 */
	int[] mQRS1, mQRS2, mQRS3, mQRS4;

	/**
	 * Determines and selects the fetal QRS in ICA-2 output.
	 * @param iInput {@literal 15000 x 4} ICA-2 output.
	 * @param iQrsM Maternal QRS locations of the current iteration.
	 * @param iInterpolatedLength Interpolated length at the end of previous iteration.
	 * @param iQRSLast Last QRS location detected in previous iteration.
	 * @param iRRMeanLast RR mean using the last 4 QRS peaks detected in previous iteration.
	 * @param iNoDetectionFlag '1' if no single channel has been determined to contain possible QRS locations else '0'.
	 * @return QRS selected and flag updates.
	 * 		<ul>
	 * 		 	<li> Final fetal QRS selected.</li>
	 * 		 	<li> Inpterpolated length.</li>
	 * 		 	<li> No detection flag.</li>
	 * 		</ul>
	 * @throws Exception If (iInput[0].length not equal to 4) .
	 */
	public Object[] fQRS(double[][] iInput, int[] iQrsM, int iInterpolatedLength, int iQRSLast, double iRRMeanLast,
			int iNoDetectionFlag) throws Exception {

		int aLength = iInput.length;
		if (aLength > SignalProcConstants.FQRS_DERIVATIVE.length) {
			if (iInput[0].length == 4) {
                ExecutorService executorService = ExecutorServiceHelper.getInstance().getExecutorService();

				mChannel1 = new double[aLength];
				mChannel2 = new double[aLength];
				mChannel3 = new double[aLength];
				mChannel4 = new double[aLength];

				Future<Boolean> channelOneTask = executorService.submit(() -> {
					Thread.currentThread().setName(SignalProcUtils.currentIteration + " 1FQRS");

                    for (int i = 0; i < aLength; i++) {
                        mChannel1[i] = iInput[i][0];
                    }

                    try {
                        mQRS1 = mMatrixFunctions.fetalQRS(mChannel1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}, true);

				Future<Boolean> channelTwoTask = executorService.submit(() -> {
					Thread.currentThread().setName(SignalProcUtils.currentIteration + " 2FQRS");

				    for (int i = 0; i < aLength; i++) {
                        mChannel2[i] = iInput[i][1];
                    }

                    try {
                        mQRS2 = mMatrixFunctions.fetalQRS(mChannel2);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}, true);

				Future<Boolean> channelThreeTask = executorService.submit(() -> {
					Thread.currentThread().setName(SignalProcUtils.currentIteration + " 3FQRS");

                    for (int i = 0; i < aLength; i++) {
                        mChannel3[i] = iInput[i][2];
                    }

                    try {
                        mQRS3 = mMatrixFunctions.fetalQRS(mChannel3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}, true);

				Future<Boolean> channelFourTask = executorService.submit(() -> {
					Thread.currentThread().setName(SignalProcUtils.currentIteration + " 4FQRS");

                    for (int i = 0; i < aLength; i++) {
                        mChannel4[i] = iInput[i][3];
                    }

                    try {
                        mQRS4 = mMatrixFunctions.fetalQRS(mChannel4);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
				}, true);


                if (channelOneTask.get() && channelTwoTask.get() && channelThreeTask.get() && channelFourTask.get()) {
                    Object[] qrsSelectionInputs = mMatrixFunctions.channelSelection(mQRS1, mQRS2, mQRS3, mQRS4,
                            SignalProcConstants.FQRS_VARIANCE_THRESHOLD, SignalProcConstants.FQRS_RR_LOW_TH, SignalProcConstants.FQRS_RR_HIGH_TH);

					QrsSelectionRobust aQrsSelect = new QrsSelectionRobust();
					int[] aQrsF = (int[]) qrsSelectionInputs[0];
					for (int i = 0; i < aQrsF.length; i++) {
						Filename.QRSF_Detected.append(aQrsF[i]+",");
					}
					Filename.QRSF_Detected.append("\n");

                    return aQrsSelect.qrsSelection((int[]) qrsSelectionInputs[0], (int) qrsSelectionInputs[1], iQrsM, iInterpolatedLength, iQRSLast, iRRMeanLast,
                            iNoDetectionFlag, (int[]) qrsSelectionInputs[2]);
                } else {
                    return new Object[]{new int[]{}, 0, 1};
                }
			} else {
				throw new Exception("Input must have four channels : fQRS");
			}
		}
		else {
			throw new Exception("Input array must have size greater than window : fQRS");
		}
	}

	

}