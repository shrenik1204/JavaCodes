package SignalProc;


/**
 * <p> List of constant values used in SignalProc for algorithm.</p>
 * <p> Change Logs :</p>
 * <ul>
 * 	   <li> 18th September, 2017
 *         <ol>
 *             <li> Changed Low-pass filter to 14th order butterworth cutoff 30Hz.</li>
 *             <li> Changed High-pass filter to 3th order butterworth cutoff 0.5Hz.</li>
 *             <li> Changed Notch filter 50Hz with lower bandwidth : 0.00024.</li>
 *             <li> Added notch filter to 100, 150, 200, 250 Hz.</li>
 *         </ol>
 *     </li>
 *     <li> 17th August, 2017
 *         <ol>
 *             <li> Changed lastQRSFIteration from 0 to -1.</li>
 *             <li><pre> Added qrsFetalMissLocation, fetalHrNew, fetalHrEnd,
 *             	qrsfLocTemp, hrfTemp, fqrsMissIndex. </pre></li>
 *         </ol>
 *     </li>
 *     <li> 23rd June, 2017
 *         <ol>
 *             <li><pre> Changed the limits of MHR : 30 - 150 from 30-130
 *             		MQRS_RR_LOW_TH = 400 (from 500)
 *             		MQRS_RR_HIGH_TH = 2000 (from 1200).	</pre> </li>
 *             	<li><pre> Added 4 new constants for MQRS and FQRS length threshold.
 *             		MQRS_MIN_SIZE = 7, MQRS_MAX_SIZE = 37,
 *             		FQRS_MIN_SIZE = 13, FQRS_MAX_SIZE = 52.	</pre></li>
 *         </ol>
 *     </li>
 *     <li> 24th May, 2017
 *         <ol>
 *             <li> First commit.</li>
 *         </ol>
 *     </li>
 * </ul>
 *
 * @author Kishore Subramanian (kishore@sattvamedtech.com)
 *
 */
public class SignalProcConstants {
	/*********************************************************
	 * Signal input Characteristics
	 *********************************************************/
	/**
	 * Length of the signal to analyse : 15sec.
	 */
	public static final int NO_OF_SAMPLES = 15000;
	public static final int BUFFER_SIZE = 25 + NO_OF_SAMPLES;
	public static final int MAX_DATA_LOSS = 25;
	/**
	 * Number of channels of data.
	 */
	public static final int NO_OF_CHANNELS = 4;
	/**
	 * Sampling frequency.
	 */
	public static final int FS = 1000;

	/*********************************************************
	 * Interpolation  constants.
	 *********************************************************/
	/**
	 * Interpolation size
	 */
	public static final int CUBIC_INTERPOLATE_SIZE = 10;

    /**
     * Lead off detection
     */
    public static final double LEADOFF_FREQ = 31.25;
    public static final int LEADOFF_DETECTION_LENGTH = 1024;
    public static final double LEADOFF_CURRENT = 6 * Math.pow(10,-9);
    public static final double LEADOFF_RESISTANCE_OFF = 2200;
    public static final double LEADOFF_RESISTANCE_ON = 4400;
	/**
	 * Maximum error detection
	 */

	public static final int SAMPLE_LENGTH = 26;
	public static final int ASCII_START = 34;
	public static final int ASCII_END = 125;
	public static final int ASCII_DIFF = ASCII_END - ASCII_START + 1;
	public static final int MAX_MISS_DETECTION = ASCII_DIFF * ASCII_DIFF;
	public static final int LEAD_OFF_ASCII_RANGE = 23;

	/*****************************************
	 * SignalProcConstants for FQRS, MQRS length Threshold
	 *****************************************/

	/**
	 * Minimum number of Maternal QRS.
	 */
	public static final int MQRS_MIN_SIZE = 7; // 7*4 = 28 HR
	/**
	 * Maximum number of Maternal QRS.
	 */
	public static final int MQRS_MAX_SIZE = 37; // 32*4 = 128 HR
	/**
	 * Minimum number of Fetal QRS.
	 */
	public static final int FQRS_MIN_SIZE = 13; // 13*4 = 52 HR
	/**
	 * Maximum number of Fetal QRS.
	 */
	public static final int FQRS_MAX_SIZE = 52; // 52*4 = 212 HR




	/*******************************************
	 * SignalProcConstants for computing print
	 *******************************************/

	/**
	 * Time difference between successive points.
	 */
	public static final int DIFFERENCE_SAMPLES = 500;
	/**
	 * Maximum Hex to decimal value on printer module.
	 */
	public static final int HR_DECIMAL_MAX = 584;
	/**
	 * Minimum Hex to decimal value on printer module.
	 */
	public static final int HR_DECIMAL_MIN = 1;
	/**
	 * Maximum HR value on printer paper.
	 */
	public static final int HR_VALUE_MAX = 240;
	/**
	 * Minimum HR value on printer paper.
	 */
	public static final int HR_VALUE_MIN = 30;
	/**
	 * Range of HR values on printer paper.
	 */
	public static final double HR_PRINT_RANGE = 240 - 30;
	/**
	 * Range of HEX values on printer module.
	 */
	public static final double HR_DECIMAL_PRINT_RANGE = 1 - 584;
	/**
	 * Number of output points per iteration.
	 */
	public static final int NO_OF_PRINT_VALUES = 20;



	/**********************************************
	 * SignalProcConstants used in IMPULSE FILTER
	 ***********************************************/
	/**
	 * No of samples to replace for boundary condition.
	 */
	public static final int IMPULSE_NO_INITIAL_SAMPLES = 10;
	/**
	 * No of samples to find median for boundary condition.
	 */
	public static final int IMPULSE_INITIAL_MEDIAN_SIZE = 3;
	/**
	 * Threshold value.
	 */
	public static final int IMPULSE_THRESHOLD = 4;
	/**
	 * Percentage to check the distribution tail.
	 * <p> Higher the value more the impulse removed.</p>
	 */
	public static final int IMPULSE_PERCENTILE = 2; // ALWAYS < 100
	/**
	 * Percent of {@link SignalProcConstants#FS fs}.
	 * <p> Window size around 60.</p>
	 */
	public static final double IMPULSE_WINDOW_PERCENT = 0.06;




	/******************************************************
	 * SignalProcConstants used in FILTER LOW-HI-NOTCH
	 ******************************************************/


	/**
	 * High-pass filter co-efficients.
	 */
	public static final double[] FILTER_ZHIGH = {-0.98453370859689782};
	/**
	 * High-pass filter co-efficients.
	 */
	public static final double FILTER_BHIGH0 = 0.984533708596897;
	/**
	 * High-pass filter co-efficients.
	 */
	public static final double FILTER_BHIGH_SUM = -1.9690674171937941;

	public static final double[] FILTER_BHIGH = {FILTER_BHIGH0, FILTER_BHIGH0 + FILTER_BHIGH_SUM};


	/**
	 * High-pass filter co-efficients.
	 */
	public static final double FILTER_AHIGH_SUM = -1.969067417193793;
    public static final double[] FILTER_AHIGH = {1.0 , 1.0 + FILTER_AHIGH_SUM};
	/**
	 * Low-pass filter co-efficients.
	 */
	public static final double FILTER_ALOW[] = { 1.000000000000000,  -0.747789178258503,   0.272214937925007};
	/**
	 * Low-pass filter co-efficients.
	 */
	public static final double FILTER_BLOW[] = { 0.131106439916626 ,  0.262212879833252 ,  0.131106439916626};
	/**
	 * Low-pass filter co-efficients.
	 */
	public static final double[] FILTER_ZLOW = {0.86889356008337393, -0.141108498008381};

	/**
	 * Boundary extension to filters of order 2.
	 */
	public static final int FILTER_NFACT2 = 3;


	/**
	 * 50Hz Notch filter co-efficients.
	 */
	public static final double FILTER_BNOTCH_50[] = { 0.963653884165502, -1.83297861197747,
            0.963653884165502 };
	/**
	 * 50Hz Notch filter co-efficients.
	 */
	public static final double FILTER_ANOTCH_50[] = { 1.0, -1.83297861197747, 0.927307768331003};
	/**
	 * 50Hz Notch filter co-efficients.
	 */
	public static final double[] FILTER_ZNOTCH_50 = {0.036346115834507468, 0.036346115834507468 + -1.7319479184152442E-14 };

	/**
	 * 100Hz Notch filter co-efficients.
	 */
	public static final double FILTER_ANOTCH_100[] = {1.0, -1.55922473797064, 0.927307768331003};

	/**
	 * 100Hz Notch filter co-efficients.
	 */
	public static final double FILTER_BNOTCH_100[] = {  0.963653884165502, -1.55922473797064,
            0.963653884165502  };

	/**
	 * 100Hz Notch filter co-efficients.
	 */
	public static final double[] FILTER_ZNOTCH_100 = {0.036346115834500828 , 0.036346115834500828 + -4.5172199314436057E-15};

	/**
	 * 150Hz Notch filter co-efficients.
	 */
	public static final double FILTER_ANOTCH_150[] = {1.0, -1.13284308285368, 0.927307768331003};
	/**
	 * 150Hz Notch filter co-efficients.
	 */
	public static final double FILTER_BNOTCH_150[] = {0.963653884165502, -1.13284308285368,
            0.963653884165502 };

	/**
	 * 150Hz Notch filter co-efficients.
	 */
	public static final double[] FILTER_ZNOTCH_150 = {0.036346115834499253 , 0.036346115834499253 + -1.4849232954361469E-15};

	/**
	 * 200Hz Notch filter co-efficients.
	 */
	public static final double FILTER_ANOTCH_200[] = {1.0, -0.595570853805134, 0.927307768331003};

	/**
	 * 200Hz Notch filter co-efficients.
	 */
	public static final double FILTER_BNOTCH_200[] = { 0.963653884165502, -0.595570853805134,
            0.963653884165502};

	/**
	 * 200Hz Notch filter co-efficients.
	 */
	public static final double[] FILTER_ZNOTCH_200 = {0.036346115834498711 , 0.036346115834498711 + -4.3715031594615539E-16};

	/**
	 * 250Hz Notch filter co-efficients.
	 */
	public static final double FILTER_ANOTCH_250[] = {1.0, -1.1801356447292E-16, 0.927307768331003};
	/**
	 * 250Hz Notch filter co-efficients.
	 */
	public static final double FILTER_BNOTCH_250[] = { 0.963653884165502, -1.1801356447292E-16,
            0.963653884165502};

	/**
	 * 250Hz Notch filter co-efficients.
	 */
	public static final double[] FILTER_ZNOTCH_250 = {0.036346115834498482 , 0.036346115834498482};

	/**
	 * 300Hz Notch filter co-efficients.
	 */
	public static final double FILTER_ANOTCH_300[] = { 1.0, -1.61742423415, 0.999246301864936 };

	/**
	 * 300Hz Notch filter co-efficients.
	 */
	public static final double FILTER_BNOTCH_300[] = { 0.999623150932468, -1.61742423415,
		    0.999623150932468 };

	/**
	 * 300Hz Notch filter co-efficients.
	 */
	public static final double[] FILTER_ZNOTCH_300 = {0.000376849067531959 , 0.000376849067531959 + 7.6056782399858136E-17};

	/**
	 * 31.25 Hz Notch co-efficients
	 */
	public static final double[] FILTER_BNOTCH_3125 = {0.999201342487990751273230216611409559846,
			-1.96000393774273651281703223503427579999 ,
			 0.999201342487990751273230216611409559846};
	/**
	 * 31.25 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ANOTCH_3125 = {1,
			-1.96000393774273651281703223503427579999 ,
			 0.998402684975981502546460433222819119692};

	/**
	 * 31.25 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ZNOTCH_3125 = {0.00079865751201190806, 0.00079865751201190806 + -5.3038086075818391E-15};

	/**
	 * 93.75 Hz Notch co-efficients
	 */
	public static final double[] FILTER_BNOTCH_9375 = {0.999201342487990751273230216611409559846,
			-1.661611105701344737184399491525255143642,
			0.999201342487990751273230216611409559846};
	/**
	 * 93.75 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ANOTCH_9375 = {1,
			-1.661611105701344737184399491525255143642,
			0.998402684975981502546460433222819119692};

	/**
	 * 93.75 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ZNOTCH_9375 = {0.798657512009560E-3 , 0.798657512008948E-3};

	/**
	 * 156.25 Hz Notch co-efficients
	 */
	public static final double[] FILTER_BNOTCH_15625 = {0.999201342487990751273230216611409559846,
			-1.110253045359104850930975771916564553976,
			0.999201342487990751273230216611409559846};
	/**
	 * 156.25 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ANOTCH_15625 = {1,
			-1.110253045359104850930975771916564553976,
			0.998402684975981502546460433222819119692};

	/**
	 * 156.25 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ZNOTCH_15625 = {0.798657512009263E-3,
			0.798657512009245E-3};
	/**
	 * 218.75 Hz Notch co-efficients
	 */
	public static final double[] FILTER_BNOTCH_21875 = {0.999201342487990751273230216611409559846,
			-0.389869023329859676341158092327532358468,
			0.999201342487990751273230216611409559846};
	/**
	 * 218.75 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ANOTCH_21875 = {1,
			-0.389869023329859676341158092327532358468,
			0.998402684975981502546460433222819119692};

	/**
	 * 218.75 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ZNOTCH_21875 = {0.798657512009246E-3,
			0.798657512009262E-3};

	/**
	 * 281.25 Hz Notch co-efficients
	 */
	public static final double[] FILTER_BNOTCH_28125 = {0.999201342487990751273230216611409559846,
			0.38986902332985939878540193603839725256,
			0.999201342487990751273230216611409559846};
	/**
	 * 281.25 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ANOTCH_28125 = {1,
			0.38986902332985939878540193603839725256,
			0.998402684975981502546460433222819119692};

	/**
	 * 281.25 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ZNOTCH_28125 = {0.798657512009259E-3,
			0.798657512009249E-3};

	/**
	 * 343.75 Hz Notch co-efficients
	 */
	public static final double[] FILTER_BNOTCH_34375 = {0.999201342487990751273230216611409559846,
            1.110253045359104184797160996822640299797,
            0.999201342487990751273230216611409559846};
	/**
	 * 343.75 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ANOTCH_34375 = {1,
            1.110253045359104184797160996822640299797,
            0.998402684975981502546460433222819119692};

	/**
	 * 343.75 Hz Notch co-efficients
	 */
	public static final double[] FILTER_ZNOTCH_34375 = {0.798657512009251E-3,
            0.798657512009256E-3};

    /**
     * 406.25 Hz Notch co-efficients
     */
    public static final double[] FILTER_BNOTCH_40625 = {0.999201342487990751273230216611409559846,
            1.661611105701344959229004416556563228369,
            0.999201342487990751273230216611409559846};
    /**
     * 406.25 Hz Notch co-efficients
     */
    public static final double[] FILTER_ANOTCH_40625 = {1,
            1.661611105701344959229004416556563228369,
            0.998402684975981502546460433224069119692};

    /**
     * 406.25 Hz Notch co-efficients
     */
    public static final double[] FILTER_ZNOTCH_40625 = {0.798657512009226E-3,
            0.798657512009282E-3};

    /**
     * 468.75 Hz Notch co-efficients
     */
    public static final double[] FILTER_BNOTCH_46875 = {0.999201342487990751273230216611409559846,
            1.96000393774273651281703223503427579999,
            0.999201342487990751273230216611409559846};
    /**
     * 468.75 Hz Notch co-efficients
     */
    public static final double[] FILTER_ANOTCH_46875 = {1,
            1.96000393774273651281703223503427579999,
            0.998402684975981502546460433222819119692};

    /**
     * 468.75 Hz Notch co-efficients
     */
    public static final double[] FILTER_ZNOTCH_46875 = {0.798657512009228E-3,
            0.798657512009280E-3};


    /**********************************************
	 * SignalProcConstants used in QRS DETECTION
	 ***********************************************/

	/**
	 * Derivative filter.
	 */
	public static final double[] MQRS_DERIVATIVE = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 0,
	        -15, -14, -13, -12, -11, -10, -9, -8, -7, -6, -5, -4, -3, -2, -1};
	/**
	 * Derivative filter.
	 */
	public static final int MQRS_DERIVATIVE_SCALE = 300;

	// Maternal QRS detection values
	/**
	 * Threshold scale for maternal QRS detection.
	 */
	public static final double MQRS_THRESHOLD_SCALE = 10;
	/**
	 * MQRS detection : high-pass filter co-efficients.
	 */
	public static final double[] MQRS_AHIGH = { 1.000000000000000,  -0.994986042646556 };
	/**
	 * MQRS detection : high-pass filter co-efficients.
	 */
	public static final double MQRS_BHIGH0 = 0.997493021323278;
	/**
	 * MQRS detection : high-pass filter co-efficients.
	 */
	public static final double MQRS_BHIGH_SUM = -1.994986042646556;
	/**
	 * MQRS detection : high-pass filter co-efficients.
	 */
	public static final double MQRS_ZHIGH[] = {-0.997493021323276};
	/**
	 * MQRS detection : low-pass filter co-efficients.
	 */
	public static final double[] MQRS_ALOW = { 1.000000000000000,  -0.981325890492688 };
	/**
	 * MQRS detection : low-pass filter co-efficients.
	 */
	public static final double[] MQRS_BLOW = { 0.009337054753656,   0.009337054753656 };
	/**
	 * MQRS detection : low-pass filter co-efficients.
	 */
	public static final double MQRS_ZLOW[] = {0.990662945246346};
	/**
	 * Threshold to remove outliers.
	 */
	public static final int MQRS_THRESHOLD_LENGTH = 13500;//(1 - 10/100)* 15000;
    /**
     * MQRS window for integrator.
     */
    public static final int MQRS_WINDOW = 50;

    /**
	 * Derivative filter.
	 */
	public static final double[] FQRS_DERIVATIVE = { -10, -9, -8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8,
			9, 10 };
	/**
	 * Derivative filter.
	 */
	public static final int FQRS_DERIVATIVE_SCALE = 64;
    /**
	 * FQRS detection : Integrator window.
	 */
	public static final int FQRS_WINDOW = 25;


	// Fetal QRS detection values
	/**
	 * Threshold scale for maternal QRS detection.
	 */
	public static final double FQRS_THRESHOLD_SCALE = 20;
	/**
	 * FQRS detection : high-pass filter co-efficients.
	 */
	public static final double[] FQRS_AHIGH = { 1.000000000000000, -0.987511929907314 };
	/**
	 * FQRS detection : high-pass filter co-efficients.
	 */
	public static final double FQRS_BHIGH0 = 0.993755964953657;
	/**
	 * FQRS detection : high-pass filter co-efficients.
	 */
	public static final double FQRS_BHIGH_SUM = -1.9875119299073141;
	/**
	 * FQRS detection : high-pass filter co-efficients.
	 */
	public static final double FQRS_ZHIGH[] = {-0.99375596495365437};
	/**
	 * FQRS detection : low-pass filter co-efficients.
	 */
	public static final double[] FQRS_ALOW = { 1.000000000000000, -0.978247159730251 };
	/**
	 * FQRS detection : low-pass filter co-efficients.
	 */
	public static final double[] FQRS_BLOW = { 0.010876420134875, 0.010876420134875 };
	/**
	 * FQRS detection : low-pass filter co-efficients.
	 */
	public static final double FQRS_ZLOW[] = {0.98912357986517108};

	/**
	 * Threshold scale for maternal QRS detection.
	 */
	public static final double FQRS_INTEGRATOR_THRESHOLD_SCALE = 19;
	/**
	 * Integrator upper limit to calculate threshold.
	 */
	public static final double QRS_INTEGRTOR_MAX = 0.9;
	/**
	 * Integrator lower limit to calculate threshold.
	 */
	public static final double QRS_INTEGRATOR_MIN = 0.1;
	/**********************************************
	 * SignalProcConstants used in CHANNEL SELECTION
	 ***********************************************/
	/**
	 * Variance used in maternal QRS selection.
	 */
	public static final int MQRS_VARIANCE_THRESHOLD = 120;
	/**
	 * RR lower threshold for maternal QRS.
	 */
	public static final int MQRS_RR_LOW_TH = 400;
	/**
	 * RR upper threshold for maternal QRS.
	 */
	public static final int MQRS_RR_HIGH_TH = 1200;

	/**
	 * Variance used in fetal QRS selection.
	 */
	public static final int FQRS_VARIANCE_THRESHOLD = 60;
	/**
	 * RR upper threshold for fetal QRS.
	 */
	public static final int FQRS_RR_LOW_TH = 285;
	/**
	 * RR upper threshold for fetal QRS.
	 */
	public static final int FQRS_RR_HIGH_TH = 700;


	public static final double CHANNEL_PERCENTAGE = 3.0/100;

	/**********************************************
	 * SignalProcConstants used in QRS SELECTION
	 ***********************************************/
	/** CHANGE BY ARAVIND 5th March 2018
	 * Variance change for 10 bpm in milliseconds.
	 */
	public static final double QRS_RR_VAR_M = 20.0/60000; // 10 beats change
	/** CHANGE BY ARAVIND 5th March 2018
	 * Variance change for 15 bpm in milliseconds.
	 */
	public static final double QRS_RR_VAR = 21.0/60000; // 17 beats change

	public static final double QRS_RR_VAR_Concat = 8.0/60000; // 10 beats change

//    public static final double QRS_RR_VAR_Continuous = 18.0/60000; // 18 beats change


    /**
	 * No of RR mean to find HR.
	 */
	public static final double QRS_NO_RR_MEAN = 4.0;
	/**
	 * RR {@literal Low %} threshold.
	 */
	public static final double QRS_RRLOW_PERC = 0.8;// 0.763128816516429;
	/**
	 * RR {@literal High %} threshold.
	 */
	public static final double QRS_RRHIGH_PERC = 1.2;// 1.261883214824392;
	/**
	 * RR {@literal Miss %} threshold.
	 */
	public static final double QRS_RR_MISS_PERCENT = 1.66;
	/**
	 * Maximum possible interpolation.
	 */
	public static final int QRS_LENGTH_MAX_INTERPOLATE = 2000; // 2 secs of data
	/**
	 * Last plot time instance in an iteration.
	 */
	public static final int QRS_END_VALUE = 12000; //
	/**
	 * First plot time instance in an iteration.
	 */
	public static final int QRS_START_VALUE = 2000; //
	/**
	 * Plot time duration in an iteration.
	 */
	public static final int QRS_SHIFT = 10000;

	// public static final int QRSM_HIGH_RR_THRESHOLD = 400;
	// public static final int QRSM_LOW_RR_THRESHOLD = 200;

	// public static final double QRSM_RR_MISS_PERC = 0.4;
	// public static final int QRSM_INITIAL_RR_COUNT = 10;
	// public static final int QRSM_RR_COUNT = 8;
	// public static final int QRSF_INITIAL_RR_COUNT = 9;
	// public static final int QRSF_RR_COUNT = 8;

	/**********************************************
	 * SignalProcConstants used in QRSM CANCELLATION
	 ***********************************************/

	/**
	 * Start percentile value to find mean RR.
	 */
	public static final int CANCEL_PERCI = 4;
	/**
	 * End percentile value to find mean RR.
	 */
	public static final int CANCEL_PERCF = 4;
	/**
	 * Number of {@literal %} samples before QRS.
	 */
	public static final double CANCEL_QRS_BEFORE_PERC = 0.2;
	/**
	 * Number of {@literal %} samples after QRS.
	 */
	public static final double CANCEL_QRS_AFTER_PERC = 0.8;
	/**
	 * Threshold to number of {@literal %} samples before QRS.
	 */
	public static final double CANCEL_QRS_AFTER_TH = 0.5;
	/**
	 * Threshold to number of {@literal %} samples to be present after last QRS.
	 */
	public static final double CANCEL_LASTQRS_TH_HIGH_PERC = 0.15;
	/**
	 * Threshold to number of {@literal %} samples to be present after last QRS.
	 */
	public static final double CANCEL_LASTQRS_TH_LOW_PERC = 0.1;
	/**
	 * Find RR mean of last 5 qrs to determine number of samples to extend.
	 */
	public static final int CANCEL_NO_SAMPLES_END = 5;


	/**
	 * Filter coefficients UC High - pass
	 */
	public static final int UA_HIGH_ORDER = 10;
	/**
	 * Filter coefficients UC High - pass
	 */
	public static final double[][] UA_HIGH_SOS =
			{{1,  -2,  1,  1,  -1.999406878148268340567028644727542996407,  0.999410430157413154361734086705837398767},
			{1,  -2,  1,  1,  -1.998286410501096455050173972267657518387,  0.998289960519695429574937861616490408778},
			{1,  -2,  1,  1,  -1.997334271812534867152066908602137118578,  0.997337820139629349647236722375964745879},
			{1,  -2,  1,  1,  -1.996643071455868945207612341619096696377,  0.996646618555024454977342429629061371088},
			{1,  -2,  1,  1,  -1.996279877755120502058616693830117583275,  0.99628342420905080523141350568039342761}};
	/**
	 * Filter coefficients UC High - pass
	 */
	public static final double[] UA_HIGH_GAIN =
			{0.999704327076420318221039451600518077612,
			0.999144092755197998911853574099950492382,
			0.998668022988041026444250292115611955523,
			0.998322422502723405557389924069866538048,
			0.99814082549104277131135631861980073154};
	/**
	 * Filter coefficients UC High - pass
	 */
	public static final double[][] UA_HIGH_Z =
			{{-0.999704327051177,	-0.999144092731476,	-0.998668023003056,	-0.998322422514772,	-0.998140825531731},
			{0.999704327051192,	0.999144092731517,	0.998668023003016,	0.998322422514731,	0.998140825531580}};
	/**
	 * Filter coefficients UC Low - pass
	 */
	public static final int UA_LOW_ORDER = 10;
	/**
	 * Filter coefficients UC Low - pass
	 */
	public static final double[][] UA_LOW_SOS =
			{{1,  2,  1,  1,  -1.993765994842548350263200518384110182524,  0.994120245556551984655868636764353141189},
			{1,  2,  1,  1,  -1.982678896530242873552651872159913182259,  0.983031177297671421300151450850535184145},
			{1,  2,  1,  1,  -1.973344249781298964663278638909105211496,  0.973694871976315212691588385496288537979},
			{1,  2,  1,  1,  -1.966617173203114798951673947158269584179,  0.96696660013665725053044752712594345212 },
			{1,  2,  1,  1,  -1.963098901496112880238342768279835581779,  0.963447703306011149493315315339714288712}};
	/**
	 * Filter coefficients UC Low - pass
	 */
	public static final double[] UA_LOW_GAIN =
			{0.000088562678500918572827016461701532535 ,
			0.000088070191857109438797295009049292958 ,
			0.00008765554875401466810008049845848177  ,
			0.000087356733385618410571947511922274998 ,
			0.000087200452474582668756404590926223364};
	/**
	 * Filter coefficients UC Low - pass
	 */
	public static final double[][] UA_LOW_Z =
			{{0.999911437321527,	0.999911929807679,	0.999912344450694,	0.999912643266594,	0.999912799547704},
			{-0.994031682878078,	-0.982943107105358,	-0.973607216427024,	-0.966879243403252,	-0.963360502853709}};

	/**
	 * UC Window for integrator
	 */
	// Change Aravind from 60 to 80
	public static final int UA_WINDOW = 80;

	public static final int UA_BATCH_DATA_SIZE = 120;

	/**
	 * MA CONSTANTS
	 */

	public static final int MA_SHIFT = 1000;
	public static final int MA_DELTA = 2000;
	public static final double MA_PSD_TH = 0.15;
	public static final int MA_COUNT_TH = 3;


}
