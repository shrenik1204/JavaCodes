package SignalProc;

//import com.sattvamedtech.fetallite.FLApplication;
//import com.sattvamedtech.fetallite.enums.FileLoggerType;
//import com.sattvamedtech.fetallite.helper.FileLoggerHelper;
//import com.sattvamedtech.fetallite.utils.ApplicationUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * <p> Final qrs selection from the best channel selected using information from previous iteration.</p>
 * <p> Change Logs :</p>
 * <ul>
 *     <li> 7th July, 2019
 *         <ol>
 *             <li> Logic for selecting correct MQRS peaks detected in all the 4 channels after ICA1</li>
 *         </ol>
 *     </li>
 * </ul>
 *
 * @author Aravind Prasad (aravind@sattvamedtech.com)
 */
public class QrsSelectionRobust_Maternal {

    /**
     * <p> Final qrs selection from the best channel selected.</p>
     *
     * @param iQRS Possible set of QRS locations.
     *             //     * @param iStartIndex First index of confirmed QRS in the iQRS array.
     * @return {aQRSFinal} : Return QRS selected and update Flags.
     * @throws Exception Message containing the exception.
     */
    public static double[] mqrsSelection(double[] iQRS, double[][] iQrsAmplitude, double[] iMeanCh) throws Exception {
        double[] aDiffArray = new double[iQRS.length];

        LinkedList<Double> aQrsFinal = new LinkedList<Double>();

        boolean aConfirmFlag = firstQrsCheck(aQrsFinal);


        for (int i = 0; i < iQRS.length - 1; i++) {
            aDiffArray[i] = iQRS[i + 1] - iQRS[i];
        }
        double iVarTh = SignalProcConstants.MQRS_VARIANCE_THRESHOLD;
        double iRRlowTh = SignalProcConstants.MQRS_RR_LOW_TH;
        double iRRhighTh = SignalProcConstants.MQRS_RR_HIGH_TH;
        double aRRDiff;
        double aRRMean, aRRLowTh, aRRHighTh;
        double iLastRRmean = SignalProcUtils.lastRRMeanMaternal;
        double aNoRR = 2; //SignalProcConstants.QRS_NO_RR_MEAN;
        double inewRRlowTh = 0;
        double inewRRhighTh = 0;
        int aStartInd = -1;
        double aRRmean = 0;

        if (iLastRRmean != 0) {
            inewRRlowTh = 1 / (1 / iLastRRmean + SignalProcConstants.QRS_RR_VAR_M);
            inewRRhighTh = 1 / (1 / iLastRRmean - SignalProcConstants.QRS_RR_VAR_M);
        } else if (iLastRRmean == 0) {
            inewRRlowTh = iRRlowTh;
            inewRRhighTh = iRRhighTh;
        }
        int aMinRRDiff0 = 0, aMinRRDiff1;
        int aIncrement1 = 0, aIncrement2 = 0;
        int aLengthQRS = iQRS.length;

        int aForwardIteration = 0;
        int aCountF = 0;
        int aCountI = 0;
        int aLen = iQRS.length;


        if (aLen > 3) {
            LinkedList<Double> aMissQrsIndex = new LinkedList<Double>();

            // adding First 2 QRS locations to QRSFinal
            aQrsFinal.add(iQRS[aForwardIteration]);
            aQrsFinal.add(iQRS[aForwardIteration + 1]);
            aForwardIteration = aForwardIteration + 2;
            aCountF = 2;
            int aMinCheckFlag = 0;
            int aFindFlag = 0;

            LinkedList<Double> aRRMeanArr = new LinkedList<>();
            aRRMeanArr.add((aQrsFinal.get(aCountF - 1) - aQrsFinal.get(aCountF - 2)));

            while (aForwardIteration < aLengthQRS) {
                aCountI = aCountF;

                if (aRRMeanArr.size() <= aNoRR) {
                    aRRMean = 0;
                    for (int it = 0; it < aRRMeanArr.size(); it++) {
                        aRRMean = aRRMean + aRRMeanArr.get(it);
                    }
                    aRRMean = aRRMean / aRRMeanArr.size();
                } else {
                    aRRMean = 0;
                    for (int it = aRRMeanArr.size() - 1; it >= aRRMeanArr.size() - aNoRR; it--) {
                        aRRMean = aRRMean + aRRMeanArr.get(it);
                    }
                    aRRMean = aRRMean / aNoRR;
                }

                aRRDiff = iQRS[aForwardIteration] - aQrsFinal.get(aCountF - 1);
//                if(SignalProcUtils.lastQRSFetal == 0 || SignalProcUtils.lastRRMeanFetal == 0){
                aRRLowTh = 1 / (1 / aRRMean + SignalProcConstants.QRS_RR_VAR_M);
                aRRHighTh = 1 / (1 / aRRMean - SignalProcConstants.QRS_RR_VAR_M);
                if (aRRLowTh < SignalProcConstants.MQRS_RR_LOW_TH) {
                    aRRLowTh = SignalProcConstants.MQRS_RR_LOW_TH;
                }
                if (aRRHighTh > SignalProcConstants.MQRS_RR_HIGH_TH) {
                    aRRHighTh = SignalProcConstants.MQRS_RR_HIGH_TH;
                }
//                }else{
//                    aRRLowTh = 1 / (1 / SignalProcUtils.lastvalidRRMeanFetal + aDelta1);
//                    aRRHighTh = 1 / (1 / SignalProcUtils.lastvalidRRMeanFetal - aDelta1);
//                }


                if (aRRDiff < aRRLowTh) {
                    aForwardIteration++;
                } else if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
                    aMinRRDiff0 = 10000;
                    aMinCheckFlag = 1;
                } else {
                    aFindFlag = 0;
                    aIncrement1 = aForwardIteration;
                    aIncrement2 = aForwardIteration + 1;
                    if (aIncrement2 >= (aLengthQRS - 1)) {
                        aForwardIteration = aIncrement2;
                        aFindFlag = 1;
                    }

                    while (aFindFlag == 0) {

                        if ((iQRS[aIncrement1] - aQrsFinal.getLast()) >= (aRRLowTh + aRRMean)) {

                            aRRDiff = iQRS[aIncrement2] - iQRS[aIncrement1];
                            if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
                                aMinRRDiff0 = 10000;
                                aFindFlag = 1;
                            } else if (aRRDiff < aRRLowTh) {
                                aIncrement2++;
                                if (aIncrement2 >= (aLengthQRS - 1)) {
                                    aForwardIteration = aIncrement2;
                                    aFindFlag = 1;
                                }
                            } else if (aRRDiff > aRRHighTh) {
                                aIncrement1++;
                                if (aIncrement2 == aIncrement1) {
                                    aIncrement2++;
                                    if (aIncrement2 >= (aLengthQRS - 1)) {
                                        aForwardIteration = aIncrement2;
                                        aFindFlag = 1;
                                    }
                                }
                            }
                        } else {
                            aIncrement1++;
                            if (aIncrement2 == aIncrement1) {
                                aIncrement2++;
                                if (aIncrement2 >= (aLengthQRS - 1)) {
                                    aForwardIteration = aIncrement2;
                                    aFindFlag = 1;
                                }
                            }
                        }
                    }

                } // END of finding if QRS is to be selected.

                if (aMinCheckFlag == 1) {
                    LinkedList<Double> aTemp = new LinkedList<Double>();
                    for (int i = aForwardIteration; i < aLengthQRS; i++) {

                        if (iQRS[i] <= aQrsFinal.get(aCountF - 1) + aRRHighTh) {
                            aTemp.add(iQRS[i]);
                        } else if (iQRS[i] > iQRS[aForwardIteration] + aRRHighTh) {
                            break;
                        }
                    }
                    double aTempQRS = 0;
                    int aShift = 0;
                    for (int j = 0; j < aTemp.size(); j++) {
                        aRRDiff = aTemp.get(j) - aQrsFinal.get(aCountF - 1);

                        if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
                            aMinRRDiff1 = (int) Math.abs(aRRDiff - aRRMean);
                            if (aMinRRDiff1 < aMinRRDiff0) {
                                aTempQRS = aTemp.get(j);
                                aShift = aForwardIteration + j + 1;
                                aMinRRDiff0 = aMinRRDiff1;
                            }
                        }
                    }
                    aQrsFinal.add(aTempQRS);
                    aMinCheckFlag = 0;
                    aCountF++;
                    aRRMeanArr.add(aQrsFinal.get(aCountF - 1) - aQrsFinal.get(aCountF - 2));
                    aForwardIteration = aShift;
                }
            }


            return null;
        }
        return null;
    }
    public boolean QrsOverlapCheck(LinkedList<Double> iQrsFinal) {


        List<Double> iQRSlist = new ArrayList<>();

        for (int i = 0; i < iQrsFinal.size(); i++) {
            while (iQrsFinal.get(i) < 5000) {
                iQRSlist.add(iQrsFinal.get(i));
                break;
            }
        }
//        for (int i : iQrsFinal) {
//            iQRSlist.add(i);
//        }
        double th = 40, lowTH, highTH, overlapCount = 0;

        for (int lastQRSM : SignalProcUtils.lastQRSMaternalArray) {
            for (int i = 0; i < iQRSlist.size(); i++) {
                if(iQRSlist.get(i) - th < 0){
                    lowTH = 0;
                    highTH = iQrsFinal.get(i) + th;
                }else if(iQRSlist.get(i) + th > 15000){
                    lowTH = iQrsFinal.get(i) - th;
                    highTH = 15000;
                }
                else{
                    lowTH = iQRSlist.get(i) - th;
                    highTH = iQRSlist.get(i) + th;
                }
                if ((lastQRSM >= lowTH && lastQRSM <= highTH)) {
                    iQRSlist.remove(i);
                    overlapCount++;
                }
            }
        }
        return iQRSlist.size() <= 3 || overlapCount > 1;
    }

    public static boolean firstQrsCheck(LinkedList<Double> iQrsFinal) {
//        LinkedList<Integer> iQrsListnew = new LinkedList<>();
//        if(iQrsFinal.size() > 0){
//            iQrsListnew.addAll(iQrsFinal);
//        }else{
//            iQrsListnew.add(0);
//            iQrsFinal.add(0);
//        }
        double iRRMeanLast = SignalProcUtils.lastRRMeanMaternal;
        double iQRSLast = SignalProcUtils.lastQRSMaternalArray.indexOf(SignalProcUtils.lastQRSMaternalArray.size());

        int i = 0;

        while (iQrsFinal.get(i) < 2000) {
            iQrsFinal.remove(i);
            if (iQrsFinal.size() <= 0) {
                break;
            }
        }

        double aDelta = SignalProcConstants.QRS_RR_VAR_M;
//        double aDelta1 = SignalProcConstants.QRS_RR_VAR_Continuous;


        double aRRLowTh = 1 / (1 / iRRMeanLast + aDelta);
        double aRRHighTh = 1 / (1 / iRRMeanLast - aDelta);

        double aRRDiff;
        boolean aCheckFlag = false;

        // Check first qrs location
        if (iQrsFinal.size() > 0) {
            while (iQrsFinal.get(i) - iQRSLast <= aRRHighTh) {
                aRRDiff = iQrsFinal.get(i) - iQRSLast;

                if (aRRDiff < aRRLowTh) {
                    iQrsFinal.remove(i);
                } else if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
                    aCheckFlag = true;
                    i++;
                    if (i >= iQrsFinal.size()) {
                        break;
                    }
                }
            }
        }
        // Check RR value range
        if (aCheckFlag) {
            aRRDiff = iQrsFinal.get(1) - iQrsFinal.get(0);
            aCheckFlag = aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh;
        } else {
            if (iQrsFinal.size() > 1) {
                double aRRLowThnew = 1 / (1 / SignalProcUtils.lastvalidRRMeanFetal + aDelta);
                double aRRHighThnew = 1 / (1 / SignalProcUtils.lastvalidRRMeanFetal - aDelta);
                aRRDiff = (iQrsFinal.get(1) - iQrsFinal.get((0)));
                aCheckFlag = aRRDiff >= aRRLowThnew && aRRDiff <= aRRHighThnew;
            }
        }

        return aCheckFlag;
    }
}
