package SignalProc;

import java.util.LinkedList;

//import timber.log.Timber;

/**
 * <p> Dynamic fetal heart rate detection. </p>
 * <p> Change Logs :</p>
 * <ul>
 *     <li><pre> 17th August, 2017
 *  Updated the HeartRate finding Algorithm.</pre>
 *     		<ol>
 *     		 	<li> Last RRmean check.</li>
 *     		 	<li> RRmean calculated in case of a missed peak.</li>
 *     		 	<li> Consider all the QRS peaks selected for finding HR from 2-15s in the iteration.</li>
 *     		 	<li><pre> Added a Flag to check if new iteration of FHR is started,
 *  in case of discontinuity in previous iteration.	</pre></li>
 *     		 	<li><pre> Check if Last QRS of previous iteration and First QRS of current iteration has difference more than 2sec.</pre></li>
 *     		</ol>
 *     	</li>
 *
 *		<li> 23rd June, 2017
 *			<ol>
 *			 	<li><pre> Added Condition in case first iteration no Peaks detected :
 *	({@link SignalProcUtils}.currentIteration - SignalProcConstants.lastQRSFIteration) == 1.	</pre> </li>
 *			</ol>
 *		</li>
 *		<li> 19th June, 2017
 *		    <ol>
 *		        <li><pre> Added a condition to compute HR, in case of peaks missed in previous iteration.</pre></li>
 *		    </ol>
 *		</li>
 *		<li> 24th May, 2017
 *		    <ol>
 *		        <li> First commit.</li>
 *		    </ol>
 *		</li>
 * </ul>
 *
 * @author Kishore Subramanian (kishore@sattvamedtech.com)
 *
 */
public class HeartRateFetal {
	// Changed the condition of Miss percentage

	/**
	 * Find fetal heart rate.
	 * @param iQRS Fetal QRS selected in current iteration.
//	 * @param iNoQrsRemoved No of fetal QRS removed that are less than {@link SignalProcConstants#QRS_START_VALUE start value} .
	 */
	public void heartRate(int[] iQRS) {

        if(SignalProcUtils.currentIteration == 41){
            SignalProcUtils.currentIteration = 41;
        }
        int[] adiffarray = new int[iQRS.length];

        for (int i = 0; i < iQRS.length-1; i++) {
            adiffarray[i] = iQRS[i+1]-iQRS[i];
        }

		SignalProcUtils.qrsfLocTemp = new LinkedList<>();
		SignalProcUtils.hrfTemp = new LinkedList<>();


        MatrixFunctions aMatrixFunctions = new MatrixFunctions();

        int aLengthQrs = iQRS.length;

        int aStartLoc = aMatrixFunctions.findVarianceMinLocation(iQRS, (int) SignalProcConstants.QRS_NO_RR_MEAN);
        LinkedList<Integer> aRRDiffArr = new LinkedList<>();

        if (aStartLoc > -1) {
            SignalProcUtils.lastRRMeanFetal = 0;
            int aLengthRR = -1;
            double aRRMean = 0;


            for (int i = aStartLoc; i < (aStartLoc + (int) SignalProcConstants.QRS_NO_RR_MEAN); i++) {
                aRRDiffArr.add(iQRS[i+1] - iQRS[i]);
                aLengthRR++;
                aRRMean = aRRMean + aRRDiffArr.getLast();
            }
            aRRMean = aRRMean / SignalProcConstants.QRS_NO_RR_MEAN ;
            SignalProcUtils.qrsfLocTemp.add(iQRS[aStartLoc +(int) SignalProcConstants.QRS_NO_RR_MEAN ]);
            SignalProcUtils.hrfTemp.add((float) (60 * SignalProcConstants.FS / aRRMean));

            int aRRDiff = 0;
            double aRRLowTh = 0;
            double aRRHighTh = 0;
            double aDelta = SignalProcConstants.QRS_RR_VAR;
            aRRLowTh = 1 / (1 / aRRMean + aDelta);
            aRRHighTh = 1 / (1 / aRRMean - aDelta);

            // Forward Iteration for FHR
            for (int i = aStartLoc +(int) SignalProcConstants.QRS_NO_RR_MEAN + 1; i < (aLengthQrs); i++) {


                aRRDiff = iQRS[i] - iQRS[i-1];


                if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
                    aRRDiffArr.add(aRRDiff);
                    aLengthRR++;
                    aRRMean = 0;
                    for (int j = 0; j < (int) SignalProcConstants.QRS_NO_RR_MEAN; j++) {
                        aRRMean += aRRDiffArr.get(aLengthRR - j);
                    }
                    aRRMean = aRRMean / SignalProcConstants.QRS_NO_RR_MEAN;
                    SignalProcUtils.qrsfLocTemp.add(iQRS[i]);
                    SignalProcUtils.hrfTemp.add((float) (60 * SignalProcConstants.FS / aRRMean));


                    if (SignalProcUtils.qrsfLocTemp.getLast() >= SignalProcConstants.QRS_END_VALUE && SignalProcUtils.qrsfLocTemp.get(SignalProcUtils.qrsfLocTemp.size()-2) < SignalProcConstants.QRS_END_VALUE) {
                        SignalProcUtils.lastRRMeanFetal = aRRMean;
                    }

                }



            }

            // Backward Iteration for FHR
            if (aStartLoc > (int) SignalProcConstants.QRS_NO_RR_MEAN) {
                // Change Aravind
                for (int i = aStartLoc; i >= 1 ; i--) {
                    aRRDiff = iQRS[aStartLoc] - iQRS[aStartLoc - 1];
                    if(aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh){

                        aRRMean = 0;
                        aRRDiffArr.addFirst( (iQRS[aStartLoc] - iQRS[aStartLoc - 1]));
                        aRRMean += aRRDiffArr.getFirst();
                        for (int j = 1; j < (int) SignalProcConstants.QRS_NO_RR_MEAN; j++) {
                            aRRMean += aRRDiffArr.get(j);
                        }

                        aRRMean = aRRMean / SignalProcConstants.QRS_NO_RR_MEAN;
                        SignalProcUtils.qrsfLocTemp.addFirst(iQRS[aStartLoc + (int) SignalProcConstants.QRS_NO_RR_MEAN - 1]);
                        SignalProcUtils.hrfTemp.addFirst((float) (60 * SignalProcConstants.FS / aRRMean));
                        break;
                    } else{
                        aStartLoc = aStartLoc-1;
                    }
                }


                // add first value

            }
            int aNoOfRR = (int) SignalProcConstants.QRS_NO_RR_MEAN;
            for (int i = aStartLoc - 1; i >= 1; i--) {


                aRRDiff = iQRS[i] - iQRS[i-1];
                aRRLowTh = 1 / (1 / aRRMean + aDelta);
                aRRHighTh = 1 / (1 / aRRMean - aDelta);

                if(aRRLowTh < SignalProcConstants.FQRS_RR_LOW_TH){
                    aRRLowTh = SignalProcConstants.FQRS_RR_LOW_TH;
                }
                if(aRRHighTh > SignalProcConstants.FQRS_RR_HIGH_TH){
                    aRRHighTh = SignalProcConstants.FQRS_RR_HIGH_TH;
                }

                if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
                    aRRDiffArr.addFirst(aRRDiff);
                    aRRMean = 0;
                    if (i < (int) SignalProcConstants.QRS_NO_RR_MEAN) {
                        aNoOfRR = i;
                    }
                    for (int j = 0; j < aNoOfRR; j++) {
                        aRRMean += aRRDiffArr.get(j);
                    }
                    aRRMean = aRRMean / aNoOfRR;
                    if (aRRMean == 0){
                        break;
                    }
                    SignalProcUtils.qrsfLocTemp.addFirst(iQRS[i + (int) SignalProcConstants.QRS_NO_RR_MEAN -1]);
                    SignalProcUtils.hrfTemp.addFirst((float) (60 * SignalProcConstants.FS / aRRMean));

                    if (SignalProcUtils.qrsfLocTemp.get(1) >= SignalProcConstants.QRS_END_VALUE && SignalProcUtils.qrsfLocTemp.getFirst() < SignalProcConstants.QRS_END_VALUE) {
                        SignalProcUtils.lastRRMeanFetal = aRRMean;
                    }
                }

            }

            // Calculate HR for first 3 values

            for (int i = (int) SignalProcConstants.QRS_NO_RR_MEAN-1 ; i >= 1  ; i--) {
                aRRMean = 0;
                for (int j = 0; j < i; j++) {
                    aRRMean += aRRDiffArr.get(j);
                }
                aRRMean = aRRMean / i;

                SignalProcUtils.qrsfLocTemp.addFirst(iQRS[i]);
                SignalProcUtils.hrfTemp.addFirst((float) (60 * SignalProcConstants.FS / aRRMean));

                if (SignalProcUtils.qrsfLocTemp.get(1) >= SignalProcConstants.QRS_END_VALUE && SignalProcUtils.qrsfLocTemp.getFirst() < SignalProcConstants.QRS_END_VALUE) {
                    SignalProcUtils.lastRRMeanFetal = aRRMean;
                }


            }

            int aQrsTemp = 0;
            float aHrTemp = 0;
            if (SignalProcUtils.qrsfLocTemp.size() > 0) {
                while (SignalProcUtils.qrsfLocTemp.getLast() > SignalProcConstants.QRS_END_VALUE - SignalProcConstants.DIFFERENCE_SAMPLES) {
                    aQrsTemp = SignalProcUtils.qrsfLocTemp.removeLast();
                    aHrTemp = SignalProcUtils.hrfTemp.removeLast();
                    if (SignalProcUtils.qrsfLocTemp.size() == 0) {
                        break;
                    }
                }

                if (aQrsTemp != 0) {
                    SignalProcUtils.qrsfLocTemp.add(aQrsTemp);
                    SignalProcUtils.hrfTemp.add(aHrTemp);
                }


                aQrsTemp = 0;

                while (SignalProcUtils.qrsfLocTemp.getFirst() < SignalProcConstants.QRS_START_VALUE) {
                    aQrsTemp = SignalProcUtils.qrsfLocTemp.removeFirst();
                    aHrTemp = SignalProcUtils.hrfTemp.removeFirst();
                    if (SignalProcUtils.qrsfLocTemp.size() == 0) {
                        break;
                    }
                }

                if (aQrsTemp != 0) {
                    SignalProcUtils.qrsfLocTemp.addFirst(aQrsTemp);
                    SignalProcUtils.hrfTemp.addFirst(aHrTemp);
                }
            }

            if (SignalProcUtils.lastRRMeanFetal == 0) {
                aRRMean = 0;
                for (int i = aRRDiffArr.size() - 1; i >= (aRRDiffArr.size () -  (int) SignalProcConstants.QRS_NO_RR_MEAN); i--) {
                    aRRMean += aRRDiffArr.get(i);
                }
                SignalProcUtils.lastRRMeanFetal = aRRMean / (int) SignalProcConstants.QRS_NO_RR_MEAN ;
            }



        }

//		LinkedList<Integer> aRRDiffArr = new LinkedList<>();
//		LinkedList<Integer> aRRDiffArrFinal = new LinkedList<>();
//		int aLenFinalArr;
//		for (int i =1; i<iQRS.size(); i++){
//			aRRDiffArr.add( iQRS.get(i) - iQRS.get(i-1));
//		}
//
//
//
//
//
//
//
//
//
//
//
//
//		if (SignalProcUtils.currentIteration == (SignalProcUtils.lastQRSFIteration + 1) && SignalProcUtils.qrsFetalLocation.size() > 4)
//		{
//
//			if (SignalProcUtils.lastFetalPlotIndex -5 > SignalProcUtils.qrsFetalMissLocation){
//				aRRDiffArr.addFirst(iQRS.getFirst() - SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.lastFetalPlotIndex -1));
//				if (aRRDiffArr.getFirst() < SignalProcConstants.QRS_LENGTH_MAX_INTERPOLATE){
//					aRRDiffArrFinal.addFirst(SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.lastFetalPlotIndex -1) - SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.lastFetalPlotIndex -2));
//					aRRDiffArrFinal.addFirst(SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.lastFetalPlotIndex -2) - SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.lastFetalPlotIndex -3));
//					aRRDiffArrFinal.addFirst(SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.lastFetalPlotIndex -3) - SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.lastFetalPlotIndex -4));
//					aRRDiffArrFinal.addFirst(SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.lastFetalPlotIndex -4) - SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.lastFetalPlotIndex -5));
//				}
//				else {
//					aRRDiffArr.removeFirst();
//				}
//			}
//			else if ( (SignalProcUtils.lastFetalPlotIndex -1) > SignalProcUtils.qrsFetalMissLocation)
//			{
//				aRRDiffArr.addFirst(iQRS.getFirst() - SignalProcUtils.qrsFetalLocation.get(SignalProcUtils.lastFetalPlotIndex -1));
//				if (aRRDiffArr.getFirst() < SignalProcConstants.QRS_LENGTH_MAX_INTERPOLATE){
//					for (int i = SignalProcUtils.lastFetalPlotIndex -1; i> SignalProcUtils.qrsFetalMissLocation; i--){
//						aRRDiffArrFinal.addFirst(SignalProcUtils.qrsFetalLocation.get(i) - SignalProcUtils.qrsFetalLocation.get(i-1));
//					}
//				}
//				else {
//					aRRDiffArr.removeFirst();
//				}
//			}
//
//		}
//		if (SignalProcUtils.fqrsMissIndex.size()>0){
//			for (int z = 0; z< SignalProcUtils.fqrsMissIndex.size(); z++){
//				SignalProcUtils.fqrsMissIndex.set(z, SignalProcUtils.fqrsMissIndex.get(z)-iNoQrsRemoved);
//				if (SignalProcUtils.fqrsMissIndex.get(z)-iNoQrsRemoved < 0){
//					SignalProcUtils.fqrsMissIndex.remove(z);
//					z = z-1;
//				}
//			}
//		}
//
//		int aCounter = SignalProcUtils.qrsFetalLocation.size() ;
//		int aCounter1 = 0;
//		LinkedList<Integer> aMissLoc = new LinkedList<>();
//		double aRRMeanCurrent, aRRMeanPrevious =0, aRRMean, aRRLowTh, aRRHighTh;
//
//		if (SignalProcUtils.currentIteration != (SignalProcUtils.lastQRSFIteration +1) || aRRDiffArrFinal.size() == 0) { // removed (  || SignalProcConstants.noDetectionFlagFetal == 1 )
//			SignalProcUtils.fetalHrNew = 1;
//			LinkedList<Integer> aRRMeanArr = new LinkedList<>();
//			int aInitialShift = (int) SignalProcConstants.QRS_NO_RR_MEAN;
//
//			if (SignalProcUtils.fqrsMissIndex.size() == 1){
//				aInitialShift = SignalProcUtils.fqrsMissIndex.removeFirst();
//				if (aInitialShift < (int) SignalProcConstants.QRS_NO_RR_MEAN){
//					aInitialShift = aInitialShift + (int) SignalProcConstants.QRS_NO_RR_MEAN;
//				}
//				else {
//					aInitialShift = (int) SignalProcConstants.QRS_NO_RR_MEAN;
//				}
//			}
////			if (aInitialShift > (int) SignalProcConstants.QRS_NO_RR_MEAN){
////				aInitialShift = (int) SignalProcConstants.QRS_NO_RR_MEAN;
////			}
//			if (SignalProcUtils.fqrsMissIndex.size() >1){
//				if (SignalProcUtils.fqrsMissIndex.get(0) > (int) SignalProcConstants.QRS_NO_RR_MEAN){
//					aInitialShift = (int) SignalProcConstants.QRS_NO_RR_MEAN;
//				}
//				else {
//					for (int i = 1; i< SignalProcUtils.fqrsMissIndex.size(); i++){
//						if (SignalProcUtils.fqrsMissIndex.get(i) - SignalProcUtils.fqrsMissIndex.get(i-1) > (int) SignalProcConstants.QRS_NO_RR_MEAN){
//							aInitialShift = SignalProcUtils.fqrsMissIndex.get(i-1) + (int) SignalProcConstants.QRS_NO_RR_MEAN;
//							break;
//						}
//					}
//				}
//
//			}
//            Filename.ExecutionLogs.append("HeartRateFetal : Initial Shift : "+aInitialShift);
//
////			Timber.i("aInitialShift : " + aInitialShift);
//			for (int k = 0; k< (int) SignalProcConstants.QRS_NO_RR_MEAN; k++){
//				aRRMeanArr.add(aRRDiffArr.get(aInitialShift-1-k));
//			}
//
//			for (int f = aInitialShift; f < iQRS.size(); f++) {
//
//				aRRMeanCurrent = 0;
//				for (int it = aRRMeanArr.size() - 1; it >= aRRMeanArr.size() - (int) SignalProcConstants.QRS_NO_RR_MEAN; it--) {
//					aRRMeanCurrent = aRRMeanCurrent + aRRMeanArr.get(it);
//				}
//				aRRMeanCurrent = aRRMeanCurrent / (int) SignalProcConstants.QRS_NO_RR_MEAN;
//
//				if (f == aInitialShift){
//					SignalProcUtils.qrsfLocTemp.add(iQRS.get(f));
//					SignalProcUtils.hrfTemp.add((float) (60 * SignalProcConstants.FS / aRRMeanCurrent));
//					aCounter1++;
//					aRRMeanArr.add(aRRDiffArr.get(f-1));
//					aRRMeanPrevious = aRRMeanCurrent;
//
//				}
//				else if ( aRRDiffArr.get(f-1) >= (1/ ((1/aRRMeanPrevious) + SignalProcConstants.QRS_RR_VAR) ) && aRRDiffArr.get(f-1) <= (1/ ((1/aRRMeanPrevious)- SignalProcConstants.QRS_RR_VAR) ) ){
//					SignalProcUtils.qrsfLocTemp.add(iQRS.get(f));
//					SignalProcUtils.hrfTemp.add((float) (60 * SignalProcConstants.FS / aRRMeanCurrent));
//					aCounter1++;
//					aRRMeanArr.add(aRRDiffArr.get(f-1));
//					aRRMeanPrevious = aRRMeanCurrent;
//				}
//				else {
//					aMissLoc.add(aCounter1);
//				}
//				SignalProcUtils.lastRRMeanFetal = aRRMeanCurrent;
//
//			}
//		} else {
//			SignalProcUtils.fetalHrNew = 0;
//
//			for (int f = 0; f < iQRS.size(); f++) {
//				aLenFinalArr = aRRDiffArrFinal.size();
//				if (aLenFinalArr >= (int) SignalProcConstants.QRS_NO_RR_MEAN){
//					aRRMean = ( aRRDiffArrFinal.getLast() + aRRDiffArrFinal.get(aLenFinalArr-2) + aRRDiffArrFinal.get(aLenFinalArr-3)+ aRRDiffArrFinal.get(aLenFinalArr-(int) SignalProcConstants.QRS_NO_RR_MEAN))/ SignalProcConstants.QRS_NO_RR_MEAN;
//				}
//				else {
//					aRRMean = 0;
//					for (int i = 0; i <aLenFinalArr; i++){
//						aRRMean = aRRMean + aRRDiffArrFinal.get(i);
//					}
//					aRRMean = aRRMean / aLenFinalArr;
//				}
//				aRRLowTh = 1/(1/aRRMean + SignalProcConstants.QRS_RR_VAR);
//				aRRHighTh = 1/(1/aRRMean - SignalProcConstants.QRS_RR_VAR);
//
//				if (aRRDiffArr.get(f) >= aRRLowTh && aRRDiffArr.get(f) <= aRRHighTh){
//					aRRDiffArrFinal.add(aRRDiffArr.get(f));
//
//					if ((aLenFinalArr) >= (int) SignalProcConstants.QRS_NO_RR_MEAN){
//						aRRMean = ( aRRDiffArrFinal.getLast() + aRRDiffArrFinal.get(aLenFinalArr-2) + aRRDiffArrFinal.get(aLenFinalArr-3)+ aRRDiffArrFinal.get(aLenFinalArr-(int) SignalProcConstants.QRS_NO_RR_MEAN))/ SignalProcConstants.QRS_NO_RR_MEAN;
//					}
//					else {
//						aRRMean = 0;
//						for (int i = 0; i <aLenFinalArr+1; i++){
//							aRRMean = aRRMean + aRRDiffArrFinal.get(i);
//						}
//						aRRMean = aRRMean / aLenFinalArr;
//					}
//					SignalProcUtils.qrsfLocTemp.add(iQRS.get(f));
//					SignalProcUtils.hrfTemp.add((float) (60 * SignalProcConstants.FS / aRRMean));
//					aCounter1++;
//
//				}
//				else {
//					aMissLoc.add(aCounter1);
//				}
//				SignalProcUtils.lastRRMeanFetal = aRRMean;
//
//			}
//		}
//
//		// Check if TEMP_LOC has greater than 12000
//
//		int i = SignalProcUtils.qrsfLocTemp.size();
//		if (i > 0){
//			while ( (SignalProcUtils.qrsfLocTemp.get(i-1) >= SignalProcUtils.currentIteration *10000 + 12000 +SignalProcUtils.dataLossCounter) ){
//
//				i--;
//				if (i == 0){
//					break;
//				}
//			}
//		}
//		if (i+1 < SignalProcUtils.qrsfLocTemp.size()){
//			for (i = i+1; i< SignalProcUtils.qrsfLocTemp.size() ; i++){
//				SignalProcUtils.qrsfLocTemp.remove(i);
//				SignalProcUtils.hrfTemp.remove(i);
//				i = i-1;
//			}
//			SignalProcUtils.fetalHrEnd = 1;
//		}
//		else {
//			SignalProcUtils.fetalHrEnd = 0;
//		}
//
//		int aLen = SignalProcUtils.qrsfLocTemp.size();
//		for (i = 0; i<aMissLoc.size(); i++){
//			if (aMissLoc.get(i) == aLen){
//				aMissLoc.set(i, aMissLoc.get(i)-1);
//			}
//			if (aMissLoc.get(i) >aLen){
//				aMissLoc.remove(i);
//			}
//
//		}
//		if (aMissLoc.size() > 0){
//			SignalProcUtils.qrsFetalMissLocation = aMissLoc.getLast() + aCounter ;
//		}

	}

}
