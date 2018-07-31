////package com.sattvamedtech.fetallite.signalproc;
//
////import com.sattvamedtech.fetallite.helper.Logger;
//
//package SignalProc;
//
///**
// * <p> Determine UC for given AECG.</p>
// * <ul>
// *     <li>
// *         11th October, 2017
// *         <ol>
// *             <li> Removed DWT based UC detection and using FFT based method.</li>
// *             <li> Filter high-pass : fc = 0.3 Hz, Order 10.</li>
// *             <li> Filter low-pass : fc = 3 Hz, Order 10.</li>
// *             <li> Decimation by 100.</li>
// *             <li> Find cummulative Median energy having median frequency less than 0.8 Hz using PSD. </li>
// *         </ol>
// *     </li>
// *     <li> 7Th July, 2017
// *         <ol>
// *             <li> Added 20 point output for 1 itertion of UC.</li>
// *             <li> Duration of computation reduced to 5000 samples (5 sec).</li>
// *             <li> Shift for each iteration changed to 500 samples (0.5 sec)</li>
// *         </ol>
// *     </li>
// *     <li> 12th Feb, 2017
// *         <ol>
// *             <li> First UC Algorithm.</li>
// *         </ol>
// *     </li>
// * </ul>
// *
// *
// * @author Kishore Subramanian (kishore@sattvamedtech.com)
// */
//public class UcAlgo
//{
//	/**
//	 * Object initialization of  {@link MatrixFunctions}
//	 */
//	MatrixFunctions mMatrixFunctions = new MatrixFunctions();
//
//	/**
//	 * <p> Determine UC using DWT.</p>
//	 * @param iInput Channel 1 ECG data.
//	 * @return UC values for every 500 ms.
//	 * @throws Exception Message containing the exception.
//	 */
//	public double[] ucAlgoDwt(double[] iInput) throws Exception
//	{
//		double[] aInput = new double[SignalProcConstants.NO_OF_SAMPLES];
//		for (int i = 0; i < SignalProcConstants.NO_OF_SAMPLES; i++) {
//			aInput[i] = iInput[i];
//		}
//		double[] aUA_Energy = new double[SignalProcConstants.NO_OF_PRINT_VALUES];
//
//		for (int i = 0; i < SignalProcConstants.NO_OF_PRINT_VALUES; i++) {
//
//			aUA_Energy[i] = 1;
//		}
//		/**
//		 * Input is 15000 array
//		 */
//
//
//			// Re-scaling of Input to 12-bit
//			for (int i = 0; i< SignalProcConstants.NO_OF_SAMPLES; i++){
//				aInput[i] = (aInput[i] * 24 * (Math.pow(2,23)-1) )/ (Math.pow(2,12) * 4.5);
//			}
//
//			int aLengthInput = aInput.length;
//
//			// Filtering between 0.3 - 3 Hz
//			mMatrixFunctions.filtfilt_Sos(aInput, SignalProcConstants.UA_HIGH_SOS, SignalProcConstants.UA_HIGH_GAIN,
//					SignalProcConstants.UA_HIGH_Z, SignalProcConstants.UA_HIGH_ORDER);
//
//			mMatrixFunctions.filtfilt_Sos(aInput, SignalProcConstants.UA_LOW_SOS, SignalProcConstants.UA_LOW_GAIN,
//					SignalProcConstants.UA_LOW_Z, SignalProcConstants.UA_LOW_ORDER);
//
//			// Decimation
//			int aDecimateFactor = 100;
//			int aDecimatedLength = aLengthInput/aDecimateFactor;
//			double[] aDecimatedInput = new double[aDecimatedLength];
//
//			for (int i =0; i<aDecimatedLength; i++) {
//				//Change Aravind
//				aDecimatedInput[i] = Math.abs(aInput[i*aDecimateFactor]);
//			}
//
//			//  StepSize and Shift
//			int aStepSize = 5000/aDecimateFactor; // 5 sec
//			int aShift = 500/aDecimateFactor;    // 500 milli sec
//
//			double[] aSignalExtract = new double[aStepSize];
//			double[] aPsdSignalExtract = new double[2048];
//			double aTotalEnergy = 0;
//			double aMidEnergy = 0;
//			double aMedianEnergy = 0;
//			int aFindMedian = 0;
//			int k = 0;
//
//			double aScale = 0.05;
//
//			for (int i = 0; i< SignalProcConstants.NO_OF_PRINT_VALUES; i++ ) {
//
//				for (int j = 0; j<aStepSize; j++) {
//					aSignalExtract[j] = aDecimatedInput[j+aShift*i];
//				}
//				aPsdSignalExtract = mMatrixFunctions.fastfouriertransform_UC(aSignalExtract);
//				aTotalEnergy = 0;
//				// Energy from 0.1 - 5 Hz
//				for (int j = 1045; j<2048; j++) {
//					aTotalEnergy = aTotalEnergy + aPsdSignalExtract[j];
//				}
//				aFindMedian = 0;
//				aMidEnergy = aTotalEnergy/2;
//
//				k = 1046;
//
//				while (aFindMedian == 0) {
//					aMedianEnergy = 0;
//					for (int j = 1045; j<=k; j++) {
//						aMedianEnergy = aMedianEnergy + aPsdSignalExtract[j];
//					}
//					if (aMedianEnergy > aMidEnergy) {
//						aFindMedian = 1;
//						k = k-1;
//					}
//					else {
//						k = k+1;
//					}
//				}
//
//				if ( (-1/2 + k/2048)*10 < 0.8 ) {
//					aMedianEnergy = 0;
//					// energy from 0.4 - 1.5 Hz
//					for (int j = 1105; j<=1331; j++) {
//						aMedianEnergy = aMedianEnergy + aPsdSignalExtract[j];
//					}
//					aMedianEnergy = aMedianEnergy / (227) / aScale;
//				}
//				else {
//					aMedianEnergy = 0;
//				}
//				// Change Aravind
//				if (SignalProcUtils.ucCounter < SignalProcConstants.UA_WINDOW) {
//					SignalProcUtils.ucEnergyTemp.add(aMedianEnergy);
//					SignalProcUtils.ucCounter++;
//					double sum = 0;
//					for (int z = 0; z< SignalProcUtils.ucCounter; z++) {
//						sum = sum + SignalProcUtils.ucEnergyTemp.get(z);
//					}
//					aUA_Energy[i] = sum / SignalProcUtils.ucCounter;
//				}
//				else {
//					SignalProcUtils.ucEnergyTemp.removeFirst();
//					SignalProcUtils.ucEnergyTemp.add(aMedianEnergy);
//					SignalProcUtils.ucCounter++;
//					double sum = 0;
//					for (int z = 0; z< SignalProcConstants.UA_WINDOW; z++) {
//						sum = sum + SignalProcUtils.ucEnergyTemp.get(z);
//					}
//					aUA_Energy[i] = sum / SignalProcConstants.UA_WINDOW;
//				}
//
////				if (SignalProcUtils.ucCounter == 0) {
////					SignalProcUtils.ucCounter++;
////					aUA_Energy[i] = aMedianEnergy;
////					SignalProcConstants.ucEnergyTemp.add(aMedianEnergy);
////					SignalProcConstants.ucPreviousEnergy = aUA_Energy[i];
////				}
////				else if (SignalProcConstants.ucCounter < SignalProcConstants.UA_WINDOW) {
////					SignalProcConstants.ucCounter++;
////					aUA_Energy[i] = ( (SignalProcConstants.ucPreviousEnergy*(SignalProcConstants.ucCounter-1) + aMedianEnergy) / SignalProcConstants.ucCounter);
////					SignalProcConstants.ucEnergyTemp.add(aMedianEnergy);
////					SignalProcConstants.ucPreviousEnergy = aUA_Energy[i];
////				}
////				else {
////					SignalProcConstants.ucCounter++;
////					aUA_Energy[i] = ( (SignalProcConstants.ucPreviousEnergy*(SignalProcConstants.UA_WINDOW) + aMedianEnergy - SignalProcConstants.ucEnergyTemp.removeFirst()) / SignalProcConstants.UA_WINDOW);
////					SignalProcConstants.ucEnergyTemp.add(aMedianEnergy);
////
////				}
//
//			}
//
//			return aUA_Energy;
//
//	}
//
//
//}
