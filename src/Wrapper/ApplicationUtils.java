package Wrapper;

import SignalProc.SignalProcConstants;

import java.util.LinkedList;
import java.util.Queue;

public class ApplicationUtils {

    public static int HANDLE_DATA_SIZE = 15000;
    public static int RETAINED_DATA_SIZE = 5000;
    public static int algoProcessStartCount = -1;

    public static double[][] mDoubleArrayBuffer = new double[SignalProcConstants.BUFFER_SIZE][SignalProcConstants.NO_OF_CHANNELS];
    public static double[] mDoubleArrayUC = new double[SignalProcConstants.BUFFER_SIZE];

    public static Queue<String> mSampleMasterQueue = new LinkedList<>();



}
