//package com.sattvamedtech.fetallite.signalproc;

//import com.sattvamedtech.fetallite.helper.Logger;

package SignalProc;

/**
 * <p> Determine UC for given AECG.</p>
 * <ul>
 *     <li>
 *         11th October, 2017
 *         <ol>
 *             <li> Removed DWT based UC detection and using FFT based method.</li>
 *             <li> Filter high-pass : fc = 0.3 Hz, Order 10.</li>
 *             <li> Filter low-pass : fc = 3 Hz, Order 10.</li>
 *             <li> Decimation by 100.</li>
 *             <li> Find cummulative Median energy having median frequency less than 0.8 Hz using PSD. </li>
 *         </ol>
 *     </li>
 *     <li> 7Th July, 2017
 *         <ol>
 *             <li> Added 20 point output for 1 itertion of UC.</li>
 *             <li> Duration of computation reduced to 5000 samples (5 sec).</li>
 *             <li> Shift for each iteration changed to 500 samples (0.5 sec)</li>
 *         </ol>
 *     </li>
 *     <li> 12th Feb, 2017
 *         <ol>
 *             <li> First UC Algorithm.</li>
 *         </ol>
 *     </li>
 * </ul>
 *
 *
 * @author Kishore Subramanian (kishore@sattvamedtech.com)
 */
public class UcAlgo_new
{
	/**
	 * Object initialization of  {@link MatrixFunctions}
	 */
	MatrixFunctions mMatrixFunctions = new MatrixFunctions();

	/**
	 * <p> Determine UC using DWT.</p>
	 * @param iInput Channel 4 ECG data.
	 * @return UC values for every 500 ms.
	 * @throws Exception Message containing the exception.
	 */
	public static double[] ucAlgoDwt(double[] iInput) throws Exception {
		MatrixFunctions mMatrixFunctions = new MatrixFunctions();

		double[] aUA_Energy = new double[SignalProcConstants.NO_OF_PRINT_VALUES];
		double[] aUA_Energy_plotdata = new double[SignalProcConstants.UA_BATCH_DATA_SIZE + SignalProcConstants.NO_OF_PRINT_VALUES];


		for (int i = 0; i < SignalProcConstants.NO_OF_PRINT_VALUES; i++) {
			aUA_Energy[i] = 1;
		}
		double[] aInput = new double[SignalProcConstants.NO_OF_SAMPLES];
		for (int i = 0; i < SignalProcConstants.NO_OF_SAMPLES; i++) {
			aInput[i] = iInput[i];
		}
		/**
		 * Input is 15000 array
		 */

		// Re-scaling of Input to 12-bit
		for (int i = 0; i< SignalProcConstants.NO_OF_SAMPLES; i++){
			aInput[i] = (aInput[i] * 24 * (Math.pow(2,23)-1) )/ (Math.pow(2,12) * 4.5);
		}

		int aLengthInput = aInput.length;

		// Filtering between 0.3 - 3 Hz
		mMatrixFunctions.filtfilt_Sos(aInput, SignalProcConstants.UA_HIGH_SOS, SignalProcConstants.UA_HIGH_GAIN,
				SignalProcConstants.UA_HIGH_Z, SignalProcConstants.UA_HIGH_ORDER);

		mMatrixFunctions.filtfilt_Sos(aInput, SignalProcConstants.UA_LOW_SOS, SignalProcConstants.UA_LOW_GAIN,
				SignalProcConstants.UA_LOW_Z, SignalProcConstants.UA_LOW_ORDER);

		// Decimation
		int aDecimateFactor = 100;

		int aDecimatedLength = aLengthInput/aDecimateFactor;

		double[] aDecimatedInput = new double[aDecimatedLength];

		for (int i =0; i<aDecimatedLength; i++) {
			//Change Aravind
			aDecimatedInput[i] = Math.abs(aInput[i*aDecimateFactor]);
		}

		//  StepSize and Shift
		int aStepSize = 5000/aDecimateFactor; // 5 sec

		int aShift = 500/aDecimateFactor;    // 500 milli sec

		double[] aSignalExtract = new double[aStepSize];
		double[] aPsdSignalExtract = new double[2048];
		double aTotalEnergy = 0;
		double aMidEnergy = 0;
		double aMedianEnergy = 0;
		int aFindMedian = 0;
		int k = 0;

		double sumE =0.0;

		while (SignalProcUtils.iter < 6){
			for (int i = 0; i< SignalProcConstants.NO_OF_PRINT_VALUES; i++ ) {
				for (int j = 0; j<aStepSize; j++) {
					aSignalExtract[j] = aDecimatedInput[j+aShift*i];
				}

				aPsdSignalExtract = mMatrixFunctions.fastfouriertransform_UA(aSignalExtract);
				aTotalEnergy = 0;

				// Energy from 0.1 - 5 Hz
				for (int j = 1045; j<2048; j++) {
					aTotalEnergy = aTotalEnergy + aPsdSignalExtract[j];
				}

				aFindMedian = 0;
				aMidEnergy = aTotalEnergy/2;

				k = 1046;

				while (aFindMedian == 0 && k < 2048) {
					aMedianEnergy = 0;

					for (int j = 1045; j<=k; j++) {
						aMedianEnergy = aMedianEnergy + aPsdSignalExtract[j];
					}

					if (aMedianEnergy > aMidEnergy) {
						aFindMedian = 1;
						k = k-1;
					} else {
						k = k+1;
					}
				}

				if ((-1/2 + k/2048)*10 < 0.8 ) {
					aMedianEnergy = 0;
					// energy from 0.4 - 1.5 Hz
					for (int j = 1105; j<=1331; j++) {
						aMedianEnergy = aMedianEnergy + aPsdSignalExtract[j];
					}

					aMedianEnergy = aMedianEnergy / (227);// / aScale;
				} else {
					aMedianEnergy = 0;
				}
				if (SignalProcUtils.uaCounter < SignalProcConstants.UA_WINDOW) {
					SignalProcUtils.uaEnergyTemp.add(aMedianEnergy);
					SignalProcUtils.uaCounter++;
					double sum = 0;
					for (int z = 0; z< SignalProcUtils.uaCounter; z++) {
						sum = sum + SignalProcUtils.uaEnergyTemp.get(z);
					}
					aUA_Energy[i] = ((sum / SignalProcUtils.uaCounter)/3);

				}
				else {
					SignalProcUtils.uaEnergyTemp.removeFirst();
					SignalProcUtils.uaEnergyTemp.add(aMedianEnergy);
					SignalProcUtils.uaCounter++;
					double sum = 0;
					for (int z = 0; z< SignalProcConstants.UA_WINDOW; z++) {
						sum = sum + SignalProcUtils.uaEnergyTemp.get(z);
					}
					aUA_Energy[i] = ((sum / SignalProcConstants.UA_WINDOW)/3);

				}
//				SignalProcUtils.aUA_Energy_iter[(SignalProcUtils.iter * 20) + i] = aMedianEnergy;
//				SignalProcUtils.aUA_Energy_iter_test[(SignalProcUtils.iter * 20) + i] = aUA_Energy[i];
			}
			SignalProcUtils.iter++;
			return aUA_Energy;
		}

		if(SignalProcUtils.iter == 6){
//			for (int z = 0; z< SignalProcUtils.aUA_Energy_iter.length; z++) {
//				sumE = sumE + SignalProcUtils.aUA_Energy_iter[z];
//			}

//			SignalProcUtils.uaAvgEnergy = SignalProcUtils.uaAvgEnergy + sumE/SignalProcUtils.aUA_Energy_iter.length;

			if(SignalProcUtils.uaAvgEnergy > 1.0){
				SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy))))+2;
			} else if(SignalProcUtils.uaAvgEnergy < 0.10){
				SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy))));
			} else {
				SignalProcUtils.uaScale = (Math.abs(Math.floor(Math.log10(SignalProcUtils.uaAvgEnergy))))+1;
			}

//			FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : UC Algo : Average Energy =" + SignalProcUtils.uaAvgEnergy, FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//			FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : UC Algo : UC Scale =" + SignalProcUtils.uaScale, FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);

//			for (int i = 0; i < SignalProcUtils.aUA_Energy_iter_test.length ; i++) {
//				aUA_Energy_plotdata[i] = SignalProcUtils.aUA_Energy_iter_test[i] * Math.pow(10,SignalProcUtils.uaScale);
//			}
		}

		for (int i = 0; i< SignalProcConstants.NO_OF_PRINT_VALUES; i++ ) {
			for (int j = 0; j<aStepSize; j++) {
				aSignalExtract[j] = aDecimatedInput[j+aShift*i];
			}
			aPsdSignalExtract = mMatrixFunctions.fastfouriertransform_UA(aSignalExtract);
			aTotalEnergy = 0;
			// Energy from 0.1 - 5 Hz
			for (int j = 1045; j<2048; j++) {
				aTotalEnergy = aTotalEnergy + aPsdSignalExtract[j];
			}
			aFindMedian = 0;
			aMidEnergy = aTotalEnergy/2;

			k = 1046;

			while (aFindMedian == 0 && k < 2048) {
				aMedianEnergy = 0;
				for (int j = 1045; j<=k; j++) {
					aMedianEnergy = aMedianEnergy + aPsdSignalExtract[j];
				}
				if (aMedianEnergy > aMidEnergy) {
					aFindMedian = 1;
					k = k-1;
				}
				else {
					k = k+1;
				}
			}

			if ( (-1/2 + k/2048)*10 < 0.8 ) {
				aMedianEnergy = 0;
				// energy from 0.4 - 1.5 Hz
				for (int j = 1105; j<=1331; j++) {
					aMedianEnergy = aMedianEnergy + aPsdSignalExtract[j];
				}
				aMedianEnergy = aMedianEnergy / (227);// / aScale;
			}
			else {
				aMedianEnergy = 0;
			}
			if (SignalProcUtils.uaCounter < SignalProcConstants.UA_WINDOW) {
				SignalProcUtils.uaEnergyTemp.add(aMedianEnergy);
				SignalProcUtils.uaCounter++;
				double sum = 0;
				for (int z = 0; z< SignalProcUtils.uaCounter; z++) {
					sum = sum + SignalProcUtils.uaEnergyTemp.get(z);
				}
				aUA_Energy[i] = ((sum / SignalProcUtils.uaCounter)/3) * Math.pow(10,SignalProcUtils.uaScale);
				if(SignalProcUtils.iter == 6){
					aUA_Energy_plotdata[SignalProcConstants.UA_BATCH_DATA_SIZE + i] = aUA_Energy[i];
				}
			}
			else {
				SignalProcUtils.uaEnergyTemp.removeFirst();
				SignalProcUtils.uaEnergyTemp.add(aMedianEnergy);
				SignalProcUtils.uaCounter++;
				double sum = 0;
				for (int z = 0; z< SignalProcConstants.UA_WINDOW; z++) {
					sum = sum + SignalProcUtils.uaEnergyTemp.get(z);
				}
				aUA_Energy[i] = ((sum / SignalProcConstants.UA_WINDOW)/3) * Math.pow(10,SignalProcUtils.uaScale);
				if(SignalProcUtils.iter == 6){
					aUA_Energy_plotdata[SignalProcConstants.UA_BATCH_DATA_SIZE + i] = aUA_Energy[i];
				}
			}
		}

//		if (SignalProcUtils.iter == 6){
//			SignalProcUtils.iter++;
//			return aUA_Energy_plotdata;
//		} else{
			SignalProcUtils.iter++;
			return aUA_Energy;
//		}
	}
}
