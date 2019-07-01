package SignalProc;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class SignalProcUtils {


    public static double[][] mDoubleArrayBuffer = new double[SignalProcConstants.BUFFER_SIZE][SignalProcConstants.NO_OF_CHANNELS];
    public static double[] mDoubleArrayUC = new double[SignalProcConstants.BUFFER_SIZE];
    public static int HANDLE_DATA_SIZE = 15000;

    public static int locationIteration = 0;

    public static int offsetCount = 0;

    /**
     * Motion Artifact end location
     */
    public static int MA_Shift = 0;
    /**
     * Motion Artifact flag
     */
    public static boolean MA_FLAG = false;

    /**
     * Iteration count.
     */
    public static int currentIteration = 0;

    /**
     * Interpolate Counter - remove
     */
    public static int interpolationCount = 0;

    public static int samplesToRemove = 0;

    /**
     * TO check 3 iterations of null data.
     */
    public static int lastIteration;

    /**
     * Stores the sample index of last valid data
     */
    public static int lastSampleIndex = -1;

    /**
     * Stores the sample index of current valid data
     */
    public static int currentSampleIndex = -1;

    /**
     * Counter for data loss and motion artifact shift
     */
    public static int dataLossCounter = 0;
    /**
     * Start location of the particular iteration
     */
    public static int qrsCurrentShift = 0;


    public static int iter = 0;

    public static int concatCount = 0;

    public static int independentCount = 0;



    /*********************************************************
     * OUTPUT values for each iteration.
     *********************************************************/

    /**
     * Output FHR for iteration.  - remove
     */
    public static int[] fhrPlot = new int[SignalProcConstants.NO_OF_PRINT_VALUES];
    /**
     * Output of MHR for iteration. - remove
     */
    public static int[] mhrPlot = new int[SignalProcConstants.NO_OF_PRINT_VALUES];

    /**
     * Output FHR print for iteration. - remove
     */
    public static String[] fhrPrint = new String[SignalProcConstants.NO_OF_PRINT_VALUES];
    /**
     * Output MHR print for iteration. - remove
     */
    public static String[] mhrPrint = new String[SignalProcConstants.NO_OF_PRINT_VALUES];






    /**********************************************
     * Variables to compute HR of Maternal and Fetal
     * throughout the duration of test
     ***********************************************/

    /**
     * Final fetal QRS locations.
     */
    public static List<Integer> qrsFetalLocation = new ArrayList<>();
    /**
     * Final fetal heart rate.
     */
    public static List<Float> hrFetal = new ArrayList<>();

    /**
     * Last QRS miss location in final array. - remove
     */
    public static int qrsFetalMissLocation = 0;
    public static int qrsMaternalMissLocation = 0;
    
    /**
     * Size of {@link SignalProcUtils#qrsFetalLocation fetal QRS location}. - remove
     */
    public static int lastFetalPlotIndex = 1;
    /**
     * A new heart rate is started in the iteration. - remove
     */
    public static int fetalHrNew = 0;
    /**
     * Last QRS value is greater than 12000 in the iteration. - remove
     */
    public static int fetalHrEnd = 0;
    /**
     * Temp QRS locations of the iteration.
     */
    public static LinkedList<Integer> qrsfLocTemp = new LinkedList<>();
    /**
     * Temp HR of the iteartion.
     */
    public static LinkedList<Float> hrfTemp = new LinkedList<>();
    /**
     * Last iteration with fetal peaks selected. - remove
     */
    public static int lastQRSFIteration = -1;
    /**
     * Flag to determine detection of fetal QRS locations in the iteration.
     */
    public static int noDetectionFlagFetal = 0;
    /**
     * RR mean using the last 4 fetal QRS peaks detected in previous iteration.
     */
    public static double lastRRMeanFetal = 0;
    /**
     * RR mean of last Independent or confirm flag iteration.
     */
    public static double lastvalidRRMeanFetal = 0;
    /**
     *  Flag for independent or confirm flag detection
     */
    public static boolean independantdet_flag = false;
    /**
     * Last fetal QRS location detected in previous iteration.
     */
    public static int lastQRSFetal = 0;
    /**
     * Last fetal QRS location >10000 detected in previous iteration.
     */
    public static List<Integer> lastQRSFetalArray = new ArrayList<>();
    /**
     * Interpolated length of fetal peaks at the end of previous iteration.
     */
    public static int interpolatedLengthFetal = 0;

    /**
     * Final maternal QRS locations.
     */
    public static List<Integer> qrsMaternalLocation = new ArrayList<>();
    /**
     * Final maternal heart rate.
     */
    public static List<Float> hrMaternal = new ArrayList<>();
    /**
     * MHR computed
     */
    public static boolean mhrComputed = false;
    /**
     * Size of {@link SignalProcUtils#qrsMaternalLocation maternal QRS location}. - remove
     */
    public static int lastMaternalPlotIndex = 1;
    /**
     * Last iteration with maternal peaks selected.  - remove
     */
    public static int lastQRSMIteration = -1;


    public static boolean rrMeanCheck_Maternal = false;
    /**
     * Flag to determine detection of maternal QRS locations in the iteration.
     */
    public static int noDetectionFlagMaternal = 0; // - remove
    public static double lastRRMeanMaternal = 0; // - remove
    public static int lastQRSMaternal = 0;  // - remove
    public static int InterpolatedLengthMaternal = 0;  // - remove
    /**
     * Temp MQRS locations of the iteration
     */
    public static LinkedList<Integer> qrsmLocTemp = new LinkedList<>();

    public static int maternalHrNew = 0; // - remove
    /**
     * Temp MHR values of the iteration
     */
    public static LinkedList<Float> hrmTemp = new LinkedList<>();
    public static int maternalHrEnd = 0; // - remove


    /**
     * Global Constant Window
     */

    public static double[][] trapezodialWindow;
    public static int[][] trapezodialWindowRegion = new int[5][3];
    public static double slope1, slope2;
    public static int noSamplesBeforeQRS, noSamplesAfterQRS;
    /**
     * UC Previous Energy - remove
     */
    public static double uaPreviousEnergy = 0;
    /**
     * UC Plot counter
     */
    public static int uaCounter = 0;
    public static double uaScale = 0;
    public static double uaAvgEnergy = 0;
    public static boolean UArecheck = false;
    public static boolean UArecheck_global = false;


    public static LinkedList<Double> uaEnergyTemp = new LinkedList<>();
//    public static Double[] aUC_Energy_iter = new Double[120];
//    public static Double[] aUC_Energy_iter_test = new Double[120];
    public static ArrayList<Double> aUA_Energy_plot = new ArrayList<>();
    public static List<Double> aUA_Energy_iter = new ArrayList<>();
    public static List<Double> aUA_Energy_iter_test = new ArrayList<>();
    public static List<Integer> aLocation_UA_Batch = new ArrayList<>();

    public static ArrayList<Double> UA = new ArrayList<>();
    public static ArrayList<Integer> UALoc = new ArrayList<>();



    private static double[] mLeadOffSine;
    private static double[] mLeadOffCosine;

    /**
     *  MA VARIABLES
     */
    public static LinkedList<Double> ma_amplitude1 = new LinkedList<>();
    public static LinkedList<Double> ma_amplitude2 = new LinkedList<>();
    public static LinkedList<Double> ma_amplitude3 = new LinkedList<>();
    public static LinkedList<Double> ma_amplitude4 = new LinkedList<>();

    public static double[][] ma_amplitudeFlag = new double[12][5];
    public static double[][] ma_psdFlag = new double[12][5];
    public static double[] ma_overlap = new double[10];


    /**
     * FIND SINE for lead off
     * @param z
     * @return
     */
    public static double findSine(int z){
        if (mLeadOffSine == null){
            mLeadOffSine = new double[SignalProcConstants.LEADOFF_DETECTION_LENGTH];
            double aFreq = SignalProcConstants.LEADOFF_FREQ;

            for (int i = 0; i < SignalProcConstants.LEADOFF_DETECTION_LENGTH; i++) {
                mLeadOffSine[i] = Math.sin(2 * Math.PI * aFreq * i/SignalProcConstants.FS);
            }
        }
        return mLeadOffSine[z];
    }
    public static double findCosine(int z){
        if (mLeadOffCosine == null){
            mLeadOffCosine = new double[SignalProcConstants.LEADOFF_DETECTION_LENGTH];
            double aFreq = SignalProcConstants.LEADOFF_FREQ;

            for (int i = 0; i < SignalProcConstants.LEADOFF_DETECTION_LENGTH; i++) {
                mLeadOffCosine[i] = Math.cos(2 * Math.PI * aFreq * i/SignalProcConstants.FS);
            }
        }
        return mLeadOffCosine[z];
    }

    public static void reset() {

        qrsCurrentShift = 0;
        currentIteration = 0;
        qrsFetalLocation.clear();
        hrFetal.clear();
        qrsMaternalLocation.clear();
        hrMaternal.clear();
        lastFetalPlotIndex = 1;
        lastMaternalPlotIndex = 1;

        MA_Shift = 0;
        MA_FLAG = false;

        lastIteration = -1;

        fhrPlot = new int[SignalProcConstants.NO_OF_PRINT_VALUES];
        mhrPlot = new int[SignalProcConstants.NO_OF_PRINT_VALUES];
        fhrPrint = new String[SignalProcConstants.NO_OF_PRINT_VALUES];
        mhrPrint = new String[SignalProcConstants.NO_OF_PRINT_VALUES];

        lastQRSMIteration = -1;
        noDetectionFlagMaternal = 0;
        mhrComputed = false;

        qrsfLocTemp.clear();
        hrfTemp.clear();
        lastQRSFIteration = -1;
        noDetectionFlagFetal = 0;
        lastRRMeanFetal = 0;
        lastvalidRRMeanFetal = 0;
        independantdet_flag = false;

        lastQRSFetal = 0;
        lastQRSFetalArray.clear();
        interpolatedLengthFetal = 0;
        interpolationCount = 0;

        rrMeanCheck_Maternal = false;
        lastRRMeanMaternal = 0;
        lastQRSMaternal = 0;

        qrsmLocTemp.clear();
        hrmTemp.clear();

        dataLossCounter = 0;

        uaCounter = 0;
        uaScale = 0;
        uaAvgEnergy = 0;
        uaPreviousEnergy = 0;
        uaEnergyTemp.clear();
        aUA_Energy_plot.clear();
        aLocation_UA_Batch.clear();
        aUA_Energy_iter.clear();// = new double[SignalProcConstants.UA_BATCH_DATA_SIZE];
        aUA_Energy_iter_test.clear();// = new double[SignalProcConstants.UA_BATCH_DATA_SIZE];
        UArecheck = false;
        UArecheck_global = false;
        UA.clear();
        UALoc.clear();
        iter = 0;
        concatCount = 0;
        independentCount = 0;

        lastSampleIndex = -1;
        currentSampleIndex = -1;

        ma_amplitude1 = new LinkedList<>();
        ma_amplitude2 = new LinkedList<>();
        ma_amplitude3 = new LinkedList<>();
        ma_amplitude4 = new LinkedList<>();

    }
}
