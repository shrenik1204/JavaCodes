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
public class QrsSelectionRobust_New {

    /**
     * <p> Final qrs selection from the best channel selected.</p>
     *
     * @param iQRS                Possible set of QRS locations.
     * @param iStartIndex         First index of confirmed QRS in the iQRS array.
     * @param iQrsM               Set of maternal QRS locations.
     * @param iInterpolatedLength Length of QRS interpolated at the end in previous iteration.
     * @param iQRSLast            Location of last QRS determined in previous iteration.
     * @param iRRMeanLast         Mean RR of last 4 QRS determined in previous iteration.
     * @param iNoDetectionFlag    <pre> (int) '1' if no single channel has been determined to
     *                                           contain possible QRS locations else '0'.  </pre>
     * @param iQrsConcat          Concatenated sorted array of possible QRS locations from all channels.
     * @return {aQRSFinal, aInterpolatedLength, aNoDetectionFLag} : Return QRS selected and update Flags.
     * @throws Exception Message containing the exception.
     */
    public Object[] qrsSelection(int[] iQRS, int iStartIndex, int[] iQrsM, int iInterpolatedLength, int iQRSLast,
                                 double iRRMeanLast, int iNoDetectionFlag, int[] iQrsConcat) throws Exception {

        QrsSelectionFunctions aQrsSelectionFunctions = new QrsSelectionFunctions();
//		QrsSelectionInterpolation aQrsInterpolate = new QrsSelectionInterpolation();

        int aInterpolatedLength = 0;

//        if(overlap){
//            Filename.ExecutionLogs.append( "true,");
//        } else{
//            Filename.ExecutionLogs.append( "false,");
//        }

        Object[] aQrsConcatOut = aQrsSelectionFunctions.qrsConcatenated(iQrsConcat, iQRSLast, iRRMeanLast, iQrsM);

        LinkedList<Integer> aQrsFinalConcat = aQrsSelectionFunctions.interpolate((LinkedList<Integer>) aQrsConcatOut[0],
                (LinkedList<Integer>) aQrsConcatOut[1], iQrsM);

        if (aQrsFinalConcat.getLast() <= SignalProcConstants.QRS_END_VALUE && aQrsFinalConcat.size() >= 2) {

            aInterpolatedLength = aInterpolatedLength + SignalProcConstants.QRS_END_VALUE - aQrsFinalConcat.getLast();
            if (aInterpolatedLength < SignalProcConstants.QRS_LENGTH_MAX_INTERPOLATE) {
                int aLenQRS = aQrsFinalConcat.size();
                int aDiffLast = aQrsFinalConcat.get(aLenQRS - 1) - aQrsFinalConcat.get(aLenQRS - 2);

                while (aQrsFinalConcat.getLast() <= SignalProcConstants.QRS_END_VALUE) {
                    aQrsFinalConcat.add(aQrsFinalConcat.getLast() + aDiffLast);
                }
            }
        }
        boolean concatOverlap = false;

        if(SignalProcUtils.currentIteration > 0 && SignalProcUtils.lastQRSFetalArray.size() > 4){
            concatOverlap = aQrsSelectionFunctions.QrsOverlapCheck(aQrsFinalConcat);
        }
        SignalProcUtils.lastQRSFetalArray.clear();
        for (int i = 0; i < aQrsFinalConcat.size(); i++) {
            while (aQrsFinalConcat.get(i) > 10000) {
                SignalProcUtils.lastQRSFetalArray.add((aQrsFinalConcat.get(i)-10000));
                break;
            }
        }
//        if(concatOverlap){
//            Filename.ExecutionLogs.append( "true,");
//        } else{
//            Filename.ExecutionLogs.append( "false,");
//        }
        if (iStartIndex <= -1) {
            if (SignalProcUtils.lastvalidRRMeanFetal != 0) {
                if(concatOverlap){
                    Filename.ExecutionLogs.append( "C O true,");
                    SignalProcUtils.independentCount++;
                    if(SignalProcUtils.independentCount == 2){
                        SignalProcUtils.concatCount = 0;
                    }
//					SignalProcUtils.lastvalidRRMeanFetal = iRRMeanLast;
                    SignalProcUtils.independantdet_flag = true;
//					FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : aConfirmFlag : %d", aConfirmFlag), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
////
                    Filename.FqrsSelectionType.append("Concat CNF\n");
                    Filename.ExecutionLogs.append("Concat CNF,");
                    Filename.ExecutionLogs.append(iQRSLast+",");
                    return new Object[] { convertListtoArray(aQrsFinalConcat), aInterpolatedLength, 0 };
                } else{
                    Filename.ExecutionLogs.append( "C O false,");

                    SignalProcUtils.concatCount++;
                    SignalProcUtils.independentCount = 0;
//                if(SignalProcUtils.concatCount == 6){
////                    FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : QrsSelectionRobust : Six Continuous/Intermittent Concat Output", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
////					throw new Exception(FLApplication.getInstance().getString(R.string.connection_issue));
//                }
                    SignalProcUtils.independantdet_flag = false;
                    Filename.ExecutionLogs.append("Concat,");
                    Filename.ExecutionLogs.append(iQRSLast+",");

                    Filename.FqrsSelectionType.append("Concat \n");
                    return new Object[]{convertListtoArray(aQrsFinalConcat), aInterpolatedLength, 1};
                }
            } else{
                Filename.ExecutionLogs.append("No Det,");

                Filename.FqrsSelectionType.append("No Det\n");

                return new Object[]{new int[]{}, 0, 1};
            }
        } else {
            aInterpolatedLength = 0;
            /**
             * Forward Iteration
             */
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

            boolean overlap = false;

            if(SignalProcUtils.currentIteration > 0 && SignalProcUtils.lastQRSFetalArray.size() > 4){
                overlap = aQrsSelectionFunctions.QrsOverlapCheck(aQrsFinal);
            }
            SignalProcUtils.lastQRSFetalArray.clear();
            for (int i = 0; i < aQrsFinal.size(); i++) {
                while (aQrsFinal.get(i) > 10000) {
                    SignalProcUtils.lastQRSFetalArray.add((aQrsFinal.get(i)-10000));
                    break;
                }
            }
            if (iNoDetectionFlag == 0 && iRRMeanLast != 0) {
                boolean aConfirmFlag = aQrsSelectionFunctions.firstQrsCheck(aQrsFinal, iQRSLast, iRRMeanLast, false);
                if (overlap || aConfirmFlag) {
                    Filename.ExecutionLogs.append( "O true,");

                    SignalProcUtils.independentCount++;
                    if (SignalProcUtils.independentCount == 2) {
                        SignalProcUtils.concatCount = 0;
                    }
//					SignalProcUtils.lastvalidRRMeanFetal = iRRMeanLast;
                    SignalProcUtils.independantdet_flag = true;
//					FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : aConfirmFlag : %d", aConfirmFlag), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
////
                    Filename.FqrsSelectionType.append("CNF\n");
                    Filename.ExecutionLogs.append("CNF,");
                    Filename.ExecutionLogs.append(iQRSLast + ",");


                    return new Object[]{convertListtoArray(aQrsFinal), aInterpolatedLength, 0};
                } else {
                    if(concatOverlap){
                        Filename.ExecutionLogs.append( "C O true,");
                        SignalProcUtils.independentCount++;
                        if(SignalProcUtils.independentCount == 2){
                            SignalProcUtils.concatCount = 0;
                        }
//					SignalProcUtils.lastvalidRRMeanFetal = iRRMeanLast;
                        SignalProcUtils.independantdet_flag = true;
//					FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : aConfirmFlag : %d", aConfirmFlag), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
////
                        Filename.FqrsSelectionType.append("Concat CNF in O\n");
                        Filename.ExecutionLogs.append("Concat CNF in O,");
                        Filename.ExecutionLogs.append(iQRSLast+",");


                        return new Object[] { convertListtoArray(aQrsFinalConcat), aInterpolatedLength, 0 };
                    } else {
                        Filename.ExecutionLogs.append( "C O false,");
                        SignalProcUtils.concatCount++;
                        SignalProcUtils.independentCount = 0;
//                if(SignalProcUtils.concatCount == 6){
////                    FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : QrsSelectionRobust : Six Continuous/Intermittent Concat Output", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
////					throw new Exception(FLApplication.getInstance().getString(R.string.connection_issue));
//                }
                        SignalProcUtils.independantdet_flag = false;
                        Filename.FqrsSelectionType.append("Concat CNF N\n");
                        Filename.ExecutionLogs.append("Concat CNF N,");
                        Filename.ExecutionLogs.append(iQRSLast + ",");


                        return new Object[]{convertListtoArray(aQrsFinalConcat), aInterpolatedLength, 1};
                    }
                }
            }else {
                Filename.ExecutionLogs.append("Nil,");
                SignalProcUtils.independentCount++;
                if(SignalProcUtils.independentCount == 2){
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