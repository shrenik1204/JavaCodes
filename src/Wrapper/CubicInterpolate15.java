package Wrapper;

import SignalProc.SignalProcConstants;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

public class CubicInterpolate15 {

    private static double mCheck = Math.pow(2, 23);
    private static double mVref = 4.5;
    private static double mGain = 24;
    private static double mCheckDivide = 2 * mCheck;

    private double[][] mDoubleArray;
    private ArrayList<SampleMissed> mSampleMissedList = new ArrayList<>();

    private String[] mSampleArray;
    private int mSampleCounter;
    private int mNextValidSampleCounter;
    private int mLengthSignal;
    public static LinkedList<String> mStringArray = new LinkedList<String>();

    public double[][] convert(String aInputFilePath){

        try {
            BufferedReader br = new BufferedReader(new FileReader(aInputFilePath));
            String line;

            int counter = 0;
            while ((line = br.readLine()) != null) {
                String[] aSplitLine = line.split("!");
                if (aSplitLine.length >0){
                    mStringArray.addAll(Arrays.asList(aSplitLine).subList(1, aSplitLine.length));
                }
                counter++;
            }
            System.out.println("count = " + counter);

            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mLengthSignal = mStringArray.size();
        mDoubleArray = new double[mLengthSignal][4];

//        SignalProcConstants.INTERPOLATE_COUNT = 0;

//        mSampleArray = new String[mStringArray.size()];
//
//        for (int i = 0; i < mStringArray.size(); i++) {
//            mSampleArray[i] = mStringArray.get(i);
//        }

        int aInputArrayCounter = 0;

        int count = 0;
        String aValidSample = "";
        int aLastSampleIndex;
        int aLastSampleIndex1,aLastSampleIndex2;
        int aCurrentSampleIndex, aDiffSampleIndex;
        int aCurrentSampleIndex1, aCurrentSampleIndex2;

        String aPreviousSample = "";
        String aSemiValidSample = getNextSemiValidSample();

        if (aSemiValidSample != null && !aSemiValidSample.isEmpty()) {

            aLastSampleIndex1 = (int) (aSemiValidSample.charAt(0)) - SignalProcConstants.ASCII_START;
            aLastSampleIndex2 = (int) (aSemiValidSample.charAt(1)) -  SignalProcConstants.ASCII_START;
            aLastSampleIndex = aLastSampleIndex1 * SignalProcConstants.ASCII_DIFF + aLastSampleIndex2;
            feedInputArray(aSemiValidSample, aInputArrayCounter);

            for (aInputArrayCounter ++; aInputArrayCounter < mLengthSignal; aInputArrayCounter++) {
                aSemiValidSample = getNextSemiValidSample();

                if (aSemiValidSample != null && !aSemiValidSample.isEmpty()) {
                    aCurrentSampleIndex1 = (int) (aSemiValidSample.charAt(0)) - SignalProcConstants.ASCII_START;
                    aCurrentSampleIndex2 = (int) (aSemiValidSample.charAt(1)) - SignalProcConstants.ASCII_START;
                    aCurrentSampleIndex = aCurrentSampleIndex1 * SignalProcConstants.ASCII_DIFF + aCurrentSampleIndex2;

                    aDiffSampleIndex = aCurrentSampleIndex - aLastSampleIndex;


                    if (aDiffSampleIndex != 1 && aDiffSampleIndex != -(SignalProcConstants.MAX_MISS_DETECTION - 1)) {

                        if (aDiffSampleIndex < 0) {
                            aDiffSampleIndex = SignalProcConstants.MAX_MISS_DETECTION + aDiffSampleIndex;

                            if (aDiffSampleIndex > mNextValidSampleCounter) {


                                SampleMissed sampleMissed = new SampleMissed(aInputArrayCounter-1, aDiffSampleIndex-1);
                                mSampleMissedList.add(sampleMissed);
                                aInputArrayCounter += aDiffSampleIndex-1;
//                                SignalProcConstants.INTERPOLATE_COUNT += aDiffSampleIndex -1;
//                                mMissLocations.add(mDoubleArrCounter-1);
//                                mSamplesMissed.add(aDiffSampleIndex-1);
                            }
                            else {

                                SampleMissed sampleMissed = new SampleMissed(aInputArrayCounter-1, mNextValidSampleCounter );
                                mSampleMissedList.add(sampleMissed);
                                aInputArrayCounter += mNextValidSampleCounter;
//                                SignalProcConstants.INTERPOLATE_COUNT += mNextValidSampleCounter;
//                                mMissLocations.add(mDoubleArrCounter-1);
//                                mSamplesMissed.add(mNextValidSampleCounter);
                            }
                        }
                        else {
                            if (aDiffSampleIndex > mNextValidSampleCounter) {

                                SampleMissed sampleMissed = new SampleMissed(aInputArrayCounter-1, aDiffSampleIndex-1);
                                mSampleMissedList.add(sampleMissed);
                                aInputArrayCounter += aDiffSampleIndex-1;
//                                SignalProcConstants.INTERPOLATE_COUNT += aDiffSampleIndex -1;
//                                mMissLocations.add(mDoubleArrCounter-1);
//                                mSamplesMissed.add(aDiffSampleIndex-1);
                            }
                            else {

                                SampleMissed sampleMissed = new SampleMissed(aInputArrayCounter-1, mNextValidSampleCounter );
                                mSampleMissedList.add(sampleMissed);
                                aInputArrayCounter += mNextValidSampleCounter;
//                                SignalProcConstants.INTERPOLATE_COUNT += mNextValidSampleCounter;
//                                mMissLocations.add(mDoubleArrCounter-1);
//                                mSamplesMissed.add(mNextValidSampleCounter);
                            }
                        }
                    }
                    else {
                        if (mNextValidSampleCounter > 0) {

                            SampleMissed sampleMissed = new SampleMissed(aInputArrayCounter-1, mNextValidSampleCounter );
                            mSampleMissedList.add(sampleMissed);
                            aInputArrayCounter += mNextValidSampleCounter;
//                            SignalProcConstants.INTERPOLATE_COUNT += mNextValidSampleCounter;
//                            mMissLocations.add(mDoubleArrCounter-1);
//                            mSamplesMissed.add(mNextValidSampleCounter);
                        }
                    }
                    feedInputArray(aSemiValidSample, aInputArrayCounter);
                    aLastSampleIndex = aCurrentSampleIndex;
                }
                else {
                    System.out.println("Conversion Helper : Empty sample inside for");
                }


            }
        }
        // Finish convertion of data to double. Store Missed Loc & no of samples

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

        for (int i = aSampleLocNegative; i <mSampleMissedList.size() ; i++) {
            aDataPointLoc = new ArrayList<>();
            aMissedSamples = new ArrayList<>();
            aMissedLocation = new ArrayList<>();

            aMissedLocation.add(mSampleMissedList.get(i).getmSampleMissedLoc());
            aMissedSamples.add(mSampleMissedList.get(i).getmNoSampleMissed());
            aCount = i;
            int aLoss = aMissedSamples.get(0);
            int aLossCheck = 0;
            if (aCount <mSampleMissedList.size()-1) {
                aLossCheck = mSampleMissedList.get(aCount + 1).getmSampleMissedLoc() - aMissedLocation.get(0) - aLoss;
                while (aLossCheck < SignalProcConstants.CUBIC_INTERPOLATE_SIZE) {
                    aMissedLocation.add(mSampleMissedList.get(aCount + 1).getmSampleMissedLoc());
                    aMissedSamples.add(mSampleMissedList.get(aCount + 1).getmNoSampleMissed());
                    aCount++;
                    aLoss += mSampleMissedList.get(aCount).getmNoSampleMissed();
                    if (aCount == (mSampleMissedList.size() - 1)) {
                        break;
                    }
                }
            }




            aDataSize = 0;
            if (aMissedLocation.get(0) < SignalProcConstants.CUBIC_INTERPOLATE_SIZE){
                for (int j = 0; j <= aMissedLocation.get(0); j++) {
                    aDataPointLoc.add(j);
                }
//                aDataSize = aMissedLocation.get(0) + 1;
            }
            else {
                for (int j = 0; j < SignalProcConstants.CUBIC_INTERPOLATE_SIZE; j++) {
                    aDataPointLoc.add(aMissedLocation.get(0)-(SignalProcConstants.CUBIC_INTERPOLATE_SIZE-1-j));
                }
//                aDataSize = 10;
            }
            aMissedLoc = aDataPointLoc.size()-1;

            boolean aRightFlag = false;
            int aRightCount = 0;
            int aIter = 0;
            while (aIter < aMissedLocation.size()){
                if (aIter+1 < aMissedLocation.size()){
                    for (int j = aMissedLocation.get(aIter)+aMissedSamples.get(aIter)+1 ; j <= aMissedLocation.get(aIter+1) ; j++) {
                        aDataPointLoc.add(j);
                        aRightCount++;
                        if (aRightCount == SignalProcConstants.CUBIC_INTERPOLATE_SIZE){
                            aRightFlag = true;
                            break;
                        }
                    }
                }
                else {
                    for (int j = aMissedLocation.get(aIter)+aMissedSamples.get(aIter)+1; j < mLengthSignal; j++) {
                        aDataPointLoc.add(j);
                        aRightCount++;
                        if (aRightCount == SignalProcConstants.CUBIC_INTERPOLATE_SIZE){
                            aRightFlag = true;
                            break;
                        }
                    }
                }

                if (aRightFlag){
                    break;
                }
                aIter++;
            }
            // For channel 1
            double[] aDataPoints = new double[aDataPointLoc.size()];
            for (int j = 0; j < aDataPointLoc.size(); j++) {
                aDataPoints[j] = mDoubleArray[aDataPointLoc.get(j)][0];
            }
            cubicInterpolateDynamic(aDataPoints,aDataPointLoc, aMissedLoc, aMissedSamples.get(0), 0);

            // For channel 2
            aDataPoints = new double[aDataPointLoc.size()];
            for (int j = 0; j < aDataPointLoc.size(); j++) {
                aDataPoints[j] = mDoubleArray[aDataPointLoc.get(j)][1];
            }
            cubicInterpolateDynamic(aDataPoints,aDataPointLoc, aMissedLoc, aMissedSamples.get(0), 1);

            // For channel 3
            aDataPoints = new double[aDataPointLoc.size()];
            for (int j = 0; j < aDataPointLoc.size(); j++) {
                aDataPoints[j] = mDoubleArray[aDataPointLoc.get(j)][2];
            }
            cubicInterpolateDynamic(aDataPoints,aDataPointLoc, aMissedLoc, aMissedSamples.get(0), 2);

            // For channel 4
            aDataPoints = new double[aDataPointLoc.size()];
            for (int j = 0; j < aDataPointLoc.size(); j++) {
                aDataPoints[j] = mDoubleArray[aDataPointLoc.get(j)][3];
            }
            cubicInterpolateDynamic(aDataPoints,aDataPointLoc, aMissedLoc, aMissedSamples.get(0), 3);
        }
        return mDoubleArray;
    }

    private void cubicInterpolateDynamic(double[] iDataPoints, ArrayList<Integer> iDataLocation,
                                         int iMissRegion, int iMissedSamples, int iCol) {

        long st = System.currentTimeMillis();

        int aNoOfDataPoints = iDataPoints.length;
        int aNoOfRegions = aNoOfDataPoints-1;


        double[] aStepSize = new double[aNoOfRegions];
        for (int i =0; i<aNoOfRegions ; i++) {
            aStepSize[i] = iDataLocation.get(i+1) - iDataLocation.get(i);
        }


//		aStepSize[aMissRegion] = iNoOfSamplesMissed+1;
        double[] aSlope = new double[aNoOfRegions];
        for (int i =0; i<aNoOfRegions; i++) {
            aSlope[i] = (iDataPoints[i+1] - iDataPoints[i])/aStepSize[i];
        }

        int aSizeOfTriDiagonalMatrix = aNoOfRegions-1;
        double[][] aTriDiagonalMatrix = new double[aSizeOfTriDiagonalMatrix][aSizeOfTriDiagonalMatrix];
        for (int i = 0; i<aSizeOfTriDiagonalMatrix; i++) {
            aTriDiagonalMatrix[i][i] = 2 * (aStepSize[i] + aStepSize[i+1]);
        }
        for (int i =1; i<aSizeOfTriDiagonalMatrix; i++) {
            aTriDiagonalMatrix[i-1][i] = aStepSize[i];
            aTriDiagonalMatrix[i][i-1] = aStepSize[i];
        }

        double[] aB = new double[aSizeOfTriDiagonalMatrix];
        for (int i = 0;i < aSizeOfTriDiagonalMatrix; i++) {
            aB[i] = 6 * (aSlope[i+1] - aSlope[i]);
        }

        solve_TriDiagonal(aTriDiagonalMatrix, aB);

        double[] aCoiefficients = new double[4];

        aCoiefficients[0] = iDataPoints[iMissRegion];
        aCoiefficients[1] = aSlope[iMissRegion] - aStepSize[iMissRegion] * (2 * aB[iMissRegion-1] + aB[iMissRegion]) / 6;
        aCoiefficients[2] = aB[iMissRegion-1]/2;
        aCoiefficients[3] = ( aB[iMissRegion] - aB[iMissRegion-1] )/ (6 * aStepSize[iMissRegion]) ;
//        LinkedList<Double> aInterpolatedSamples = new LinkedList<>();

        for (int i = iMissedSamples; i>0; i--) {
            mDoubleArray[iDataLocation.get(iMissRegion) + i][iCol] =  aCoiefficients[0] + aCoiefficients[1] * i + aCoiefficients[2] * Math.pow(i,2) + aCoiefficients[3] * Math.pow(i,3) ;
//            aInterpolatedSamples.addFirst( aCoiefficients[0] + aCoiefficients[1] * i + aCoiefficients[2] * Math.pow(i,2) + aCoiefficients[3] * Math.pow(i,3) );

        }

        System.out.println("Time for cubic interpolation "+(System.currentTimeMillis() - st));

//        return aInterpolatedSamples;



    }

    private void solve_TriDiagonal(double[][] iTriDiagonalMatrix, double[] iRHS) {
        long st = System.currentTimeMillis();
        int aLength = iTriDiagonalMatrix.length;

        double[] aAlpha = new double[aLength];
        double[] aBeta = new double[aLength-1];

        aAlpha[0] = iTriDiagonalMatrix[0][0];
        for (int i =1; i< aLength; i++) {
            aAlpha[i] = iTriDiagonalMatrix[i][i];
            aBeta[i-1] = iTriDiagonalMatrix[i][i-1];
        }
        double aTemp;
        for (int i = 1; i<aLength; i++) {
            aTemp = aBeta[i-1];
            aBeta[i-1] = aTemp / aAlpha[i-1];
            aAlpha[i] = aAlpha[i] - aTemp * aBeta[i-1];
        }

        iRHS[0] = iRHS[0] / aAlpha[0];
        for (int i = 1 ; i<aLength; i++) {
            iRHS[i] = ( iRHS[i] - (aBeta[i-1] * aAlpha[i-1] * iRHS[i-1]) ) / aAlpha[i];
        }

        for (int i = aLength-2; i>=0; i--) {
            iRHS[i] = iRHS[i] - aBeta[i] * iRHS[i+1];
        }

        System.out.println("Time for tri-diagonal "+(System.currentTimeMillis() - st));
    }


    private String getNextSemiValidSample() {
        String aValidSample = "";
        if (mSampleCounter < mLengthSignal) {
            mNextValidSampleCounter = -1;
            do {
                aValidSample = mStringArray.remove();
                mSampleCounter++;
                mNextValidSampleCounter++;
            } while (aValidSample.length() != SignalProcConstants.SAMPLE_LENGTH && mSampleCounter < mLengthSignal);
        }
        return aValidSample;
    }

    private void feedInputArray(String iInputString, int iInputArrayCounter) {
        if (iInputString.contains("HK0022C5FF8C")) {
            int i = 0;
        }

        try {
            for (int i = 0; i < SignalProcConstants.NO_OF_CHANNELS; i++) {
                mDoubleArray[iInputArrayCounter][i] = stringToDouble(iInputString.substring(6 * i + 2, 6 * i + (2+6) ));
            }
        }
        catch (Exception e){
            System.out.println("Error Index");
        }

    }

    private double stringToDouble(String iChannelInput) {
        return doubleConv(new BigInteger(iChannelInput, 16).doubleValue());
    }

    private double doubleConv(double iDoubleValue) {
        double aOut;
        if (iDoubleValue >= mCheck) {
            aOut = (iDoubleValue - mCheckDivide) * mVref / (mCheck - 1) / mGain;
        } else {
            aOut = iDoubleValue / (mCheck - 1) / mGain * mVref;
        }
        return aOut;
    }

}
