package Wrapper;

import SignalProc.SignalProcConstants;
import SignalProc.SignalProcUtils;
import helper.ExecutorServiceHelper;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class ConversionHelper {

    private static final double DOUBLE_UNSIGNED = Math.pow(2, 23);

    private static final double V_REF = 4.5;

    private static final double GAIN = 24;

    private static final double DOUBLE_SIGNED = 2 * DOUBLE_UNSIGNED;

    private static final int NO_SAMPLE_INDICES = 2;

    private static final int NO_CHAR_PER_CHANNEL = 6;

    private double[] aUcFinal;

    private ArrayList<SampleMissed> mSampleMissedList = new ArrayList<>();

    private int mSampleCounter;

    private static int mNextValidSampleCounter = -1;

    private Object aFinal[];

    private long mTotalTime, mStartTime = 0;

    private void resetData() {
        mSampleMissedList.clear();
        mSampleCounter = 0;
        mTotalTime = 0;

    }

    public void convert() {
        if (populateInputArray()) {
            SignalProcUtils.lastIteration = SignalProcUtils.currentIteration;
            ApplicationUtils.HANDLE_DATA_SIZE = 10000;
            ApplicationUtils.RETAINED_DATA_SIZE = 5000;
        } else {
//            ApplicationUtils.mConversionFlag = ApplicationUtils.IDLE;

            return;
        }

    }


    private boolean populateInputArray() {
//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Iteration " + (ApplicationUtils.algoProcessStartCount+1), FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);

        long aST = System.currentTimeMillis();

        int aInputArrayCounter = SignalProcConstants.NO_OF_SAMPLES - ApplicationUtils.HANDLE_DATA_SIZE;

        SignalProcUtils.interpolationCount = 0;
        int aDiffSampleIndex;

        String aSemiValidSample = getNextSemiValidSample();

        int aLastSampleIndex1, aLastSampleIndex2;
        int aCurrentSampleIndex1, aCurrentSampleIndex2;

        if (aSemiValidSample != null && (aSemiValidSample.length() == SignalProcConstants.SAMPLE_LENGTH)) {
            if (ApplicationUtils.HANDLE_DATA_SIZE == 15000) {
                aLastSampleIndex1 = (int) (aSemiValidSample.charAt(0)) - SignalProcConstants.ASCII_START;
                aLastSampleIndex2 = (int) (aSemiValidSample.charAt(1)) - SignalProcConstants.ASCII_START;
                SignalProcUtils.lastSampleIndex = aLastSampleIndex1 * SignalProcConstants.ASCII_DIFF + aLastSampleIndex2;
                feedInputArray(aSemiValidSample, aInputArrayCounter);
            } else {
                aInputArrayCounter--;
            }

            while (aInputArrayCounter < SignalProcConstants.NO_OF_SAMPLES) {
                aInputArrayCounter++;
                aSemiValidSample = getNextSemiValidSample();

                if (aSemiValidSample != null && (aSemiValidSample.length() == SignalProcConstants.SAMPLE_LENGTH)) {
                    aCurrentSampleIndex1 = (int) (aSemiValidSample.charAt(0)) - SignalProcConstants.ASCII_START;
                    aCurrentSampleIndex2 = (int) (aSemiValidSample.charAt(1)) - SignalProcConstants.ASCII_START;
                    SignalProcUtils.currentSampleIndex = aCurrentSampleIndex1 * SignalProcConstants.ASCII_DIFF + aCurrentSampleIndex2;
                    aDiffSampleIndex = SignalProcUtils.currentSampleIndex - SignalProcUtils.lastSampleIndex;

                    if (aDiffSampleIndex != 1 && aDiffSampleIndex != -(SignalProcConstants.MAX_MISS_DETECTION - 1)) {
                        if (aDiffSampleIndex <= 0) {
                            aDiffSampleIndex = SignalProcConstants.MAX_MISS_DETECTION + aDiffSampleIndex;

                            if (aDiffSampleIndex > mNextValidSampleCounter) {
//                                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Input Array Index    : " + (aInputArrayCounter-1), FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                          sample  missed : " + aSemiValidSample, FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                    no of samples missed : " + (aDiffSampleIndex-1), FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                Timber.i(" : current sample index : %d", aInputArrayCounter);
//                                Timber.i(" : no of samples missed : %d", aDiffSampleIndex);

                                feedInputArray(aSemiValidSample, aInputArrayCounter + aDiffSampleIndex - 1);

                                if ((aDiffSampleIndex - 1) < 5) {
                                    doLinearInterpolation(aInputArrayCounter - 1, aDiffSampleIndex - 1);
                                } else {
                                    SampleMissed sampleMissed = new SampleMissed(aInputArrayCounter - 1, aDiffSampleIndex - 1);
                                    mSampleMissedList.add(sampleMissed);
                                }


                                aInputArrayCounter += aDiffSampleIndex - 1;
                                SignalProcUtils.interpolationCount += aDiffSampleIndex - 1;

                                if (aDiffSampleIndex > SignalProcConstants.MAX_DATA_LOSS && aInputArrayCounter < SignalProcConstants.BUFFER_SIZE) {
                                    SignalProcUtils.dataLossCounter += aInputArrayCounter;
                                    ApplicationUtils.mDoubleArrayBuffer[0][0] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][0];
                                    ApplicationUtils.mDoubleArrayBuffer[0][1] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][1];
                                    ApplicationUtils.mDoubleArrayBuffer[0][2] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][2];
                                    ApplicationUtils.mDoubleArrayBuffer[0][3] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][3];
                                    ApplicationUtils.mDoubleArrayUC[0] = ApplicationUtils.mDoubleArrayUC[aInputArrayCounter];
                                    SignalProcUtils.lastSampleIndex = SignalProcUtils.currentSampleIndex;
                                    ApplicationUtils.HANDLE_DATA_SIZE = 15000 - 1; // Have saved the last value of previous iteration
                                    return false;
                                } else if (aDiffSampleIndex > SignalProcConstants.MAX_DATA_LOSS && aInputArrayCounter >= SignalProcConstants.BUFFER_SIZE) {
                                    ApplicationUtils.HANDLE_DATA_SIZE = 15000;
                                    SignalProcUtils.lastSampleIndex = -1;
                                    return false;
                                }
                            } else {
//                                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Input Array Index    : " + (aInputArrayCounter-1), FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                          sample  missed : " + aSemiValidSample, FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                    no of samples missed : " + mNextValidSampleCounter, FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                Timber.i(" : current sample index : %d", aInputArrayCounter);
//                                Timber.i(" : no of samples missed : %d", mNextValidSampleCounter);
                                feedInputArray(aSemiValidSample, aInputArrayCounter + mNextValidSampleCounter);

                                if (mNextValidSampleCounter < 5) {
                                    doLinearInterpolation(aInputArrayCounter - 1, mNextValidSampleCounter);
                                } else {
                                    SampleMissed sampleMissed = new SampleMissed(aInputArrayCounter - 1, mNextValidSampleCounter);
                                    mSampleMissedList.add(sampleMissed);
                                }

                                aInputArrayCounter += mNextValidSampleCounter;
                                // Change Aravind
                                SignalProcUtils.interpolationCount += mNextValidSampleCounter;
                                // End

                                if (aDiffSampleIndex > SignalProcConstants.MAX_DATA_LOSS && aInputArrayCounter < SignalProcConstants.BUFFER_SIZE) {
                                    SignalProcUtils.dataLossCounter += aInputArrayCounter;
                                    ApplicationUtils.mDoubleArrayBuffer[0][0] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][0];
                                    ApplicationUtils.mDoubleArrayBuffer[0][1] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][1];
                                    ApplicationUtils.mDoubleArrayBuffer[0][2] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][2];
                                    ApplicationUtils.mDoubleArrayBuffer[0][3] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][3];
                                    ApplicationUtils.mDoubleArrayUC[0] = ApplicationUtils.mDoubleArrayUC[aInputArrayCounter];
                                    SignalProcUtils.lastSampleIndex = SignalProcUtils.currentSampleIndex;
                                    ApplicationUtils.HANDLE_DATA_SIZE = 15000 - 1;

                                    return false;
                                } else if (aDiffSampleIndex > SignalProcConstants.MAX_DATA_LOSS && aInputArrayCounter >= SignalProcConstants.BUFFER_SIZE) {
                                    ApplicationUtils.HANDLE_DATA_SIZE = 15000;
                                    SignalProcUtils.lastSampleIndex = -1;
                                    return false;
                                }
                            }
                        } else {
                            if (aDiffSampleIndex > mNextValidSampleCounter) {
                                if ((aInputArrayCounter - 1) != 4999) {
//                                    FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Input Array Index : " + (aInputArrayCounter-1), FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                    FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                       sample  missed : " + aSemiValidSample, FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                    FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                current sample  Index : " + SignalProcUtils.currentSampleIndex, FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                    FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                   last sample  Index : " + SignalProcUtils.lastSampleIndex, FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                    FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                 no of samples missed : " + (aDiffSampleIndex-1), FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                    Timber.i(" : current sample index : %d", aInputArrayCounter);
//                                    Timber.i(" : no of samples missed : %d", aDiffSampleIndex);
                                }

                                feedInputArray(aSemiValidSample, aInputArrayCounter + aDiffSampleIndex - 1);

                                if ((aDiffSampleIndex - 1) < 5) {
                                    doLinearInterpolation(aInputArrayCounter - 1, aDiffSampleIndex - 1);
                                } else {
                                    SampleMissed sampleMissed = new SampleMissed(aInputArrayCounter - 1, aDiffSampleIndex - 1);
                                    mSampleMissedList.add(sampleMissed);
                                }


                                aInputArrayCounter += aDiffSampleIndex - 1;
                                // Change Aravind
                                SignalProcUtils.interpolationCount += aDiffSampleIndex - 1;
                                // End

                                if (aDiffSampleIndex > SignalProcConstants.MAX_DATA_LOSS && aInputArrayCounter < SignalProcConstants.BUFFER_SIZE) {
                                    SignalProcUtils.dataLossCounter += aInputArrayCounter;
                                    ApplicationUtils.mDoubleArrayBuffer[0][0] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][0];
                                    ApplicationUtils.mDoubleArrayBuffer[0][1] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][1];
                                    ApplicationUtils.mDoubleArrayBuffer[0][2] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][2];
                                    ApplicationUtils.mDoubleArrayBuffer[0][3] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][3];
                                    ApplicationUtils.mDoubleArrayUC[0] = ApplicationUtils.mDoubleArrayUC[aInputArrayCounter];
                                    SignalProcUtils.lastSampleIndex = SignalProcUtils.currentSampleIndex;
                                    ApplicationUtils.HANDLE_DATA_SIZE = 15000 - 1;
                                    return false;
                                } else if (aDiffSampleIndex > SignalProcConstants.MAX_DATA_LOSS && aInputArrayCounter >= SignalProcConstants.BUFFER_SIZE) {
                                    SignalProcUtils.lastSampleIndex = -1;
                                    ApplicationUtils.HANDLE_DATA_SIZE = 15000;
                                    return false;
                                }
                            } else {
                                feedInputArray(aSemiValidSample, aInputArrayCounter + mNextValidSampleCounter);

                                if (mNextValidSampleCounter < 5) {
                                    doLinearInterpolation(aInputArrayCounter - 1, mNextValidSampleCounter);
                                } else {
                                    SampleMissed sampleMissed = new SampleMissed(aInputArrayCounter - 1, mNextValidSampleCounter);
                                    mSampleMissedList.add(sampleMissed);
                                }

//                                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Input Array Index    : " + (aInputArrayCounter-1), FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                          sample  missed : " + aSemiValidSample, FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                    no of samples missed : " + mNextValidSampleCounter, FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                                Timber.i(" : current sample index : %d", aInputArrayCounter);
//                                Timber.i(" : no of samples missed : %d", mNextValidSampleCounter);

                                aInputArrayCounter += mNextValidSampleCounter;
                                // Change Aravind
                                SignalProcUtils.interpolationCount += mNextValidSampleCounter;
                                // End

                                if (aDiffSampleIndex > SignalProcConstants.MAX_DATA_LOSS && aInputArrayCounter < SignalProcConstants.BUFFER_SIZE) {
                                    SignalProcUtils.dataLossCounter += aInputArrayCounter;
                                    ApplicationUtils.mDoubleArrayBuffer[0][0] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][0];
                                    ApplicationUtils.mDoubleArrayBuffer[0][1] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][1];
                                    ApplicationUtils.mDoubleArrayBuffer[0][2] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][2];
                                    ApplicationUtils.mDoubleArrayBuffer[0][3] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][3];
                                    ApplicationUtils.mDoubleArrayUC[0] = ApplicationUtils.mDoubleArrayUC[aInputArrayCounter];
                                    ApplicationUtils.HANDLE_DATA_SIZE = 15000 - 1; // Have saved the last value of previous iteration
                                    SignalProcUtils.lastSampleIndex = SignalProcUtils.currentSampleIndex;
                                    return false;
                                } else if (aDiffSampleIndex > SignalProcConstants.MAX_DATA_LOSS && aInputArrayCounter >= SignalProcConstants.BUFFER_SIZE) {
                                    ApplicationUtils.HANDLE_DATA_SIZE = 15000;
                                    SignalProcUtils.lastSampleIndex = -1;
                                    return false;
                                }
                            }
                        }
                    } else {
                        if (mNextValidSampleCounter > 0) {
                            feedInputArray(aSemiValidSample, aInputArrayCounter + mNextValidSampleCounter);

                            if (mNextValidSampleCounter < 5) {
                                doLinearInterpolation(aInputArrayCounter - 1, mNextValidSampleCounter);
                            } else {
                                SampleMissed sampleMissed = new SampleMissed(aInputArrayCounter - 1, mNextValidSampleCounter);
                                mSampleMissedList.add(sampleMissed);
                            }

//                            FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Input Array Index    : " + (aInputArrayCounter-1), FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                            FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " :                    no of samples missed : " + mNextValidSampleCounter, FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                            Timber.i(" : current sample index : %d", aInputArrayCounter);
//                            Timber.i(" : no of samples missed : %d", mNextValidSampleCounter);

                            aInputArrayCounter += mNextValidSampleCounter;
                            // Change Aravind
                            SignalProcUtils.interpolationCount += mNextValidSampleCounter;
                            // End

                            if (aDiffSampleIndex > SignalProcConstants.MAX_DATA_LOSS && aInputArrayCounter < SignalProcConstants.BUFFER_SIZE) {
                                SignalProcUtils.dataLossCounter += aInputArrayCounter;
                                ApplicationUtils.mDoubleArrayBuffer[0][0] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][0];
                                ApplicationUtils.mDoubleArrayBuffer[0][1] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][1];
                                ApplicationUtils.mDoubleArrayBuffer[0][2] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][2];
                                ApplicationUtils.mDoubleArrayBuffer[0][3] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][3];
                                ApplicationUtils.mDoubleArrayUC[0] = ApplicationUtils.mDoubleArrayUC[aInputArrayCounter];
                                SignalProcUtils.lastSampleIndex = SignalProcUtils.currentSampleIndex;
                                ApplicationUtils.HANDLE_DATA_SIZE = 15000 - 1; // Have saved the last value of previous iteration
                                return false;
                            } else if (aDiffSampleIndex > SignalProcConstants.MAX_DATA_LOSS && aInputArrayCounter >= SignalProcConstants.BUFFER_SIZE) {
                                SignalProcUtils.lastSampleIndex = -1;
                                ApplicationUtils.HANDLE_DATA_SIZE = 15000; // Have saved the last value of previous iteration
                                return false;
                            }
                        } else {
                            feedInputArray(aSemiValidSample, aInputArrayCounter);
                        }
                    }

                    SignalProcUtils.lastSampleIndex = SignalProcUtils.currentSampleIndex;
                } else {
                    if (mSampleCounter >= ApplicationUtils.HANDLE_DATA_SIZE) {
                        if (aInputArrayCounter < 14500 && (SignalProcUtils.currentIteration - SignalProcUtils.lastIteration >= 3)) {
//                            if (ExceptionHandling.getInstance().getExceptionListener() != null) {
////                                Timber.w("Throwing Exception : Issue with connection. Please restart app");
//                                ExceptionHandling.getInstance().getExceptionListener().onException(new Exception("Issue with connection. Please restart app"));
//                            }

                            ApplicationUtils.mDoubleArrayBuffer[0][0] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][0];
                            ApplicationUtils.mDoubleArrayBuffer[0][1] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][1];
                            ApplicationUtils.mDoubleArrayBuffer[0][2] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][2];
                            ApplicationUtils.mDoubleArrayBuffer[0][3] = ApplicationUtils.mDoubleArrayBuffer[aInputArrayCounter][3];
                            ApplicationUtils.mDoubleArrayUC[0] = ApplicationUtils.mDoubleArrayUC[aInputArrayCounter];
                            ApplicationUtils.HANDLE_DATA_SIZE = 15000 - 1; // Have saved the last value of previous iteration
                            return false;
                        }
                    } else {
//                        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Received null data at : " + mSampleCounter, FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                        Timber.i("Received null data at : %d", mSampleCounter);
                    }
                }
            }

            if (aInputArrayCounter >= SignalProcConstants.NO_OF_SAMPLES && (mSampleMissedList.size() > 0)) {
                SampleMissed aSampleMissed = mSampleMissedList.remove(mSampleMissedList.size() - 1);
                doLinearInterpolation(aSampleMissed.getmSampleMissedLoc(), aSampleMissed.getmNoSampleMissed());
            }
        } else {
//            FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Invalid data : " + aSemiValidSample, FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//            Timber.i("Invalid Data : %s", aSemiValidSample);

//            if (ExceptionHandling.getInstance().getExceptionListener() != null) {
//                ExceptionHandling.getInstance().getExceptionListener().onException(new Exception("Issue with connection. Please restart app"));
//            }

            return false;
        }

//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Conversion to double completed", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//        Timber.i("Conversion to double completed");

        /**
         * Finish conversion of data to double.
         * Store Missed Loc & no of samples.
         */

        ArrayList<Integer> aDataPointLoc = new ArrayList<>();
        int aMissedLoc;
        int aDataSize;
        ArrayList<Integer> aMissedSamples;
        ArrayList<Integer> aMissedLocation;
        int aLastLeftIndex, aFirstLeftIndex, aLastRightIndex, aFirstRightIndex;
        int aCount;
        int aSampleLocNegative = 0;

        if (mSampleMissedList.size() > 0) {
            while (mSampleMissedList.get(0).getmNoSampleMissed() < 0) {
                aSampleLocNegative++;
            }
        }

//        Timber.i("No of times to interpolate : " + (mSampleMissedList.size() - aSampleLocNegative));

        for (int i = aSampleLocNegative; i < mSampleMissedList.size(); i++) {
            aDataPointLoc = new ArrayList<>();
            aMissedSamples = new ArrayList<>();
            aMissedLocation = new ArrayList<>();

            aMissedLocation.add(mSampleMissedList.get(i).getmSampleMissedLoc());
            aMissedSamples.add(mSampleMissedList.get(i).getmNoSampleMissed());
            aCount = i;

            int aLoss = aMissedSamples.get(0);
            int aLossCheck = 0;

            if (aCount < mSampleMissedList.size() - 1) {
                aLossCheck = mSampleMissedList.get(aCount + 1).getmSampleMissedLoc() - aMissedLocation.get(0) - aLoss;
                while (aLossCheck < SignalProcConstants.CUBIC_INTERPOLATE_SIZE) {
                    aMissedLocation.add(mSampleMissedList.get(aCount + 1).getmSampleMissedLoc());
                    aMissedSamples.add(mSampleMissedList.get(aCount + 1).getmNoSampleMissed());
                    aLoss += mSampleMissedList.get(aCount + 1).getmNoSampleMissed();
                    aCount++;
                    if (aCount == (mSampleMissedList.size() - 1)) {
                        break;
                    }
                }
            }

            aDataSize = 0;

            if (aMissedLocation.get(0) < SignalProcConstants.CUBIC_INTERPOLATE_SIZE) {
                for (int j = 0; j <= aMissedLocation.get(0); j++) {
                    aDataPointLoc.add(j);
                }
//                aDataSize = aMissedLocation.get(0) + 1;
            } else {
                for (int j = 0; j < SignalProcConstants.CUBIC_INTERPOLATE_SIZE; j++) {
                    aDataPointLoc.add(aMissedLocation.get(0) - (SignalProcConstants.CUBIC_INTERPOLATE_SIZE - 1 - j));
                }
//                aDataSize = 10;
            }

            aMissedLoc = aDataPointLoc.size() - 1;

            boolean aRightFlag = false;
            int aRightCount = 0;
            int aIter = 0;

            while (aIter < aMissedLocation.size()) {
                if (aIter + 1 < aMissedLocation.size()) {
                    for (int j = aMissedLocation.get(aIter) + aMissedSamples.get(aIter) + 1; j <= aMissedLocation.get(aIter + 1); j++) {
                        aDataPointLoc.add(j);
                        aRightCount++;

                        if (aRightCount == SignalProcConstants.CUBIC_INTERPOLATE_SIZE) {
                            aRightFlag = true;
                            break;
                        }
                    }
                } else {
                    for (int j = aMissedLocation.get(aIter) + aMissedSamples.get(aIter) + 1; j < SignalProcConstants.NO_OF_SAMPLES; j++) {
                        aDataPointLoc.add(j);
                        aRightCount++;

                        if (aRightCount == SignalProcConstants.CUBIC_INTERPOLATE_SIZE) {
                            aRightFlag = true;
                            break;
                        }
                    }
                }

                aIter++;

                if (aRightFlag) {
                    break;
                }
            }

            doInterpolation(aDataPointLoc, aMissedLoc, aMissedSamples.get(0));
        }


//        FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + " : ConversionHelper : Populate completed in :"+(System.currentTimeMillis() - aST)+" msec.", FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
//        Timber.i("Time for Populate Input Array : %d", (System.currentTimeMillis() - aST));

        return true;
    }


    private void populateRetainedData() {
        for (int i = 0; i < ApplicationUtils.RETAINED_DATA_SIZE; i++) {
            ApplicationUtils.mDoubleArrayBuffer[i][0] = ApplicationUtils.mDoubleArrayBuffer[ApplicationUtils.HANDLE_DATA_SIZE + i][0];
            ApplicationUtils.mDoubleArrayBuffer[i][1] = ApplicationUtils.mDoubleArrayBuffer[ApplicationUtils.HANDLE_DATA_SIZE + i][1];
            ApplicationUtils.mDoubleArrayBuffer[i][2] = ApplicationUtils.mDoubleArrayBuffer[ApplicationUtils.HANDLE_DATA_SIZE + i][2];
            ApplicationUtils.mDoubleArrayBuffer[i][3] = ApplicationUtils.mDoubleArrayBuffer[ApplicationUtils.HANDLE_DATA_SIZE + i][3];
            ApplicationUtils.mDoubleArrayUC[i] = ApplicationUtils.mDoubleArrayBuffer[ApplicationUtils.HANDLE_DATA_SIZE + i][0];
        }
    }

    private void doLinearInterpolation(int iMissedLocation, int iNoOfSamplesMissed) {
        int aStartIndex = iMissedLocation;
        int aEndIndex = (iMissedLocation + 1) + iNoOfSamplesMissed;
        for (int i = 0; i < SignalProcConstants.NO_OF_CHANNELS; i++) {
            double aScale = (ApplicationUtils.mDoubleArrayBuffer[aEndIndex][i] - ApplicationUtils.mDoubleArrayBuffer[aStartIndex][i]) / (aEndIndex - aStartIndex);
            for (int j = aStartIndex + 1; j < aEndIndex; j++) {
                ApplicationUtils.mDoubleArrayBuffer[j][i] = ApplicationUtils.mDoubleArrayBuffer[aStartIndex][i] + aScale * (j - aStartIndex);
                if (i == 0) {
                    ApplicationUtils.mDoubleArrayUC[j] = ApplicationUtils.mDoubleArrayBuffer[j][i];
                }
            }
        }
    }

    private void doInterpolation(final ArrayList<Integer> aDataPointLoc, final int missedLocation, final int missedSamples) {
        long aSt = System.currentTimeMillis();

        ExecutorService executorService = ExecutorServiceHelper.getInstance().getExecutorService();

        Future<Boolean> channelOneInterpolation = executorService.submit(() -> {
            final double[] aDataPoints = new double[aDataPointLoc.size()];

            for (int j = 0; j < aDataPointLoc.size(); j++) {
                aDataPoints[j] = ApplicationUtils.mDoubleArrayBuffer[aDataPointLoc.get(j)][0];
            }

            cubicInterpolateDynamic(aDataPoints, aDataPointLoc, missedLocation, missedSamples, 0);
        }, true);

        Future<Boolean> channelTwoInterpolation = executorService.submit(() -> {
            final double[] aDataPoints = new double[aDataPointLoc.size()];
            for (int j = 0; j < aDataPointLoc.size(); j++) {
                aDataPoints[j] = ApplicationUtils.mDoubleArrayBuffer[aDataPointLoc.get(j)][1];
            }
            cubicInterpolateDynamic(aDataPoints, aDataPointLoc, missedLocation, missedSamples, 1);
        }, true);

        Future<Boolean> channelThreeInterpolation = executorService.submit(() -> {
            final double[] aDataPoints = new double[aDataPointLoc.size()];
            for (int j = 0; j < aDataPointLoc.size(); j++) {
                aDataPoints[j] = ApplicationUtils.mDoubleArrayBuffer[aDataPointLoc.get(j)][2];
            }
            cubicInterpolateDynamic(aDataPoints, aDataPointLoc, missedLocation, missedSamples, 2);
        }, true);

        Future<Boolean> channelFourInterpolation = executorService.submit(() -> {
            final double[] aDataPoints = new double[aDataPointLoc.size()];
            for (int j = 0; j < aDataPointLoc.size(); j++) {
                aDataPoints[j] = ApplicationUtils.mDoubleArrayBuffer[aDataPointLoc.get(j)][3];
            }
            cubicInterpolateDynamic(aDataPoints, aDataPointLoc, missedLocation, missedSamples, 3);
        }, true);

        try {
            if (channelOneInterpolation.get() && channelTwoInterpolation.get() && channelThreeInterpolation.get() && channelFourInterpolation.get()) {
//                FileLoggerHelper.getInstance().sendLogData(ApplicationUtils.getCurrentTime() + "Time for cubic interpolation of 4 channels : " + (System.currentTimeMillis() - aSt), FileLoggerType.INTERPOLATION, FLApplication.mFileTimeStamp);
//                Timber.i("Time for cubic interpolation of 4 channels : %d", (System.currentTimeMillis() - aSt));
            }
        } catch (ExecutionException e) {
            e.printStackTrace();

//            if (ExceptionHandling.getInstance().getExceptionListener() != null) {
//                ExceptionHandling.getInstance().getExceptionListener().onException(e);
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();

//            if (ExceptionHandling.getInstance().getExceptionListener() != null) {
//                ExceptionHandling.getInstance().getExceptionListener().onException(e);
//            }
        }
    }

    private void cubicInterpolateDynamic(double[] iDataPoints, ArrayList<Integer> iDataLocation, int iMissRegion, int iMissedSamples, int iCol) {
        long st = System.currentTimeMillis();

        int aNoOfDataPoints = iDataPoints.length;
        int aNoOfRegions = aNoOfDataPoints - 1;


        double[] aStepSize = new double[aNoOfRegions];
        for (int i = 0; i < aNoOfRegions; i++) {
            aStepSize[i] = iDataLocation.get(i + 1) - iDataLocation.get(i);
        }

//		aStepSize[aMissRegion] = iNoOfSamplesMissed+1;
        double[] aSlope = new double[aNoOfRegions];
        for (int i = 0; i < aNoOfRegions; i++) {
            aSlope[i] = (iDataPoints[i + 1] - iDataPoints[i]) / aStepSize[i];
        }

        int aSizeOfTriDiagonalMatrix = aNoOfRegions - 1;
        double[][] aTriDiagonalMatrix = new double[aSizeOfTriDiagonalMatrix][aSizeOfTriDiagonalMatrix];
        for (int i = 0; i < aSizeOfTriDiagonalMatrix; i++) {
            aTriDiagonalMatrix[i][i] = 2 * (aStepSize[i] + aStepSize[i + 1]);
        }
        for (int i = 1; i < aSizeOfTriDiagonalMatrix; i++) {
            aTriDiagonalMatrix[i - 1][i] = aStepSize[i];
            aTriDiagonalMatrix[i][i - 1] = aStepSize[i];
        }

        double[] aB = new double[aSizeOfTriDiagonalMatrix];
        for (int i = 0; i < aSizeOfTriDiagonalMatrix; i++) {
            aB[i] = 6 * (aSlope[i + 1] - aSlope[i]);
        }

        solve_TriDiagonal(aTriDiagonalMatrix, aB);

        double[] aCoiefficients = new double[4];

        aCoiefficients[0] = iDataPoints[iMissRegion];
        aCoiefficients[1] = aSlope[iMissRegion] - aStepSize[iMissRegion] * (2 * aB[iMissRegion - 1] + aB[iMissRegion]) / 6;
        aCoiefficients[2] = aB[iMissRegion - 1] / 2;
        aCoiefficients[3] = (aB[iMissRegion] - aB[iMissRegion - 1]) / (6 * aStepSize[iMissRegion]);

        for (int i = iMissedSamples; i > 0; i--) {
            ApplicationUtils.mDoubleArrayBuffer[iDataLocation.get(iMissRegion) + i][iCol] = aCoiefficients[0] + aCoiefficients[1] * i + aCoiefficients[2] * Math.pow(i, 2) + aCoiefficients[3] * Math.pow(i, 3);
//            aInterpolatedSamples.addFirst( aCoiefficients[0] + aCoiefficients[1] * i + aCoiefficients[2] * Math.pow(i,2) + aCoiefficients[3] * Math.pow(i,3) );
            if (iCol == 0) {
                ApplicationUtils.mDoubleArrayUC[iDataLocation.get(iMissRegion) + i] = ApplicationUtils.mDoubleArrayBuffer[iDataLocation.get(iMissRegion) + i][iCol];
            }
        }
    }

    private void solve_TriDiagonal(double[][] iTriDiagonalMatrix, double[] iRHS) {
        long st = System.currentTimeMillis();
        int aLength = iTriDiagonalMatrix.length;

        double[] aAlpha = new double[aLength];
        double[] aBeta = new double[aLength - 1];

        aAlpha[0] = iTriDiagonalMatrix[0][0];
        for (int i = 1; i < aLength; i++) {
            aAlpha[i] = iTriDiagonalMatrix[i][i];
            aBeta[i - 1] = iTriDiagonalMatrix[i][i - 1];
        }
        double aTemp;
        for (int i = 1; i < aLength; i++) {
            aTemp = aBeta[i - 1];
            aBeta[i - 1] = aTemp / aAlpha[i - 1];
            aAlpha[i] = aAlpha[i] - aTemp * aBeta[i - 1];
        }

        iRHS[0] = iRHS[0] / aAlpha[0];
        for (int i = 1; i < aLength; i++) {
            iRHS[i] = (iRHS[i] - (aBeta[i - 1] * aAlpha[i - 1] * iRHS[i - 1])) / aAlpha[i];
        }

        for (int i = aLength - 2; i >= 0; i--) {
            iRHS[i] = iRHS[i] - aBeta[i] * iRHS[i + 1];
        }
    }

    private String getNextSemiValidSample() {
        String aValidSample = "";

        if (mSampleCounter < ApplicationUtils.HANDLE_DATA_SIZE) {
            mNextValidSampleCounter = -1;

            do {
                aValidSample = ApplicationUtils.mSampleMasterQueue.remove();
                mSampleCounter++;
                mNextValidSampleCounter++;
            } while (aValidSample != null && aValidSample.length() != SignalProcConstants.SAMPLE_LENGTH && mSampleCounter < ApplicationUtils.HANDLE_DATA_SIZE);
        }
        return aValidSample;
    }

    private void feedInputArray(String iInputString, int iInputArrayCounter) {
        for (int i = 0; i < SignalProcConstants.NO_OF_CHANNELS; i++) {
            if (iInputArrayCounter >= SignalProcConstants.BUFFER_SIZE) {
                break;
            }

            try {
                ApplicationUtils.mDoubleArrayBuffer[iInputArrayCounter][i] = stringToDouble(iInputString.substring(6 * i + NO_SAMPLE_INDICES, 6 * i + (NO_SAMPLE_INDICES + NO_CHAR_PER_CHANNEL)));

                if (i == 0) {
                    ApplicationUtils.mDoubleArrayUC[iInputArrayCounter] = ApplicationUtils.mDoubleArrayBuffer[iInputArrayCounter][0];
                }
            } catch (Exception e) {
                e.printStackTrace();

//                ExceptionHandling.getInstance().getExceptionListener().onException(e);
            }
        }
    }

    private double stringToDouble(String iChannelInput) {
        return doubleConv(new BigInteger(iChannelInput, 16).doubleValue());
    }

    private double doubleConv(double iDoubleValue) {
        double aOut;

        if (iDoubleValue >= DOUBLE_UNSIGNED) {
            aOut = (iDoubleValue - DOUBLE_SIGNED) * V_REF / (DOUBLE_UNSIGNED - 1) / GAIN;
        } else {
            aOut = iDoubleValue / (DOUBLE_UNSIGNED - 1) / GAIN * V_REF;
        }

        return aOut;
    }
}
