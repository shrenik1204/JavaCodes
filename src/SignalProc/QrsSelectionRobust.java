package SignalProc;

//import com.sattvamedtech.fetallite.FLApplication;
//import com.sattvamedtech.fetallite.enums.FileLoggerType;
//import com.sattvamedtech.fetallite.helper.FileLoggerHelper;
//import com.sattvamedtech.fetallite.utils.ApplicationUtils;

import Wrapper.Filename;

import java.util.LinkedList;

/**
 * <p> Final qrs selection from the best channel selected using information from previous iteration.</p>
 * <p> Change Logs :</p>
 * <ul>
 *     <li> 28th August, 2017
 *         <ol>
 *             <li> Added condition to find miss locations only for Fetal QRS selection.</li>
 *             <li> Linked Maternal QRS selection to QRS Selection Robust.</li>
 *         </ol>
 *     </li>
 *     <li> 21st August, 2017
 *			<ol>
 *			 <li> Restructured to simple functions.</li>
 *			</ol>
 *     </li>
 *     <li> 8th August, 2017
 *     	<ol>
 *     	    <li> First commit.</li>
 *     	</ol>
 *     	</li>
 * </ul>
 *
 * @author Kishore Subramanian (kishore@sattvamedtech.com)
 */
public class QrsSelectionRobust {

	/**
	 * <p> Final qrs selection from the best channel selected.</p>
	 * @param iQRS Possible set of QRS locations.
	 * @param iStartIndex First index of confirmed QRS in the iQRS array.
	 * @param iQrsM Set of maternal QRS locations.
	 * @param iInterpolatedLength Length of QRS interpolated at the end in previous iteration.
	 * @param iQRSLast Location of last QRS determined in previous iteration.
	 * @param iRRMeanLast Mean RR of last 4 QRS determined in previous iteration.
	 * @param iNoDetectionFlag <pre> (int) '1' if no single channel has been determined to
	 *                contain possible QRS locations else '0'.  </pre>
	 * @param iQrsConcat Concatenated sorted array of possible QRS locations from all channels.
	 * @return {aQRSFinal, aInterpolatedLength, aNoDetectionFLag} : Return QRS selected and update Flags.
	 * @throws Exception Message containing the exception.
	 */
	public Object[] qrsSelection(int[] iQRS, int iStartIndex, int[] iQrsM, int iInterpolatedLength, int iQRSLast,
			double iRRMeanLast, int iNoDetectionFlag, int[] iQrsConcat) throws Exception{

		QrsSelectionFunctions aQrsSelectionFunctions = new QrsSelectionFunctions();
//		QrsSelectionInterpolation aQrsInterpolate = new QrsSelectionInterpolation();

		int aInterpolatedLength = 0;
		if (iStartIndex > -1) {

			/**
			 * Forward Iteration
			 */
//			QrsSelectionForwardIteration aQrsForward = new QrsSelectionForwardIteration();
			Object[] aQrsForwardOut = aQrsSelectionFunctions.qrsForwardIteration(iQRS, iStartIndex);

			LinkedList<Integer> aQrsFinal = aQrsSelectionFunctions.interpolate((LinkedList<Integer>) aQrsForwardOut[0],
					(LinkedList<Integer>) aQrsForwardOut[1], iQrsM);

			/**
			 * Interpolate at the end
			 */

			if (aQrsFinal.getLast() <= SignalProcConstants.QRS_END_VALUE && aQrsFinal.size() >= 2) {

				aInterpolatedLength = aInterpolatedLength + SignalProcConstants.QRS_END_VALUE - aQrsFinal.getLast();
				if (aInterpolatedLength < SignalProcConstants.QRS_LENGTH_MAX_INTERPOLATE) {
					int aLenQRS = aQrsFinal.size();
					int aDiffLast = aQrsFinal.get(aLenQRS - 1) - aQrsFinal.get(aLenQRS - 2);

					while (aQrsFinal.getLast() <= SignalProcConstants.QRS_END_VALUE) {
						aQrsFinal.add(aQrsFinal.getLast() + aDiffLast);
					}
				}
			}
			int aForwardLen = aQrsFinal.size();
			/**
			 * Backward Iteration
			 */

//			QrsSelectionBackwardIteration aQrsBackward = new QrsSelectionBackwardIteration();
			Object[] aQrsBackwardOut = aQrsSelectionFunctions.qrsBackwardIteration(iQRS, iStartIndex, aQrsFinal);

			aQrsFinal = aQrsSelectionFunctions.interpolate((LinkedList<Integer>) aQrsBackwardOut[0],
					(LinkedList<Integer>) aQrsBackwardOut[1], iQrsM);

			/**
			 * Interpolate at the start
			 */

			if (aQrsFinal.getFirst() >= SignalProcConstants.QRS_START_VALUE && aQrsFinal.size() >= 2) {

				aInterpolatedLength = aInterpolatedLength + aQrsFinal.getFirst() - SignalProcConstants.QRS_START_VALUE;
				if (aInterpolatedLength < SignalProcConstants.QRS_LENGTH_MAX_INTERPOLATE) {
					int aDiffLast = aQrsFinal.get(1) - aQrsFinal.get(0);

					while (aQrsFinal.getFirst() > SignalProcConstants.QRS_START_VALUE) {
						aQrsFinal.addFirst(aQrsFinal.getFirst() - aDiffLast);
					}
				}
			}



			/**
			 * Check for first QRS within range of last RRmean n last Qrs
			 */
			if ( iNoDetectionFlag == 0 && iRRMeanLast !=0 ){
				boolean aConfirmFlag = aQrsSelectionFunctions.firstQrsCheck(aQrsFinal, iQRSLast, iRRMeanLast, false);

				if (aConfirmFlag) {
                    SignalProcUtils.independentCount++;
                    if(SignalProcUtils.independentCount == 3){
                        SignalProcUtils.concatCount = 0;
                    }
//					SignalProcUtils.lastvalidRRMeanFetal = iRRMeanLast;
                    SignalProcUtils.independantdet_flag = true;
//					FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : aConfirmFlag : %d", aConfirmFlag), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
////
                    Filename.FqrsSelectionType.append("Confirm Flag\n");
					Filename.ExecutionLogs.append("Confirm Flag,");
					Filename.ExecutionLogs.append(iQRSLast+",");


					return new Object[] { convertListtoArray(aQrsFinal), aInterpolatedLength, 0 };
				} else {
				    /* NEW IMPLEMENTATION BY ARAVIND   */
//                    aQrsSelectionFunctions.qrsIndependent(aQrsFinal);

                    boolean recheckFlag = aQrsSelectionFunctions.firstQrsCheck(aQrsFinal, iQRSLast, iRRMeanLast, true);


				     /*NEW IMPLEMENTATION BY ARAVIND END*/
                    if(!recheckFlag) {

                        SignalProcUtils.concatCount++;
                        SignalProcUtils.independentCount = 0;
                        if (SignalProcUtils.concatCount == 6) {
//                        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : QrsSelectionRobust : Six Continuous/Intermittent Concat Output", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//						throw new Exception(FLApplication.getInstance().getString(R.string.connection_issue));
                        }
////
                        SignalProcUtils.independantdet_flag = false;

                        Object[] aQrsConcatOut = aQrsSelectionFunctions.qrsConcatenated(iQrsConcat, iQRSLast, iRRMeanLast, iQrsM);

                        aQrsFinal = aQrsSelectionFunctions.interpolate((LinkedList<Integer>) aQrsConcatOut[0],
                                (LinkedList<Integer>) aQrsConcatOut[1], iQrsM);

                        aInterpolatedLength = 0;
                        if (aQrsFinal.getLast() <= SignalProcConstants.QRS_END_VALUE && aQrsFinal.size() >= 2) {

                            aInterpolatedLength = aInterpolatedLength + SignalProcConstants.QRS_END_VALUE - aQrsFinal.getLast();
                            if (aInterpolatedLength < SignalProcConstants.QRS_LENGTH_MAX_INTERPOLATE) {
                                int aLenQRS = aQrsFinal.size();
                                int aDiffLast = aQrsFinal.get(aLenQRS - 1) - aQrsFinal.get(aLenQRS - 2);

                                while (aQrsFinal.getLast() <= SignalProcConstants.QRS_END_VALUE) {
                                    aQrsFinal.add(aQrsFinal.getLast() + aDiffLast);
                                }
                            }
                        }
                        Filename.FqrsSelectionType.append("CFN : Concat\n");
                        Filename.ExecutionLogs.append("CFN : Concat,");
                        Filename.ExecutionLogs.append(iQRSLast+",");


                        return new Object[] { convertListtoArray(aQrsFinal), aInterpolatedLength, 1 };
                    } else{
//                        while (aQrsFinal.get(i) < 2000) {
//                            aQrsFinal.remove(i);
//                            if (aQrsFinal.size() <= 0) {
//                                break;
//                            }
//                        }
                        SignalProcUtils.independantdet_flag = false;

                        Filename.FqrsSelectionType.append("CFN : Independent\n");
                        Filename.ExecutionLogs.append("CFN : Independent,");
                        Filename.ExecutionLogs.append(iQRSLast+",");


                        return new Object[] { convertListtoArray(aQrsFinal), aInterpolatedLength, 0 };
                    }
//					FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : Concatenated FQRS detection"), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

					/**
					 * ADD fqrsMissIndex indentification
					 */

				}
			}
			else {
                SignalProcUtils.independentCount++;
                if(SignalProcUtils.independentCount == 3){
                    SignalProcUtils.concatCount = 0;
                }
//				SignalProcUtils.lastvalidRRMeanFetal = iRRMeanLast;
                SignalProcUtils.independantdet_flag = true;

                Filename.ExecutionLogs.append("Independent,");
				Filename.ExecutionLogs.append(iQRSLast+",");

				Filename.FqrsSelectionType.append("Independent \n");

//
//				FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : Independent FQRS detection"), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
				return new Object[] { convertListtoArray(aQrsFinal), aInterpolatedLength, 0 };
			}
		} else {
//			FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : No FQRS detection"), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

            if (SignalProcUtils.lastvalidRRMeanFetal != 0) {
                SignalProcUtils.concatCount++;
                SignalProcUtils.independentCount = 0;
                if(SignalProcUtils.concatCount == 6){
//                    FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : QrsSelectionRobust : Six Continuous/Intermittent Concat Output", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//					throw new Exception(FLApplication.getInstance().getString(R.string.connection_issue));
                }
                SignalProcUtils.independantdet_flag = false;

                Object[] aQrsConcatOut = aQrsSelectionFunctions.qrsConcatenated(iQrsConcat, iQRSLast, iRRMeanLast, iQrsM);

                LinkedList<Integer> aQrsFinal = aQrsSelectionFunctions.interpolate((LinkedList<Integer>) aQrsConcatOut[0],
                        (LinkedList<Integer>) aQrsConcatOut[1], iQrsM);

                aInterpolatedLength = 0;
                if (aQrsFinal.getLast() <= SignalProcConstants.QRS_END_VALUE && aQrsFinal.size() >= 2) {

                    aInterpolatedLength = aInterpolatedLength + SignalProcConstants.QRS_END_VALUE - aQrsFinal.getLast();
                    if (aInterpolatedLength < SignalProcConstants.QRS_LENGTH_MAX_INTERPOLATE) {
                        int aLenQRS = aQrsFinal.size();
                        int aDiffLast = aQrsFinal.get(aLenQRS - 1) - aQrsFinal.get(aLenQRS - 2);

                        while (aQrsFinal.getLast() <= SignalProcConstants.QRS_END_VALUE) {
                            aQrsFinal.add(aQrsFinal.getLast() + aDiffLast);
                        }
                    }
                }

//					FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : Concatenated FQRS detection"), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

                /**
                 * ADD fqrsMissIndex indentification
                 */
				Filename.ExecutionLogs.append("Concat,");
				Filename.ExecutionLogs.append(iQRSLast+",");

				Filename.FqrsSelectionType.append("Concat \n");
                return new Object[]{convertListtoArray(aQrsFinal), aInterpolatedLength, 1};
            }
            else {
				Filename.ExecutionLogs.append("No Det,");

				Filename.FqrsSelectionType.append("No Det\n");

                return new Object[]{new int[]{}, 0, 1};
            }
		}

	}

	/**
	 * <p>Convert linked list to array.</p>
	 * @param iQRS Input list.
	 * @return Integer array.
	 */
	private int[] convertListtoArray(LinkedList<Integer> iQRS){
		int aLen = iQRS.size();
		int[] aQRS = new int[aLen];
		for (int i =0; i<aLen; i++){
			aQRS[i] = iQRS.get(i);
		}
		return aQRS;
	}

}
