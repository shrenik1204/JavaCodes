package SignalProc;

import java.util.LinkedList;


/**
 * <p> Functions to perform QRS selection.</p>
 * <p> Change Logs :</p>
 * <ul>
 *     <li> 21st August, 2017
 *     <ol>
 *         <li> First commit.</li>
 *     </ol>
 *
 *     	</li>
 * </ul>
 *
 * @author Kishore Subramanian (kishore@sattvamedtech.com)
 */
public class QrsSelectionFunctions {
	/**
	 * Object initialization of {@link MatrixFunctions}
	 */
	MatrixFunctions mMatrixFunctions = new MatrixFunctions();
	// CHanged RR LOw and High TH to 10 beat change
	// Changed Miss index to (aRRLowTh + aRRMean)

	/**
	 * <p> Forward QRS selection.</p>
	 * @param iQRS Possible set of QRS locations.
	 * @param iStartIndex First index of confirmed QRS in the iQRS array.
	 * @return {aQrsFinal, aMissQrsIndex} : Final QRS selected array, missed QRS location.
	 * @throws Exception If {@literal (iQRS.length < iStartIndex)}.
	 */
	public Object[] qrsForwardIteration(int[] iQRS, int iStartIndex) throws Exception{

		if (iQRS.length > (iStartIndex + 1)){

			int aRRDiff;
			double aRRMean, aRRLowTh, aRRHighTh;

			double aNoRR = SignalProcConstants.QRS_NO_RR_MEAN;
			// double aLowPerc = SignalProcConstants.QRS_RRLOW_PERC;
			// double aHighPerc = SignalProcConstants.QRS_RRHIGH_PERC;
			double aDelta = SignalProcConstants.QRS_RR_VAR;

			int aMinRRDiff0 = 0, aMinRRDiff1;
			int aIncrement1 = 0, aIncrement2 = 0;
			int aLengthQRS = iQRS.length;

			int aForwardIteration = 0;
			int aCountF = 0;
			int aCountI = 0;

			LinkedList<Integer> aQrsFinal = new LinkedList<Integer>();
			LinkedList<Integer> aMissQrsIndex = new LinkedList<Integer>();

			// adding First 2 QRS locations to QRSFinal
			aForwardIteration = iStartIndex;
			aQrsFinal.add(iQRS[aForwardIteration]);
			aQrsFinal.add(iQRS[aForwardIteration + 1]);
			aForwardIteration = aForwardIteration + 2;

			aCountF = 2;

			int aMinCheckFlag = 0;
			int aHarmonicCheckFlag = 0;

			int aFindFlag = 0;

			LinkedList<Double> aRRMeanArr = new LinkedList<>();
			aRRMeanArr.add((double) (aQrsFinal.get(aCountF - 1) - aQrsFinal.get(aCountF - 2)));

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
				aRRLowTh = 1 / (1 / aRRMean + aDelta);
				aRRHighTh = 1 / (1 / aRRMean - aDelta);

				if (aRRDiff < aRRLowTh) {
					aForwardIteration++;
				} else if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
					aMinRRDiff0 = 10000;
					aMinCheckFlag = 1;
				} else {
					aFindFlag = 0;
					aIncrement1 = aForwardIteration;
					aIncrement2 = aForwardIteration + 1;
					if (aIncrement2 >= (aLengthQRS-1)) {
						aForwardIteration = aIncrement2;
						aFindFlag = 1;
					}

					while (aFindFlag == 0) {

						if ((iQRS[aIncrement1] - aQrsFinal.getLast()) >= (aRRLowTh + aRRMean)) {

							aRRDiff = iQRS[aIncrement2] - iQRS[aIncrement1];
							if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
								aMinRRDiff0 = 10000;
								aHarmonicCheckFlag = 1;
								aFindFlag = 1;
							} else if (aRRDiff < aRRLowTh) {
								aIncrement2++;
								if (aIncrement2 >= (aLengthQRS-1)) {
									aForwardIteration = aIncrement2;
									aFindFlag = 1;
								}
							} else if (aRRDiff > aRRHighTh) {
								aIncrement1++;
								if (aIncrement2 == aIncrement1) {
									aIncrement2++;
									if (aIncrement2 >= (aLengthQRS-1)) {
										aForwardIteration = aIncrement2;
										aFindFlag = 1;
									}
								}
							}
						} else {
							aIncrement1++;
							if (aIncrement2 == aIncrement1) {
								aIncrement2++;
								if (aIncrement2 >= (aLengthQRS-1)) {
									aForwardIteration = aIncrement2;
									aFindFlag = 1;
								}
							}
						}
					}

				} // END of finding if QRS is to be selected.

				if (aMinCheckFlag == 1) {
					LinkedList<Integer> aTemp = new LinkedList<Integer>();
					for (int i = aForwardIteration; i < aLengthQRS; i++) {

						if (iQRS[i] <= aQrsFinal.get(aCountF - 1) + aRRHighTh) {
							aTemp.add(iQRS[i]);
						} else if (iQRS[i] > iQRS[aForwardIteration] + aRRHighTh) {
							break;
						}
					}
					int aTempQRS = 0;
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
					aRRMeanArr.add((double) (aQrsFinal.get(aCountF - 1) - aQrsFinal.get(aCountF - 2)));
					aForwardIteration = aShift;
				} // End addition of next qrs location

				if (aHarmonicCheckFlag == 1) {
					LinkedList<Integer> aTemp1 = new LinkedList<Integer>();
					LinkedList<Integer> aTemp2 = new LinkedList<Integer>();

					for (int i = aIncrement1; i < aLengthQRS; i++) {
						if (iQRS[i] <= iQRS[aIncrement1] + aRRHighTh) {
							aTemp1.add(iQRS[i]);
						} else if (iQRS[i] > iQRS[aIncrement1] + aRRHighTh) {
							break;
						}
					}

					for (int i = aIncrement2; i < aLengthQRS; i++) {
						if (iQRS[i] <= iQRS[aIncrement2] + aRRHighTh) {
							aTemp2.add(iQRS[i]);
						} else if (iQRS[i] > iQRS[aIncrement2] + aRRHighTh) {
							break;
						}
					}

					int[] aTempQRS = new int[2];
					for (int i = 0; i < aTemp1.size(); i++) {
						for (int j = 0; j < aTemp2.size(); j++) {
							aRRDiff = aTemp2.get(j) - aTemp1.get(i);

							if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
								aMinRRDiff1 = (int) Math.abs(aRRDiff - aRRMean);
								if (aMinRRDiff1 < aMinRRDiff0) {
									aTempQRS[0] = aTemp1.get(i);
									aTempQRS[1] = aTemp2.get(j);
									aForwardIteration = aIncrement2 + j + 1;
									aMinRRDiff0 = aMinRRDiff1;
								}
							}
						}
					}
					aQrsFinal.add(aTempQRS[0]);
					aQrsFinal.add(aTempQRS[1]);
					aCountF = aCountF + 2;
					aRRMeanArr.add((double) (aQrsFinal.get(aCountF - 1) - aQrsFinal.get(aCountF - 2)));
					aHarmonicCheckFlag = 0;
				} // end addition of harmonic qrs locations.

				if (aCountI < aCountF && aCountF >= 3) {
					if (aQrsFinal.get(aCountF - 2) - aQrsFinal.get(aCountF - 3) > (aRRLowTh + aRRMean)) {

						aMissQrsIndex.add(aCountF - 2);
					}
				}

			} // End while loop for forward iteration


			return new Object[] { aQrsFinal, aMissQrsIndex };
		}
		else {
			throw new Exception("Invalid input paramaters : qrsForwardIteration ");
		}

	}


	// Changed add to addFirst in miss identification to have uniform
	// interpolation code

	/**
	 * <p> Backward QRS selection.</p>
	 * @param iQRS Possible set of QRS locations.
	 * @param iStartIndex First index of confirmed QRS in the iQRS array.
	 * @param iQrsFinal Final QRS selected array after forward iteration.
	 * @return {iQrsFinal, aMissQrsIndex} : Final QRS selected array, missed QRS location.
	 * @throws Exception If {@literal (iQRS.length < iStartIndex)} and {@literal iQrsFinal.size < 2}.
	 */
	public Object[] qrsBackwardIteration(int[] iQRS, int iStartIndex, LinkedList<Integer> iQrsFinal) throws Exception{

		if (iQRS.length > iStartIndex  && iQrsFinal.size() >= 2){
			int aRRDiff;
			double aRRMean, aRRLowTh, aRRHighTh;

			double aNoRR = SignalProcConstants.QRS_NO_RR_MEAN;
			// double aLowPerc = SignalProcConstants.QRS_RRLOW_PERC;
			// double aHighPerc = SignalProcConstants.QRS_RRHIGH_PERC;
			double aDelta = SignalProcConstants.QRS_RR_VAR;

			int aMinRRDiff0 = 0, aMinRRDiff1;

			int aMinCheckFlag = 0;
			int aHarmonicCheckFlag = 0;

			int aFindFlag = 0;

			LinkedList<Integer> aMissQrsIndex = new LinkedList<Integer>();
			int aDecrement1 = 0, aDecrement2 = 0;
			int aCountI = 0;
			int aCountF = 0;

			int aBackIteration = iStartIndex - 1;

			LinkedList<Double> aRRMeanArr = new LinkedList<>();

			for (int i = 1; i<iQrsFinal.size(); i++){
				aRRMeanArr.add((double) (iQrsFinal.get(i) - iQrsFinal.get(i-1)) );
			}

			while (aBackIteration >= 0) {
				aCountI = aCountF;

				if (aRRMeanArr.size() <= aNoRR) {
					aRRMean = 0;
					for (int it = 0; it < aRRMeanArr.size(); it++) {
						aRRMean = aRRMean + aRRMeanArr.get(it);
					}
					aRRMean = aRRMean / aRRMeanArr.size();
				} else {
					aRRMean = 0;
					for (int it = 0; it < aNoRR; it++) {
						aRRMean = aRRMean + aRRMeanArr.get(it);
					}
					aRRMean = aRRMean / aNoRR;
				}

				aRRDiff = iQrsFinal.getFirst() - iQRS[aBackIteration];
				aRRLowTh = 1 / (1 / aRRMean + aDelta);
				aRRHighTh = 1 / (1 / aRRMean - aDelta);

				if (aRRDiff < aRRLowTh) {
					aBackIteration--;
				} else if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
					aMinRRDiff0 = 10000;
					aMinCheckFlag = 1;
				} else {
					aFindFlag = 0;
					aDecrement1 = aBackIteration;
					aDecrement2 = aBackIteration - 1;

					if (aDecrement2 <= 0) {
						aFindFlag = 1;
						aBackIteration = aDecrement2;
					}

					while (aFindFlag == 0) {
						if ((iQrsFinal.getFirst() - iQRS[aDecrement1]) >= (aRRLowTh + aRRMean)) {
							aRRDiff = iQRS[aDecrement1] - iQRS[aDecrement2];

							if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
								aMinRRDiff0 = 10000;
								aHarmonicCheckFlag = 1;
								aFindFlag = 1;
							} else if (aRRDiff < aRRLowTh) {
								aDecrement2--;
								if (aDecrement2 <= 0) {
									aFindFlag = 1;
									aBackIteration = aDecrement2;
								}
							} else if (aRRDiff > aRRHighTh) {
								aDecrement1--;
								if (aDecrement1 == aDecrement2) {
									aDecrement2--;

									if (aDecrement2 <= 0) {
										aFindFlag = 1;
										aBackIteration = aDecrement2;
									}
								}
							}
						} else {
							aDecrement1--;
							if (aDecrement1 == aDecrement2) {
								aDecrement2--;

								if (aDecrement2 <= 0) {
									aFindFlag = 1;
									aBackIteration = aDecrement2;
								}
							}
						}
					}

				} // END of finding if QRS is to be selected.

				if (aMinCheckFlag == 1) {
					LinkedList<Integer> aTemp = new LinkedList<Integer>();

					for (int i = aBackIteration; i >= 0; i--) {

						if (iQRS[i] >= iQRS[aBackIteration] - aRRLowTh) {
							aTemp.add(iQRS[i]);
						} else if (iQRS[i] < iQRS[aBackIteration] - aRRLowTh) {
							break;
						}
					}
					int aTempQRS = 0;
					int aShift = 0;
					for (int j = 0; j < aTemp.size(); j++) {
						aRRDiff = iQrsFinal.getFirst() - aTemp.get(j);

						if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
							aMinRRDiff1 = (int) Math.abs(aRRDiff - aRRMean);

							if (aMinRRDiff1 < aMinRRDiff0) {
								aTempQRS = aTemp.get(j);
								aShift = aBackIteration - j - 1;
								aMinRRDiff0 = aMinRRDiff1;
							}
						}
					}
					iQrsFinal.addFirst(aTempQRS);
					aMinCheckFlag = 0;
					aCountF++;
					aRRMeanArr.addFirst((double) (iQrsFinal.get(1) - iQrsFinal.get(0)) );
					aBackIteration = aShift;
					if (aMissQrsIndex.size() > 0) {
						int aTemp1;

						for (int i = 0; i < aMissQrsIndex.size(); i++) {
							aTemp1 = aMissQrsIndex.get(i) + 1;
							aMissQrsIndex.set(i, aTemp1);
						}
					}
				} // End addition of next qrs location

				if (aHarmonicCheckFlag == 1) {
					LinkedList<Integer> aTemp1 = new LinkedList<Integer>();
					LinkedList<Integer> aTemp2 = new LinkedList<Integer>();

					for (int i = aDecrement1; i >= 0; i--) {

						if (iQRS[i] >= iQRS[aDecrement1] - aRRLowTh) {
							aTemp1.add(iQRS[i]);
						} else if (iQRS[i] < iQRS[aDecrement1] - aRRLowTh) {
							break;
						}
					}
					for (int i = aDecrement2; i >= 0; i--) {

						if (iQRS[i] >= iQRS[aDecrement2] - aRRLowTh) {
							aTemp2.add(iQRS[i]);
						} else if (iQRS[i] < iQRS[aDecrement2] - aRRLowTh) {
							break;
						}
					}
					int[] aTempQRS = new int[2];
					int aShift = 0;
					for (int i = 0; i < aTemp1.size(); i++) {
						for (int j = 0; j < aTemp2.size(); j++) {
							aRRDiff = aTemp1.get(i) - aTemp2.get(j);

							if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
								aMinRRDiff1 = (int) Math.abs(aRRDiff - aRRMean);
								if (aMinRRDiff1 < aMinRRDiff0) {
									aTempQRS[0] = aTemp1.get(i);
									aTempQRS[1] = aTemp2.get(j);
									aShift = aDecrement2 - j - 1;
									aMinRRDiff0 = aMinRRDiff1;
								}
							}
						}
					}
					iQrsFinal.addFirst(aTempQRS[0]);
					iQrsFinal.addFirst(aTempQRS[1]);
					aRRMeanArr.addFirst((double) (iQrsFinal.get(1) - iQrsFinal.get(0)) );

					aCountF = aCountF + 2;
					aHarmonicCheckFlag = 0;
					aBackIteration = aShift;
					if (aMissQrsIndex.size() > 0) {
						int aTemp;

						for (int i = 0; i < aMissQrsIndex.size(); i++) {
							aTemp = aMissQrsIndex.get(i) + 2;
							aMissQrsIndex.set(i, aTemp);
						}
					}
				} // end addition of harmonic qrs locations.

				if (aCountI < aCountF) {

					if (iQrsFinal.get(2) - iQrsFinal.get(1) > (aRRLowTh + aRRMean)) {
						aMissQrsIndex.addFirst(2);
					}
				}

			}

			return new Object[] { iQrsFinal, aMissQrsIndex };
		}
		else {
			throw new Exception ("Invalid input paramaters : qrsBackwardIteration");
		}
	}


	/**
	 * <p> Interpolate the harmonic at missed locations.</p>
	 * @param iQrsFinal Final QRS selected.
	 * @param iMissQrsIndex Location of missed QRS.
	 * @param iQrsM Set of maternal QRS locations.
	 * @return iQrsFinal.
	 */
	public LinkedList<Integer> interpolate(LinkedList<Integer> iQrsFinal, LinkedList<Integer> iMissQrsIndex, int[] iQrsM){

		double aNoRR = SignalProcConstants.QRS_NO_RR_MEAN;
		double aRRMean = 0, aRRLowTh, aRRHighTh, aRRDiff;
		double aDelta = SignalProcConstants.QRS_RR_VAR;

		int aLengthMiss = iMissQrsIndex.size();
		int aIndMiss = -1;

		int aFactor = 0;
		int aOverlapFlag = 0;
		int aQrsInter;
		int aElementAdded = 0;
		double aDiffMiss;
		int aPreviousMissInd = 0;
		int aQrsAddFlag = 0;
		double aDiffInd;
		for (int i = 0; i < aLengthMiss; i++) {

			aIndMiss = iMissQrsIndex.get(i) + aElementAdded;
			aQrsAddFlag = 0;
			if (iQrsFinal.size() > aIndMiss && aIndMiss >=2) {
				aDiffMiss = (iQrsFinal.get(aIndMiss) - iQrsFinal.get(aIndMiss - 1));
				//					aDiffDenominator = (iQrsFinal.get(aIndMiss - 1) - iQrsFinal.get(aIndMiss - 2));
				//					aFactor = (int) Math.round(aDiffMiss / aDiffDenominator);
				aOverlapFlag = 0;
				aDiffInd = aIndMiss - aPreviousMissInd - 1;
				if (aDiffInd <= aNoRR) {
					aRRMean = (iQrsFinal.get(aIndMiss - 1) - iQrsFinal.get(aPreviousMissInd)) / aDiffInd;
				} else {
					aRRMean = (iQrsFinal.get(aIndMiss - 1)
							- iQrsFinal.get(aIndMiss - 1 - (int) aNoRR)) / aNoRR;
				}
				aFactor = (int) Math.round(aDiffMiss / aRRMean);
				aRRLowTh = 1 / (1 / aRRMean + aDelta);
				aRRHighTh = 1 / (1 / aRRMean - aDelta);


				if (aFactor == 2) {
					aQrsInter = mMatrixFunctions.findOverlapMqrsLoc(iQrsM, iQrsFinal.get(aIndMiss - 1),
							iQrsFinal.get(aIndMiss));

					aRRDiff = aQrsInter - iQrsFinal.get(aIndMiss - 1) + 1;

					if ((aRRDiff >= aRRLowTh) && (aRRDiff <= aRRHighTh)) {
						iQrsFinal.add(aIndMiss, aQrsInter + 1);
						aOverlapFlag = 1;
						aQrsAddFlag = 1;
					}
				}

				aRRDiff = aDiffMiss / aFactor;
				if (aOverlapFlag == 0 && (aRRDiff >= aRRLowTh) && (aRRDiff <= aRRHighTh)) {
					// aInterpolatedLength = (int) (aInterpolatedLength
					// +
					// aDiffMiss);
					for (int f = aFactor - 1; f >= 1; f--) {
						aQrsInter = (int) (iQrsFinal.get(aIndMiss - 1) + aDiffMiss * f / aFactor);
						iQrsFinal.add(aIndMiss, aQrsInter);
						aQrsAddFlag = 1;
					}
				}
			}
			
			aPreviousMissInd = aIndMiss;
			if (aQrsAddFlag == 1) {
				aElementAdded = aElementAdded + (aFactor - 1);
				aPreviousMissInd = aPreviousMissInd + (aFactor - 1);
			}

		}



		return iQrsFinal;
	}

	/**
	 * Check the first QRS location and first RR value with previous iteration.
     *
     *
	 * @param iQrsFinal Final QRS selected.
	 * @param iQRSLast Location of last QRS determined in previous iteration.
	 * @param iRRMeanLast Mean RR of last 4 QRS determined in previous iteration.
	 * @return Flag : 0 or 1.
	 */
	public int firstQrsCheck(LinkedList<Integer> iQrsFinal, int iQRSLast, double iRRMeanLast){
		
		int i = 0;

		while (iQrsFinal.get(i) < 2000 ){
			iQrsFinal.remove(i);
			if (iQrsFinal.size() <= 0){
				break;
			}
		}

		double aDelta = SignalProcConstants.QRS_RR_VAR;

		double aRRLowTh = 1 / (1 / iRRMeanLast + aDelta);
		double aRRHighTh = 1 / (1 / iRRMeanLast - aDelta);

		double aRRDiff;
		int aCheckFlag = 0;

		// Check first qrs location
		if (iQrsFinal.size() > 0){
			while (iQrsFinal.get(i) - iQRSLast <= aRRHighTh ){
				aRRDiff = iQrsFinal.get(i) - iQRSLast;

				if (aRRDiff < aRRLowTh){
					iQrsFinal.remove(i);
				}
				else if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
					aCheckFlag = 1;
					i++;
					if (i >= iQrsFinal.size()) {
						break;
					}
				}
			}
		}

		// Check RR value range
		if (aCheckFlag == 1){
			aRRDiff = iQrsFinal.get(1) - iQrsFinal.get(0);
			if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh){
				aCheckFlag = 1;
			}
			else {
				aCheckFlag = 0;
			}
		}

		return aCheckFlag;
	}


	/**
	 * <p> QRS selection forward using information from previous iteration.</p>
	 * @param iQRS Possible set of QRS locations.
	 * @param iQRSLast Location of last QRS determined in previous iteration.
	 * @param iRRMeanLast Mean RR of last 4 QRS determined in previous iteration.
	 * @return {aQrsFinal, aMissQrsIndex} : Final QRS selected array, missed QRS location.
	 * @throws Exception If {@literal iQRS.length <= 0}.
	 */
	public Object[] qrsConcatenated(int[] iQRS, int iQRSLast, double iRRMeanLast) throws Exception{
		if (iQRS.length > 0){
			if (iQRS[iQRS.length-1] > iQRSLast){
				int aRRDiff;
				double aRRMean, aRRLowTh, aRRHighTh;

				double aNoRR = SignalProcConstants.QRS_NO_RR_MEAN;
				// double aLowPerc = SignalProcConstants.QRS_RRLOW_PERC;
				// double aHighPerc = SignalProcConstants.QRS_RRHIGH_PERC;
				double aDelta = SignalProcConstants.QRS_RR_VAR;

				int aMinRRDiff0 = 0, aMinRRDiff1;
				int aIncrement1 = 0, aIncrement2 = 0;
				int aLengthQRS = iQRS.length;

				int aForwardIteration = 0;
				int aCountF = 0;
				int aCountI = 0;

				LinkedList<Integer> aQrsFinal = new LinkedList<Integer>();
				LinkedList<Integer> aMissQrsIndex = new LinkedList<Integer>();

				// adding First 2 QRS locations to QRSFinal

				aQrsFinal.add(iQRSLast);
				aCountF = 1;

				while (iQRS[aForwardIteration] < iQRSLast) {
					aForwardIteration++;
				}

				int aMinCheckFlag = 0;
				int aHarmonicCheckFlag = 0;

				int aFindFlag = 0;

				LinkedList<Double> aRRMeanArr = new LinkedList<>();
				aRRMeanArr.add(iRRMeanLast);

				while (aForwardIteration < aLengthQRS){

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
					aRRLowTh = 1 / (1 / aRRMean + aDelta);
					aRRHighTh = 1 / (1 / aRRMean - aDelta);

					if (aRRDiff < aRRLowTh) {
						aForwardIteration++;
					} else if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
						aMinRRDiff0 = 10000;
						aMinCheckFlag = 1;
					} else {
						aFindFlag = 0;
						aIncrement1 = aForwardIteration;
						aIncrement2 = aForwardIteration + 1;
						if (aIncrement2 >= aLengthQRS) {
							aForwardIteration = aIncrement2;
							aFindFlag = 1;
						}

						while (aFindFlag == 0) {

							if ((iQRS[aIncrement1] - aQrsFinal.getLast()) >= (aRRLowTh + aRRMean)) {

								aRRDiff = iQRS[aIncrement2] - iQRS[aIncrement1];
								if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
									aMinRRDiff0 = 10000;
									aHarmonicCheckFlag = 1;
									aFindFlag = 1;
								} else if (aRRDiff < aRRLowTh) {
									aIncrement2++;
									if (aIncrement2 >= aLengthQRS) {
										aForwardIteration = aIncrement2;
										aFindFlag = 1;
									}
								} else if (aRRDiff > aRRHighTh) {
									aIncrement1++;
									if (aIncrement2 == aIncrement1) {
										aIncrement2++;
										if (aIncrement2 >= aLengthQRS) {
											aForwardIteration = aIncrement2;
											aFindFlag = 1;
										}
									}
								}
							} else {
								aIncrement1++;
								if (aIncrement2 == aIncrement1) {
									aIncrement2++;
									if (aIncrement2 >= aLengthQRS) {
										aForwardIteration = aIncrement2;
										aFindFlag = 1;
									}
								}
							}
						}

					} // END of finding if QRS is to be selected.

					if (aMinCheckFlag == 1) {
						LinkedList<Integer> aTemp = new LinkedList<Integer>();
						for (int i = aForwardIteration; i < aLengthQRS; i++) {

							if (iQRS[i] <= iQRS[aForwardIteration] + aRRHighTh) {
								aTemp.add(iQRS[i]);
							} else if (iQRS[i] > iQRS[aForwardIteration] + aRRHighTh) {
								break;
							}
						}
						int aTempQRS = 0;
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
						aRRMeanArr.add((double) (aQrsFinal.get(aCountF - 1) - aQrsFinal.get(aCountF - 2)));
						aForwardIteration = aShift;
					} // End addition of next qrs location

					if (aHarmonicCheckFlag == 1) {
						LinkedList<Integer> aTemp1 = new LinkedList<Integer>();
						LinkedList<Integer> aTemp2 = new LinkedList<Integer>();

						for (int i = aIncrement1; i < aLengthQRS; i++) {
							if (iQRS[i] <= iQRS[aIncrement1] + aRRHighTh) {
								aTemp1.add(iQRS[i]);
							} else if (iQRS[i] > iQRS[aIncrement1] + aRRHighTh) {
								break;
							}
						}

						for (int i = aIncrement2; i < aLengthQRS; i++) {
							if (iQRS[i] <= iQRS[aIncrement2] + aRRHighTh) {
								aTemp2.add(iQRS[i]);
							} else if (iQRS[i] > iQRS[aIncrement2] + aRRHighTh) {
								break;
							}
						}

						int[] aTempQRS = new int[2];
						for (int i = 0; i < aTemp1.size(); i++) {
							for (int j = 0; j < aTemp2.size(); j++) {
								aRRDiff = aTemp2.get(j) - aTemp1.get(i);

								if (aRRDiff >= aRRLowTh && aRRDiff <= aRRHighTh) {
									aMinRRDiff1 = (int) Math.abs(aRRDiff - aRRMean);
									if (aMinRRDiff1 < aMinRRDiff0) {
										aTempQRS[0] = aTemp1.get(i);
										aTempQRS[1] = aTemp2.get(j);
										aForwardIteration = aIncrement2 + j + 1;
										aMinRRDiff0 = aMinRRDiff1;
									}
								}
							}
						}
						aQrsFinal.add(aTempQRS[0]);
						aQrsFinal.add(aTempQRS[1]);
						aCountF = aCountF + 2;
						aRRMeanArr.add((double) (aQrsFinal.get(aCountF - 1) - aQrsFinal.get(aCountF - 2)));
						aHarmonicCheckFlag = 0;
					} // end addition of harmonic qrs locations.

					if (aCountI < aCountF && aCountF >= 3) {
						if (aQrsFinal.get(aCountF - 2) - aQrsFinal.get(aCountF - 3) > (aRRLowTh + aRRMean)) {

							aMissQrsIndex.add(aCountF - 2);
						}
					}

				}


				return new Object[]{aQrsFinal, aMissQrsIndex};
			}
			else {
				throw new Exception ("Invalid last qrs : qrsConcatenated");
			}
		}
		else{
			throw new Exception ("Invalid qrs input : qrsConcatenated");
		}
	}

}
