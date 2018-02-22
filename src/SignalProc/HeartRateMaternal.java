package SignalProc;
import java.util.LinkedList;


public class HeartRateMaternal {

	/**
	 * Find maternal heart rate.
	 * @param iQRS Maternal QRS locations in current iteration.
	 */
	public void heartRate(int[] iQRS) {

	    MatrixFunctions aMatrixFunctions = new MatrixFunctions();

		SignalProcUtils.qrsmLocTemp = new LinkedList<>();
		SignalProcUtils.hrmTemp = new LinkedList<>();

		int aLengthQrs = iQRS.length;

		int aStartLoc = aMatrixFunctions.findVarianceMinLocation(iQRS, (int) SignalProcConstants.QRS_NO_RR_MEAN);
        LinkedList<Integer> aRRDiffArr = new LinkedList<>();

        if (aStartLoc > -1) {
            int aLengthRR = -1;
            double aRRMean = 0;

            for (int i = aStartLoc; i < (aStartLoc + (int) SignalProcConstants.QRS_NO_RR_MEAN); i++) {
                aRRDiffArr.add(iQRS[i+1] - iQRS[i]);
                aLengthRR++;
                aRRMean = aRRMean + aRRDiffArr.getLast();
            }
            aRRMean = aRRMean / SignalProcConstants.QRS_NO_RR_MEAN ;
            SignalProcUtils.qrsmLocTemp.add(iQRS[aStartLoc +(int) SignalProcConstants.QRS_NO_RR_MEAN ]);
            SignalProcUtils.hrmTemp.add((float) (60 * SignalProcConstants.FS / aRRMean));

            int aRRDiff = 0;
            double aRRLowTh = 0;
            double aRRHighTh = 0;
            double aDelta = SignalProcConstants.QRS_RR_VAR;

            // Forward Iteration for MHR
            for (int i = aStartLoc +(int) SignalProcConstants.QRS_NO_RR_MEAN + 1; i < (aLengthQrs ); i++) {


                aRRDiff = iQRS[i] - iQRS[i-1];
                aRRLowTh = 1 / (1 / aRRMean + aDelta);
                aRRHighTh = 1 / (1 / aRRMean - aDelta);

                if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
                    aRRDiffArr.add(aRRDiff);
                    aLengthRR++;
                    aRRMean = 0;
                    for (int j = 0; j < (int) SignalProcConstants.QRS_NO_RR_MEAN; j++) {
                        aRRMean += aRRDiffArr.get(aLengthRR - j);
                    }
                    aRRMean = aRRMean / SignalProcConstants.QRS_NO_RR_MEAN;
                    SignalProcUtils.qrsmLocTemp.add(iQRS[i]);
                    SignalProcUtils.hrmTemp.add((float) (60 * SignalProcConstants.FS / aRRMean));


                }


            }

            // Backward Iteration for MHR
            aRRMean = 0;
            if (aStartLoc > (int) SignalProcConstants.QRS_NO_RR_MEAN) {
                // add first value
                aRRDiffArr.addFirst( (iQRS[aStartLoc] - iQRS[aStartLoc - 1]));
                aRRMean += aRRDiffArr.getFirst();
                for (int i = 1; i < (int) SignalProcConstants.QRS_NO_RR_MEAN; i++) {
                    aRRMean += aRRDiffArr.get(i);
                }
                aRRMean = aRRMean / SignalProcConstants.QRS_NO_RR_MEAN ;
                SignalProcUtils.qrsmLocTemp.addFirst(iQRS[aStartLoc + (int) SignalProcConstants.QRS_NO_RR_MEAN - 1]);
                SignalProcUtils.hrmTemp.addFirst((float) (60 * SignalProcConstants.FS / aRRMean));

            }


            for (int i = aStartLoc - 1; i >= 1; i--) {


                aRRDiff = iQRS[i] - iQRS[i - 1];
                aRRLowTh = 1 / (1 / aRRMean + aDelta);
                aRRHighTh = 1 / (1 / aRRMean - aDelta);

                if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
                    aRRDiffArr.addFirst(aRRDiff);
                    aRRMean = 0;
                    for (int j = 0; j < (int) SignalProcConstants.QRS_NO_RR_MEAN; j++) {
                        aRRMean += aRRDiffArr.get(j);
                    }
                    aRRMean = aRRMean / (int) SignalProcConstants.QRS_NO_RR_MEAN;
                    SignalProcUtils.qrsmLocTemp.addFirst(iQRS[i + (int) SignalProcConstants.QRS_NO_RR_MEAN -1]);
                    SignalProcUtils.hrmTemp.addFirst((float) (60 * SignalProcConstants.FS / aRRMean));

                }

            }

            // Calculate HR for first 3 values

            for (int i = (int) SignalProcConstants.QRS_NO_RR_MEAN-1 ; i >= 1  ; i--) {
                aRRMean = 0;
                for (int j = 0; j < i; j++) {
                    aRRMean += aRRDiffArr.get(j);
                }
                aRRMean = aRRMean / i;

                SignalProcUtils.qrsmLocTemp.addFirst(iQRS[i]);
                SignalProcUtils.hrmTemp.addFirst((float) (60 * SignalProcConstants.FS / aRRMean));

            }

            int aQrsTemp = 0;
            float aHrTemp = 0;
            if (SignalProcUtils.qrsmLocTemp.size() > 0) {
                while (SignalProcUtils.qrsmLocTemp.getLast() > SignalProcConstants.QRS_END_VALUE - SignalProcConstants.DIFFERENCE_SAMPLES) {
                    aQrsTemp = SignalProcUtils.qrsmLocTemp.removeLast();
                    aHrTemp = SignalProcUtils.hrmTemp.removeLast();
                    if (SignalProcUtils.qrsmLocTemp.size() == 0) {
                        break;
                    }
                }

                if (aQrsTemp != 0) {
                    SignalProcUtils.qrsmLocTemp.add(aQrsTemp);
                    SignalProcUtils.hrmTemp.add(aHrTemp);
                }

                aQrsTemp = 0;

                while (SignalProcUtils.qrsmLocTemp.getFirst() < SignalProcConstants.QRS_START_VALUE) {
                    aQrsTemp = SignalProcUtils.qrsmLocTemp.removeFirst();
                    aHrTemp = SignalProcUtils.hrmTemp.removeFirst();
                    if (SignalProcUtils.qrsmLocTemp.size() == 0) {
                        break;
                    }
                }

                if (aQrsTemp != 0) {
                    SignalProcUtils.qrsmLocTemp.addFirst(aQrsTemp);
                    SignalProcUtils.hrmTemp.addFirst(aHrTemp);
                }
            }

        }









//		LinkedList<Integer> aRRDiffArrFinal = new LinkedList<>();
//
//
//		int aLenFinalArr;
//		for (int i =1; i<iQRS.size(); i++){
//			aRRDiffArr.add( iQRS.get(i) - iQRS.get(i-1));
//		}
//		if (SignalProcUtils.currentIteration == (SignalProcUtils.lastQRSMIteration + 1) && SignalProcUtils.qrsMaternalLocation.size() > 4)
//		{
//
//			if (SignalProcUtils.lastMaternalPlotIndex -5 > SignalProcUtils.qrsMaternalMissLocation){
//				aRRDiffArr.addFirst(iQRS.getFirst() - SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex -1));
//				if (aRRDiffArr.getFirst() < SignalProcConstants.QRS_LENGTH_MAX_INTERPOLATE){
//					aRRDiffArrFinal.addFirst(SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex -1) - SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex -2));
//					aRRDiffArrFinal.addFirst(SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex -2) - SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex -3));
//					aRRDiffArrFinal.addFirst(SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex -3) - SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex -4));
//					aRRDiffArrFinal.addFirst(SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex -4) - SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex -5));
//				}
//				else {
//					aRRDiffArr.removeFirst();
//				}
//			}
//			else if ( (SignalProcUtils.lastMaternalPlotIndex -1) > SignalProcUtils.qrsMaternalMissLocation)
//			{
//				aRRDiffArr.addFirst(iQRS.getFirst() - SignalProcUtils.qrsMaternalLocation.get(SignalProcUtils.lastMaternalPlotIndex -1));
//				if (aRRDiffArr.getFirst() < SignalProcConstants.QRS_LENGTH_MAX_INTERPOLATE){
//					for (int i = SignalProcUtils.lastMaternalPlotIndex -1; i> SignalProcUtils.qrsMaternalMissLocation; i--){
//						aRRDiffArrFinal.addFirst(SignalProcUtils.qrsMaternalLocation.get(i) - SignalProcUtils.qrsMaternalLocation.get(i-1));
//					}
//				}
//				else {
//					aRRDiffArr.removeFirst();
//				}
//			}
//
//		}
//
//		int aCounter = SignalProcUtils.qrsMaternalLocation.size() ;
//		int aCounter1 = 0;
//		LinkedList<Integer> aMissLoc = new LinkedList<>();
//		double aRRMeanCurrent, aRRMeanPrevious =0, aRRMean, aRRLowTh, aRRHighTh;
//		if (SignalProcUtils.currentIteration != (SignalProcUtils.lastQRSMIteration +1) || aRRDiffArrFinal.size() == 0) { // removed (  || SignalProcConstants.noDetectionFlagFetal == 1 )
//			SignalProcUtils.maternalHrNew = 1;
//			LinkedList<Integer> aRRMeanArr = new LinkedList<>();
//			int aInitialShift = (int) SignalProcConstants.QRS_NO_RR_MEAN;
//
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
//					SignalProcUtils.qrsmLocTemp.add(iQRS.get(f));
//					SignalProcUtils.hrmTemp.add((float) (60 * SignalProcConstants.FS / aRRMeanCurrent));
//					aCounter1++;
//					aRRMeanArr.add(aRRDiffArr.get(f-1));
//					aRRMeanPrevious = aRRMeanCurrent;
//
//				}
//				else if ( aRRDiffArr.get(f-1) >= (1/ ((1/aRRMeanPrevious) + SignalProcConstants.QRS_RR_VAR) ) && aRRDiffArr.get(f-1) <= (1/ ((1/aRRMeanPrevious)- SignalProcConstants.QRS_RR_VAR) ) ){
//					SignalProcUtils.qrsmLocTemp.add(iQRS.get(f));
//					SignalProcUtils.hrmTemp.add((float) (60 * SignalProcConstants.FS / aRRMeanCurrent));
//					aCounter1++;
//					aRRMeanArr.add(aRRDiffArr.get(f-1));
//					aRRMeanPrevious = aRRMeanCurrent;
//				}
//				else {
//					aMissLoc.add(aCounter1);
//				}
//
//			}
//		} else {
//			SignalProcUtils.maternalHrNew = 0;
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
//					if (aLenFinalArr >= (int) SignalProcConstants.QRS_NO_RR_MEAN){
//						aRRMean = ( aRRDiffArrFinal.getLast() + aRRDiffArrFinal.get(aLenFinalArr-2) + aRRDiffArrFinal.get(aLenFinalArr-3)+ aRRDiffArrFinal.get(aLenFinalArr-(int) SignalProcConstants.QRS_NO_RR_MEAN))/ SignalProcConstants.QRS_NO_RR_MEAN;
//					}
//					else {
//						aRRMean = 0;
//						for (int i = 0; i <aLenFinalArr; i++){
//							aRRMean = aRRMean + aRRDiffArrFinal.get(i);
//						}
//						aRRMean = aRRMean / aLenFinalArr;
//					}
//					SignalProcUtils.qrsmLocTemp.add(iQRS.get(f));
//					SignalProcUtils.hrmTemp.add((float) (60 * SignalProcConstants.FS / aRRMean));
//					aCounter1++;
//
//				}
//				else {
//					aMissLoc.add(aCounter1);
//				}
//
//			}
//		}
//
//		// Check if TEMP_LOC has greater than 12000
//
//		int i = SignalProcUtils.qrsmLocTemp.size();
//		if (i > 0){
//			while ( (SignalProcUtils.qrsmLocTemp.get(i-1) >= SignalProcUtils.currentIteration *10000 + 12000 + SignalProcUtils.dataLossCounter) ){
//
//				i--;
//				if (i == 0){
//					break;
//				}
//			}
//		}
//		if (i+1 < SignalProcUtils.qrsmLocTemp.size()){
//			for (i = i+1; i< SignalProcUtils.qrsmLocTemp.size() ; i++){
//				SignalProcUtils.qrsmLocTemp.remove(i);
//				SignalProcUtils.hrmTemp.remove(i);
//				i = i-1;
//			}
//			SignalProcUtils.maternalHrEnd = 1;
//		}
//		else {
//			SignalProcUtils.maternalHrEnd = 0;
//		}
//
//		int aLen = SignalProcUtils.qrsmLocTemp.size();
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
//			SignalProcUtils.qrsMaternalMissLocation = aMissLoc.getLast() + aCounter ;
//		}
//
	}

}
