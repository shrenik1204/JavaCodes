package SignalProc;


import Wrapper.Filename;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;

/**
 * <p> Generic functions used at various stages in Algorithm.</p>
 * <p> Change Logs :</p>
 * <ul>
 * 	   <li> 15th Sep, 2017
 *     		<ol>
 *     		 	<li> Added N-th order filter. (filterN)</li>
 *     		</ol>
 *     </li>
 *     <li> 24th May, 2017
 *     		<ol>
 *     		 	<li> First commit.</li>
 *     		</ol>
 *     </li>
 * </ul>
 *
 * @author Kishore Subramanian (kishore@sattvamedtech.com)
 *         
 */

public class MatrixFunctions {

	/**
	 * <p> Copies the matrix into a different 2D - array.</p>
	 * @param iInput Input matrix.
	 * @param iOutput Output matrix.
	 * @throws Exception If array dimension do not match.
	 */
	public void copy(double[][] iInput, double[][] iOutput) throws Exception {

		if (iInput.length > 0 && iOutput.length > 0) {
			int aLength = iInput.length;
			int aWidth = iInput[0].length;
			if (aLength == iOutput.length && aWidth == iOutput[0].length) {
				for (int i = 0; i < aLength; i++) {
					for (int j = 0; j < aWidth; j++) {
						iOutput[i][j] = iInput[i][j];
					}
				}
			} else {
				throw new Exception("Enter Arrays of same dimension : Copy function");
			}
		} else {
			throw new Exception(" Enter non empty array : Copy function");
		}
	}

	/**
	 * <p> Find median of the given Input array.</p>
	 * @param iInput Input data
	 * @return Median of the array.
	 * @throws Exception If input is empty.
	 */
	public double findMedian(double[] iInput) throws Exception {

		if (iInput.length > 0) {
			Arrays.sort(iInput);
			int aLen = iInput.length;
			double aMedian = 0;
			if (aLen % 2 == 1) {
				aMedian = iInput[aLen / 2];
			} else {
				aMedian = (iInput[aLen / 2] + iInput[aLen / 2 - 1]) / 2;
			}
			return aMedian;
		} else {
			throw new Exception("Enter non empty Array : findMedian");
		}

	}

	/**
	 * <p> Find the value of the entry at the given percentile location in an array.</p>
	 * @param iInput Input data.
	 * @param iPrecentile Percentage to check the distribution tail.
	 * @return Value at the location of percentile.
	 * @throws Exception If iPrecentile is not within {@literal (0 - 99)}.
	 */
	public double findPercentileValue(double[] iInput, int iPrecentile) throws Exception {
		if (iInput.length > 0) {
			if (iPrecentile >= 0 && iPrecentile <= 99) {
				Arrays.sort(iInput);
				int aFinalIndex = iInput.length - (iInput.length * iPrecentile / 100);
				return iInput[aFinalIndex - 1];
			} else {
				throw new Exception("Percentile should be between 0-99 : findPercentileValue");
			}
		} else {
			throw new Exception("Enter non empty array : findPercentileValue");
		}
	}


	/**
	 * <p> Multiply 2 matrices A and B and return in new matrix C.</p>
	 * @param iA Input matrix A.
	 * @param iB Input matrix B.
	 * @return Product of A and B.
	 * @throws Exception If the conditions of matrix multiplication are not met.
	 */
	public double[][] multiply(double[][] iA, double[][] iB) throws Exception {
		if (iA.length > 0 && iB.length > 0) {
			int aColA = iA[0].length;
			int aRowA = iA.length;
			int aColB = iB[0].length;
			int aRowB = iB.length;
			double aC[][] = new double[aRowA][aColB];
			if (aColA == aRowB)
			{
				for (int i = 0; i < aRowA; i++) {
					for (int j = 0; j < aColB; j++) {
						for (int k = 0; k < aRowB; k++) {
							aC[i][j] = aC[i][j] + iA[i][k] * iB[k][j];
						}
					}
				}
				return aC;
			} else {
				throw new Exception("Enter a valid Matrix : multiply");
			}
		} else {
			throw new Exception("Enter non empty matrix : multiply");
		}
	}

	/**
	 * <p> Find mean of each column and subtract it from the entries of corresponding columns.</p>
	 * @param iInput Input data.
	 * @throws Exception If input is empty.
	 */
	public void subtractMeanColumn(double[][] iInput) throws Exception {

		int aRow = iInput.length;
		if (aRow > 0) {
			int aCol = iInput[0].length;

			for (int i = 0; i < aCol; i++) {
				double aMean = 0;
				for (int j = 0; j < aRow; j++) {
					aMean = aMean + iInput[j][i];
				}
				aMean = aMean / aRow;
				for (int j = 0; j < aRow; j++) {
					iInput[j][i] = iInput[j][i] - aMean;
				}
			}
		} else {
			throw new Exception("Enter non empty matrix : subtractMeanColumn");
		}
	}

	/**
	 * Find covariance of the input matrix.
	 * @param iInput Input matrix.
	 * @return ( a^T * a)/T
	 * @throws Exception If input data is empty.
	 */
	public double[][] setEigenCovarianceMatrix(double[][] iInput) throws Exception {
		if (iInput.length > 0) {
			int aRow = iInput.length;
			int aCol = iInput[0].length;
			double[][] aCoVariance = new double[aCol][aCol];
			double aSum;

			for (int i = 0; i < aCol; i++) {
				for (int j = 0; j < aCol; j++) {
					aSum = 0;
					for (int k = 0; k < aRow; k++) {
						aSum = aSum + iInput[k][i] * iInput[k][j];
					}
					aCoVariance[i][j] = aSum / aRow;
				}
			}

			return aCoVariance;
		} else {
			throw new Exception("Enter non empty matrix : setEigenCovarianceMatrix");
		}
	}

	/**
	 * <p> Multiply matrices 'A' and 'B^T' and update the matrix 'A' with the product.</p>
	 * @param iA Input matrix A.
	 * @param iB Input matrix B.
	 * @throws Exception If no of columns of A is not same as no of columns of B.
	 */

	public void multiply_ABtranspose(double[][] iA, double[][] iB) throws Exception {
		int aRowA = iA.length;
		int aRowB = iB.length;
		if (aRowA > 0 && aRowB > 0) {
			int aColA = iA[0].length;

			int aColB = iB[0].length;

			double[] aTemp = new double[aColA];
			double aSum;
			if (aColA == aColB) {
				for (int i = 0; i < aRowA; i++) {
					for (int j = 0; j < aColA; j++) {
						aTemp[j] = iA[i][j];
					}
					for (int j = 0; j < aColA; j++) {
						aSum = 0;
						for (int k = 0; k < aRowB; k++) {
							aSum = aSum + aTemp[k] * iB[j][k];
						}
						iA[i][j] = aSum;
					}
				}
			} else {
				throw new Exception("Invalid Matrices for multiplication : multiply_ABtranspose");
				// return exception and break
			}
		} else {
			throw new Exception("Enter non empty array : multiply_ABtranspose");
		}
	}

	/**
	 * <p> Cummulant matrix : </p>
	 * <ol>
	 *     <li> Xij = X(:,im) .* X(:,jm)</li>
	 *     <li> CM = ((Xij .* X)' * X)/T </li>
	 *     <li> CM = CM - I(:,im)*I(:,jm) - I(:,jm) * I(:,im) - aI</li>
	 * </ol>
	 * @param iInput Input data (X).
	 * @param iIm Column index 1 (im).
	 * @param iJm Column index 2 (jm).
	 * @param iScale Factor to multiply in step 3 (a).
	 * @return Cummulant matrix (CM).
	 * @throws Exception Entered column indices has to lie within the input matrix dimension.
	 */
	public double[][] findCumulantMatrixEntries(double[][] iInput, int iIm, int iJm, double iScale) throws Exception {
		int aRow = iInput.length;
		if (aRow > 0) {
			int aCol = iInput[0].length;
			if (iIm < aCol && iJm < aCol && (iIm >= 0 && iJm >= 0)) {
				double[][] aTempCM = new double[aCol][aCol];

				// Xij = X(:,im) .* X(:,jm);
				double[] aTempColSquare = new double[aRow];
				for (int i = 0; i < aRow; i++) {
					aTempColSquare[i] = iInput[i][iIm] * iInput[i][iJm];
				}
				// CM = ((Xij .* X)' * X)/T
				double aSum;
				for (int i = 0; i < aCol; i++) {
					for (int j = 0; j < aCol; j++) {
						aSum = 0;
						for (int k = 0; k < aRow; k++) {
							aSum = aSum + aTempColSquare[k] * iInput[k][i] * iInput[k][j];
						}
						aTempCM[i][j] = iScale * aSum / aRow;
					}
				}
				// CM = CM - I(:,im)*I(:,jm) - I(:,jm) * I(:,im) - I;
				aTempCM[iIm][iJm] = aTempCM[iIm][iJm] - iScale;
				aTempCM[iJm][iIm] = aTempCM[iJm][iIm] - iScale;
				if (iIm == iJm) {
					for (int i = 0; i < aCol; i++) {
						aTempCM[i][i] = aTempCM[i][i] - iScale;
					}
				}

				return aTempCM;
			} else {
				throw new Exception("Entered index is outside the range of columns : findCumulantMatrixEntries");
			}
		} else {
			throw new Exception("Enter a non empty array : findCumulantMatrixEntries");
		}
	}

	/**
	 * <P> Find theta of the 2-D rotation matrix.</P>
	 * <ol>
	 *     <li> g = iA</li>
	 *     <li> gg = g * g'</li>
	 *     <li> ton = gg(1,1) - gg(2,2)</li>
	 *     <li> toff = gg(1,2) + gg(2,1)</li>
	 *     <li> theta = 0.5*atan2( toff , ton+sqrt(ton*ton+toff*toff) ).</li>
	 * </ol>
	 * @param iA Rotation matrix.
	 * @return Angle of rotation.
	 * @throws Exception If input is empty.
	 */
	public double findGivensTheta(double[][] iA) throws Exception {
		int aRow = iA.length;
		if (aRow > 0) {
			int aCol = iA[0].length;

			double aTon = 0;
			double aToff = 0;
			for (int i = 0; i < aRow; i++) {
				for (int j = 0; j < aRow; j++) {
					if (i == j) {
						for (int k = 0; k < aCol; k++) {
							aTon = aTon + Math.pow(-1, i) * iA[i][k] * iA[j][k];
						}
					} else {
						for (int k = 0; k < aCol; k++) {
							aToff = aToff + iA[i][k] * iA[j][k];
						}
					}
				}
			}
			return 0.5 * Math.atan2(aToff, aTon + Math.sqrt(aTon * aTon + aToff * aToff));
		} else {
			throw new Exception("Enter non empty array : findGivensTheta");
		}
	}
	
	/**
	 * <p> Create Identity matrix of given size.</p>
	 * @param iSize Size of identity matrix required.
	 * @return Identity matrix.
	 * @throws Exception If input size is non-positive.
	 */
	public double[][] identity(int iSize) throws Exception {
		if (iSize > 0) {
			double[][] aIdentity = new double[iSize][iSize];
			for (int i = 0; i < iSize; i++) {
				aIdentity[i][i] = 1.0;
			}

			return aIdentity;
		} else {
			throw new Exception("Matrix size has to be positive : identity");
		}
	}

	
	/**
	 * <p> Element wise multiplication of A and B.</p>
	 * @param iA Input matrix A.
	 * @param iB Input matrix B.
	 * @return A.*B
	 * @throws Exception If matrix dimensions are not same.
	 */
	public double[][] elementWiseMultiply(double[][] iA, double[][] iB) throws Exception {
		int aRowA = iA.length;
		int aRowB = iB.length;

		if (aRowA > 0 && aRowB > 0) {
			int aColA = iA[0].length;

			int aColB = iB[0].length;

			double[][] aOut = new double[aRowA][aColA];
			if (aColA == aColB && aRowA == aRowB) // The dimensions of Matrix A
													// and
													// B
													// must match
			{
				for (int i = 0; i < aRowA; i++) {
					for (int j = 0; j < aColA; j++) {
						aOut[i][j] = iA[i][j] * iB[i][j];
					}
				}
				return aOut;
			} else {
				throw new Exception("Enter matrices with valid dimension : elementWiseMultiply");
			}
		} else {
			throw new Exception("Enter non empty array : elementWiseMultiply");
		}
	}


	/**
	 * <p> Element wise division of A and B.</p>
	 * @param iA Input matrix A.
	 * @param iB Input matrix B.
	 * @return A./B
	 * @throws Exception If matrix dimensions are not same and B has any entry 0.
	 */
	public double[][] elementWiseDivide(double[][] iA, double[][] iB) throws Exception {
		int aRowA = iA.length;
		int aRowB = iB.length;
		if (aRowA > 0 && aRowB > 0) {
			int aColA = iA[0].length;
			int aColB = iB[0].length;

			double[][] aOut = new double[aRowA][aColA];
			if (aColA == aColB && aRowA == aRowB)
			{
				for (int i = 0; i < aRowA; i++) {
					for (int j = 0; j < aColA; j++) {
						if (iB[i][j] != 0) {
							aOut[i][j] = iA[i][j] / iB[i][j];
						} else {
						    aOut[i][j] = iA[i][j];
//							throw new Exception("Cannot divide by 0 : elementWiseDivide");
						}
					}
				}
				return aOut;
			} else {
				throw new Exception("Enter matrices with valid dimension : elementWiseDivide");
			}
		} else {
			throw new Exception("Enter non empty array : elementWiseDivide");

		}
	}

	/**
	 * Transpose of a matrix.
	 * @param iA Input matrix.
	 * @return Transpose of iA.
	 */
	public double[][] transpose(double[][] iA) {
		int aRow = iA.length;
		int aCol = iA[0].length;
		double[][] aTranspose = new double[aCol][aRow];
		for (int i = 0; i < aRow; i++) {
			for (int j = 0; j < aCol; j++) {
				aTranspose[j][i] = iA[i][j];
			}
		}

		return aTranspose;
	}
	 
	/**
	 * <p> Extract a submatrix from a given matrix.</p>
	 * @param iA Input matrix.
	 * @param iRowI Start row index to extract submatrix.
	 * @param iRowF End row index to extract submatrix.
	 * @param iColI Start column index to extract submatrix.
	 * @param iColF End column index to extract submatrix.
	 * @return The submatrix extracted from the input matrix.
	 * @throws Exception If any of the row/column index is outside the range of matrix dimension.
	 */
	public double[][] subMatrix(double[][] iA, int iRowI, int iRowF, int iColI, int iColF) throws Exception {
		int aRow = iA.length;
		if (aRow > 0) {
			int aCol = iA[0].length;

			// size of the sub matrix
			if (((iRowI >= 0 && iRowI <= iRowF) && (iRowF < aRow && iRowF >= iRowI))
					&& ((iColI >= 0 && iColI <= iColF) && (iColF < aCol && iColF >= iColI)))
			// The boundary conditions must lie within the size of the Matrix
			{
				double[][] aSubMatrix = new double[(iRowF - iRowI) + 1][(iColF - iColI) + 1];

				for (int i = 0; i < iRowF - iRowI + 1; i++) {
					for (int j = 0; j < iColF - iColI + 1; j++) {
						aSubMatrix[i][j] = iA[iRowI + i][iColI + j];
					}
				}
				return aSubMatrix;
			} else {
				throw new Exception("Entered index is outside the range of matrix : subMatrix");
			}
		} else {
			throw new Exception("Enter non empty array : subMatrix");
		}
	}

	/**
	 * <p> Concatenate Matrix A and B into a tall matrix.</p>
	 * @param iA Input matrix A.
	 * @param iB Input matrix B.
	 * @return Concatenated tall matrix.
	 * @throws Exception If the columns of both A and B do not match.
	 */
	public double[][] verticalConcat(double[][] iA, double[][] iB) throws Exception {

		int aRowA = iA.length;

		int aRowB = iB.length;

		if (aRowA > 0 && aRowB == 0) {
			return iA;
		} else if (aRowA == 0 && aRowB > 0) {
			return iB;
		}
		if ((aRowA == 0 && aRowB == 0)) {
			throw new Exception("Enter non empty matrices : verticalConcat");
		}
		int aColA = iA[0].length;
		int aColB = iB[0].length;
		if (aColA != aColB) {
			throw new Exception("Columns of both matrices must be same : verticalConcat");
		} else {
			double[][] iC = new double[aRowA + aRowB][aColA];
			for (int i = 0; i < aRowA + aRowB; i++) {
				for (int j = 0; j < aColA; j++) {
					if (i < aRowA) {
						iC[i][j] = iA[i][j];
					} else {
						iC[i][j] = iB[i - aRowA][j];
					}
				}
			}

			return iC;
		}

	}

	/**
	 * Find index of positive entries of an array.
	 * @param iInput Input data.
	 * @return Location of positive entry.
	 */
	public int[] findingPositiveElementsIndex(double iInput[]) {

		int aOutput[] = new int[iInput.length];
		int aT = 0;
		int aCount = 0;

		for (int y = 0; y < iInput.length; y++) {
			if (iInput[y] > 0) {
				aOutput[aT] = y;
				++aT;
				if (y == 0) {
					aCount = aCount + 1;
				}
			}
		}

		if (aCount > 0) {
			for (int i = 1; i < aOutput.length; i++) {
				if (aOutput[i] > 0) {
					aCount = aCount + 1;
				} else {
					break;
				}
			}
		} else if (aCount == 0) {
			for (int i = 0; i < aOutput.length; i++) {
				if (aOutput[i] > 0) {
					aCount = aCount + 1;
				} else {
					break;
				}
			}
		}
		if (aCount > 0) {
			int[] aOut = new int[aCount];
			for (int i = 0; i < aCount; i++) {
				aOut[i] = aOutput[i];
			}
			return aOut;
		} else {
			return new int[] {};
		}

	}

	
	/**
	 * <p> Generate a trapezoidal window function of given length.</p>
	 * @param nSamplesBeforeQRS Length of window before QRS.
	 * @param nSamplesAfterQRS Length of window after QRS.
	 * @param fs Sampling frequency.
	 * @return Trapezoidal window.
	 * @throws Exception If {@literal nSamplesBeforeQRS < (fs * 14 / 100)} and {@literal nSamplesAfterQRS < (fs * 6 / 100)}.
	 */
	public double[][] weightFunction(int nSamplesBeforeQRS, int nSamplesAfterQRS, int fs) throws Exception {
		// integer A, B, C.
		if (fs > 0 && (nSamplesBeforeQRS >= (fs * 14 / 100) && nSamplesAfterQRS >= (fs * 6 / 100))) {
			int nSamplesBefore1 = fs * 6 / 100;
			int nSamplesAfter1 = fs * 6 / 100;
			int nSamplesBefore2 = fs * 8 / 100;
			int nSamplesAfter2 = Math.min(fs * 20 / 100, (nSamplesAfterQRS - nSamplesAfter1));

			int iend1 = nSamplesBeforeQRS - nSamplesBefore1 - nSamplesBefore2;
			int iend2 = iend1 + nSamplesBefore2;
			int istart3 = iend2 + 1;
			int iend3 = iend2 + nSamplesBefore1 + nSamplesAfter1 + 1;
			int iend4 = iend3 + nSamplesAfter2;
			int istart5 = iend4 + 1;
			int iend5 = nSamplesBeforeQRS + nSamplesAfterQRS + 1;



			double wwg[][] = new double[iend5][1];
			int flag = 0;

			double constantValue = 0.2;
			double slopeValue = 0.8;
			for (int i = 0; i < iend1; i++) {
				wwg[i][0] = constantValue;
				flag = i;
			}


            int k = 0;
			while (flag < iend2 && k <= nSamplesBefore2) {
				flag = flag + 1;
				k = k + 1;
				wwg[flag][0] = constantValue + ((slopeValue * (k)) / nSamplesBefore2);
			}

			for (int i = istart3 - 1; i < iend3; i++) {
				wwg[i][0] = 1;
				flag = i;// 12
			}

			k = 1;


			while (flag < iend4 && k <= nSamplesAfter2) {
				flag = flag + 1;
				wwg[flag][0] = (1 - ((slopeValue * (k)) / nSamplesAfter2));
				k = k + 1;
			}

			for (int i = istart5 - 1; i < iend5; i++) {
				wwg[i][0] = constantValue;
//				flag = i;
			}
            SignalProcUtils.trapezodialWindowRegion[0][0] = 0;
			SignalProcUtils.trapezodialWindowRegion[0][1] = iend1-1;
			SignalProcUtils.trapezodialWindowRegion[0][2] = iend1;
			SignalProcUtils.trapezodialWindowRegion[1][0] = iend1;
			SignalProcUtils.trapezodialWindowRegion[1][1] = iend2-1;
			SignalProcUtils.trapezodialWindowRegion[1][2] = iend2 - iend1;
			SignalProcUtils.trapezodialWindowRegion[2][0] = iend2;
			SignalProcUtils.trapezodialWindowRegion[2][1] = iend3-1;
			SignalProcUtils.trapezodialWindowRegion[2][2] = iend3 - iend2;
			SignalProcUtils.trapezodialWindowRegion[3][0] = iend3;
			SignalProcUtils.trapezodialWindowRegion[3][1] = iend4-1;
			SignalProcUtils.trapezodialWindowRegion[3][2] = iend4 - iend3;
			SignalProcUtils.trapezodialWindowRegion[4][0] = iend4;
			SignalProcUtils.trapezodialWindowRegion[4][1] = iend5-1;
			SignalProcUtils.trapezodialWindowRegion[4][2] = iend5 - iend4;

			return wwg;
		} else {
			throw new Exception("Invalid Entries : weightFunction");
		}
	}

	public double[][] weightFunctionDynamic(int iQrs1, int iQrs2, int iQrs3, int iQrsLocation, int iQrsLength, int channel){

        int aNoPointBefore = SignalProcUtils.noSamplesBeforeQRS;
        int aNoPointsAfter = SignalProcUtils.noSamplesAfterQRS;
        int[] aRR = {iQrs2- iQrs1, iQrs3-iQrs2};
        int aShift = iQrs2 - aNoPointBefore +1;

        int aStartIndex = 0;
        int aEndIndex = aNoPointBefore + aNoPointsAfter + 1;
        boolean aChangeFlag = false;
        if (iQrsLocation > 0){
            if (aRR[0] <= SignalProcUtils.trapezodialWindowRegion[2][2]){
                aStartIndex = aNoPointBefore - aRR[0]/2;
                aChangeFlag = true;
            } else if (aRR[0] > SignalProcUtils.trapezodialWindowRegion[2][2] && (aRR[0] <= SignalProcUtils.trapezodialWindowRegion[1][2] + SignalProcUtils.trapezodialWindowRegion[2][2] + SignalProcUtils.trapezodialWindowRegion[3][2]) ){
                aStartIndex = (int) Math.round(((SignalProcUtils.trapezodialWindow[SignalProcUtils.trapezodialWindowRegion[3][0]][0] - SignalProcUtils.trapezodialWindow[SignalProcUtils.trapezodialWindowRegion[1][1]][0]) +
                            SignalProcUtils.slope2 * (iQrs2 -60) - SignalProcUtils.slope1 * (iQrs1 + 62)) / (SignalProcUtils.slope2 - SignalProcUtils.slope1));

                aStartIndex = aStartIndex - aShift +1;
                aChangeFlag = true;
            }
        }

        if (iQrsLocation < iQrsLength-1) {
            if (aRR[1] <= SignalProcUtils.trapezodialWindowRegion[2][2]) {
                aEndIndex = aNoPointBefore + aRR[1] / 2;
                aChangeFlag = true;
            } else if (aRR[1] > SignalProcUtils.trapezodialWindowRegion[2][2] && (aRR[1] <= SignalProcUtils.trapezodialWindowRegion[1][2] + SignalProcUtils.trapezodialWindowRegion[2][2] + SignalProcUtils.trapezodialWindowRegion[3][2])) {

                aEndIndex = (int) Math.round(((SignalProcUtils.trapezodialWindow[SignalProcUtils.trapezodialWindowRegion[3][0]][0] - SignalProcUtils.trapezodialWindow[SignalProcUtils.trapezodialWindowRegion[1][1]][0]) +
                        SignalProcUtils.slope2 * (iQrs3 - 60) - SignalProcUtils.slope1 * (iQrs2 + 62)) / (SignalProcUtils.slope2 - SignalProcUtils.slope1));

                aEndIndex = aEndIndex - aShift + 1;
                aChangeFlag = true;
            }

        }
        double[][] aWindow = new double[SignalProcUtils.trapezodialWindow.length][1];
        for (int i = 0; i < SignalProcUtils.trapezodialWindow.length; i++) {
            aWindow[i][0] = SignalProcUtils.trapezodialWindow[i][0];
        }
        if (aChangeFlag){
            if (channel == 0) {
//                FileLoggerHelper.getInstance().sendLogData(String.format(ApplicationUtils.getCurrentTime() + " : Close QRS location at : %d", iQrs2), FileLoggerType.EXECUTION, FLApplication.mFileTimeStamp);
            }


            for (int i = 0; i < aStartIndex-1; i++) {
                aWindow[i][0] = 0;
            }
            for (int i = aEndIndex; i <= SignalProcUtils.trapezodialWindowRegion[4][1]; i++) {
                aWindow[i][0] = 0;

            }
            return aWindow;
        }
        else {
            return SignalProcUtils.trapezodialWindow;
        }
    }

	/**
	 * Replicate a row vector N times.
	 * @param iInputArr Row vector {@literal (1xM)}.
	 * @param iN No of times to replicate.
	 * @return Replicated output {@literal (N x M)}.
	 * @throws Exception
	 * <ul>
	 *     <li> If {@literal iN < 0}.</li>
	 *     <li> If iInputArr is not a row vector/empty.</li>
	 * </ul>
	 */
	public double[][] repmat(double[][] iInputArr, int iN) throws Exception {
		// replicate a row vector to many rows.
		int aRow = iInputArr.length;
		if (aRow == 1) {
			if (iN >= 0) {
				double[][] aExt = new double[iN][iInputArr[0].length];
				for (int i = 0; i < iN; i++) {
					for (int j = 0; j < iInputArr[0].length; j++) {
						aExt[i][j] = iInputArr[0][j];
					}
				}
				return aExt;
			} else {
				throw new Exception("Matrix dimension has to be positive : repmat");
			}
		} else {
			throw new Exception("Input should be a row matrix : repmat");
		}
	}

	/**
	 * <p> Find mean of iInpArr between iPercI and iPercF.</p>
	 * @param iInpArr Input data.
	 * @param iPercI Percentile to neglect data at start.
	 * @param iPercF Percentile to neglect data at end.
	 * @return Mean value of data between the two percentile.
	 * @throws Exception
	 * <ul>
	 *     <li> If any percentile value out of {@literal (0 - 99)}.</li>
	 *     <li> If input is empty.</li>
	 * </ul>
	 */
	public double findMeanBetweenDistributionTails(double[] iInpArr, int iPercI, int iPercF) throws Exception {
		int aLen = iInpArr.length;
		if (aLen > 0) {
			if ((iPercI > -1 && iPercI < 100) && (iPercF > -1 && iPercF < 100)) {
				if ((iPercI + iPercF) < 100) {
					double[] aArr = new double[aLen];
					for (int i = 0; i < aLen; i++) {
						aArr[i] = iInpArr[i];
					}
					Arrays.sort(aArr);
					int aInitIndex = 1 + aLen * iPercI / 100;
					int aFinalIndex = aLen - aLen * iPercF / 100;
					double aSum = 0;
					for (int i = aInitIndex - 1; i < aFinalIndex; i++) {
						aSum = aSum + aArr[i];
					}

					return aSum / (aFinalIndex - aInitIndex + 1);
				} else {
					throw new Exception("Input values must be within 100 : findMeanBetweenDistributionTails");
				}
			} else {
				throw new Exception("Input values must be within 100 : findMeanBetweenDistributionTails");
			}
		} else {
			throw new Exception("Enter non empty matrix : findMeanBetweenDistributionTails");
		}
	}


	/**
	 *<p> Forward and Backward filtering for SOS filter : Zero-phase filtering.</p>
	 * @param iInput Input data.
	 * @param iSOS Direct-Form II, Second-Order Sections co-efficients.
	 * @param iGain Second-Order Sections Gain.
	 * @param iZ Delay element for each Sections.
	 * @param iOrder Filter order.
	 * @throws Exception
     * <ul>
     *     <li> If {@literal iSOS is empty or has column size other than 6}.</li>
     *     <li> If {@literal Length of iGain greater than rows of iSOS.}.</li>
     *     <li> If {@literal iZ.length + 1 != iA.length}</li>
     * </ul>
	 */
public void filtfilt_Sos(double[] iInput, double[][] iSOS,  double[] iGain, double[][] iZ, int iOrder) throws Exception{

	int aLengthSos = iSOS.length;
	if (aLengthSos > 0) {

		int aCols = iSOS[0].length;
		if (aCols == 6) {

			int aLengthGain = iGain.length;
			// Copy SOS co-efficients to local variable.
			double[][] aSOS = new double[aLengthSos][aCols];
			for (int i=0; i<aLengthSos; i++) {
				for (int j =0; j<aCols; j++) {
					aSOS[i][j] = iSOS[i][j];
				}
			}

			// Multiply Gain to corresponding Section of SOS.
			// The extra Gain to the last SOS section if present.
			if ( aLengthGain > (aLengthSos+1)) {
				throw new Exception("Invalid dimension gain values.");
			}
			else if (aLengthGain == (aLengthSos+1)) {
				for (int i = 0; i<3; i++) {
					aSOS[aLengthSos-1][i] = aSOS[aLengthSos-1][i] * iGain[aLengthSos];
				}
				aLengthGain--;
			}

			for (int j = 0; j<aLengthGain; j++) {
				for (int i = 0; i<3; i++) {
					aSOS[j][i] = iSOS[j][i] * iGain[j];
				}
			}

			// Perform filtfilt for each SOS.
			int aNfact = 3 * iOrder;
			int aLengthInput = iInput.length;
			int aLengthExtension = aLengthInput + 2*aNfact;
			double[] aMirrorInput = new double[aLengthExtension];
			double[] aA = new double[3];
			double[] aB = new double[3];
			double[] aZ = new double[2];
			if (aLengthInput > 0 && aLengthExtension > 0) {
				for (int i = 0; i<aLengthSos; i++) {
					// extract each filter co-efficients
					for (int j = 0; j<3; j++) {
						aA[j] = aSOS[i][j+3];
						aB[j] = aSOS[i][j];
					}
					long aTime = System.currentTimeMillis();
					mirrorInput(iInput, aMirrorInput);
					aTime = System.currentTimeMillis();
					for (int j =0; j<2; j++) {
						aZ[j] = iZ[j][i] * aMirrorInput[0];
					}

					aMirrorInput = filterN(aMirrorInput, aB, aA, aZ);

					aTime = System.currentTimeMillis();
					reverse(aMirrorInput);
					aTime = System.currentTimeMillis();

					for (int j =0; j<2; j++) {
						aZ[j] = iZ[j][i] * aMirrorInput[0];
					}

					aMirrorInput = filterN(aMirrorInput, aB, aA, aZ);
					aTime = System.currentTimeMillis();

					reverse(aMirrorInput);
					aTime = System.currentTimeMillis();

					for (int j =0; j<aLengthInput; j++) {
						iInput[j] = aMirrorInput[j+aNfact];
					}
				}

			}
			else {
				throw new Exception("Input has to be non-empty : filtfilt_Sos.");
			}
		}
		else {
			throw new Exception("Input SOS matrix is invalid : filtfilt_Sos.");
		}
	} else {
		throw new Exception("Input SOS matrix has to be non-empty : filtfilt_Sos.");
	}
}

    /**
     * <p> Forward and Backward filtering : Zero-phase filtering.</p>
     * @param iInput Input data.
     * @param iA Filter co-efficient 1.
     * @param iB Filter co-efficient 2.
     * @param iZ Delay element.
     * @throws Exception
     * <ul>
     *     <li> If {@literal iInput.length < 0}.</li>
     * </ul>
     */
	public void filtfilt(double[] iInput, double[] iA, double[] iB, double[] iZ) throws Exception{
		
		int aLength = iInput.length;
		int aLengthExt = 3 * (iA.length - 1);

		if (aLengthExt > 0 && aLength > 0) {
            long aTime = System.currentTimeMillis();
			double[] aMirrorExtension = new double[aLength + 2*aLengthExt];
			mirrorInput(iInput, aMirrorExtension);
            aTime = System.currentTimeMillis();

			double[] aZ = new double[iZ.length];
			for (int i =0; i<iZ.length; i++) {
				aZ[i] = iZ[i] * aMirrorExtension[0];
			}

            aMirrorExtension = filterN(aMirrorExtension, iB, iA, aZ);
            aTime = System.currentTimeMillis();
			reverse(aMirrorExtension);
            aTime = System.currentTimeMillis();

			for (int i =0; i<iZ.length; i++) {
				aZ[i] = iZ[i] * aMirrorExtension[0];
			}

            aMirrorExtension = filterN(aMirrorExtension, iB, iA, aZ);
            aTime = System.currentTimeMillis();
			reverse(aMirrorExtension);
            aTime = System.currentTimeMillis();
			
			for (int i =0; i<aLength; i++) {
				iInput[i] = aMirrorExtension[i+aLengthExt];
			}

		}
		else {
			throw new Exception("Enter valid Inputs : filtfilt");
		}
		
		
		
	}
	
	/**
	 * <p> Filtering of signals with filter of order N and delay.</p>
	 * @param iInput Input data.
	 * @param iA Filter co-efficient 1.
	 * @param iB Filter co-efficient 2.
	 * @param iZ Delay element.
	 * @throws Exception
	 * <ul>
	 *     <li> If {@literal iInput.length < iA.length}.</li>
	 *     <li> If {@literal iA.length & iB.length are different size}.</li>
     *     <li> If {@literal iZ.length + 1 != iA.length}</li>
	 * </ul>
	 */

	public double[] filterN(double[] iInput, double[] iB, double[] iA, double[] iZ) throws Exception {
		int aLengthI = iInput.length;
		int aLengthA = iA.length;
		int aLengthB = iB.length;
		int alengthZ = iZ.length;
		double aSum;

		if ((aLengthA == aLengthB) && ((alengthZ + 1) == aLengthA) && (aLengthI > aLengthA)) {

			double[] aOutput = new double[aLengthI];
			for (int i =0; i<alengthZ; i++) {
                aSum = 0;
                for (int j =0; j<=i; j++) {
                    aSum = aSum + iB[j] * iInput[i-j];
                    if (j>=1) {
                        aSum = aSum - iA[j]* aOutput[i-j];
                    }
                }
                aOutput[i] = aSum + iZ[i];
            }

			for (int i = alengthZ ; i<aLengthI; i++ ) {
                aSum = 0;
                for (int j = 0; j< aLengthA; j++) {
                    aSum = aSum + iB[j] * iInput[i-j];
                    if (j>=1) {
                        aSum = aSum - iA[j]* aOutput[i-j];
                    }
                }
                aOutput[i] = aSum;
            }
			return aOutput;
		} else {
			throw new Exception("Invalid input : filterN");
		}

	}

	/**
	 * <p> Extends the input on either sides.</p>
	 * @param iInput Input data.
	 * @param iMirrorExtension Output to be extended.
	 * @throws Exception If {@literal iMirrorExtension.length < iInput.length}.
	 */
	public void mirrorInput(double[] iInput, double[] iMirrorExtension) throws Exception {

		int aLength = iInput.length;
		int aLengthExtension = iMirrorExtension.length;

		if ((aLength > 0) && (aLengthExtension > 0)) {
			if (aLength <= aLengthExtension) {

				int aNfact = (aLengthExtension - aLength) / 2;
				int aNfactEnd = aLength + (aNfact - 1);
				int aNoShift = 2 * aLength + aNfact - 2;
				for (int i = 0; i < aLengthExtension; i++) {
					if (i < aNfact) {
						iMirrorExtension[i] = 2 * iInput[0] - iInput[aNfact - i];
					} else if (i > aNfactEnd) {
						iMirrorExtension[i] = 2 * iInput[aLength - 1] - iInput[aNoShift - i];
					} else {
						iMirrorExtension[i] = iInput[i - aNfact];
					}
				}
			} else {
				throw new Exception("Cannot extend to a smaller array : mirrorInput");
			}
		} else {
			throw new Exception("Enter non empty array : mirrorInput");
		}

	}

	/**
	 * <p> Reverse the input array.</p>
	 * @param iInput Input data.
	 * @throws Exception If input is empty.
	 */
	public void reverse(double[] iInput) throws Exception {
		int aLength = iInput.length;
		if (aLength > 0) {

			double aTemp;
			for (int i = 0; i < aLength / 2; i++) {
				aTemp = iInput[i];
				iInput[i] = iInput[aLength - i - 1];
				iInput[aLength - i - 1] = aTemp;
			}
		} else {
			throw new Exception("Enter non empty array : reverse");
		}

	}

	/**
	 * QRS DETECTION fucntions
	 */


	/**
	 * <p> Finds derivative and then squares the output.</p>
	 * @param iInput Input data.
	 * @param iFilter Derivative filter.
	 * @throws Exception If any of the input is empty array.
	 */
	public void convolutionQRSDetection(double[] iInput, double[] iFilter, int iScale) throws Exception {

		int aLengthInput = iInput.length;
		int aLengthFilter = iFilter.length;

		if (aLengthInput > 0 && aLengthFilter > 0) {
			int aLengthExtension = aLengthInput + aLengthFilter - 1;

			double aExtension[] = new double[aLengthExtension];

			for (int i = 0; i < aLengthExtension; i++) {
				if (i >= aLengthFilter / 2 && i < aLengthFilter / 2 + aLengthInput)
					aExtension[i] = iInput[i - aLengthFilter / 2];
				else
					aExtension[i] = 0;
			}

			double aSum;
			for (int i = 0; i < aLengthInput; i++) {
				aSum = 0;
				for (int j = 0; j < aLengthFilter; j++) {
					aSum = aSum + iFilter[aLengthFilter - 1 - j] * aExtension[j + i] / iScale;
				}
				iInput[i] = aSum * aSum;
			}
		} else {
			throw new Exception("Enter non empty array : convolutionQRSDetection");
		}
	}

	
	/**
	 * <p> Peak detection for array with minimum difference of delta between local extremum.</p>
	 * @param iInput Input data.
	 * @param iDelta Minimum difference between local maximum and local minimum.
	 * @return Locations of local maximum.
	 * @throws Exception If {@literal iDelta <= 0} or input is empty.
	 */
	public int[] peakDetection(double[] iInput, double iDelta) throws Exception {
		double aMinimum = 100000, aMaximum = -100000;
		double aMaxPos = 0;
		double aLookformax = 1;
		double aThisVar = 0;
		int aCountMax = 0;
		int aCountMin = 0;
		int aLength = iInput.length;
		if (aLength > 0) {
			if (iDelta >= 0) {

				double[] aPeakLoc = new double[aLength];

				for (int ind = 0; ind < aLength; ind++) {
					aThisVar = iInput[ind];
					// check max and min are greater and lesser to x[y][0]
					// respectively
					if (aThisVar > aMaximum) {
						aMaximum = aThisVar;
						aMaxPos = ind;
					}
					if (aThisVar < aMinimum) {
						aMinimum = aThisVar;
					}

					if (aLookformax == 1) {
						if (aThisVar < (aMaximum - iDelta)) {
							aPeakLoc[aCountMax] = aMaxPos;
							aCountMax = aCountMax + 1; // next row
							aMinimum = aThisVar;
							aLookformax = 0;
						}
					} else if (aLookformax == 0) {
						if (aThisVar > (aMinimum + iDelta)) {
							aCountMin = aCountMin + 1;
							aMaximum = aThisVar;
							aMaxPos = ind;
							aLookformax = 1;
						}
					}
				}

				int aCount = 0;
				if (aPeakLoc[0] >= 0 && aPeakLoc[1] > 0) {
					aCount = aCount + 1;
				}
				for (int i = 1; i < aPeakLoc.length; i++) {
					if (aPeakLoc[i] > 0) {
						aCount = aCount + 1;
					} else {
						break;
					}
				}

				int[] aPeakLocFinal = new int[aCount];
				for (int i = 0; i < aCount; i++) {
					aPeakLocFinal[i] = (int) (Math.floor(aPeakLoc[i]));
				}
				return aPeakLocFinal;
			} else {
				throw new Exception("Delta has to be positive : peakDetection");
			}
		} else {
			throw new Exception("Enter non empty array : peakDetection");
		}
	}

    public double[] peakDetectionMinMax(double[] iInput, double iDelta) throws Exception {
        double aMinimum = 100000, aMaximum = -100000;
        int aMaxPos = -10;
        int aMinPos = -10;
        int aLookformax = 1;
        double aThisVar;
        int aCountMax = 0;
        int aCountMin = 0;
        int aLength = iInput.length;
        if (aLength > 0) {
            if (iDelta >= 0) {

                int[][] aPeakLoc = new int[aLength][2];

                for (int ind = 0; ind < aLength; ind++) {
                    aThisVar = iInput[ind];
                    // check max and min are greater and lesser to x[y][0]
                    // respectively
                    if (aThisVar > aMaximum) {
                        aMaximum = aThisVar;
                        aMaxPos = ind;
                    }
                    if (aThisVar < aMinimum) {
                        aMinimum = aThisVar;
                        aMinPos = ind;
                    }

                    if (aLookformax == 1) {
                        if (aThisVar < (aMaximum - iDelta)) {
                            aPeakLoc[aCountMax][0] = aMaxPos;
                            aCountMax = aCountMax + 1; // next row
                            aMinimum = aThisVar;
                            aLookformax = 0;
                            aMinPos = ind;
                        }
                    } else if (aLookformax == 0) {
                        if (aThisVar > (aMinimum + iDelta)) {
                            aPeakLoc[aCountMin][1] = aMinPos;
                            aCountMin = aCountMin + 1;
                            aMaximum = aThisVar;
                            aMaxPos = ind;
                            aLookformax = 1;
                        }
                    }
                }

                int aCount = 0;
                if (aPeakLoc[0][0] >= 0 && aPeakLoc[1][0] > 0 && aPeakLoc[0][1] >= 0 && aPeakLoc[1][1] > 0) {
                    aCount = aCount + 1;
                }
                for (int i = 1; i < aPeakLoc.length; i++) {
                    if (aPeakLoc[i][0] > 0 && aPeakLoc[i][1] > 0) {
                        aCount = aCount + 1;
                    } else {
                        break;
                    }
                }

                int[][] aPeakLocFinal = new int[aCount][2];
                double[] aDelta = new double[aCount];
                for (int i = 0; i < aCount; i++) {
                    aPeakLocFinal[i][0] = aPeakLoc[i][0];
                    aPeakLocFinal[i][1] = aPeakLoc[i][1];
                    aDelta[i] = iInput[aPeakLoc[i][0]] - iInput[aPeakLoc[i][1]];

                }
                Arrays.sort(aDelta);
                double aMean = 0;
                for (int i = 0; i < aCount - 1; i++) {
                    aMean += aDelta[i];
                }
                aMean = aMean/(aCount-1);

                double aMedian = findMedian(aDelta);


                return new double[]{aMedian, aMean};
            } else {
                throw new Exception("Delta has to be positive : peakDetection");
            }
        } else {
            throw new Exception("Enter non empty array : peakDetection");
        }
    }


    public int[] peakDetection0(double[] iInput, double iDelta) throws Exception {
        double aMinimum = 100000, aMaximum = -100000;
        int aMaxPos = 0;
        double aLookformax = 1;
        double aThisVar = 0;
        int aCountMax = 0;
        int aCountMin = 0;
        int aLength = iInput.length;
        if (aLength > 0) {
            if (iDelta > 0) {

                int[] aPeakLoc = new int[aLength];

                for (int ind = 0; ind < aLength; ind++) {
                    aThisVar = iInput[ind];
                    // check max and min are greater and lesser to x[y][0]
                    // respectively
                    if (aThisVar > aMaximum ) {
                        aMaximum = aThisVar;
                        aMaxPos = ind;
                    }
                    if (aThisVar < aMinimum) {
                        aMinimum = aThisVar;
                    }

                    if (aLookformax == 1) {
                        if (aThisVar < (aMaximum - iDelta) && aThisVar < Math.pow(10,-10)) {
                            aPeakLoc[aCountMax] = aMaxPos;
                            aCountMax = aCountMax + 1; // next row
                            aMinimum = aThisVar;
                            aLookformax = 0;
                        }
                    } else if (aLookformax == 0) {
                        if (aThisVar > (aMinimum + iDelta)) {
                            aCountMin = aCountMin + 1;
                            aMaximum = aThisVar;
                            aMaxPos = ind;
                            aLookformax = 1;
                        }
                    }
                }

                int aCount = 0;
                if (aPeakLoc[0] >= 0 && aPeakLoc[1] > 0) { // to consider if the first location is zero and next are positive.
                    aCount = aCount + 1;
                }
                for (int i = aCount; i < aPeakLoc.length; i++) {
                    if (aPeakLoc[i] > 0) {
                        aCount = aCount + 1;
                    } else {
                        break;
                    }
                }

                int[] aPeakLocFinal = new int[aCount];
                for (int i = 0; i < aCount; i++) {
                    aPeakLocFinal[i] =  aPeakLoc[i];
                }
                return aPeakLocFinal;
            } else {
                throw new Exception("Delta has to be positive : peakDetection");
            }
        } else {
            throw new Exception("Enter non empty array : peakDetection");
        }
    }

    /**
     * <p> Maternal QRS detection.</p>
     * @param iInput One channel of ICA1.
     * @return
     * <ul>
     *     <li> QRS locations.</li>
     *     <li> Median Value of Threshold.</li>
     *     <li> Mean Value of Threshold.</li>
     * </ul>
     * @throws Exception
     */
	public double[] mqrsDetection(double[] iInput) throws Exception{

        double aMedianVal = 0;
        double aMeanVal = 0;
        convolutionQRSDetection(iInput, SignalProcConstants.MQRS_DERIVATIVE, SignalProcConstants.MQRS_DERIVATIVE_SCALE);
        double[] aEcgSq_sort = new double[SignalProcConstants.NO_OF_SAMPLES];
        aEcgSq_sort = iInput.clone();
        Arrays.sort(aEcgSq_sort);

        double aMean = 0;
        for (int i = 0; i < SignalProcConstants.MQRS_THRESHOLD_LENGTH; i++) {
            aMean += aEcgSq_sort[i];
        }
        aMean = aMean/ SignalProcConstants.MQRS_THRESHOLD_LENGTH;
        double aStdDevTh = 0;
        for (int i = 0; i < SignalProcConstants.MQRS_THRESHOLD_LENGTH; i++) {
            aStdDevTh += Math.pow((aEcgSq_sort[i] - aMean),2);
        }
        aStdDevTh = 2.5 * Math.sqrt(aStdDevTh/(SignalProcConstants.MQRS_THRESHOLD_LENGTH-1));

        for (int i = 0; i < SignalProcConstants.NO_OF_SAMPLES; i++) {
            if (iInput[i] < aStdDevTh){
                iInput[i] = 0;
            }
            else {
                iInput[i] = iInput[i] - aStdDevTh;
            }
        }

        double[] aEcg_Integrator = new double[SignalProcConstants.NO_OF_SAMPLES];
        double aSum = 0;
        for (int i = 0; i < SignalProcConstants.MQRS_WINDOW; i++) {
            aSum += iInput[i];
        }
        aEcg_Integrator[SignalProcConstants.MQRS_WINDOW-1] = aSum/ SignalProcConstants.MQRS_WINDOW;

        for (int i = SignalProcConstants.MQRS_WINDOW; i < SignalProcConstants.NO_OF_SAMPLES; i++) {
            aEcg_Integrator[i] = aEcg_Integrator[i-1] + (iInput[i] - iInput[i- SignalProcConstants.MQRS_WINDOW])/ SignalProcConstants.MQRS_WINDOW;
        }

		double[] aMedianMean = peakDetectionMinMax(aEcg_Integrator,0);

        double aThreshold = aMedianMean[0];
        if (aThreshold < 0.1){
            aThreshold = aMedianMean[1];
        }

        int[] aPeakLoc = peakDetection0(aEcg_Integrator,aThreshold/2);
        int aLengthPeaks = aPeakLoc.length;

        if (aLengthPeaks > SignalProcConstants.MQRS_MIN_SIZE && aLengthPeaks < SignalProcConstants.MQRS_MAX_SIZE){
            aMedianVal = aMedianMean[0];
            if (aMedianVal < 0.1){
                aMeanVal = aMedianMean[1];
            }
        }
        int aDelay = 25;
        int[] aQrs1 = new int[aPeakLoc.length];
        int aCount = 0;
        for (int i = 0; i<aPeakLoc.length ;i++) {
            aQrs1[i] = aPeakLoc[i] - aDelay;
            if (aQrs1[i] > 0) {
                aCount++;
            }
        }

        double[] aQrs = new double[aCount+2];
        int aTempShift = aPeakLoc.length - aCount;
        for (int i = 0; i<aCount; i++) {
            aQrs[i+2] = aQrs1[i+aTempShift];
        }
        aQrs[0] = aMedianVal;
        aQrs[1] = aMeanVal;
        return aQrs;
    }




	public int[] fetalQRS(double[] iChannel) throws Exception {
		
		int aLength = iChannel.length;
		if (aLength >= SignalProcConstants.FQRS_WINDOW) {
			// differentiate and square
			convolutionQRSDetection(iChannel, SignalProcConstants.FQRS_DERIVATIVE, SignalProcConstants.FQRS_DERIVATIVE_SCALE);

			/**
			 * FIltering 2 - 3.5 Hz
			 */

			double bhigh[] = new double[2];
			for (int i0 = 0; i0 < 2; i0++) {
				bhigh[i0] = SignalProcConstants.FQRS_BHIGH0 + SignalProcConstants.FQRS_BHIGH_SUM * (double) i0;
			}

			filtfilt(iChannel, SignalProcConstants.FQRS_AHIGH, bhigh, SignalProcConstants.FQRS_ZHIGH);

			// Have to add 6th order filter

			filtfilt(iChannel, SignalProcConstants.FQRS_ALOW, SignalProcConstants.FQRS_BLOW, SignalProcConstants.FQRS_ZLOW);

			/**
			 * Integrator
			 */

			double[] integrator = new double[aLength];

			double sum = 0;

			for (int j = 0; j < SignalProcConstants.FQRS_WINDOW; j++) {
				sum = sum + iChannel[SignalProcConstants.FQRS_WINDOW - j - 1];
			}
			integrator[SignalProcConstants.FQRS_WINDOW - 1] = sum / SignalProcConstants.FQRS_WINDOW;

			for (int i = SignalProcConstants.FQRS_WINDOW; i < aLength; i++) {
				integrator[i] = integrator[i - 1]
						+ (-iChannel[i - SignalProcConstants.FQRS_WINDOW] + iChannel[i]) / SignalProcConstants.FQRS_WINDOW;
			}
			/**
			 * Find the 90% and 10% value to find the threshold
			 */

			double threshold = setIntegratorThreshold(integrator,
					SignalProcConstants.FQRS_INTEGRATOR_THRESHOLD_SCALE);

			/**
			 * Peak Detection , not sure about return type have to change it
			 * Just return the first column of the Maxtab. No need the
			 * magnitudes.
			 */
			int peakLoc[] = peakDetection(integrator, threshold);

			int delay = (int) Math.ceil(SignalProcConstants.FQRS_WINDOW / 2.0);
			int peakLength = peakLoc.length;
			// Check the starting peak is greater than delay/2 or remove nIt
			int count = 0;
			for (int i = 0; i < peakLength; i++) {
				if (peakLoc[i] < delay - 1) {
					count = count + 1;
				}
			}

			int lenQrs = peakLength - count;
			int[] qrs = new int[lenQrs];
			for (int i = 0; i < lenQrs; i++) {
				qrs[i] = peakLoc[i + count] - delay;
			}

			return qrs;
		}
		{
			throw new Exception("Input array must have size greater than window : fetalQRS");
		}
	}
	public double setIntegratorThreshold(double[] iIntegrator, double iScale) throws Exception {
		int aLength = iIntegrator.length;
		if (aLength > 0) {
			if (iScale != 0) {
				double aIntegratorSort[] = new double[aLength];

				for (int i = 0; i < aLength; i++) {
					aIntegratorSort[i] = iIntegrator[i];
				}
				Arrays.sort(aIntegratorSort);

				int aMaxLoc = (int) Math.ceil(aLength * SignalProcConstants.QRS_INTEGRTOR_MAX);
				int aMinLoc = (int) Math.ceil(aLength * SignalProcConstants.QRS_INTEGRATOR_MIN);

				double aMaxVal = aIntegratorSort[aMaxLoc-1];
				double aMinVal = aIntegratorSort[aMinLoc-1];

				double aThreshold = (aMaxVal - aMinVal) / iScale;

				for (int i = 0; i < aLength; i++) {
					if (iIntegrator[i] < aThreshold) {
						iIntegrator[i] = 0;
					}
				}

				return aThreshold;
			} else {
				throw new Exception("Scale has to be non zero : setIntegratorThreshold");
			}
		} else {
			throw new Exception("Enter non empty array : setIntegratorThreshold");
		}
	}


	/**
	 * <p> Select the best channel containing QRS peaks.</p>
	 * @param iQRS1 QRS locations in channel 1.
	 * @param iQRS2 QRS locations in channel 2.
	 * @param iQRS3 QRS locations in channel 3.
	 * @param iQRS4 QRS locations in channel 4.
	 * @param iVarTh Variance threshold.
	 * @param iRRlowTh Lower limit of RR range.
	 * @param iRRhighTh Upper limit of RR range.
	 * @return Will return the best QRS array for Maternal/Fetal and start index.
	 *
	 * @throws Exception If Thresholds are non-positive.
	 */
	public Object[] channelSelection(int[] iQRS1, int[] iQRS2, int[] iQRS3, int[] iQRS4, int iVarTh, int iRRlowTh,
			int iRRhighTh) throws Exception {
		/**
		 * Channel selection part
		 */

		int aLen1 = iQRS1.length;
		int aLen2 = iQRS2.length;
		int aLen3 = iQRS3.length;
		int aLen4 = iQRS4.length;

		if (iVarTh > 0 && iRRhighTh > 0 && iRRlowTh > 0) {
			double aInd1 = 0;
			double aInd2 = 0;
			double aInd3 = 0;
			double aInd4 = 0;
			// to get the start index in each channel
			int aStartInd1 = -1;
			int aStartInd2 = -1;
			int aStartInd3 = -1;
			int aStartInd4 = -1;
			// RR mean for each channel
			double aRRmean1 = 0;
			double aRRmean2 = 0;
			double aRRmean3 = 0;
			double aRRmean4 = 0;

			if (aLen1 > 3) {
				int aNIt = aLen1 - 3;
				double aVar1[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				aVarMin = 1000;
				double counter = 0;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS1[i + 1] - iQRS1[i];
					t2 = iQRS1[i + 2] - iQRS1[i + 1];
					t3 = iQRS1[i + 3] - iQRS1[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar1[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar1[i] < iVarTh) {
						aRRTemp = iQRS1[i + 1] - iQRS1[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean1 = aRRmean1 + aRRTemp;
							counter = counter + 1;
							if (aVar1[i] < aVarMin) {
								aVarMin = aVar1[i];
								aStartInd1 = i;
							}
						}
					}
				}
				aRRmean1 = aRRmean1 / counter;
				aInd1 = counter / aNIt;
			}

			if (aLen2 > 3) {
				int aNIt = aLen2 - 3;
				double aVar2[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				double counter = 0;
				aVarMin = 1000;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS2[i + 1] - iQRS2[i];
					t2 = iQRS2[i + 2] - iQRS2[i + 1];
					t3 = iQRS2[i + 3] - iQRS2[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar2[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar2[i] < iVarTh) {
						aRRTemp = iQRS2[i + 1] - iQRS2[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean2 = aRRmean2 + aRRTemp;
							counter = counter + 1;
							if (aVar2[i] < aVarMin) {
								aVarMin = aVar2[i];
								aStartInd2 = i;
							}
						}
					}
				}
				aRRmean2 = aRRmean2 / counter;

				aInd2 = counter / aNIt;
			}

			if (aLen3 > 3) {
				int aNIt = aLen3 - 3;
				double aVar3[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				double counter = 0;
				aVarMin = 1000;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS3[i + 1] - iQRS3[i];
					t2 = iQRS3[i + 2] - iQRS3[i + 1];
					t3 = iQRS3[i + 3] - iQRS3[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar3[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar3[i] < iVarTh) {
						aRRTemp = iQRS3[i + 1] - iQRS3[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean3 = aRRmean3 + 1;
							counter = counter + 1;
							if (aVar3[i] < aVarMin) {
								aVarMin = aVar3[i];
								aStartInd3 = i;
							}
						}
					}
				}
				aRRmean3 = aRRmean3 / counter;
				aInd3 = counter / aNIt;
			}

			if (aLen4 > 3) {
				int aNIt = aLen4 - 3;
				double aVar4[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				double counter = 0;
				aVarMin = 1000;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS4[i + 1] - iQRS4[i];
					t2 = iQRS4[i + 2] - iQRS4[i + 1];
					t3 = iQRS4[i + 3] - iQRS4[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar4[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar4[i] < iVarTh) {
						aRRTemp = iQRS4[i + 1] - iQRS4[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean4 = aRRmean4 + 1;
							counter = counter + 1;
							if (aVar4[i] < aVarMin) {
								aVarMin = aVar4[i];
								aStartInd4 = i;
							}
						}
					}
				}
				aRRmean4 = aRRmean4 / counter;
				aInd4 = counter / aNIt;
			}
			// FInd the maximum value of 'ind'
			// Have to add mean RR value also to this computation to get better
			// estimate of 'ch'
            Filename.CHF_Ind.append(SignalProcUtils.currentIteration+",");
            Filename.CHF_Ind.append(aInd1+",");
            Filename.CHF_Ind.append(aInd2+",");
            Filename.CHF_Ind.append(aInd3+",");
            Filename.CHF_Ind.append(aInd4+",");

            Filename.CHF_Ind.append((aLen1-3)*aInd1+",");
            Filename.CHF_Ind.append((aLen2-3)*aInd2+",");
            Filename.CHF_Ind.append((aLen3-3)*aInd3+",");
            Filename.CHF_Ind.append((aLen4-3)*aInd4+",");
            Filename.CHF_Ind.append("\n ");
			if (aInd1 <= SignalProcConstants.CHANNEL_PERCENTAGE && aInd2 <= SignalProcConstants.CHANNEL_PERCENTAGE && aInd3 <= SignalProcConstants.CHANNEL_PERCENTAGE && aInd4 <= SignalProcConstants.CHANNEL_PERCENTAGE) {
				int qrs[] = new int[aLen1 + aLen2 + aLen3 + aLen4];
				for (int i = 0; i < aLen1; i++) {
					qrs[i] = iQRS1[i];
				}
				int shift = aLen1;
				for (int i = 0; i < aLen2; i++) {
					qrs[i + shift] = iQRS2[i];
				}
				shift = shift + aLen2;
				for (int i = 0; i < aLen3; i++) {
					qrs[i + shift] = iQRS3[i];
				}
				shift = shift + aLen3;
				for (int i = 0; i < aLen4; i++) {
					qrs[i + shift] = iQRS4[i];
				}
				Arrays.sort(qrs);
				return new Object[] { qrs,  -1, qrs };
			} else {
				double ind = aInd1;
				int length_Final = aLen1;
				int ch = 1;
				double RRmean = 0;
				for (int i = 0; i < aLen1 - 1; i++) {
					RRmean = RRmean + iQRS1[i + 1] - iQRS1[i];
				}
				RRmean = RRmean / (aLen1 - 1);
				if (aInd2 == ind) {
					double RRmean2 = 0;
					for (int i = 0; i < aLen2 - 1; i++) {
						RRmean2 = RRmean2 + iQRS2[i + 1] - iQRS2[i];
					}
					RRmean2 = RRmean2 / (aLen2 - 1);
					if (RRmean < RRmean2) {
						ind = aInd2;
						ch = 2;
						length_Final = aLen2;
						RRmean = RRmean2;
					}
				} else if (aInd2 > ind) {
					ind = aInd2;
					ch = 2;
					length_Final = aLen2;
					double RRmean2 = 0;
					for (int i = 0; i < aLen2 - 1; i++) {
						RRmean2 = RRmean2 + iQRS2[i + 1] - iQRS2[i];
					}
					RRmean = RRmean2 / (aLen2 - 1);
				}
				if (aInd3 == ind) {
					double RRmean3 = 0;
					for (int i = 0; i < aLen3 - 1; i++) {
						RRmean3 = RRmean3 + iQRS3[i + 1] - iQRS3[i];
					}
					RRmean3 = RRmean3 / (aLen3 - 1);
					if (RRmean < RRmean3) {
						ind = aInd3;
						ch = 3;
						length_Final = aLen3;
						RRmean = RRmean3;
					}
				} else if (aInd3 > ind) {
					ind = aInd3;
					ch = 3;
					length_Final = aLen3;
					double RRmean3 = 0;
					for (int i = 0; i < aLen3 - 1; i++) {
						RRmean3 = RRmean3 + iQRS3[i + 1] - iQRS3[i];
					}
					RRmean = RRmean3 / (aLen3 - 1);
				}
				if (aInd4 > ind) {
					double RRmean4 = 0;
					for (int i = 0; i < aLen4 - 1; i++) {
						RRmean4 = RRmean4 + iQRS4[i + 1] - iQRS4[i];
					}
					RRmean4 = RRmean4 / (aLen4 - 1);
					if (RRmean < RRmean4) {
						ind = aInd4;
						ch = 4;
						length_Final = aLen4;
						RRmean = RRmean4;
					}
				} else if (aInd4 > ind) {
					ind = aInd4;
					ch = 4;
					length_Final = aLen4;
					double RRmean4 = 0;
					for (int i = 0; i < aLen4 - 1; i++) {
						RRmean4 = RRmean4 + iQRS4[i + 1] - iQRS4[i];
					}
					RRmean = RRmean4 / (aLen4 - 1);
				}
				/**
				 * Get the start Index and qrs values to find the final QRS.
				 */
				int[] qrs = new int[length_Final];
				int startIndex = -1;
				if (ch == 1) {
					startIndex = aStartInd1;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS1[i];
					}
				} else if (ch == 2) {
					startIndex = aStartInd2;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS2[i];
					}
				} else if (ch == 3) {
					startIndex = aStartInd3;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS3[i];
					}
				} else if (ch == 4) {
					startIndex = aStartInd4;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS4[i];
					}
				}


				int qrs1[] = new int[aLen1 + aLen2 + aLen3 + aLen4];
				for (int i = 0; i < aLen1; i++) {
					qrs1[i] = iQRS1[i];
				}
				int shift = aLen1;
				for (int i = 0; i < aLen2; i++) {
					qrs1[i + shift] = iQRS2[i];
				}
				shift = shift + aLen2;
				for (int i = 0; i < aLen3; i++) {
					qrs1[i + shift] = iQRS3[i];
				}
				shift = shift + aLen3;
				for (int i = 0; i < aLen4; i++) {
					qrs1[i + shift] = iQRS4[i];
				}
				Arrays.sort(qrs1);
			
				return new Object[] { qrs, startIndex,qrs1 };
			}
		} else {
			throw new Exception("Threshold has to be positive : channelSelection");
		}
	}

	public Object[] channelSelection_Mqrs(double[] iQRS1, double[] iQRS2, double[] iQRS3, double[] iQRS4, int iVarTh, int iRRlowTh,
									 int iRRhighTh) throws Exception {
		/**
		 * Channel selection part
		 */

		int aLen1 = iQRS1.length;
		int aLen2 = iQRS2.length;
		int aLen3 = iQRS3.length;
		int aLen4 = iQRS4.length;

		if (iVarTh > 0 && iRRhighTh > 0 && iRRlowTh > 0) {
			double aInd1 = 0;
			double aInd2 = 0;
			double aInd3 = 0;
			double aInd4 = 0;
			// to get the start index in each channel
			int aStartInd1 = -1;
			int aStartInd2 = -1;
			int aStartInd3 = -1;
			int aStartInd4 = -1;
			// RR mean for each channel
			double aRRmean1 = 0;
			double aRRmean2 = 0;
			double aRRmean3 = 0;
			double aRRmean4 = 0;

			if (aLen1 > 3) {
				int aNIt = aLen1 - 3;
				double aVar1[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				aVarMin = 1000;
				double counter = 0;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS1[i + 1] - iQRS1[i];
					t2 = iQRS1[i + 2] - iQRS1[i + 1];
					t3 = iQRS1[i + 3] - iQRS1[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar1[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar1[i] < iVarTh) {
						aRRTemp = iQRS1[i + 1] - iQRS1[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean1 = aRRmean1 + aRRTemp;
							counter = counter + 1;
							if (aVar1[i] < aVarMin) {
								aVarMin = aVar1[i];
								aStartInd1 = i;
							}
						}
					}
				}
				aRRmean1 = aRRmean1 / counter;
				aInd1 = counter / aNIt;
			}

			if (aLen2 > 3) {
				int aNIt = aLen2 - 3;
				double aVar2[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				double counter = 0;
				aVarMin = 1000;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS2[i + 1] - iQRS2[i];
					t2 = iQRS2[i + 2] - iQRS2[i + 1];
					t3 = iQRS2[i + 3] - iQRS2[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar2[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar2[i] < iVarTh) {
						aRRTemp = iQRS2[i + 1] - iQRS2[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean2 = aRRmean2 + aRRTemp;
							counter = counter + 1;
							if (aVar2[i] < aVarMin) {
								aVarMin = aVar2[i];
								aStartInd2 = i;
							}
						}
					}
				}
				aRRmean2 = aRRmean2 / counter;

				aInd2 = counter / aNIt;
			}

			if (aLen3 > 3) {
				int aNIt = aLen3 - 3;
				double aVar3[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				double counter = 0;
				aVarMin = 1000;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS3[i + 1] - iQRS3[i];
					t2 = iQRS3[i + 2] - iQRS3[i + 1];
					t3 = iQRS3[i + 3] - iQRS3[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar3[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar3[i] < iVarTh) {
						aRRTemp = iQRS3[i + 1] - iQRS3[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean3 = aRRmean3 + 1;
							counter = counter + 1;
							if (aVar3[i] < aVarMin) {
								aVarMin = aVar3[i];
								aStartInd3 = i;
							}
						}
					}
				}
				aRRmean3 = aRRmean3 / counter;
				aInd3 = counter / aNIt;
			}

			if (aLen4 > 3) {
				int aNIt = aLen4 - 3;
				double aVar4[] = new double[aNIt];
				double t1, t2, t3, aMean, aRRTemp, aVarMin;
				double counter = 0;
				aVarMin = 1000;
				for (int i = 0; i < aNIt; i++) {
					t1 = iQRS4[i + 1] - iQRS4[i];
					t2 = iQRS4[i + 2] - iQRS4[i + 1];
					t3 = iQRS4[i + 3] - iQRS4[i + 2];

					aMean = (t1 + t2 + t3) / 3;

					aVar4[i] = Math.sqrt(
							((t1 - aMean) * (t1 - aMean) + (t2 - aMean) * (t2 - aMean) + (t3 - aMean) * (t3 - aMean))
									/ 2);
					if (aVar4[i] < iVarTh) {
						aRRTemp = iQRS4[i + 1] - iQRS4[i];
						if (aRRTemp > iRRlowTh && aRRTemp < iRRhighTh) {
							aRRmean4 = aRRmean4 + 1;
							counter = counter + 1;
							if (aVar4[i] < aVarMin) {
								aVarMin = aVar4[i];
								aStartInd4 = i;
							}
						}
					}
				}
				aRRmean4 = aRRmean4 / counter;
				aInd4 = counter / aNIt;
			}
			// FInd the maximum value of 'ind'
			// Have to add mean RR value also to this computation to get better
			// estimate of 'ch'
			Filename.CHF_Ind.append(SignalProcUtils.currentIteration+",");
			Filename.CHF_Ind.append(aInd1+",");
			Filename.CHF_Ind.append(aInd2+",");
			Filename.CHF_Ind.append(aInd3+",");
			Filename.CHF_Ind.append(aInd4+",");

			Filename.CHF_Ind.append((aLen1-3)*aInd1+",");
			Filename.CHF_Ind.append((aLen2-3)*aInd2+",");
			Filename.CHF_Ind.append((aLen3-3)*aInd3+",");
			Filename.CHF_Ind.append((aLen4-3)*aInd4+",");
			Filename.CHF_Ind.append("\n ");
//			if (aInd1 <= SignalProcConstants.CHANNEL_PERCENTAGE && aInd2 <= SignalProcConstants.CHANNEL_PERCENTAGE && aInd3 <= SignalProcConstants.CHANNEL_PERCENTAGE && aInd4 <= SignalProcConstants.CHANNEL_PERCENTAGE) {
//				double qrs[] = new double[aLen1 + aLen2 + aLen3 + aLen4];
//				for (int i = 0; i < aLen1; i++) {
//					qrs[i] = iQRS1[i];
//				}
//				int shift = aLen1;
//				for (int i = 0; i < aLen2; i++) {
//					qrs[i + shift] = iQRS2[i];
//				}
//				shift = shift + aLen2;
//				for (int i = 0; i < aLen3; i++) {
//					qrs[i + shift] = iQRS3[i];
//				}
//				shift = shift + aLen3;
//				for (int i = 0; i < aLen4; i++) {
//					qrs[i + shift] = iQRS4[i];
//				}
//				Arrays.sort(qrs);
//				return new Object[] { qrs,  -1, qrs };
//			} else {
				double ind = aInd1;
				int length_Final = aLen1;
				int ch = 1;
				double RRmean = 0;
				for (int i = 0; i < aLen1 - 1; i++) {
					RRmean = RRmean + iQRS1[i + 1] - iQRS1[i];
				}
				RRmean = RRmean / (aLen1 - 1);
				if (aInd2 == ind) {
					double RRmean2 = 0;
					for (int i = 0; i < aLen2 - 1; i++) {
						RRmean2 = RRmean2 + iQRS2[i + 1] - iQRS2[i];
					}
					RRmean2 = RRmean2 / (aLen2 - 1);
					if (RRmean < RRmean2) {
						ind = aInd2;
						ch = 2;
						length_Final = aLen2;
						RRmean = RRmean2;
					}
				} else if (aInd2 > ind) {
					ind = aInd2;
					ch = 2;
					length_Final = aLen2;
					double RRmean2 = 0;
					for (int i = 0; i < aLen2 - 1; i++) {
						RRmean2 = RRmean2 + iQRS2[i + 1] - iQRS2[i];
					}
					RRmean = RRmean2 / (aLen2 - 1);
				}
				if (aInd3 == ind) {
					double RRmean3 = 0;
					for (int i = 0; i < aLen3 - 1; i++) {
						RRmean3 = RRmean3 + iQRS3[i + 1] - iQRS3[i];
					}
					RRmean3 = RRmean3 / (aLen3 - 1);
					if (RRmean < RRmean3) {
						ind = aInd3;
						ch = 3;
						length_Final = aLen3;
						RRmean = RRmean3;
					}
				} else if (aInd3 > ind) {
					ind = aInd3;
					ch = 3;
					length_Final = aLen3;
					double RRmean3 = 0;
					for (int i = 0; i < aLen3 - 1; i++) {
						RRmean3 = RRmean3 + iQRS3[i + 1] - iQRS3[i];
					}
					RRmean = RRmean3 / (aLen3 - 1);
				}
				if (aInd4 > ind) {
					double RRmean4 = 0;
					for (int i = 0; i < aLen4 - 1; i++) {
						RRmean4 = RRmean4 + iQRS4[i + 1] - iQRS4[i];
					}
					RRmean4 = RRmean4 / (aLen4 - 1);
					if (RRmean < RRmean4) {
						ind = aInd4;
						ch = 4;
						length_Final = aLen4;
						RRmean = RRmean4;
					}
				} else if (aInd4 > ind) {
					ind = aInd4;
					ch = 4;
					length_Final = aLen4;
					double RRmean4 = 0;
					for (int i = 0; i < aLen4 - 1; i++) {
						RRmean4 = RRmean4 + iQRS4[i + 1] - iQRS4[i];
					}
					RRmean = RRmean4 / (aLen4 - 1);
				}
				/**
				 * Get the start Index and qrs values to find the final QRS.
				 */
				double[] qrs = new double[length_Final];
				int startIndex = -1;
				if (ch == 1) {
					startIndex = aStartInd1;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS1[i];
					}
				} else if (ch == 2) {
					startIndex = aStartInd2;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS2[i];
					}
				} else if (ch == 3) {
					startIndex = aStartInd3;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS3[i];
					}
				} else if (ch == 4) {
					startIndex = aStartInd4;
					for (int i = 0; i < length_Final; i++) {
						qrs[i] = iQRS4[i];
					}
				}


//				double qrs1[] = new double[aLen1 + aLen2 + aLen3 + aLen4];
//				for (int i = 0; i < aLen1; i++) {
//					qrs1[i] = iQRS1[i];
//				}
//				int shift = aLen1;
//				for (int i = 0; i < aLen2; i++) {
//					qrs1[i + shift] = iQRS2[i];
//				}
//				shift = shift + aLen2;
//				for (int i = 0; i < aLen3; i++) {
//					qrs1[i + shift] = iQRS3[i];
//				}
//				shift = shift + aLen3;
//				for (int i = 0; i < aLen4; i++) {
//					qrs1[i + shift] = iQRS4[i];
//				}
//				Arrays.sort(qrs1);

				return new Object[] { qrs, startIndex,ch };
//			}
		} else {
			throw new Exception("Threshold has to be positive : channelSelection");
		}
	}


	/**
	 * <p> Find the intermediate qrs location between two integers.</p>
	 * @param iQrsM Locations of maternal QRS.
	 * @param iA Lower limit.
	 * @param iB Upper limit.
	 * @return Only one location or -1.
	 */
	public int findOverlapMqrsLoc(int[] iQrsM, int iA, int iB) {
		int lenM = iQrsM.length;
		for (int k = 0; k < lenM - 1; k++) {
			if (iQrsM[k] > iA) {
				if (iQrsM[k] < iB && iQrsM[k + 1] < iB) {
					return -1;
				} else if (iQrsM[k] < iB){
					return iQrsM[k];
				}
				else {
					return -1;
				}
			}
		}
		return -1;
	}

	/**
	 * <p> Sample heart rate every 500 ms.</p>
	 * @param iFinalQrsHrPlot HR at 20 locations in 1 to plot.
	 * @param iFinalHR HR at qrs locations.
	 * @param iFinalQRS Qrs locations.
	 * @return HR values in HEX format.
	 * @throws Exception If {@literal iFinalQrsHrPlot.length != }{@link SignalProcConstants#NO_OF_PRINT_VALUES No of print values}.
	 */
	public String[] convertHR2MilliSec(int[] iFinalQrsHrPlot, float[] iFinalHR, int[] iFinalQRS) throws Exception{
		

		/**
		 * 1. First QRS is below 2000+10000*CurrentIter .
		 * 
		 * 2. First QRS is == 2000+10000*CurrentIter && First HR = 0; 3. The
		 * Points are from [2, 2.5, 3, ... , 10, 10.5, 11, 11.5] = 20 points.
		 * Hence, the last point HR is equal to the heart rate in the last point
		 * even if the QRS location is less than 11.5 , else it will be taken
		 * accordingly.
		 */
//		double aHrRange = 240 - 30;
//		double aDecimalRange = 1 - 584;
		int[] aHrDecimalPrint = new int[SignalProcConstants.NO_OF_PRINT_VALUES];
		if (iFinalQrsHrPlot.length == SignalProcConstants.NO_OF_PRINT_VALUES && iFinalHR.length == iFinalQRS.length){
			

			int aQrsScale = SignalProcUtils.qrsCurrentShift;

			int aInitialQrsThreshold = aQrsScale + 2000;
			int aQRS1, aQRS2;
			int aCurrentIndex, aNextIndex;
			int aPreviousQRSIndex = 0;
			if (iFinalHR[0] == 0 && iFinalQRS[0] == aInitialQrsThreshold) {
				// Check for each pair of values , btw which they lie.
				aPreviousQRSIndex = 1;
			}

			aQRS1 = (iFinalQRS[aPreviousQRSIndex] - aQrsScale);
			if (aQRS1 > 1500) {
				aCurrentIndex = aQRS1 / SignalProcConstants.DIFFERENCE_SAMPLES + 1 - 4;
			} else if (aQRS1 > 1000){
				aCurrentIndex = aQRS1 / SignalProcConstants.DIFFERENCE_SAMPLES + 1 - 3;
			}
			else if (aQRS1 > 500){
				aCurrentIndex = aQRS1 / SignalProcConstants.DIFFERENCE_SAMPLES + 1 - 2;
			}
			else {
				aCurrentIndex = aQRS1 / SignalProcConstants.DIFFERENCE_SAMPLES + 1 - 1;
				// IF the QRS value is below 1500, i.e for maternal can go be 14xx
				// That time we need to subtract it by 3.
			}
			// 1. 4 is the minimum scale. Even if its 2500, consider from the next
			// sample. Here we consider {.} ( least integer function).
			// 2. aCurrentIndex cannot go negative, but later have to write for that
			// exception case also.

			if (aCurrentIndex >=0){
				for (int i = aPreviousQRSIndex + 1; i < iFinalQRS.length; i++) {

					aQRS2 = iFinalQRS[i] - aQrsScale;
					aNextIndex = aQRS2 / SignalProcConstants.DIFFERENCE_SAMPLES - 4; // No +1, because it should be till
					// the [.] (greatest Integer
					// function)
					if (aNextIndex >= aCurrentIndex && aCurrentIndex < SignalProcConstants.NO_OF_PRINT_VALUES ) { // (&& aNextIndex <= SignalProcConstants.NO_OF_PRINT_VALUES)
						for (int it = aCurrentIndex; it <= aNextIndex; it++) {
							if (it < SignalProcConstants.NO_OF_PRINT_VALUES){
								// a = y1 + (y2-y1) * (ax - x1)/(x2-x1) ;
								iFinalQrsHrPlot[it] = (int) (iFinalHR[aPreviousQRSIndex]
										+ (iFinalHR[i] - iFinalHR[aPreviousQRSIndex]) * (aCurrentIndex * SignalProcConstants.DIFFERENCE_SAMPLES + 2000 - aQRS1)
										/ (aQRS2 - aQRS1));
								aHrDecimalPrint[it] = (int) (SignalProcConstants.HR_DECIMAL_MAX + SignalProcConstants.HR_DECIMAL_PRINT_RANGE / SignalProcConstants.HR_PRINT_RANGE * (iFinalQrsHrPlot[it] - SignalProcConstants.HR_VALUE_MIN));

							}
						}
						aPreviousQRSIndex = i;
						aCurrentIndex = aNextIndex + 1;
						aQRS1 = aQRS2;
					} else {
						// If the next value lies within the 500 limit, then
						// use this value for next interpolation as its closer to the
						// next 500 limit.
						aPreviousQRSIndex = i;
						aCurrentIndex = aNextIndex + 1;
						aQRS1 = aQRS2;
					}

				}
				// This is if the last QRS is less than 11500, then do this.
				// If more than 1 HR is empty in the last, then do it for the last qrs
				// and leave remaining to 0 ( as error in Algorithm ).
				while (aCurrentIndex < SignalProcConstants.NO_OF_PRINT_VALUES-1) {
					iFinalQrsHrPlot[aCurrentIndex ] = (int) (iFinalHR[aPreviousQRSIndex]);
					aHrDecimalPrint[aCurrentIndex ] = (int) (SignalProcConstants.HR_DECIMAL_MAX + SignalProcConstants.HR_DECIMAL_PRINT_RANGE / SignalProcConstants.HR_PRINT_RANGE * (iFinalQrsHrPlot[aCurrentIndex + 1] - SignalProcConstants.HR_VALUE_MIN));
					aCurrentIndex++;
				}
				if (aCurrentIndex == SignalProcConstants.NO_OF_PRINT_VALUES-1){
					iFinalQrsHrPlot[aCurrentIndex] = (int) (iFinalHR[aPreviousQRSIndex]);
					aHrDecimalPrint[aCurrentIndex] = (int) (SignalProcConstants.HR_DECIMAL_MAX + SignalProcConstants.HR_DECIMAL_PRINT_RANGE / SignalProcConstants.HR_PRINT_RANGE * (iFinalQrsHrPlot[aCurrentIndex] - SignalProcConstants.HR_VALUE_MIN));
				}
			}
		}
		else {
			throw new Exception("Incorrect input paramaters : convertHR2MilliSec");
		}
		/**
		 * Convert Decimal HR to Hexadecimal.
		 */

		String[] aHrPrint = new String[SignalProcConstants.NO_OF_PRINT_VALUES];
		int aInputVal;
		int aQuotient;
		int aScale;
		for (int j = 0; j < SignalProcConstants.NO_OF_PRINT_VALUES; j++) {
			aHrPrint[j] = "";
			aInputVal = aHrDecimalPrint[j];
			if (aInputVal > 0) {
				for (int i = 3; i >= 0; i--) {
					aScale = (int) Math.pow(16, i);
					aQuotient = aInputVal / aScale;
					aInputVal = aInputVal - aQuotient * aScale;
					if (aQuotient == 10) {
						aHrPrint[j] = aHrPrint[j] + "A";
					} else if (aQuotient == 11) {
						aHrPrint[j] = aHrPrint[j] + "B";
					} else if (aQuotient == 12) {
						aHrPrint[j] = aHrPrint[j] + "C";
					} else if (aQuotient == 13) {
						aHrPrint[j] = aHrPrint[j] + "D";
					} else if (aQuotient == 14) {
						aHrPrint[j] = aHrPrint[j] + "E";
					} else if (aQuotient == 15) {
						aHrPrint[j] = aHrPrint[j] + "F";
					} else {
						aHrPrint[j] = aHrPrint[j] + aQuotient;
					}
				}
			} else {
				aHrPrint[j] = aHrPrint[j] + "0001";
			}
		}

		return aHrPrint;
	} // end Function to convert to milliSec and Hex values.

	
	public double[] fastfouriertransform_UC(double[] X ) throws Exception {

		if (X.length == 50) {
		int i;
		int ix;
		double y_re[] = new double[2048];
		double y_im[] = new double[2048];
		int ju;
		int iy;
		boolean tst;
		double temp_re;
		double temp_im;
		int k;
		int ihi;
		int ic;
		int j;
		int vstride;
		double twid_re;

		double dv0[] ={ 1.0, 0.99999529380957619,
				0.99998117528260111, 0.9999576445519639, 0.9999247018391445,
				0.99988234745421256, 0.9998305817958234, 0.99976940535121528,
				0.99969881869620425, 0.99961882249517864, 0.99952941750109314,
				0.99943060455546173, 0.99932238458834954, 0.99920475861836389,
				0.99907772775264536, 0.99894129318685687, 0.99879545620517241,
				0.99864021818026527, 0.99847558057329477, 0.99830154493389289,
				0.99811811290014918, 0.997925286198596, 0.99772306664419164,
				0.99751145614030345, 0.99729045667869021, 0.997060070339483,
				0.99682029929116567, 0.99657114579055484, 0.996312612182778,
				0.996044700901252, 0.99576741446765982, 0.99548075549192694,
				0.99518472667219693, 0.99487933079480562, 0.99456457073425542,
				0.9942404494531879, 0.99390697000235606, 0.9935641355205953,
				0.9932119492347945, 0.9928504144598651, 0.99247953459871, 0.9920993131421918,
				0.99170975366909953, 0.99131085984611544, 0.99090263542778,
				0.99048508425645709, 0.99005821026229712, 0.98962201746320089,
				0.989176509964781, 0.98872169196032378, 0.98825756773074946,
				0.98778414164457218, 0.98730141815785843, 0.98680940181418553,
				0.98630809724459867, 0.98579750916756748, 0.98527764238894122,
				0.98474850180190421, 0.984210092386929, 0.98366241921173025,
				0.98310548743121629, 0.98253930228744124, 0.98196386910955524,
				0.98137919331375456, 0.98078528040323043, 0.98018213596811743,
				0.97956976568544052, 0.9789481753190622, 0.97831737071962765,
				0.97767735782450993, 0.97702814265775439, 0.97636973133002114,
				0.97570213003852857, 0.97502534506699412, 0.97433938278557586,
				0.973644249650812, 0.97293995220556018, 0.97222649707893627,
				0.97150389098625178, 0.97077214072895035, 0.970031253194544,
				0.96928123535654853, 0.96852209427441727, 0.96775383709347551,
				0.96697647104485207, 0.9661900034454125, 0.9653944416976894,
				0.96458979328981276, 0.96377606579543984, 0.96295326687368388,
				0.96212140426904158, 0.96128048581132064, 0.96043051941556579,
				0.95957151308198452, 0.9587034748958716, 0.95782641302753291,
				0.95694033573220882, 0.95604525134999641, 0.95514116830577078,
				0.95422809510910567, 0.95330604035419386, 0.95237501271976588,
				0.95143502096900834, 0.9504860739494817, 0.94952818059303667,
				0.94856134991573027, 0.94758559101774109, 0.94660091308328353,
				0.94560732538052128, 0.94460483726148026, 0.94359345816196039,
				0.94257319760144687, 0.94154406518302081, 0.9405060705932683,
				0.93945922360218992, 0.93840353406310806, 0.937339011912575,
				0.93626566717027826, 0.93518350993894761, 0.93409255040425887,
				0.932992798834739, 0.93188426558166815, 0.93076696107898371,
				0.92964089584318121, 0.92850608047321559, 0.92736252565040111,
				0.92621024213831138, 0.92504924078267758, 0.92387953251128674,
				0.92270112833387863, 0.9215140393420419, 0.92031827670911059,
				0.91911385169005777, 0.9179007756213905, 0.9166790599210427,
				0.91544871608826783, 0.91420975570353069, 0.91296219042839821,
				0.91170603200542988, 0.91044129225806725, 0.90916798309052238,
				0.90788611648766626, 0.90659570451491533, 0.90529675931811882,
				0.90398929312344334, 0.90267331823725883, 0.901348847046022,
				0.90001589201616017, 0.89867446569395382, 0.89732458070541832,
				0.89596624975618522, 0.8945994856313827, 0.89322430119551532,
				0.89184070939234272, 0.89044872324475788, 0.88904835585466457,
				0.88763962040285393, 0.88622253014888064, 0.88479709843093779,
				0.88336333866573158, 0.881921264348355, 0.88047088905216075,
				0.87901222642863353, 0.87754529020726135, 0.8760700941954066,
				0.87458665227817611, 0.87309497841829009, 0.87159508665595109,
				0.87008699110871146, 0.8685707059713409, 0.86704624551569265,
				0.86551362409056909, 0.8639728561215867, 0.8624239561110405,
				0.86086693863776731, 0.85930181835700847, 0.85772861000027212,
				0.85614732837519447, 0.85455798836540053, 0.85296060493036363,
				0.8513551931052652, 0.84974176800085255, 0.84812034480329723,
				0.84649093877405213, 0.84485356524970712, 0.84320823964184544,
				0.84155497743689844, 0.83989379419599952, 0.83822470555483808,
				0.836547727223512, 0.83486287498638, 0.83317016470191319,
				0.83146961230254524, 0.829761233794523, 0.8280450452577558,
				0.82632106284566353, 0.82458930278502529, 0.82284978137582643,
				0.82110251499110465, 0.819347520076797, 0.81758481315158371,
				0.81581441080673378, 0.81403632970594841, 0.81225058658520388,
				0.81045719825259477, 0.808656181588175, 0.80684755354379933,
				0.80503133114296366, 0.80320753148064494, 0.80137617172314024,
				0.799537269107905, 0.79769084094339116, 0.79583690460888357,
				0.79397547755433717, 0.79210657730021239, 0.79023022143731,
				0.78834642762660634, 0.78645521359908577, 0.78455659715557524,
				0.78265059616657573, 0.78073722857209449, 0.778816512381476,
				0.77688846567323244, 0.77495310659487393, 0.773010453362737,
				0.77106052426181382, 0.7691033376455797, 0.7671389119358204,
				0.765167265622459, 0.76318841726338127, 0.76120238548426178,
				0.759209188978388, 0.75720884650648457, 0.75520137689653655,
				0.75318679904361252, 0.75116513190968637, 0.74913639452345937,
				0.74710060598018013, 0.745057785441466, 0.74300795213512172,
				0.74095112535495922, 0.73888732446061511, 0.73681656887736979,
				0.7347388780959635, 0.73265427167241282, 0.73056276922782759,
				0.7284643904482252, 0.726359155084346, 0.724247082951467,
				0.72212819392921535, 0.72000250796138165, 0.71787004505573171,
				0.71573082528381859, 0.71358486878079352, 0.71143219574521643,
				0.70927282643886569, 0.70710678118654757, 0.70493408037590488,
				0.7027547444572253, 0.70056879394324834, 0.69837624940897292,
				0.696177131491463, 0.69397146088965389, 0.69175925836415775,
				0.68954054473706683, 0.687315340891759, 0.68508366777270036,
				0.68284554638524808, 0.680600997795453, 0.67835004312986147,
				0.67609270357531592, 0.673829000378756, 0.67155895484701833,
				0.669282588346636, 0.66699992230363747, 0.66471097820334479,
				0.66241577759017178, 0.66011434206742048, 0.65780669329707864,
				0.65549285299961535, 0.65317284295377676, 0.650846684996381,
				0.64851440102211244, 0.64617601298331628, 0.64383154288979139,
				0.641481012808583, 0.63912444486377573, 0.6367618612362842,
				0.63439328416364549, 0.63201873593980906, 0.629638238914927,
				0.62725181549514408, 0.62485948814238634, 0.62246127937415,
				0.6200572117632891, 0.61764730793780387, 0.61523159058062682,
				0.61281008242940971, 0.61038280627630948, 0.60794978496777363,
				0.60551104140432555, 0.60306659854034816, 0.600616479383869,
				0.59816070699634238, 0.59569930449243336, 0.5932322950397998,
				0.59075970185887416, 0.58828154822264522, 0.58579785745643886,
				0.58330865293769829, 0.58081395809576453, 0.57831379641165559,
				0.57580819141784534, 0.5732971666980422, 0.57078074588696726,
				0.56825895267013149, 0.56573181078361312, 0.56319934401383409,
				0.560661576197336, 0.5581185312205561, 0.55557023301960218,
				0.55301670558002747, 0.55045797293660481, 0.54789405917310019,
				0.54532498842204646, 0.54275078486451589, 0.54017147272989285,
				0.53758707629564539, 0.53499761988709715, 0.5324031278771979,
				0.52980362468629461, 0.52719913478190128, 0.524589682678469,
				0.52197529293715439, 0.51935599016558964, 0.51673179901764987,
				0.51410274419322166, 0.5114688504379703, 0.508830142543107,
				0.50618664534515523, 0.50353838372571758, 0.50088538261124071,
				0.49822766697278181, 0.49556526182577254, 0.49289819222978404,
				0.49022648328829116, 0.487550160148436, 0.48486924800079106,
				0.48218377207912272, 0.47949375766015295, 0.47679923006332209,
				0.47410021465054997, 0.47139673682599764, 0.46868882203582796,
				0.46597649576796618, 0.46325978355186015, 0.46053871095824,
				0.45781330359887717, 0.45508358712634384, 0.45234958723377089,
				0.44961132965460654, 0.44686884016237416, 0.4441221445704292,
				0.44137126873171667, 0.43861623853852766, 0.43585707992225547,
				0.43309381885315196, 0.43032648134008261, 0.42755509343028208,
				0.42477968120910881, 0.42200027079979968, 0.41921688836322391,
				0.41642956009763715, 0.4136383122384345, 0.41084317105790391,
				0.40804416286497869, 0.40524131400498986, 0.40243465085941843,
				0.39962419984564679, 0.39680998741671031, 0.3939920400610481,
				0.39117038430225387, 0.38834504669882625, 0.38551605384391885,
				0.38268343236508978, 0.37984720892405116, 0.37700741021641826,
				0.37416406297145793, 0.37131719395183749, 0.36846682995337232,
				0.36561299780477385, 0.36275572436739723, 0.35989503653498811,
				0.35703096123343, 0.35416352542049034, 0.35129275608556709,
				0.34841868024943456, 0.34554132496398909, 0.34266071731199438,
				0.33977688440682685, 0.33688985339222005, 0.33399965144200938,
				0.33110630575987643, 0.3282098435790925, 0.32531029216226293,
				0.32240767880106985, 0.31950203081601569, 0.31659337555616585,
				0.31368174039889152, 0.31076715274961147, 0.30784964004153487,
				0.30492922973540237, 0.30200594931922808, 0.29907982630804048,
				0.29615088824362379, 0.29321916269425863, 0.29028467725446233,
				0.28734745954472951, 0.28440753721127188, 0.28146493792575794,
				0.27851968938505306, 0.27557181931095814, 0.272621355449949,
				0.26966832557291509, 0.26671275747489837, 0.26375467897483135,
				0.26079411791527551, 0.257831102162159, 0.25486565960451457,
				0.25189781815421697, 0.24892760574572015, 0.24595505033579459,
				0.24298017990326387, 0.2400030224487415, 0.2370236059943672,
				0.23404195858354343, 0.23105810828067111, 0.22807208317088573,
				0.22508391135979283, 0.22209362097320351, 0.2191012401568698,
				0.21610679707621952, 0.21311031991609136, 0.21011183688046961,
				0.20711137619221856, 0.20410896609281687, 0.2011046348420919,
				0.19809841071795356, 0.19509032201612825, 0.19208039704989244,
				0.18906866414980619, 0.18605515166344663, 0.18303988795514095,
				0.18002290140569951, 0.17700422041214875, 0.17398387338746382,
				0.17096188876030122, 0.16793829497473117, 0.16491312048996992,
				0.16188639378011183, 0.15885814333386145, 0.15582839765426523,
				0.15279718525844344, 0.14976453467732151, 0.14673047445536175,
				0.14369503315029447, 0.14065823933284921, 0.13762012158648604,
				0.13458070850712617, 0.13154002870288312, 0.12849811079379317,
				0.12545498341154623, 0.1224106751992162, 0.11936521481099135,
				0.11631863091190475, 0.11327095217756435, 0.11022220729388306,
				0.10717242495680884, 0.10412163387205459, 0.10106986275482782,
				0.0980171403295606, 0.094963495329638992, 0.091908956497132724,
				0.0888535525825246, 0.0857973123444399, 0.082740264549375692,
				0.079682437971430126, 0.076623861392031492, 0.073564563599667426,
				0.070504573389613856, 0.067443919563664051, 0.064382630929857465,
				0.061320736302208578, 0.058258264500435752, 0.055195244349689941,
				0.052131704680283324, 0.049067674327418015, 0.046003182130914623,
				0.04293825693494082, 0.039872927587739811, 0.036807222941358832,
				0.03374117185137758, 0.030674803176636626, 0.02760814577896574,
				0.024541228522912288, 0.021474080275469508, 0.01840672990580482,
				0.0153392062849881, 0.012271538285719925, 0.00920375478205982,
				0.0061358846491544753, 0.0030679567629659761, 0.0, -0.0030679567629659761,
				-0.0061358846491544753, -0.00920375478205982, -0.012271538285719925,
				-0.0153392062849881, -0.01840672990580482, -0.021474080275469508,
				-0.024541228522912288, -0.02760814577896574, -0.030674803176636626,
				-0.03374117185137758, -0.036807222941358832, -0.039872927587739811,
				-0.04293825693494082, -0.046003182130914623, -0.049067674327418015,
				-0.052131704680283324, -0.055195244349689941, -0.058258264500435752,
				-0.061320736302208578, -0.064382630929857465, -0.067443919563664051,
				-0.070504573389613856, -0.073564563599667426, -0.076623861392031492,
				-0.079682437971430126, -0.082740264549375692, -0.0857973123444399,
				-0.0888535525825246, -0.091908956497132724, -0.094963495329638992,
				-0.0980171403295606, -0.10106986275482782, -0.10412163387205459,
				-0.10717242495680884, -0.11022220729388306, -0.11327095217756435,
				-0.11631863091190475, -0.11936521481099135, -0.1224106751992162,
				-0.12545498341154623, -0.12849811079379317, -0.13154002870288312,
				-0.13458070850712617, -0.13762012158648604, -0.14065823933284921,
				-0.14369503315029447, -0.14673047445536175, -0.14976453467732151,
				-0.15279718525844344, -0.15582839765426523, -0.15885814333386145,
				-0.16188639378011183, -0.16491312048996992, -0.16793829497473117,
				-0.17096188876030122, -0.17398387338746382, -0.17700422041214875,
				-0.18002290140569951, -0.18303988795514095, -0.18605515166344663,
				-0.18906866414980619, -0.19208039704989244, -0.19509032201612825,
				-0.19809841071795356, -0.2011046348420919, -0.20410896609281687,
				-0.20711137619221856, -0.21011183688046961, -0.21311031991609136,
				-0.21610679707621952, -0.2191012401568698, -0.22209362097320351,
				-0.22508391135979283, -0.22807208317088573, -0.23105810828067111,
				-0.23404195858354343, -0.2370236059943672, -0.2400030224487415,
				-0.24298017990326387, -0.24595505033579459, -0.24892760574572015,
				-0.25189781815421697, -0.25486565960451457, -0.257831102162159,
				-0.26079411791527551, -0.26375467897483135, -0.26671275747489837,
				-0.26966832557291509, -0.272621355449949, -0.27557181931095814,
				-0.27851968938505306, -0.28146493792575794, -0.28440753721127188,
				-0.28734745954472951, -0.29028467725446233, -0.29321916269425863,
				-0.29615088824362379, -0.29907982630804048, -0.30200594931922808,
				-0.30492922973540237, -0.30784964004153487, -0.31076715274961147,
				-0.31368174039889152, -0.31659337555616585, -0.31950203081601569,
				-0.32240767880106985, -0.32531029216226293, -0.3282098435790925,
				-0.33110630575987643, -0.33399965144200938, -0.33688985339222005,
				-0.33977688440682685, -0.34266071731199438, -0.34554132496398909,
				-0.34841868024943456, -0.35129275608556709, -0.35416352542049034,
				-0.35703096123343, -0.35989503653498811, -0.36275572436739723,
				-0.36561299780477385, -0.36846682995337232, -0.37131719395183749,
				-0.37416406297145793, -0.37700741021641826, -0.37984720892405116,
				-0.38268343236508978, -0.38551605384391885, -0.38834504669882625,
				-0.39117038430225387, -0.3939920400610481, -0.39680998741671031,
				-0.39962419984564679, -0.40243465085941843, -0.40524131400498986,
				-0.40804416286497869, -0.41084317105790391, -0.4136383122384345,
				-0.41642956009763715, -0.41921688836322391, -0.42200027079979968,
				-0.42477968120910881, -0.42755509343028208, -0.43032648134008261,
				-0.43309381885315196, -0.43585707992225547, -0.43861623853852766,
				-0.44137126873171667, -0.4441221445704292, -0.44686884016237416,
				-0.44961132965460654, -0.45234958723377089, -0.45508358712634384,
				-0.45781330359887717, -0.46053871095824, -0.46325978355186015,
				-0.46597649576796618, -0.46868882203582796, -0.47139673682599764,
				-0.47410021465054997, -0.47679923006332209, -0.47949375766015295,
				-0.48218377207912272, -0.48486924800079106, -0.487550160148436,
				-0.49022648328829116, -0.49289819222978404, -0.49556526182577254,
				-0.49822766697278181, -0.50088538261124071, -0.50353838372571758,
				-0.50618664534515523, -0.508830142543107, -0.5114688504379703,
				-0.51410274419322166, -0.51673179901764987, -0.51935599016558964,
				-0.52197529293715439, -0.524589682678469, -0.52719913478190128,
				-0.52980362468629461, -0.5324031278771979, -0.53499761988709715,
				-0.53758707629564539, -0.54017147272989285, -0.54275078486451589,
				-0.54532498842204646, -0.54789405917310019, -0.55045797293660481,
				-0.55301670558002747, -0.55557023301960218, -0.5581185312205561,
				-0.560661576197336, -0.56319934401383409, -0.56573181078361312,
				-0.56825895267013149, -0.57078074588696726, -0.5732971666980422,
				-0.57580819141784534, -0.57831379641165559, -0.58081395809576453,
				-0.58330865293769829, -0.58579785745643886, -0.58828154822264522,
				-0.59075970185887416, -0.5932322950397998, -0.59569930449243336,
				-0.59816070699634238, -0.600616479383869, -0.60306659854034816,
				-0.60551104140432555, -0.60794978496777363, -0.61038280627630948,
				-0.61281008242940971, -0.61523159058062682, -0.61764730793780387,
				-0.6200572117632891, -0.62246127937415, -0.62485948814238634,
				-0.62725181549514408, -0.629638238914927, -0.63201873593980906,
				-0.63439328416364549, -0.6367618612362842, -0.63912444486377573,
				-0.641481012808583, -0.64383154288979139, -0.64617601298331628,
				-0.64851440102211244, -0.650846684996381, -0.65317284295377676,
				-0.65549285299961535, -0.65780669329707864, -0.66011434206742048,
				-0.66241577759017178, -0.66471097820334479, -0.66699992230363747,
				-0.669282588346636, -0.67155895484701833, -0.673829000378756,
				-0.67609270357531592, -0.67835004312986147, -0.680600997795453,
				-0.68284554638524808, -0.68508366777270036, -0.687315340891759,
				-0.68954054473706683, -0.69175925836415775, -0.69397146088965389,
				-0.696177131491463, -0.69837624940897292, -0.70056879394324834,
				-0.7027547444572253, -0.70493408037590488, -0.70710678118654757,
				-0.70927282643886569, -0.71143219574521643, -0.71358486878079352,
				-0.71573082528381859, -0.71787004505573171, -0.72000250796138165,
				-0.72212819392921535, -0.724247082951467, -0.726359155084346,
				-0.7284643904482252, -0.73056276922782759, -0.73265427167241282,
				-0.7347388780959635, -0.73681656887736979, -0.73888732446061511,
				-0.74095112535495922, -0.74300795213512172, -0.745057785441466,
				-0.74710060598018013, -0.74913639452345937, -0.75116513190968637,
				-0.75318679904361252, -0.75520137689653655, -0.75720884650648457,
				-0.759209188978388, -0.76120238548426178, -0.76318841726338127,
				-0.765167265622459, -0.7671389119358204, -0.7691033376455797,
				-0.77106052426181382, -0.773010453362737, -0.77495310659487393,
				-0.77688846567323244, -0.778816512381476, -0.78073722857209449,
				-0.78265059616657573, -0.78455659715557524, -0.78645521359908577,
				-0.78834642762660634, -0.79023022143731, -0.79210657730021239,
				-0.79397547755433717, -0.79583690460888357, -0.79769084094339116,
				-0.799537269107905, -0.80137617172314024, -0.80320753148064494,
				-0.80503133114296366, -0.80684755354379933, -0.808656181588175,
				-0.81045719825259477, -0.81225058658520388, -0.81403632970594841,
				-0.81581441080673378, -0.81758481315158371, -0.819347520076797,
				-0.82110251499110465, -0.82284978137582643, -0.82458930278502529,
				-0.82632106284566353, -0.8280450452577558, -0.829761233794523,
				-0.83146961230254524, -0.83317016470191319, -0.83486287498638,
				-0.836547727223512, -0.83822470555483808, -0.83989379419599952,
				-0.84155497743689844, -0.84320823964184544, -0.84485356524970712,
				-0.84649093877405213, -0.84812034480329723, -0.84974176800085255,
				-0.8513551931052652, -0.85296060493036363, -0.85455798836540053,
				-0.85614732837519447, -0.85772861000027212, -0.85930181835700847,
				-0.86086693863776731, -0.8624239561110405, -0.8639728561215867,
				-0.86551362409056909, -0.86704624551569265, -0.8685707059713409,
				-0.87008699110871146, -0.87159508665595109, -0.87309497841829009,
				-0.87458665227817611, -0.8760700941954066, -0.87754529020726135,
				-0.87901222642863353, -0.88047088905216075, -0.881921264348355,
				-0.88336333866573158, -0.88479709843093779, -0.88622253014888064,
				-0.88763962040285393, -0.88904835585466457, -0.89044872324475788,
				-0.89184070939234272, -0.89322430119551532, -0.8945994856313827,
				-0.89596624975618522, -0.89732458070541832, -0.89867446569395382,
				-0.90001589201616017, -0.901348847046022, -0.90267331823725883,
				-0.90398929312344334, -0.90529675931811882, -0.90659570451491533,
				-0.90788611648766626, -0.90916798309052238, -0.91044129225806725,
				-0.91170603200542988, -0.91296219042839821, -0.91420975570353069,
				-0.91544871608826783, -0.9166790599210427, -0.9179007756213905,
				-0.91911385169005777, -0.92031827670911059, -0.9215140393420419,
				-0.92270112833387863, -0.92387953251128674, -0.92504924078267758,
				-0.92621024213831138, -0.92736252565040111, -0.92850608047321559,
				-0.92964089584318121, -0.93076696107898371, -0.93188426558166815,
				-0.932992798834739, -0.93409255040425887, -0.93518350993894761,
				-0.93626566717027826, -0.937339011912575, -0.93840353406310806,
				-0.93945922360218992, -0.9405060705932683, -0.94154406518302081,
				-0.94257319760144687, -0.94359345816196039, -0.94460483726148026,
				-0.94560732538052128, -0.94660091308328353, -0.94758559101774109,
				-0.94856134991573027, -0.94952818059303667, -0.9504860739494817,
				-0.95143502096900834, -0.95237501271976588, -0.95330604035419386,
				-0.95422809510910567, -0.95514116830577078, -0.95604525134999641,
				-0.95694033573220882, -0.95782641302753291, -0.9587034748958716,
				-0.95957151308198452, -0.96043051941556579, -0.96128048581132064,
				-0.96212140426904158, -0.96295326687368388, -0.96377606579543984,
				-0.96458979328981276, -0.9653944416976894, -0.9661900034454125,
				-0.96697647104485207, -0.96775383709347551, -0.96852209427441727,
				-0.96928123535654853, -0.970031253194544, -0.97077214072895035,
				-0.97150389098625178, -0.97222649707893627, -0.97293995220556018,
				-0.973644249650812, -0.97433938278557586, -0.97502534506699412,
				-0.97570213003852857, -0.97636973133002114, -0.97702814265775439,
				-0.97767735782450993, -0.97831737071962765, -0.9789481753190622,
				-0.97956976568544052, -0.98018213596811743, -0.98078528040323043,
				-0.98137919331375456, -0.98196386910955524, -0.98253930228744124,
				-0.98310548743121629, -0.98366241921173025, -0.984210092386929,
				-0.98474850180190421, -0.98527764238894122, -0.98579750916756748,
				-0.98630809724459867, -0.98680940181418553, -0.98730141815785843,
				-0.98778414164457218, -0.98825756773074946, -0.98872169196032378,
				-0.989176509964781, -0.98962201746320089, -0.99005821026229712,
				-0.99048508425645709, -0.99090263542778, -0.99131085984611544,
				-0.99170975366909953, -0.9920993131421918, -0.99247953459871,
				-0.9928504144598651, -0.9932119492347945, -0.9935641355205953,
				-0.99390697000235606, -0.9942404494531879, -0.99456457073425542,
				-0.99487933079480562, -0.99518472667219693, -0.99548075549192694,
				-0.99576741446765982, -0.996044700901252, -0.996312612182778,
				-0.99657114579055484, -0.99682029929116567, -0.997060070339483,
				-0.99729045667869021, -0.99751145614030345, -0.99772306664419164,
				-0.997925286198596, -0.99811811290014918, -0.99830154493389289,
				-0.99847558057329477, -0.99864021818026527, -0.99879545620517241,
				-0.99894129318685687, -0.99907772775264536, -0.99920475861836389,
				-0.99932238458834954, -0.99943060455546173, -0.99952941750109314,
				-0.99961882249517864, -0.99969881869620425, -0.99976940535121528,
				-0.9998305817958234, -0.99988234745421256, -0.9999247018391445,
				-0.9999576445519639, -0.99998117528260111, -0.99999529380957619, -1.0 };
		double twid_im;
		double dv1[] = { 0.0, -0.0030679567629659761,
				-0.0061358846491544753, -0.00920375478205982, -0.012271538285719925,
				-0.0153392062849881, -0.01840672990580482, -0.021474080275469508,
				-0.024541228522912288, -0.02760814577896574, -0.030674803176636626,
				-0.03374117185137758, -0.036807222941358832, -0.039872927587739811,
				-0.04293825693494082, -0.046003182130914623, -0.049067674327418015,
				-0.052131704680283324, -0.055195244349689941, -0.058258264500435752,
				-0.061320736302208578, -0.064382630929857465, -0.067443919563664051,
				-0.070504573389613856, -0.073564563599667426, -0.076623861392031492,
				-0.079682437971430126, -0.082740264549375692, -0.0857973123444399,
				-0.0888535525825246, -0.091908956497132724, -0.094963495329638992,
				-0.0980171403295606, -0.10106986275482782, -0.10412163387205459,
				-0.10717242495680884, -0.11022220729388306, -0.11327095217756435,
				-0.11631863091190475, -0.11936521481099135, -0.1224106751992162,
				-0.12545498341154623, -0.12849811079379317, -0.13154002870288312,
				-0.13458070850712617, -0.13762012158648604, -0.14065823933284921,
				-0.14369503315029447, -0.14673047445536175, -0.14976453467732151,
				-0.15279718525844344, -0.15582839765426523, -0.15885814333386145,
				-0.16188639378011183, -0.16491312048996992, -0.16793829497473117,
				-0.17096188876030122, -0.17398387338746382, -0.17700422041214875,
				-0.18002290140569951, -0.18303988795514095, -0.18605515166344663,
				-0.18906866414980619, -0.19208039704989244, -0.19509032201612825,
				-0.19809841071795356, -0.2011046348420919, -0.20410896609281687,
				-0.20711137619221856, -0.21011183688046961, -0.21311031991609136,
				-0.21610679707621952, -0.2191012401568698, -0.22209362097320351,
				-0.22508391135979283, -0.22807208317088573, -0.23105810828067111,
				-0.23404195858354343, -0.2370236059943672, -0.2400030224487415,
				-0.24298017990326387, -0.24595505033579459, -0.24892760574572015,
				-0.25189781815421697, -0.25486565960451457, -0.257831102162159,
				-0.26079411791527551, -0.26375467897483135, -0.26671275747489837,
				-0.26966832557291509, -0.272621355449949, -0.27557181931095814,
				-0.27851968938505306, -0.28146493792575794, -0.28440753721127188,
				-0.28734745954472951, -0.29028467725446233, -0.29321916269425863,
				-0.29615088824362379, -0.29907982630804048, -0.30200594931922808,
				-0.30492922973540237, -0.30784964004153487, -0.31076715274961147,
				-0.31368174039889152, -0.31659337555616585, -0.31950203081601569,
				-0.32240767880106985, -0.32531029216226293, -0.3282098435790925,
				-0.33110630575987643, -0.33399965144200938, -0.33688985339222005,
				-0.33977688440682685, -0.34266071731199438, -0.34554132496398909,
				-0.34841868024943456, -0.35129275608556709, -0.35416352542049034,
				-0.35703096123343, -0.35989503653498811, -0.36275572436739723,
				-0.36561299780477385, -0.36846682995337232, -0.37131719395183749,
				-0.37416406297145793, -0.37700741021641826, -0.37984720892405116,
				-0.38268343236508978, -0.38551605384391885, -0.38834504669882625,
				-0.39117038430225387, -0.3939920400610481, -0.39680998741671031,
				-0.39962419984564679, -0.40243465085941843, -0.40524131400498986,
				-0.40804416286497869, -0.41084317105790391, -0.4136383122384345,
				-0.41642956009763715, -0.41921688836322391, -0.42200027079979968,
				-0.42477968120910881, -0.42755509343028208, -0.43032648134008261,
				-0.43309381885315196, -0.43585707992225547, -0.43861623853852766,
				-0.44137126873171667, -0.4441221445704292, -0.44686884016237416,
				-0.44961132965460654, -0.45234958723377089, -0.45508358712634384,
				-0.45781330359887717, -0.46053871095824, -0.46325978355186015,
				-0.46597649576796618, -0.46868882203582796, -0.47139673682599764,
				-0.47410021465054997, -0.47679923006332209, -0.47949375766015295,
				-0.48218377207912272, -0.48486924800079106, -0.487550160148436,
				-0.49022648328829116, -0.49289819222978404, -0.49556526182577254,
				-0.49822766697278181, -0.50088538261124071, -0.50353838372571758,
				-0.50618664534515523, -0.508830142543107, -0.5114688504379703,
				-0.51410274419322166, -0.51673179901764987, -0.51935599016558964,
				-0.52197529293715439, -0.524589682678469, -0.52719913478190128,
				-0.52980362468629461, -0.5324031278771979, -0.53499761988709715,
				-0.53758707629564539, -0.54017147272989285, -0.54275078486451589,
				-0.54532498842204646, -0.54789405917310019, -0.55045797293660481,
				-0.55301670558002747, -0.55557023301960218, -0.5581185312205561,
				-0.560661576197336, -0.56319934401383409, -0.56573181078361312,
				-0.56825895267013149, -0.57078074588696726, -0.5732971666980422,
				-0.57580819141784534, -0.57831379641165559, -0.58081395809576453,
				-0.58330865293769829, -0.58579785745643886, -0.58828154822264522,
				-0.59075970185887416, -0.5932322950397998, -0.59569930449243336,
				-0.59816070699634238, -0.600616479383869, -0.60306659854034816,
				-0.60551104140432555, -0.60794978496777363, -0.61038280627630948,
				-0.61281008242940971, -0.61523159058062682, -0.61764730793780387,
				-0.6200572117632891, -0.62246127937415, -0.62485948814238634,
				-0.62725181549514408, -0.629638238914927, -0.63201873593980906,
				-0.63439328416364549, -0.6367618612362842, -0.63912444486377573,
				-0.641481012808583, -0.64383154288979139, -0.64617601298331628,
				-0.64851440102211244, -0.650846684996381, -0.65317284295377676,
				-0.65549285299961535, -0.65780669329707864, -0.66011434206742048,
				-0.66241577759017178, -0.66471097820334479, -0.66699992230363747,
				-0.669282588346636, -0.67155895484701833, -0.673829000378756,
				-0.67609270357531592, -0.67835004312986147, -0.680600997795453,
				-0.68284554638524808, -0.68508366777270036, -0.687315340891759,
				-0.68954054473706683, -0.69175925836415775, -0.69397146088965389,
				-0.696177131491463, -0.69837624940897292, -0.70056879394324834,
				-0.7027547444572253, -0.70493408037590488, -0.70710678118654757,
				-0.70927282643886569, -0.71143219574521643, -0.71358486878079352,
				-0.71573082528381859, -0.71787004505573171, -0.72000250796138165,
				-0.72212819392921535, -0.724247082951467, -0.726359155084346,
				-0.7284643904482252, -0.73056276922782759, -0.73265427167241282,
				-0.7347388780959635, -0.73681656887736979, -0.73888732446061511,
				-0.74095112535495922, -0.74300795213512172, -0.745057785441466,
				-0.74710060598018013, -0.74913639452345937, -0.75116513190968637,
				-0.75318679904361252, -0.75520137689653655, -0.75720884650648457,
				-0.759209188978388, -0.76120238548426178, -0.76318841726338127,
				-0.765167265622459, -0.7671389119358204, -0.7691033376455797,
				-0.77106052426181382, -0.773010453362737, -0.77495310659487393,
				-0.77688846567323244, -0.778816512381476, -0.78073722857209449,
				-0.78265059616657573, -0.78455659715557524, -0.78645521359908577,
				-0.78834642762660634, -0.79023022143731, -0.79210657730021239,
				-0.79397547755433717, -0.79583690460888357, -0.79769084094339116,
				-0.799537269107905, -0.80137617172314024, -0.80320753148064494,
				-0.80503133114296366, -0.80684755354379933, -0.808656181588175,
				-0.81045719825259477, -0.81225058658520388, -0.81403632970594841,
				-0.81581441080673378, -0.81758481315158371, -0.819347520076797,
				-0.82110251499110465, -0.82284978137582643, -0.82458930278502529,
				-0.82632106284566353, -0.8280450452577558, -0.829761233794523,
				-0.83146961230254524, -0.83317016470191319, -0.83486287498638,
				-0.836547727223512, -0.83822470555483808, -0.83989379419599952,
				-0.84155497743689844, -0.84320823964184544, -0.84485356524970712,
				-0.84649093877405213, -0.84812034480329723, -0.84974176800085255,
				-0.8513551931052652, -0.85296060493036363, -0.85455798836540053,
				-0.85614732837519447, -0.85772861000027212, -0.85930181835700847,
				-0.86086693863776731, -0.8624239561110405, -0.8639728561215867,
				-0.86551362409056909, -0.86704624551569265, -0.8685707059713409,
				-0.87008699110871146, -0.87159508665595109, -0.87309497841829009,
				-0.87458665227817611, -0.8760700941954066, -0.87754529020726135,
				-0.87901222642863353, -0.88047088905216075, -0.881921264348355,
				-0.88336333866573158, -0.88479709843093779, -0.88622253014888064,
				-0.88763962040285393, -0.88904835585466457, -0.89044872324475788,
				-0.89184070939234272, -0.89322430119551532, -0.8945994856313827,
				-0.89596624975618522, -0.89732458070541832, -0.89867446569395382,
				-0.90001589201616017, -0.901348847046022, -0.90267331823725883,
				-0.90398929312344334, -0.90529675931811882, -0.90659570451491533,
				-0.90788611648766626, -0.90916798309052238, -0.91044129225806725,
				-0.91170603200542988, -0.91296219042839821, -0.91420975570353069,
				-0.91544871608826783, -0.9166790599210427, -0.9179007756213905,
				-0.91911385169005777, -0.92031827670911059, -0.9215140393420419,
				-0.92270112833387863, -0.92387953251128674, -0.92504924078267758,
				-0.92621024213831138, -0.92736252565040111, -0.92850608047321559,
				-0.92964089584318121, -0.93076696107898371, -0.93188426558166815,
				-0.932992798834739, -0.93409255040425887, -0.93518350993894761,
				-0.93626566717027826, -0.937339011912575, -0.93840353406310806,
				-0.93945922360218992, -0.9405060705932683, -0.94154406518302081,
				-0.94257319760144687, -0.94359345816196039, -0.94460483726148026,
				-0.94560732538052128, -0.94660091308328353, -0.94758559101774109,
				-0.94856134991573027, -0.94952818059303667, -0.9504860739494817,
				-0.95143502096900834, -0.95237501271976588, -0.95330604035419386,
				-0.95422809510910567, -0.95514116830577078, -0.95604525134999641,
				-0.95694033573220882, -0.95782641302753291, -0.9587034748958716,
				-0.95957151308198452, -0.96043051941556579, -0.96128048581132064,
				-0.96212140426904158, -0.96295326687368388, -0.96377606579543984,
				-0.96458979328981276, -0.9653944416976894, -0.9661900034454125,
				-0.96697647104485207, -0.96775383709347551, -0.96852209427441727,
				-0.96928123535654853, -0.970031253194544, -0.97077214072895035,
				-0.97150389098625178, -0.97222649707893627, -0.97293995220556018,
				-0.973644249650812, -0.97433938278557586, -0.97502534506699412,
				-0.97570213003852857, -0.97636973133002114, -0.97702814265775439,
				-0.97767735782450993, -0.97831737071962765, -0.9789481753190622,
				-0.97956976568544052, -0.98018213596811743, -0.98078528040323043,
				-0.98137919331375456, -0.98196386910955524, -0.98253930228744124,
				-0.98310548743121629, -0.98366241921173025, -0.984210092386929,
				-0.98474850180190421, -0.98527764238894122, -0.98579750916756748,
				-0.98630809724459867, -0.98680940181418553, -0.98730141815785843,
				-0.98778414164457218, -0.98825756773074946, -0.98872169196032378,
				-0.989176509964781, -0.98962201746320089, -0.99005821026229712,
				-0.99048508425645709, -0.99090263542778, -0.99131085984611544,
				-0.99170975366909953, -0.9920993131421918, -0.99247953459871,
				-0.9928504144598651, -0.9932119492347945, -0.9935641355205953,
				-0.99390697000235606, -0.9942404494531879, -0.99456457073425542,
				-0.99487933079480562, -0.99518472667219693, -0.99548075549192694,
				-0.99576741446765982, -0.996044700901252, -0.996312612182778,
				-0.99657114579055484, -0.99682029929116567, -0.997060070339483,
				-0.99729045667869021, -0.99751145614030345, -0.99772306664419164,
				-0.997925286198596, -0.99811811290014918, -0.99830154493389289,
				-0.99847558057329477, -0.99864021818026527, -0.99879545620517241,
				-0.99894129318685687, -0.99907772775264536, -0.99920475861836389,
				-0.99932238458834954, -0.99943060455546173, -0.99952941750109314,
				-0.99961882249517864, -0.99969881869620425, -0.99976940535121528,
				-0.9998305817958234, -0.99988234745421256, -0.9999247018391445,
				-0.9999576445519639, -0.99998117528260111, -0.99999529380957619, -1.0,
				-0.99999529380957619, -0.99998117528260111, -0.9999576445519639,
				-0.9999247018391445, -0.99988234745421256, -0.9998305817958234,
				-0.99976940535121528, -0.99969881869620425, -0.99961882249517864,
				-0.99952941750109314, -0.99943060455546173, -0.99932238458834954,
				-0.99920475861836389, -0.99907772775264536, -0.99894129318685687,
				-0.99879545620517241, -0.99864021818026527, -0.99847558057329477,
				-0.99830154493389289, -0.99811811290014918, -0.997925286198596,
				-0.99772306664419164, -0.99751145614030345, -0.99729045667869021,
				-0.997060070339483, -0.99682029929116567, -0.99657114579055484,
				-0.996312612182778, -0.996044700901252, -0.99576741446765982,
				-0.99548075549192694, -0.99518472667219693, -0.99487933079480562,
				-0.99456457073425542, -0.9942404494531879, -0.99390697000235606,
				-0.9935641355205953, -0.9932119492347945, -0.9928504144598651,
				-0.99247953459871, -0.9920993131421918, -0.99170975366909953,
				-0.99131085984611544, -0.99090263542778, -0.99048508425645709,
				-0.99005821026229712, -0.98962201746320089, -0.989176509964781,
				-0.98872169196032378, -0.98825756773074946, -0.98778414164457218,
				-0.98730141815785843, -0.98680940181418553, -0.98630809724459867,
				-0.98579750916756748, -0.98527764238894122, -0.98474850180190421,
				-0.984210092386929, -0.98366241921173025, -0.98310548743121629,
				-0.98253930228744124, -0.98196386910955524, -0.98137919331375456,
				-0.98078528040323043, -0.98018213596811743, -0.97956976568544052,
				-0.9789481753190622, -0.97831737071962765, -0.97767735782450993,
				-0.97702814265775439, -0.97636973133002114, -0.97570213003852857,
				-0.97502534506699412, -0.97433938278557586, -0.973644249650812,
				-0.97293995220556018, -0.97222649707893627, -0.97150389098625178,
				-0.97077214072895035, -0.970031253194544, -0.96928123535654853,
				-0.96852209427441727, -0.96775383709347551, -0.96697647104485207,
				-0.9661900034454125, -0.9653944416976894, -0.96458979328981276,
				-0.96377606579543984, -0.96295326687368388, -0.96212140426904158,
				-0.96128048581132064, -0.96043051941556579, -0.95957151308198452,
				-0.9587034748958716, -0.95782641302753291, -0.95694033573220882,
				-0.95604525134999641, -0.95514116830577078, -0.95422809510910567,
				-0.95330604035419386, -0.95237501271976588, -0.95143502096900834,
				-0.9504860739494817, -0.94952818059303667, -0.94856134991573027,
				-0.94758559101774109, -0.94660091308328353, -0.94560732538052128,
				-0.94460483726148026, -0.94359345816196039, -0.94257319760144687,
				-0.94154406518302081, -0.9405060705932683, -0.93945922360218992,
				-0.93840353406310806, -0.937339011912575, -0.93626566717027826,
				-0.93518350993894761, -0.93409255040425887, -0.932992798834739,
				-0.93188426558166815, -0.93076696107898371, -0.92964089584318121,
				-0.92850608047321559, -0.92736252565040111, -0.92621024213831138,
				-0.92504924078267758, -0.92387953251128674, -0.92270112833387863,
				-0.9215140393420419, -0.92031827670911059, -0.91911385169005777,
				-0.9179007756213905, -0.9166790599210427, -0.91544871608826783,
				-0.91420975570353069, -0.91296219042839821, -0.91170603200542988,
				-0.91044129225806725, -0.90916798309052238, -0.90788611648766626,
				-0.90659570451491533, -0.90529675931811882, -0.90398929312344334,
				-0.90267331823725883, -0.901348847046022, -0.90001589201616017,
				-0.89867446569395382, -0.89732458070541832, -0.89596624975618522,
				-0.8945994856313827, -0.89322430119551532, -0.89184070939234272,
				-0.89044872324475788, -0.88904835585466457, -0.88763962040285393,
				-0.88622253014888064, -0.88479709843093779, -0.88336333866573158,
				-0.881921264348355, -0.88047088905216075, -0.87901222642863353,
				-0.87754529020726135, -0.8760700941954066, -0.87458665227817611,
				-0.87309497841829009, -0.87159508665595109, -0.87008699110871146,
				-0.8685707059713409, -0.86704624551569265, -0.86551362409056909,
				-0.8639728561215867, -0.8624239561110405, -0.86086693863776731,
				-0.85930181835700847, -0.85772861000027212, -0.85614732837519447,
				-0.85455798836540053, -0.85296060493036363, -0.8513551931052652,
				-0.84974176800085255, -0.84812034480329723, -0.84649093877405213,
				-0.84485356524970712, -0.84320823964184544, -0.84155497743689844,
				-0.83989379419599952, -0.83822470555483808, -0.836547727223512,
				-0.83486287498638, -0.83317016470191319, -0.83146961230254524,
				-0.829761233794523, -0.8280450452577558, -0.82632106284566353,
				-0.82458930278502529, -0.82284978137582643, -0.82110251499110465,
				-0.819347520076797, -0.81758481315158371, -0.81581441080673378,
				-0.81403632970594841, -0.81225058658520388, -0.81045719825259477,
				-0.808656181588175, -0.80684755354379933, -0.80503133114296366,
				-0.80320753148064494, -0.80137617172314024, -0.799537269107905,
				-0.79769084094339116, -0.79583690460888357, -0.79397547755433717,
				-0.79210657730021239, -0.79023022143731, -0.78834642762660634,
				-0.78645521359908577, -0.78455659715557524, -0.78265059616657573,
				-0.78073722857209449, -0.778816512381476, -0.77688846567323244,
				-0.77495310659487393, -0.773010453362737, -0.77106052426181382,
				-0.7691033376455797, -0.7671389119358204, -0.765167265622459,
				-0.76318841726338127, -0.76120238548426178, -0.759209188978388,
				-0.75720884650648457, -0.75520137689653655, -0.75318679904361252,
				-0.75116513190968637, -0.74913639452345937, -0.74710060598018013,
				-0.745057785441466, -0.74300795213512172, -0.74095112535495922,
				-0.73888732446061511, -0.73681656887736979, -0.7347388780959635,
				-0.73265427167241282, -0.73056276922782759, -0.7284643904482252,
				-0.726359155084346, -0.724247082951467, -0.72212819392921535,
				-0.72000250796138165, -0.71787004505573171, -0.71573082528381859,
				-0.71358486878079352, -0.71143219574521643, -0.70927282643886569,
				-0.70710678118654757, -0.70493408037590488, -0.7027547444572253,
				-0.70056879394324834, -0.69837624940897292, -0.696177131491463,
				-0.69397146088965389, -0.69175925836415775, -0.68954054473706683,
				-0.687315340891759, -0.68508366777270036, -0.68284554638524808,
				-0.680600997795453, -0.67835004312986147, -0.67609270357531592,
				-0.673829000378756, -0.67155895484701833, -0.669282588346636,
				-0.66699992230363747, -0.66471097820334479, -0.66241577759017178,
				-0.66011434206742048, -0.65780669329707864, -0.65549285299961535,
				-0.65317284295377676, -0.650846684996381, -0.64851440102211244,
				-0.64617601298331628, -0.64383154288979139, -0.641481012808583,
				-0.63912444486377573, -0.6367618612362842, -0.63439328416364549,
				-0.63201873593980906, -0.629638238914927, -0.62725181549514408,
				-0.62485948814238634, -0.62246127937415, -0.6200572117632891,
				-0.61764730793780387, -0.61523159058062682, -0.61281008242940971,
				-0.61038280627630948, -0.60794978496777363, -0.60551104140432555,
				-0.60306659854034816, -0.600616479383869, -0.59816070699634238,
				-0.59569930449243336, -0.5932322950397998, -0.59075970185887416,
				-0.58828154822264522, -0.58579785745643886, -0.58330865293769829,
				-0.58081395809576453, -0.57831379641165559, -0.57580819141784534,
				-0.5732971666980422, -0.57078074588696726, -0.56825895267013149,
				-0.56573181078361312, -0.56319934401383409, -0.560661576197336,
				-0.5581185312205561, -0.55557023301960218, -0.55301670558002747,
				-0.55045797293660481, -0.54789405917310019, -0.54532498842204646,
				-0.54275078486451589, -0.54017147272989285, -0.53758707629564539,
				-0.53499761988709715, -0.5324031278771979, -0.52980362468629461,
				-0.52719913478190128, -0.524589682678469, -0.52197529293715439,
				-0.51935599016558964, -0.51673179901764987, -0.51410274419322166,
				-0.5114688504379703, -0.508830142543107, -0.50618664534515523,
				-0.50353838372571758, -0.50088538261124071, -0.49822766697278181,
				-0.49556526182577254, -0.49289819222978404, -0.49022648328829116,
				-0.487550160148436, -0.48486924800079106, -0.48218377207912272,
				-0.47949375766015295, -0.47679923006332209, -0.47410021465054997,
				-0.47139673682599764, -0.46868882203582796, -0.46597649576796618,
				-0.46325978355186015, -0.46053871095824, -0.45781330359887717,
				-0.45508358712634384, -0.45234958723377089, -0.44961132965460654,
				-0.44686884016237416, -0.4441221445704292, -0.44137126873171667,
				-0.43861623853852766, -0.43585707992225547, -0.43309381885315196,
				-0.43032648134008261, -0.42755509343028208, -0.42477968120910881,
				-0.42200027079979968, -0.41921688836322391, -0.41642956009763715,
				-0.4136383122384345, -0.41084317105790391, -0.40804416286497869,
				-0.40524131400498986, -0.40243465085941843, -0.39962419984564679,
				-0.39680998741671031, -0.3939920400610481, -0.39117038430225387,
				-0.38834504669882625, -0.38551605384391885, -0.38268343236508978,
				-0.37984720892405116, -0.37700741021641826, -0.37416406297145793,
				-0.37131719395183749, -0.36846682995337232, -0.36561299780477385,
				-0.36275572436739723, -0.35989503653498811, -0.35703096123343,
				-0.35416352542049034, -0.35129275608556709, -0.34841868024943456,
				-0.34554132496398909, -0.34266071731199438, -0.33977688440682685,
				-0.33688985339222005, -0.33399965144200938, -0.33110630575987643,
				-0.3282098435790925, -0.32531029216226293, -0.32240767880106985,
				-0.31950203081601569, -0.31659337555616585, -0.31368174039889152,
				-0.31076715274961147, -0.30784964004153487, -0.30492922973540237,
				-0.30200594931922808, -0.29907982630804048, -0.29615088824362379,
				-0.29321916269425863, -0.29028467725446233, -0.28734745954472951,
				-0.28440753721127188, -0.28146493792575794, -0.27851968938505306,
				-0.27557181931095814, -0.272621355449949, -0.26966832557291509,
				-0.26671275747489837, -0.26375467897483135, -0.26079411791527551,
				-0.257831102162159, -0.25486565960451457, -0.25189781815421697,
				-0.24892760574572015, -0.24595505033579459, -0.24298017990326387,
				-0.2400030224487415, -0.2370236059943672, -0.23404195858354343,
				-0.23105810828067111, -0.22807208317088573, -0.22508391135979283,
				-0.22209362097320351, -0.2191012401568698, -0.21610679707621952,
				-0.21311031991609136, -0.21011183688046961, -0.20711137619221856,
				-0.20410896609281687, -0.2011046348420919, -0.19809841071795356,
				-0.19509032201612825, -0.19208039704989244, -0.18906866414980619,
				-0.18605515166344663, -0.18303988795514095, -0.18002290140569951,
				-0.17700422041214875, -0.17398387338746382, -0.17096188876030122,
				-0.16793829497473117, -0.16491312048996992, -0.16188639378011183,
				-0.15885814333386145, -0.15582839765426523, -0.15279718525844344,
				-0.14976453467732151, -0.14673047445536175, -0.14369503315029447,
				-0.14065823933284921, -0.13762012158648604, -0.13458070850712617,
				-0.13154002870288312, -0.12849811079379317, -0.12545498341154623,
				-0.1224106751992162, -0.11936521481099135, -0.11631863091190475,
				-0.11327095217756435, -0.11022220729388306, -0.10717242495680884,
				-0.10412163387205459, -0.10106986275482782, -0.0980171403295606,
				-0.094963495329638992, -0.091908956497132724, -0.0888535525825246,
				-0.0857973123444399, -0.082740264549375692, -0.079682437971430126,
				-0.076623861392031492, -0.073564563599667426, -0.070504573389613856,
				-0.067443919563664051, -0.064382630929857465, -0.061320736302208578,
				-0.058258264500435752, -0.055195244349689941, -0.052131704680283324,
				-0.049067674327418015, -0.046003182130914623, -0.04293825693494082,
				-0.039872927587739811, -0.036807222941358832, -0.03374117185137758,
				-0.030674803176636626, -0.02760814577896574, -0.024541228522912288,
				-0.021474080275469508, -0.01840672990580482, -0.0153392062849881,
				-0.012271538285719925, -0.00920375478205982, -0.0061358846491544753,
				-0.0030679567629659761, -0.0 };



		int midoffset;
		for (i = 0; i<2048; i++) {
			// not required in java
			y_re[i] = 0;
			y_im[i] = 0;
		}

		ix = 0;
		ju = 0;
		iy = 0;

		for (i = 0; i<49; i++) {
			y_re[iy] = X[ix];
			y_im[iy] = 0;
			iy = 2048;
			tst = true;
			while (tst) {
				iy >>= 1;
				ju ^= iy;
				tst = ((ju & iy) == 0);
			}
			iy = ju;
			ix++;
		}

		y_re[iy] = X[ix];
		y_im[iy] = 0;

		for (i = 0; i <= 2047; i += 2) {
			temp_re = y_re[i + 1];
			temp_im = y_im[i + 1];
			y_re[i + 1] = y_re[i] - y_re[i + 1];
			y_im[i + 1] = y_im[i] - y_im[i + 1];
			y_re[i] += temp_re;
			y_im[i] += temp_im;
		}

		iy = 2;
		ix = 4;
		k = 512;
		ju = 2045;

		while (k > 0) {
			for (i = 0; i < ju; i += ix) {
				temp_re = y_re[i + iy];
				temp_im = y_im[i + iy];
				y_re[i + iy] = y_re[i] - temp_re;
				y_im[i + iy] = y_im[i] - temp_im;
				y_re[i] += temp_re;
				y_im[i] += temp_im;
			}

			ic = 1;
			for (j = k; j < 1024; j += k) {
				twid_re = dv0[j];
				twid_im = dv1[j];
				i = ic;
				ihi = ic + ju;
				while (i < ihi) {
					temp_re = twid_re * y_re[i + iy] - twid_im * y_im[i + iy];
					temp_im = twid_re * y_im[i + iy] + twid_im * y_re[i + iy];
					y_re[i + iy] = y_re[i] - temp_re;
					y_im[i + iy] = y_im[i] - temp_im;
					y_re[i] += temp_re;
					y_im[i] += temp_im;
					i += ix;
				}

				ic++;
			}

			k /= 2;
			iy = ix;
			ix += ix;
			ju -= iy;
		}





		for (ihi = 0; ihi < 2; ihi++) {
			if (ihi + 1 <= 1) {
				iy = 2048;
			} else {
				iy = 1;
			}

			if (!(iy <= 1)) {
				i = iy / 2;
				vstride = 1;
				k = 1;
				while (k <= ihi) {
					vstride <<= 11;
					k = 2;
				}

				midoffset = i * vstride - 1;
				if (i << 1 == iy) {
					iy = 0;
					for (j = 1; j <= vstride; j++) {
						iy++;
						ix = iy-1;
						ju = (iy + midoffset);
						for (k = 1; k <= i; k++) {
							temp_re = y_re[ix];
							temp_im = y_im[ix];
							y_re[ix] = y_re[ju];
							y_im[ix] = y_im[ju];
							y_re[ju] = temp_re;
							y_im[ju] = temp_im;
							ix += vstride;
							ju += vstride;
						}
					}
				} else {
					iy = 0;
					for (j = 1; j <= vstride; j++) {
						iy++;
						ix = iy-1;
						ju = (iy + midoffset);
						temp_re = y_re[ju];
						temp_im = y_im[ju];
						for (k = 1; k <= i; k++) {
							ic = ju + vstride;
							y_re[ju] = y_re[ix];
							y_re[ix] = y_re[ic];
							y_im[ju] = y_im[ix];
							y_im[ix] = y_im[ic];
							ix += vstride;
							ju = ic;
						}

						y_re[ju] = temp_re;
						y_im[ju] = temp_im;
					}
				}
			}
		}

		double[] fftX = new double[2048];
		for (k = 0; k < 2048; k++) {
			fftX[k] = rt_hypotd_snf(y_re[k], y_im[k]);
		}

		return fftX;
		}
		else {
			throw new Exception("Invalid input for FFT : UC.");
		}
	}

	public double rt_hypotd_snf(double u0, double u1) {
		// TODO Auto-generated method stub
		double y;
		double a;
		double b;

		a = Math.abs(u0);
		b = Math.abs(u1);

		if (a<b) {
			a/= b;
			y = b * Math.sqrt(a * a + 1.0);
		}
		else if (a > b) {
			b/= a;
			y = a * Math.sqrt(b * b + 1.0);
		}
		else if (Double.isNaN(b)) {
			y = b;
		}
		else {
			y = a * 1.4142135623730951;
		}

		return (Math.pow(y,2));

	}


	public double[] fastfouriertransform_MA(double[] X ) throws Exception {

		if (X.length == 2000){

		    int i;
		    int ix;
		    double[] y_re = new double[2048];
		    double[] y_im = new double[2048];
		    int ju;
		    int iy;
		    boolean tst;
		    double temp_re;
		    double temp_im;
		    int k;
		    int ihi;
		    int ic;
		    int j;
		    int vstride;
		    double twid_re;
		    double twid_im;

            double dv0[] = { 1.0, 0.99999529380957619,
                    0.99998117528260111, 0.9999576445519639, 0.9999247018391445,
                    0.99988234745421256, 0.9998305817958234, 0.99976940535121528,
                    0.99969881869620425, 0.99961882249517864, 0.99952941750109314,
                    0.99943060455546173, 0.99932238458834954, 0.99920475861836389,
                    0.99907772775264536, 0.99894129318685687, 0.99879545620517241,
                    0.99864021818026527, 0.99847558057329477, 0.99830154493389289,
                    0.99811811290014918, 0.997925286198596, 0.99772306664419164,
                    0.99751145614030345, 0.99729045667869021, 0.997060070339483,
                    0.99682029929116567, 0.99657114579055484, 0.996312612182778,
                    0.996044700901252, 0.99576741446765982, 0.99548075549192694,
                    0.99518472667219693, 0.99487933079480562, 0.99456457073425542,
                    0.9942404494531879, 0.99390697000235606, 0.9935641355205953,
                    0.9932119492347945, 0.9928504144598651, 0.99247953459871, 0.9920993131421918,
                    0.99170975366909953, 0.99131085984611544, 0.99090263542778,
                    0.99048508425645709, 0.99005821026229712, 0.98962201746320089,
                    0.989176509964781, 0.98872169196032378, 0.98825756773074946,
                    0.98778414164457218, 0.98730141815785843, 0.98680940181418553,
                    0.98630809724459867, 0.98579750916756748, 0.98527764238894122,
                    0.98474850180190421, 0.984210092386929, 0.98366241921173025,
                    0.98310548743121629, 0.98253930228744124, 0.98196386910955524,
                    0.98137919331375456, 0.98078528040323043, 0.98018213596811743,
                    0.97956976568544052, 0.9789481753190622, 0.97831737071962765,
                    0.97767735782450993, 0.97702814265775439, 0.97636973133002114,
                    0.97570213003852857, 0.97502534506699412, 0.97433938278557586,
                    0.973644249650812, 0.97293995220556018, 0.97222649707893627,
                    0.97150389098625178, 0.97077214072895035, 0.970031253194544,
                    0.96928123535654853, 0.96852209427441727, 0.96775383709347551,
                    0.96697647104485207, 0.9661900034454125, 0.9653944416976894,
                    0.96458979328981276, 0.96377606579543984, 0.96295326687368388,
                    0.96212140426904158, 0.96128048581132064, 0.96043051941556579,
                    0.95957151308198452, 0.9587034748958716, 0.95782641302753291,
                    0.95694033573220882, 0.95604525134999641, 0.95514116830577078,
                    0.95422809510910567, 0.95330604035419386, 0.95237501271976588,
                    0.95143502096900834, 0.9504860739494817, 0.94952818059303667,
                    0.94856134991573027, 0.94758559101774109, 0.94660091308328353,
                    0.94560732538052128, 0.94460483726148026, 0.94359345816196039,
                    0.94257319760144687, 0.94154406518302081, 0.9405060705932683,
                    0.93945922360218992, 0.93840353406310806, 0.937339011912575,
                    0.93626566717027826, 0.93518350993894761, 0.93409255040425887,
                    0.932992798834739, 0.93188426558166815, 0.93076696107898371,
                    0.92964089584318121, 0.92850608047321559, 0.92736252565040111,
                    0.92621024213831138, 0.92504924078267758, 0.92387953251128674,
                    0.92270112833387863, 0.9215140393420419, 0.92031827670911059,
                    0.91911385169005777, 0.9179007756213905, 0.9166790599210427,
                    0.91544871608826783, 0.91420975570353069, 0.91296219042839821,
                    0.91170603200542988, 0.91044129225806725, 0.90916798309052238,
                    0.90788611648766626, 0.90659570451491533, 0.90529675931811882,
                    0.90398929312344334, 0.90267331823725883, 0.901348847046022,
                    0.90001589201616017, 0.89867446569395382, 0.89732458070541832,
                    0.89596624975618522, 0.8945994856313827, 0.89322430119551532,
                    0.89184070939234272, 0.89044872324475788, 0.88904835585466457,
                    0.88763962040285393, 0.88622253014888064, 0.88479709843093779,
                    0.88336333866573158, 0.881921264348355, 0.88047088905216075,
                    0.87901222642863353, 0.87754529020726135, 0.8760700941954066,
                    0.87458665227817611, 0.87309497841829009, 0.87159508665595109,
                    0.87008699110871146, 0.8685707059713409, 0.86704624551569265,
                    0.86551362409056909, 0.8639728561215867, 0.8624239561110405,
                    0.86086693863776731, 0.85930181835700847, 0.85772861000027212,
                    0.85614732837519447, 0.85455798836540053, 0.85296060493036363,
                    0.8513551931052652, 0.84974176800085255, 0.84812034480329723,
                    0.84649093877405213, 0.84485356524970712, 0.84320823964184544,
                    0.84155497743689844, 0.83989379419599952, 0.83822470555483808,
                    0.836547727223512, 0.83486287498638, 0.83317016470191319,
                    0.83146961230254524, 0.829761233794523, 0.8280450452577558,
                    0.82632106284566353, 0.82458930278502529, 0.82284978137582643,
                    0.82110251499110465, 0.819347520076797, 0.81758481315158371,
                    0.81581441080673378, 0.81403632970594841, 0.81225058658520388,
                    0.81045719825259477, 0.808656181588175, 0.80684755354379933,
                    0.80503133114296366, 0.80320753148064494, 0.80137617172314024,
                    0.799537269107905, 0.79769084094339116, 0.79583690460888357,
                    0.79397547755433717, 0.79210657730021239, 0.79023022143731,
                    0.78834642762660634, 0.78645521359908577, 0.78455659715557524,
                    0.78265059616657573, 0.78073722857209449, 0.778816512381476,
                    0.77688846567323244, 0.77495310659487393, 0.773010453362737,
                    0.77106052426181382, 0.7691033376455797, 0.7671389119358204,
                    0.765167265622459, 0.76318841726338127, 0.76120238548426178,
                    0.759209188978388, 0.75720884650648457, 0.75520137689653655,
                    0.75318679904361252, 0.75116513190968637, 0.74913639452345937,
                    0.74710060598018013, 0.745057785441466, 0.74300795213512172,
                    0.74095112535495922, 0.73888732446061511, 0.73681656887736979,
                    0.7347388780959635, 0.73265427167241282, 0.73056276922782759,
                    0.7284643904482252, 0.726359155084346, 0.724247082951467,
                    0.72212819392921535, 0.72000250796138165, 0.71787004505573171,
                    0.71573082528381859, 0.71358486878079352, 0.71143219574521643,
                    0.70927282643886569, 0.70710678118654757, 0.70493408037590488,
                    0.7027547444572253, 0.70056879394324834, 0.69837624940897292,
                    0.696177131491463, 0.69397146088965389, 0.69175925836415775,
                    0.68954054473706683, 0.687315340891759, 0.68508366777270036,
                    0.68284554638524808, 0.680600997795453, 0.67835004312986147,
                    0.67609270357531592, 0.673829000378756, 0.67155895484701833,
                    0.669282588346636, 0.66699992230363747, 0.66471097820334479,
                    0.66241577759017178, 0.66011434206742048, 0.65780669329707864,
                    0.65549285299961535, 0.65317284295377676, 0.650846684996381,
                    0.64851440102211244, 0.64617601298331628, 0.64383154288979139,
                    0.641481012808583, 0.63912444486377573, 0.6367618612362842,
                    0.63439328416364549, 0.63201873593980906, 0.629638238914927,
                    0.62725181549514408, 0.62485948814238634, 0.62246127937415,
                    0.6200572117632891, 0.61764730793780387, 0.61523159058062682,
                    0.61281008242940971, 0.61038280627630948, 0.60794978496777363,
                    0.60551104140432555, 0.60306659854034816, 0.600616479383869,
                    0.59816070699634238, 0.59569930449243336, 0.5932322950397998,
                    0.59075970185887416, 0.58828154822264522, 0.58579785745643886,
                    0.58330865293769829, 0.58081395809576453, 0.57831379641165559,
                    0.57580819141784534, 0.5732971666980422, 0.57078074588696726,
                    0.56825895267013149, 0.56573181078361312, 0.56319934401383409,
                    0.560661576197336, 0.5581185312205561, 0.55557023301960218,
                    0.55301670558002747, 0.55045797293660481, 0.54789405917310019,
                    0.54532498842204646, 0.54275078486451589, 0.54017147272989285,
                    0.53758707629564539, 0.53499761988709715, 0.5324031278771979,
                    0.52980362468629461, 0.52719913478190128, 0.524589682678469,
                    0.52197529293715439, 0.51935599016558964, 0.51673179901764987,
                    0.51410274419322166, 0.5114688504379703, 0.508830142543107,
                    0.50618664534515523, 0.50353838372571758, 0.50088538261124071,
                    0.49822766697278181, 0.49556526182577254, 0.49289819222978404,
                    0.49022648328829116, 0.487550160148436, 0.48486924800079106,
                    0.48218377207912272, 0.47949375766015295, 0.47679923006332209,
                    0.47410021465054997, 0.47139673682599764, 0.46868882203582796,
                    0.46597649576796618, 0.46325978355186015, 0.46053871095824,
                    0.45781330359887717, 0.45508358712634384, 0.45234958723377089,
                    0.44961132965460654, 0.44686884016237416, 0.4441221445704292,
                    0.44137126873171667, 0.43861623853852766, 0.43585707992225547,
                    0.43309381885315196, 0.43032648134008261, 0.42755509343028208,
                    0.42477968120910881, 0.42200027079979968, 0.41921688836322391,
                    0.41642956009763715, 0.4136383122384345, 0.41084317105790391,
                    0.40804416286497869, 0.40524131400498986, 0.40243465085941843,
                    0.39962419984564679, 0.39680998741671031, 0.3939920400610481,
                    0.39117038430225387, 0.38834504669882625, 0.38551605384391885,
                    0.38268343236508978, 0.37984720892405116, 0.37700741021641826,
                    0.37416406297145793, 0.37131719395183749, 0.36846682995337232,
                    0.36561299780477385, 0.36275572436739723, 0.35989503653498811,
                    0.35703096123343, 0.35416352542049034, 0.35129275608556709,
                    0.34841868024943456, 0.34554132496398909, 0.34266071731199438,
                    0.33977688440682685, 0.33688985339222005, 0.33399965144200938,
                    0.33110630575987643, 0.3282098435790925, 0.32531029216226293,
                    0.32240767880106985, 0.31950203081601569, 0.31659337555616585,
                    0.31368174039889152, 0.31076715274961147, 0.30784964004153487,
                    0.30492922973540237, 0.30200594931922808, 0.29907982630804048,
                    0.29615088824362379, 0.29321916269425863, 0.29028467725446233,
                    0.28734745954472951, 0.28440753721127188, 0.28146493792575794,
                    0.27851968938505306, 0.27557181931095814, 0.272621355449949,
                    0.26966832557291509, 0.26671275747489837, 0.26375467897483135,
                    0.26079411791527551, 0.257831102162159, 0.25486565960451457,
                    0.25189781815421697, 0.24892760574572015, 0.24595505033579459,
                    0.24298017990326387, 0.2400030224487415, 0.2370236059943672,
                    0.23404195858354343, 0.23105810828067111, 0.22807208317088573,
                    0.22508391135979283, 0.22209362097320351, 0.2191012401568698,
                    0.21610679707621952, 0.21311031991609136, 0.21011183688046961,
                    0.20711137619221856, 0.20410896609281687, 0.2011046348420919,
                    0.19809841071795356, 0.19509032201612825, 0.19208039704989244,
                    0.18906866414980619, 0.18605515166344663, 0.18303988795514095,
                    0.18002290140569951, 0.17700422041214875, 0.17398387338746382,
                    0.17096188876030122, 0.16793829497473117, 0.16491312048996992,
                    0.16188639378011183, 0.15885814333386145, 0.15582839765426523,
                    0.15279718525844344, 0.14976453467732151, 0.14673047445536175,
                    0.14369503315029447, 0.14065823933284921, 0.13762012158648604,
                    0.13458070850712617, 0.13154002870288312, 0.12849811079379317,
                    0.12545498341154623, 0.1224106751992162, 0.11936521481099135,
                    0.11631863091190475, 0.11327095217756435, 0.11022220729388306,
                    0.10717242495680884, 0.10412163387205459, 0.10106986275482782,
                    0.0980171403295606, 0.094963495329638992, 0.091908956497132724,
                    0.0888535525825246, 0.0857973123444399, 0.082740264549375692,
                    0.079682437971430126, 0.076623861392031492, 0.073564563599667426,
                    0.070504573389613856, 0.067443919563664051, 0.064382630929857465,
                    0.061320736302208578, 0.058258264500435752, 0.055195244349689941,
                    0.052131704680283324, 0.049067674327418015, 0.046003182130914623,
                    0.04293825693494082, 0.039872927587739811, 0.036807222941358832,
                    0.03374117185137758, 0.030674803176636626, 0.02760814577896574,
                    0.024541228522912288, 0.021474080275469508, 0.01840672990580482,
                    0.0153392062849881, 0.012271538285719925, 0.00920375478205982,
                    0.0061358846491544753, 0.0030679567629659761, 0.0, -0.0030679567629659761,
                    -0.0061358846491544753, -0.00920375478205982, -0.012271538285719925,
                    -0.0153392062849881, -0.01840672990580482, -0.021474080275469508,
                    -0.024541228522912288, -0.02760814577896574, -0.030674803176636626,
                    -0.03374117185137758, -0.036807222941358832, -0.039872927587739811,
                    -0.04293825693494082, -0.046003182130914623, -0.049067674327418015,
                    -0.052131704680283324, -0.055195244349689941, -0.058258264500435752,
                    -0.061320736302208578, -0.064382630929857465, -0.067443919563664051,
                    -0.070504573389613856, -0.073564563599667426, -0.076623861392031492,
                    -0.079682437971430126, -0.082740264549375692, -0.0857973123444399,
                    -0.0888535525825246, -0.091908956497132724, -0.094963495329638992,
                    -0.0980171403295606, -0.10106986275482782, -0.10412163387205459,
                    -0.10717242495680884, -0.11022220729388306, -0.11327095217756435,
                    -0.11631863091190475, -0.11936521481099135, -0.1224106751992162,
                    -0.12545498341154623, -0.12849811079379317, -0.13154002870288312,
                    -0.13458070850712617, -0.13762012158648604, -0.14065823933284921,
                    -0.14369503315029447, -0.14673047445536175, -0.14976453467732151,
                    -0.15279718525844344, -0.15582839765426523, -0.15885814333386145,
                    -0.16188639378011183, -0.16491312048996992, -0.16793829497473117,
                    -0.17096188876030122, -0.17398387338746382, -0.17700422041214875,
                    -0.18002290140569951, -0.18303988795514095, -0.18605515166344663,
                    -0.18906866414980619, -0.19208039704989244, -0.19509032201612825,
                    -0.19809841071795356, -0.2011046348420919, -0.20410896609281687,
                    -0.20711137619221856, -0.21011183688046961, -0.21311031991609136,
                    -0.21610679707621952, -0.2191012401568698, -0.22209362097320351,
                    -0.22508391135979283, -0.22807208317088573, -0.23105810828067111,
                    -0.23404195858354343, -0.2370236059943672, -0.2400030224487415,
                    -0.24298017990326387, -0.24595505033579459, -0.24892760574572015,
                    -0.25189781815421697, -0.25486565960451457, -0.257831102162159,
                    -0.26079411791527551, -0.26375467897483135, -0.26671275747489837,
                    -0.26966832557291509, -0.272621355449949, -0.27557181931095814,
                    -0.27851968938505306, -0.28146493792575794, -0.28440753721127188,
                    -0.28734745954472951, -0.29028467725446233, -0.29321916269425863,
                    -0.29615088824362379, -0.29907982630804048, -0.30200594931922808,
                    -0.30492922973540237, -0.30784964004153487, -0.31076715274961147,
                    -0.31368174039889152, -0.31659337555616585, -0.31950203081601569,
                    -0.32240767880106985, -0.32531029216226293, -0.3282098435790925,
                    -0.33110630575987643, -0.33399965144200938, -0.33688985339222005,
                    -0.33977688440682685, -0.34266071731199438, -0.34554132496398909,
                    -0.34841868024943456, -0.35129275608556709, -0.35416352542049034,
                    -0.35703096123343, -0.35989503653498811, -0.36275572436739723,
                    -0.36561299780477385, -0.36846682995337232, -0.37131719395183749,
                    -0.37416406297145793, -0.37700741021641826, -0.37984720892405116,
                    -0.38268343236508978, -0.38551605384391885, -0.38834504669882625,
                    -0.39117038430225387, -0.3939920400610481, -0.39680998741671031,
                    -0.39962419984564679, -0.40243465085941843, -0.40524131400498986,
                    -0.40804416286497869, -0.41084317105790391, -0.4136383122384345,
                    -0.41642956009763715, -0.41921688836322391, -0.42200027079979968,
                    -0.42477968120910881, -0.42755509343028208, -0.43032648134008261,
                    -0.43309381885315196, -0.43585707992225547, -0.43861623853852766,
                    -0.44137126873171667, -0.4441221445704292, -0.44686884016237416,
                    -0.44961132965460654, -0.45234958723377089, -0.45508358712634384,
                    -0.45781330359887717, -0.46053871095824, -0.46325978355186015,
                    -0.46597649576796618, -0.46868882203582796, -0.47139673682599764,
                    -0.47410021465054997, -0.47679923006332209, -0.47949375766015295,
                    -0.48218377207912272, -0.48486924800079106, -0.487550160148436,
                    -0.49022648328829116, -0.49289819222978404, -0.49556526182577254,
                    -0.49822766697278181, -0.50088538261124071, -0.50353838372571758,
                    -0.50618664534515523, -0.508830142543107, -0.5114688504379703,
                    -0.51410274419322166, -0.51673179901764987, -0.51935599016558964,
                    -0.52197529293715439, -0.524589682678469, -0.52719913478190128,
                    -0.52980362468629461, -0.5324031278771979, -0.53499761988709715,
                    -0.53758707629564539, -0.54017147272989285, -0.54275078486451589,
                    -0.54532498842204646, -0.54789405917310019, -0.55045797293660481,
                    -0.55301670558002747, -0.55557023301960218, -0.5581185312205561,
                    -0.560661576197336, -0.56319934401383409, -0.56573181078361312,
                    -0.56825895267013149, -0.57078074588696726, -0.5732971666980422,
                    -0.57580819141784534, -0.57831379641165559, -0.58081395809576453,
                    -0.58330865293769829, -0.58579785745643886, -0.58828154822264522,
                    -0.59075970185887416, -0.5932322950397998, -0.59569930449243336,
                    -0.59816070699634238, -0.600616479383869, -0.60306659854034816,
                    -0.60551104140432555, -0.60794978496777363, -0.61038280627630948,
                    -0.61281008242940971, -0.61523159058062682, -0.61764730793780387,
                    -0.6200572117632891, -0.62246127937415, -0.62485948814238634,
                    -0.62725181549514408, -0.629638238914927, -0.63201873593980906,
                    -0.63439328416364549, -0.6367618612362842, -0.63912444486377573,
                    -0.641481012808583, -0.64383154288979139, -0.64617601298331628,
                    -0.64851440102211244, -0.650846684996381, -0.65317284295377676,
                    -0.65549285299961535, -0.65780669329707864, -0.66011434206742048,
                    -0.66241577759017178, -0.66471097820334479, -0.66699992230363747,
                    -0.669282588346636, -0.67155895484701833, -0.673829000378756,
                    -0.67609270357531592, -0.67835004312986147, -0.680600997795453,
                    -0.68284554638524808, -0.68508366777270036, -0.687315340891759,
                    -0.68954054473706683, -0.69175925836415775, -0.69397146088965389,
                    -0.696177131491463, -0.69837624940897292, -0.70056879394324834,
                    -0.7027547444572253, -0.70493408037590488, -0.70710678118654757,
                    -0.70927282643886569, -0.71143219574521643, -0.71358486878079352,
                    -0.71573082528381859, -0.71787004505573171, -0.72000250796138165,
                    -0.72212819392921535, -0.724247082951467, -0.726359155084346,
                    -0.7284643904482252, -0.73056276922782759, -0.73265427167241282,
                    -0.7347388780959635, -0.73681656887736979, -0.73888732446061511,
                    -0.74095112535495922, -0.74300795213512172, -0.745057785441466,
                    -0.74710060598018013, -0.74913639452345937, -0.75116513190968637,
                    -0.75318679904361252, -0.75520137689653655, -0.75720884650648457,
                    -0.759209188978388, -0.76120238548426178, -0.76318841726338127,
                    -0.765167265622459, -0.7671389119358204, -0.7691033376455797,
                    -0.77106052426181382, -0.773010453362737, -0.77495310659487393,
                    -0.77688846567323244, -0.778816512381476, -0.78073722857209449,
                    -0.78265059616657573, -0.78455659715557524, -0.78645521359908577,
                    -0.78834642762660634, -0.79023022143731, -0.79210657730021239,
                    -0.79397547755433717, -0.79583690460888357, -0.79769084094339116,
                    -0.799537269107905, -0.80137617172314024, -0.80320753148064494,
                    -0.80503133114296366, -0.80684755354379933, -0.808656181588175,
                    -0.81045719825259477, -0.81225058658520388, -0.81403632970594841,
                    -0.81581441080673378, -0.81758481315158371, -0.819347520076797,
                    -0.82110251499110465, -0.82284978137582643, -0.82458930278502529,
                    -0.82632106284566353, -0.8280450452577558, -0.829761233794523,
                    -0.83146961230254524, -0.83317016470191319, -0.83486287498638,
                    -0.836547727223512, -0.83822470555483808, -0.83989379419599952,
                    -0.84155497743689844, -0.84320823964184544, -0.84485356524970712,
                    -0.84649093877405213, -0.84812034480329723, -0.84974176800085255,
                    -0.8513551931052652, -0.85296060493036363, -0.85455798836540053,
                    -0.85614732837519447, -0.85772861000027212, -0.85930181835700847,
                    -0.86086693863776731, -0.8624239561110405, -0.8639728561215867,
                    -0.86551362409056909, -0.86704624551569265, -0.8685707059713409,
                    -0.87008699110871146, -0.87159508665595109, -0.87309497841829009,
                    -0.87458665227817611, -0.8760700941954066, -0.87754529020726135,
                    -0.87901222642863353, -0.88047088905216075, -0.881921264348355,
                    -0.88336333866573158, -0.88479709843093779, -0.88622253014888064,
                    -0.88763962040285393, -0.88904835585466457, -0.89044872324475788,
                    -0.89184070939234272, -0.89322430119551532, -0.8945994856313827,
                    -0.89596624975618522, -0.89732458070541832, -0.89867446569395382,
                    -0.90001589201616017, -0.901348847046022, -0.90267331823725883,
                    -0.90398929312344334, -0.90529675931811882, -0.90659570451491533,
                    -0.90788611648766626, -0.90916798309052238, -0.91044129225806725,
                    -0.91170603200542988, -0.91296219042839821, -0.91420975570353069,
                    -0.91544871608826783, -0.9166790599210427, -0.9179007756213905,
                    -0.91911385169005777, -0.92031827670911059, -0.9215140393420419,
                    -0.92270112833387863, -0.92387953251128674, -0.92504924078267758,
                    -0.92621024213831138, -0.92736252565040111, -0.92850608047321559,
                    -0.92964089584318121, -0.93076696107898371, -0.93188426558166815,
                    -0.932992798834739, -0.93409255040425887, -0.93518350993894761,
                    -0.93626566717027826, -0.937339011912575, -0.93840353406310806,
                    -0.93945922360218992, -0.9405060705932683, -0.94154406518302081,
                    -0.94257319760144687, -0.94359345816196039, -0.94460483726148026,
                    -0.94560732538052128, -0.94660091308328353, -0.94758559101774109,
                    -0.94856134991573027, -0.94952818059303667, -0.9504860739494817,
                    -0.95143502096900834, -0.95237501271976588, -0.95330604035419386,
                    -0.95422809510910567, -0.95514116830577078, -0.95604525134999641,
                    -0.95694033573220882, -0.95782641302753291, -0.9587034748958716,
                    -0.95957151308198452, -0.96043051941556579, -0.96128048581132064,
                    -0.96212140426904158, -0.96295326687368388, -0.96377606579543984,
                    -0.96458979328981276, -0.9653944416976894, -0.9661900034454125,
                    -0.96697647104485207, -0.96775383709347551, -0.96852209427441727,
                    -0.96928123535654853, -0.970031253194544, -0.97077214072895035,
                    -0.97150389098625178, -0.97222649707893627, -0.97293995220556018,
                    -0.973644249650812, -0.97433938278557586, -0.97502534506699412,
                    -0.97570213003852857, -0.97636973133002114, -0.97702814265775439,
                    -0.97767735782450993, -0.97831737071962765, -0.9789481753190622,
                    -0.97956976568544052, -0.98018213596811743, -0.98078528040323043,
                    -0.98137919331375456, -0.98196386910955524, -0.98253930228744124,
                    -0.98310548743121629, -0.98366241921173025, -0.984210092386929,
                    -0.98474850180190421, -0.98527764238894122, -0.98579750916756748,
                    -0.98630809724459867, -0.98680940181418553, -0.98730141815785843,
                    -0.98778414164457218, -0.98825756773074946, -0.98872169196032378,
                    -0.989176509964781, -0.98962201746320089, -0.99005821026229712,
                    -0.99048508425645709, -0.99090263542778, -0.99131085984611544,
                    -0.99170975366909953, -0.9920993131421918, -0.99247953459871,
                    -0.9928504144598651, -0.9932119492347945, -0.9935641355205953,
                    -0.99390697000235606, -0.9942404494531879, -0.99456457073425542,
                    -0.99487933079480562, -0.99518472667219693, -0.99548075549192694,
                    -0.99576741446765982, -0.996044700901252, -0.996312612182778,
                    -0.99657114579055484, -0.99682029929116567, -0.997060070339483,
                    -0.99729045667869021, -0.99751145614030345, -0.99772306664419164,
                    -0.997925286198596, -0.99811811290014918, -0.99830154493389289,
                    -0.99847558057329477, -0.99864021818026527, -0.99879545620517241,
                    -0.99894129318685687, -0.99907772775264536, -0.99920475861836389,
                    -0.99932238458834954, -0.99943060455546173, -0.99952941750109314,
                    -0.99961882249517864, -0.99969881869620425, -0.99976940535121528,
                    -0.9998305817958234, -0.99988234745421256, -0.9999247018391445,
                    -0.9999576445519639, -0.99998117528260111, -0.99999529380957619, -1.0 };

            double dv1[] = { 0.0, -0.0030679567629659761,
                    -0.0061358846491544753, -0.00920375478205982, -0.012271538285719925,
                    -0.0153392062849881, -0.01840672990580482, -0.021474080275469508,
                    -0.024541228522912288, -0.02760814577896574, -0.030674803176636626,
                    -0.03374117185137758, -0.036807222941358832, -0.039872927587739811,
                    -0.04293825693494082, -0.046003182130914623, -0.049067674327418015,
                    -0.052131704680283324, -0.055195244349689941, -0.058258264500435752,
                    -0.061320736302208578, -0.064382630929857465, -0.067443919563664051,
                    -0.070504573389613856, -0.073564563599667426, -0.076623861392031492,
                    -0.079682437971430126, -0.082740264549375692, -0.0857973123444399,
                    -0.0888535525825246, -0.091908956497132724, -0.094963495329638992,
                    -0.0980171403295606, -0.10106986275482782, -0.10412163387205459,
                    -0.10717242495680884, -0.11022220729388306, -0.11327095217756435,
                    -0.11631863091190475, -0.11936521481099135, -0.1224106751992162,
                    -0.12545498341154623, -0.12849811079379317, -0.13154002870288312,
                    -0.13458070850712617, -0.13762012158648604, -0.14065823933284921,
                    -0.14369503315029447, -0.14673047445536175, -0.14976453467732151,
                    -0.15279718525844344, -0.15582839765426523, -0.15885814333386145,
                    -0.16188639378011183, -0.16491312048996992, -0.16793829497473117,
                    -0.17096188876030122, -0.17398387338746382, -0.17700422041214875,
                    -0.18002290140569951, -0.18303988795514095, -0.18605515166344663,
                    -0.18906866414980619, -0.19208039704989244, -0.19509032201612825,
                    -0.19809841071795356, -0.2011046348420919, -0.20410896609281687,
                    -0.20711137619221856, -0.21011183688046961, -0.21311031991609136,
                    -0.21610679707621952, -0.2191012401568698, -0.22209362097320351,
                    -0.22508391135979283, -0.22807208317088573, -0.23105810828067111,
                    -0.23404195858354343, -0.2370236059943672, -0.2400030224487415,
                    -0.24298017990326387, -0.24595505033579459, -0.24892760574572015,
                    -0.25189781815421697, -0.25486565960451457, -0.257831102162159,
                    -0.26079411791527551, -0.26375467897483135, -0.26671275747489837,
                    -0.26966832557291509, -0.272621355449949, -0.27557181931095814,
                    -0.27851968938505306, -0.28146493792575794, -0.28440753721127188,
                    -0.28734745954472951, -0.29028467725446233, -0.29321916269425863,
                    -0.29615088824362379, -0.29907982630804048, -0.30200594931922808,
                    -0.30492922973540237, -0.30784964004153487, -0.31076715274961147,
                    -0.31368174039889152, -0.31659337555616585, -0.31950203081601569,
                    -0.32240767880106985, -0.32531029216226293, -0.3282098435790925,
                    -0.33110630575987643, -0.33399965144200938, -0.33688985339222005,
                    -0.33977688440682685, -0.34266071731199438, -0.34554132496398909,
                    -0.34841868024943456, -0.35129275608556709, -0.35416352542049034,
                    -0.35703096123343, -0.35989503653498811, -0.36275572436739723,
                    -0.36561299780477385, -0.36846682995337232, -0.37131719395183749,
                    -0.37416406297145793, -0.37700741021641826, -0.37984720892405116,
                    -0.38268343236508978, -0.38551605384391885, -0.38834504669882625,
                    -0.39117038430225387, -0.3939920400610481, -0.39680998741671031,
                    -0.39962419984564679, -0.40243465085941843, -0.40524131400498986,
                    -0.40804416286497869, -0.41084317105790391, -0.4136383122384345,
                    -0.41642956009763715, -0.41921688836322391, -0.42200027079979968,
                    -0.42477968120910881, -0.42755509343028208, -0.43032648134008261,
                    -0.43309381885315196, -0.43585707992225547, -0.43861623853852766,
                    -0.44137126873171667, -0.4441221445704292, -0.44686884016237416,
                    -0.44961132965460654, -0.45234958723377089, -0.45508358712634384,
                    -0.45781330359887717, -0.46053871095824, -0.46325978355186015,
                    -0.46597649576796618, -0.46868882203582796, -0.47139673682599764,
                    -0.47410021465054997, -0.47679923006332209, -0.47949375766015295,
                    -0.48218377207912272, -0.48486924800079106, -0.487550160148436,
                    -0.49022648328829116, -0.49289819222978404, -0.49556526182577254,
                    -0.49822766697278181, -0.50088538261124071, -0.50353838372571758,
                    -0.50618664534515523, -0.508830142543107, -0.5114688504379703,
                    -0.51410274419322166, -0.51673179901764987, -0.51935599016558964,
                    -0.52197529293715439, -0.524589682678469, -0.52719913478190128,
                    -0.52980362468629461, -0.5324031278771979, -0.53499761988709715,
                    -0.53758707629564539, -0.54017147272989285, -0.54275078486451589,
                    -0.54532498842204646, -0.54789405917310019, -0.55045797293660481,
                    -0.55301670558002747, -0.55557023301960218, -0.5581185312205561,
                    -0.560661576197336, -0.56319934401383409, -0.56573181078361312,
                    -0.56825895267013149, -0.57078074588696726, -0.5732971666980422,
                    -0.57580819141784534, -0.57831379641165559, -0.58081395809576453,
                    -0.58330865293769829, -0.58579785745643886, -0.58828154822264522,
                    -0.59075970185887416, -0.5932322950397998, -0.59569930449243336,
                    -0.59816070699634238, -0.600616479383869, -0.60306659854034816,
                    -0.60551104140432555, -0.60794978496777363, -0.61038280627630948,
                    -0.61281008242940971, -0.61523159058062682, -0.61764730793780387,
                    -0.6200572117632891, -0.62246127937415, -0.62485948814238634,
                    -0.62725181549514408, -0.629638238914927, -0.63201873593980906,
                    -0.63439328416364549, -0.6367618612362842, -0.63912444486377573,
                    -0.641481012808583, -0.64383154288979139, -0.64617601298331628,
                    -0.64851440102211244, -0.650846684996381, -0.65317284295377676,
                    -0.65549285299961535, -0.65780669329707864, -0.66011434206742048,
                    -0.66241577759017178, -0.66471097820334479, -0.66699992230363747,
                    -0.669282588346636, -0.67155895484701833, -0.673829000378756,
                    -0.67609270357531592, -0.67835004312986147, -0.680600997795453,
                    -0.68284554638524808, -0.68508366777270036, -0.687315340891759,
                    -0.68954054473706683, -0.69175925836415775, -0.69397146088965389,
                    -0.696177131491463, -0.69837624940897292, -0.70056879394324834,
                    -0.7027547444572253, -0.70493408037590488, -0.70710678118654757,
                    -0.70927282643886569, -0.71143219574521643, -0.71358486878079352,
                    -0.71573082528381859, -0.71787004505573171, -0.72000250796138165,
                    -0.72212819392921535, -0.724247082951467, -0.726359155084346,
                    -0.7284643904482252, -0.73056276922782759, -0.73265427167241282,
                    -0.7347388780959635, -0.73681656887736979, -0.73888732446061511,
                    -0.74095112535495922, -0.74300795213512172, -0.745057785441466,
                    -0.74710060598018013, -0.74913639452345937, -0.75116513190968637,
                    -0.75318679904361252, -0.75520137689653655, -0.75720884650648457,
                    -0.759209188978388, -0.76120238548426178, -0.76318841726338127,
                    -0.765167265622459, -0.7671389119358204, -0.7691033376455797,
                    -0.77106052426181382, -0.773010453362737, -0.77495310659487393,
                    -0.77688846567323244, -0.778816512381476, -0.78073722857209449,
                    -0.78265059616657573, -0.78455659715557524, -0.78645521359908577,
                    -0.78834642762660634, -0.79023022143731, -0.79210657730021239,
                    -0.79397547755433717, -0.79583690460888357, -0.79769084094339116,
                    -0.799537269107905, -0.80137617172314024, -0.80320753148064494,
                    -0.80503133114296366, -0.80684755354379933, -0.808656181588175,
                    -0.81045719825259477, -0.81225058658520388, -0.81403632970594841,
                    -0.81581441080673378, -0.81758481315158371, -0.819347520076797,
                    -0.82110251499110465, -0.82284978137582643, -0.82458930278502529,
                    -0.82632106284566353, -0.8280450452577558, -0.829761233794523,
                    -0.83146961230254524, -0.83317016470191319, -0.83486287498638,
                    -0.836547727223512, -0.83822470555483808, -0.83989379419599952,
                    -0.84155497743689844, -0.84320823964184544, -0.84485356524970712,
                    -0.84649093877405213, -0.84812034480329723, -0.84974176800085255,
                    -0.8513551931052652, -0.85296060493036363, -0.85455798836540053,
                    -0.85614732837519447, -0.85772861000027212, -0.85930181835700847,
                    -0.86086693863776731, -0.8624239561110405, -0.8639728561215867,
                    -0.86551362409056909, -0.86704624551569265, -0.8685707059713409,
                    -0.87008699110871146, -0.87159508665595109, -0.87309497841829009,
                    -0.87458665227817611, -0.8760700941954066, -0.87754529020726135,
                    -0.87901222642863353, -0.88047088905216075, -0.881921264348355,
                    -0.88336333866573158, -0.88479709843093779, -0.88622253014888064,
                    -0.88763962040285393, -0.88904835585466457, -0.89044872324475788,
                    -0.89184070939234272, -0.89322430119551532, -0.8945994856313827,
                    -0.89596624975618522, -0.89732458070541832, -0.89867446569395382,
                    -0.90001589201616017, -0.901348847046022, -0.90267331823725883,
                    -0.90398929312344334, -0.90529675931811882, -0.90659570451491533,
                    -0.90788611648766626, -0.90916798309052238, -0.91044129225806725,
                    -0.91170603200542988, -0.91296219042839821, -0.91420975570353069,
                    -0.91544871608826783, -0.9166790599210427, -0.9179007756213905,
                    -0.91911385169005777, -0.92031827670911059, -0.9215140393420419,
                    -0.92270112833387863, -0.92387953251128674, -0.92504924078267758,
                    -0.92621024213831138, -0.92736252565040111, -0.92850608047321559,
                    -0.92964089584318121, -0.93076696107898371, -0.93188426558166815,
                    -0.932992798834739, -0.93409255040425887, -0.93518350993894761,
                    -0.93626566717027826, -0.937339011912575, -0.93840353406310806,
                    -0.93945922360218992, -0.9405060705932683, -0.94154406518302081,
                    -0.94257319760144687, -0.94359345816196039, -0.94460483726148026,
                    -0.94560732538052128, -0.94660091308328353, -0.94758559101774109,
                    -0.94856134991573027, -0.94952818059303667, -0.9504860739494817,
                    -0.95143502096900834, -0.95237501271976588, -0.95330604035419386,
                    -0.95422809510910567, -0.95514116830577078, -0.95604525134999641,
                    -0.95694033573220882, -0.95782641302753291, -0.9587034748958716,
                    -0.95957151308198452, -0.96043051941556579, -0.96128048581132064,
                    -0.96212140426904158, -0.96295326687368388, -0.96377606579543984,
                    -0.96458979328981276, -0.9653944416976894, -0.9661900034454125,
                    -0.96697647104485207, -0.96775383709347551, -0.96852209427441727,
                    -0.96928123535654853, -0.970031253194544, -0.97077214072895035,
                    -0.97150389098625178, -0.97222649707893627, -0.97293995220556018,
                    -0.973644249650812, -0.97433938278557586, -0.97502534506699412,
                    -0.97570213003852857, -0.97636973133002114, -0.97702814265775439,
                    -0.97767735782450993, -0.97831737071962765, -0.9789481753190622,
                    -0.97956976568544052, -0.98018213596811743, -0.98078528040323043,
                    -0.98137919331375456, -0.98196386910955524, -0.98253930228744124,
                    -0.98310548743121629, -0.98366241921173025, -0.984210092386929,
                    -0.98474850180190421, -0.98527764238894122, -0.98579750916756748,
                    -0.98630809724459867, -0.98680940181418553, -0.98730141815785843,
                    -0.98778414164457218, -0.98825756773074946, -0.98872169196032378,
                    -0.989176509964781, -0.98962201746320089, -0.99005821026229712,
                    -0.99048508425645709, -0.99090263542778, -0.99131085984611544,
                    -0.99170975366909953, -0.9920993131421918, -0.99247953459871,
                    -0.9928504144598651, -0.9932119492347945, -0.9935641355205953,
                    -0.99390697000235606, -0.9942404494531879, -0.99456457073425542,
                    -0.99487933079480562, -0.99518472667219693, -0.99548075549192694,
                    -0.99576741446765982, -0.996044700901252, -0.996312612182778,
                    -0.99657114579055484, -0.99682029929116567, -0.997060070339483,
                    -0.99729045667869021, -0.99751145614030345, -0.99772306664419164,
                    -0.997925286198596, -0.99811811290014918, -0.99830154493389289,
                    -0.99847558057329477, -0.99864021818026527, -0.99879545620517241,
                    -0.99894129318685687, -0.99907772775264536, -0.99920475861836389,
                    -0.99932238458834954, -0.99943060455546173, -0.99952941750109314,
                    -0.99961882249517864, -0.99969881869620425, -0.99976940535121528,
                    -0.9998305817958234, -0.99988234745421256, -0.9999247018391445,
                    -0.9999576445519639, -0.99998117528260111, -0.99999529380957619, -1.0,
                    -0.99999529380957619, -0.99998117528260111, -0.9999576445519639,
                    -0.9999247018391445, -0.99988234745421256, -0.9998305817958234,
                    -0.99976940535121528, -0.99969881869620425, -0.99961882249517864,
                    -0.99952941750109314, -0.99943060455546173, -0.99932238458834954,
                    -0.99920475861836389, -0.99907772775264536, -0.99894129318685687,
                    -0.99879545620517241, -0.99864021818026527, -0.99847558057329477,
                    -0.99830154493389289, -0.99811811290014918, -0.997925286198596,
                    -0.99772306664419164, -0.99751145614030345, -0.99729045667869021,
                    -0.997060070339483, -0.99682029929116567, -0.99657114579055484,
                    -0.996312612182778, -0.996044700901252, -0.99576741446765982,
                    -0.99548075549192694, -0.99518472667219693, -0.99487933079480562,
                    -0.99456457073425542, -0.9942404494531879, -0.99390697000235606,
                    -0.9935641355205953, -0.9932119492347945, -0.9928504144598651,
                    -0.99247953459871, -0.9920993131421918, -0.99170975366909953,
                    -0.99131085984611544, -0.99090263542778, -0.99048508425645709,
                    -0.99005821026229712, -0.98962201746320089, -0.989176509964781,
                    -0.98872169196032378, -0.98825756773074946, -0.98778414164457218,
                    -0.98730141815785843, -0.98680940181418553, -0.98630809724459867,
                    -0.98579750916756748, -0.98527764238894122, -0.98474850180190421,
                    -0.984210092386929, -0.98366241921173025, -0.98310548743121629,
                    -0.98253930228744124, -0.98196386910955524, -0.98137919331375456,
                    -0.98078528040323043, -0.98018213596811743, -0.97956976568544052,
                    -0.9789481753190622, -0.97831737071962765, -0.97767735782450993,
                    -0.97702814265775439, -0.97636973133002114, -0.97570213003852857,
                    -0.97502534506699412, -0.97433938278557586, -0.973644249650812,
                    -0.97293995220556018, -0.97222649707893627, -0.97150389098625178,
                    -0.97077214072895035, -0.970031253194544, -0.96928123535654853,
                    -0.96852209427441727, -0.96775383709347551, -0.96697647104485207,
                    -0.9661900034454125, -0.9653944416976894, -0.96458979328981276,
                    -0.96377606579543984, -0.96295326687368388, -0.96212140426904158,
                    -0.96128048581132064, -0.96043051941556579, -0.95957151308198452,
                    -0.9587034748958716, -0.95782641302753291, -0.95694033573220882,
                    -0.95604525134999641, -0.95514116830577078, -0.95422809510910567,
                    -0.95330604035419386, -0.95237501271976588, -0.95143502096900834,
                    -0.9504860739494817, -0.94952818059303667, -0.94856134991573027,
                    -0.94758559101774109, -0.94660091308328353, -0.94560732538052128,
                    -0.94460483726148026, -0.94359345816196039, -0.94257319760144687,
                    -0.94154406518302081, -0.9405060705932683, -0.93945922360218992,
                    -0.93840353406310806, -0.937339011912575, -0.93626566717027826,
                    -0.93518350993894761, -0.93409255040425887, -0.932992798834739,
                    -0.93188426558166815, -0.93076696107898371, -0.92964089584318121,
                    -0.92850608047321559, -0.92736252565040111, -0.92621024213831138,
                    -0.92504924078267758, -0.92387953251128674, -0.92270112833387863,
                    -0.9215140393420419, -0.92031827670911059, -0.91911385169005777,
                    -0.9179007756213905, -0.9166790599210427, -0.91544871608826783,
                    -0.91420975570353069, -0.91296219042839821, -0.91170603200542988,
                    -0.91044129225806725, -0.90916798309052238, -0.90788611648766626,
                    -0.90659570451491533, -0.90529675931811882, -0.90398929312344334,
                    -0.90267331823725883, -0.901348847046022, -0.90001589201616017,
                    -0.89867446569395382, -0.89732458070541832, -0.89596624975618522,
                    -0.8945994856313827, -0.89322430119551532, -0.89184070939234272,
                    -0.89044872324475788, -0.88904835585466457, -0.88763962040285393,
                    -0.88622253014888064, -0.88479709843093779, -0.88336333866573158,
                    -0.881921264348355, -0.88047088905216075, -0.87901222642863353,
                    -0.87754529020726135, -0.8760700941954066, -0.87458665227817611,
                    -0.87309497841829009, -0.87159508665595109, -0.87008699110871146,
                    -0.8685707059713409, -0.86704624551569265, -0.86551362409056909,
                    -0.8639728561215867, -0.8624239561110405, -0.86086693863776731,
                    -0.85930181835700847, -0.85772861000027212, -0.85614732837519447,
                    -0.85455798836540053, -0.85296060493036363, -0.8513551931052652,
                    -0.84974176800085255, -0.84812034480329723, -0.84649093877405213,
                    -0.84485356524970712, -0.84320823964184544, -0.84155497743689844,
                    -0.83989379419599952, -0.83822470555483808, -0.836547727223512,
                    -0.83486287498638, -0.83317016470191319, -0.83146961230254524,
                    -0.829761233794523, -0.8280450452577558, -0.82632106284566353,
                    -0.82458930278502529, -0.82284978137582643, -0.82110251499110465,
                    -0.819347520076797, -0.81758481315158371, -0.81581441080673378,
                    -0.81403632970594841, -0.81225058658520388, -0.81045719825259477,
                    -0.808656181588175, -0.80684755354379933, -0.80503133114296366,
                    -0.80320753148064494, -0.80137617172314024, -0.799537269107905,
                    -0.79769084094339116, -0.79583690460888357, -0.79397547755433717,
                    -0.79210657730021239, -0.79023022143731, -0.78834642762660634,
                    -0.78645521359908577, -0.78455659715557524, -0.78265059616657573,
                    -0.78073722857209449, -0.778816512381476, -0.77688846567323244,
                    -0.77495310659487393, -0.773010453362737, -0.77106052426181382,
                    -0.7691033376455797, -0.7671389119358204, -0.765167265622459,
                    -0.76318841726338127, -0.76120238548426178, -0.759209188978388,
                    -0.75720884650648457, -0.75520137689653655, -0.75318679904361252,
                    -0.75116513190968637, -0.74913639452345937, -0.74710060598018013,
                    -0.745057785441466, -0.74300795213512172, -0.74095112535495922,
                    -0.73888732446061511, -0.73681656887736979, -0.7347388780959635,
                    -0.73265427167241282, -0.73056276922782759, -0.7284643904482252,
                    -0.726359155084346, -0.724247082951467, -0.72212819392921535,
                    -0.72000250796138165, -0.71787004505573171, -0.71573082528381859,
                    -0.71358486878079352, -0.71143219574521643, -0.70927282643886569,
                    -0.70710678118654757, -0.70493408037590488, -0.7027547444572253,
                    -0.70056879394324834, -0.69837624940897292, -0.696177131491463,
                    -0.69397146088965389, -0.69175925836415775, -0.68954054473706683,
                    -0.687315340891759, -0.68508366777270036, -0.68284554638524808,
                    -0.680600997795453, -0.67835004312986147, -0.67609270357531592,
                    -0.673829000378756, -0.67155895484701833, -0.669282588346636,
                    -0.66699992230363747, -0.66471097820334479, -0.66241577759017178,
                    -0.66011434206742048, -0.65780669329707864, -0.65549285299961535,
                    -0.65317284295377676, -0.650846684996381, -0.64851440102211244,
                    -0.64617601298331628, -0.64383154288979139, -0.641481012808583,
                    -0.63912444486377573, -0.6367618612362842, -0.63439328416364549,
                    -0.63201873593980906, -0.629638238914927, -0.62725181549514408,
                    -0.62485948814238634, -0.62246127937415, -0.6200572117632891,
                    -0.61764730793780387, -0.61523159058062682, -0.61281008242940971,
                    -0.61038280627630948, -0.60794978496777363, -0.60551104140432555,
                    -0.60306659854034816, -0.600616479383869, -0.59816070699634238,
                    -0.59569930449243336, -0.5932322950397998, -0.59075970185887416,
                    -0.58828154822264522, -0.58579785745643886, -0.58330865293769829,
                    -0.58081395809576453, -0.57831379641165559, -0.57580819141784534,
                    -0.5732971666980422, -0.57078074588696726, -0.56825895267013149,
                    -0.56573181078361312, -0.56319934401383409, -0.560661576197336,
                    -0.5581185312205561, -0.55557023301960218, -0.55301670558002747,
                    -0.55045797293660481, -0.54789405917310019, -0.54532498842204646,
                    -0.54275078486451589, -0.54017147272989285, -0.53758707629564539,
                    -0.53499761988709715, -0.5324031278771979, -0.52980362468629461,
                    -0.52719913478190128, -0.524589682678469, -0.52197529293715439,
                    -0.51935599016558964, -0.51673179901764987, -0.51410274419322166,
                    -0.5114688504379703, -0.508830142543107, -0.50618664534515523,
                    -0.50353838372571758, -0.50088538261124071, -0.49822766697278181,
                    -0.49556526182577254, -0.49289819222978404, -0.49022648328829116,
                    -0.487550160148436, -0.48486924800079106, -0.48218377207912272,
                    -0.47949375766015295, -0.47679923006332209, -0.47410021465054997,
                    -0.47139673682599764, -0.46868882203582796, -0.46597649576796618,
                    -0.46325978355186015, -0.46053871095824, -0.45781330359887717,
                    -0.45508358712634384, -0.45234958723377089, -0.44961132965460654,
                    -0.44686884016237416, -0.4441221445704292, -0.44137126873171667,
                    -0.43861623853852766, -0.43585707992225547, -0.43309381885315196,
                    -0.43032648134008261, -0.42755509343028208, -0.42477968120910881,
                    -0.42200027079979968, -0.41921688836322391, -0.41642956009763715,
                    -0.4136383122384345, -0.41084317105790391, -0.40804416286497869,
                    -0.40524131400498986, -0.40243465085941843, -0.39962419984564679,
                    -0.39680998741671031, -0.3939920400610481, -0.39117038430225387,
                    -0.38834504669882625, -0.38551605384391885, -0.38268343236508978,
                    -0.37984720892405116, -0.37700741021641826, -0.37416406297145793,
                    -0.37131719395183749, -0.36846682995337232, -0.36561299780477385,
                    -0.36275572436739723, -0.35989503653498811, -0.35703096123343,
                    -0.35416352542049034, -0.35129275608556709, -0.34841868024943456,
                    -0.34554132496398909, -0.34266071731199438, -0.33977688440682685,
                    -0.33688985339222005, -0.33399965144200938, -0.33110630575987643,
                    -0.3282098435790925, -0.32531029216226293, -0.32240767880106985,
                    -0.31950203081601569, -0.31659337555616585, -0.31368174039889152,
                    -0.31076715274961147, -0.30784964004153487, -0.30492922973540237,
                    -0.30200594931922808, -0.29907982630804048, -0.29615088824362379,
                    -0.29321916269425863, -0.29028467725446233, -0.28734745954472951,
                    -0.28440753721127188, -0.28146493792575794, -0.27851968938505306,
                    -0.27557181931095814, -0.272621355449949, -0.26966832557291509,
                    -0.26671275747489837, -0.26375467897483135, -0.26079411791527551,
                    -0.257831102162159, -0.25486565960451457, -0.25189781815421697,
                    -0.24892760574572015, -0.24595505033579459, -0.24298017990326387,
                    -0.2400030224487415, -0.2370236059943672, -0.23404195858354343,
                    -0.23105810828067111, -0.22807208317088573, -0.22508391135979283,
                    -0.22209362097320351, -0.2191012401568698, -0.21610679707621952,
                    -0.21311031991609136, -0.21011183688046961, -0.20711137619221856,
                    -0.20410896609281687, -0.2011046348420919, -0.19809841071795356,
                    -0.19509032201612825, -0.19208039704989244, -0.18906866414980619,
                    -0.18605515166344663, -0.18303988795514095, -0.18002290140569951,
                    -0.17700422041214875, -0.17398387338746382, -0.17096188876030122,
                    -0.16793829497473117, -0.16491312048996992, -0.16188639378011183,
                    -0.15885814333386145, -0.15582839765426523, -0.15279718525844344,
                    -0.14976453467732151, -0.14673047445536175, -0.14369503315029447,
                    -0.14065823933284921, -0.13762012158648604, -0.13458070850712617,
                    -0.13154002870288312, -0.12849811079379317, -0.12545498341154623,
                    -0.1224106751992162, -0.11936521481099135, -0.11631863091190475,
                    -0.11327095217756435, -0.11022220729388306, -0.10717242495680884,
                    -0.10412163387205459, -0.10106986275482782, -0.0980171403295606,
                    -0.094963495329638992, -0.091908956497132724, -0.0888535525825246,
                    -0.0857973123444399, -0.082740264549375692, -0.079682437971430126,
                    -0.076623861392031492, -0.073564563599667426, -0.070504573389613856,
                    -0.067443919563664051, -0.064382630929857465, -0.061320736302208578,
                    -0.058258264500435752, -0.055195244349689941, -0.052131704680283324,
                    -0.049067674327418015, -0.046003182130914623, -0.04293825693494082,
                    -0.039872927587739811, -0.036807222941358832, -0.03374117185137758,
                    -0.030674803176636626, -0.02760814577896574, -0.024541228522912288,
                    -0.021474080275469508, -0.01840672990580482, -0.0153392062849881,
                    -0.012271538285719925, -0.00920375478205982, -0.0061358846491544753,
                    -0.0030679567629659761, -0.0 };


            int midoffset;
            for (i = 0; i<2048; i++){
                y_re[i] = 0.0;
                y_im[i] = 0.0;

            }

            ix = 0;
            ju = 0;
            iy = 0;
            for (i = 0; i<1999; i++){
                y_re[iy] = X[ix];
                y_im[iy] = 0.0;
                iy = 2048;
                tst = true;
                while (tst){
                    iy >>=1;
                    ju ^= iy;
                    tst = ((ju & iy) == 0);
                }
                iy = ju;
                ix++;
            }

            y_re[iy] = X[ix];
            y_im[iy] = 0.0;

            for (i = 0; i<=2047; i += 2){
                temp_re = y_re[i+1];
                temp_im = y_im[i+1];
                y_re[i+1] = y_re[i] - y_re[i+1];
                y_im[i+1] = y_im[i] - y_im[i+1];
                y_re[i] += temp_re;
                y_im[i] += temp_im;
            }

            iy = 2;
            ix = 4;
            k = 512;
            ju = 2045;

            while (k > 0){
                for (i = 0; i < ju; i += ix){
                    temp_re = y_re[i+iy];
                    temp_im = y_im[i+iy];
                    y_re[i+iy] = y_re[i] - temp_re;
                    y_im[i+iy] = y_im[i] - temp_im;
                    y_re[i] += temp_re;
                    y_im[i] += temp_im;
                }

                ic = 1;
                for (j = k; j < 1024; j += k){
                    twid_re = dv0[j];
                    twid_im = dv1[j];
                    i = ic;
                    ihi = ic + ju;
                    while (i < ihi) {
                        temp_re = twid_re * y_re[i + iy] - twid_im * y_im[i + iy];
                        temp_im = twid_re * y_im[i + iy] + twid_im * y_re[i + iy];
                        y_re[i + iy] = y_re[i] - temp_re;
                        y_im[i + iy] = y_im[i] - temp_im;
                        y_re[i] += temp_re;
                        y_im[i] += temp_im;
                        i += ix;
                    }
                    ic++;
                }

                k /= 2;
                iy = ix;
                ix += ix;
                ju -= iy;
            }

            for (ihi = 0; ihi < 2; ihi++){
                if (ihi + 1 <= 1){
                    iy = 2048;
                } else {
                    iy = 1;
                }

                if ( !(iy <= 1)) {
                    i = iy/2;
                    vstride = 1;
                    k = 1;
                    while ( k <= ihi) {
                        vstride <<= 11;
                        k = 2;
                    }

                    midoffset = i * vstride - 1;
                    if (i << 1 == iy) {
                        iy = 0;
                        for (j = 1; j <= vstride; j++) {
                            iy++;
                            ix = iy - 1;
                            ju = iy + midoffset;
                            for (k = 1; k <= i; k++) {
                                temp_re = y_re[ix];
                                temp_im = y_im[ix];
                                y_re[ix] = y_re[ju];
                                y_im[ix] = y_im[ju];
                                y_re[ju] = temp_re;
                                y_im[ju] = temp_im;
                            }
                        }
                    } else {
                        iy = 0;
                        for (j = 1; j <= vstride; j++) {
                            iy++;
                            ix = iy - 1;
                            ju = iy + midoffset;
                            temp_re = y_re[ju];
                            temp_im = y_im[ju];
                            for (k = 1; k <= i; k++) {
                                ic = ju + vstride;
                                y_re[ju] = y_re[ix];
                                y_im[ju] = y_im[ix];
                                y_re[ix] = y_re[ic];
                                y_im[ix] = y_im[ic];
                                ix += vstride;
                                ju = ic;
                            }
                            y_re[ju] = temp_re;
                            y_im[ju] = temp_im;
                        }
                    }
                }
            }

            double[] abs_fft = new double[2048];
            for (k = 0; k < 2048; k++) {
                abs_fft[k] = rt_hypotd_snf(y_re[k], y_im[k]);
            }


            return abs_fft;
		}
		else {
			throw new Exception("Invalid input for FFT : MA.");
		}

	}


	public void findAmplitudeMA(double[] iInput, LinkedList<Double> iAmplitude , int iChannel) throws Exception{
        double aTempVal = 0;
        double[] aTempInput = new double[2000];

        int aStart;
        int aCtr = 0;

        for (int i = 6; i <= 14; i++) {

            double aMaxVal = -1000000;
            double aMinVal = 1000000;

            aStart = SignalProcConstants.MA_SHIFT * (i-1);
            for (int j = 0; j < SignalProcConstants.MA_DELTA; j++) {
                if (iInput[aStart+j] > aMaxVal){
                    aMaxVal = iInput[aStart + j];
                }
                if (iInput[aStart+j] < aMinVal){
                    aMinVal = iInput[aStart + j];
                }
                aTempInput[j] = iInput[aStart+j];
            }
            aTempVal = aMaxVal - aMinVal;
            int aSize = iAmplitude.size();
            double aMedian;

            // Insert double value into the array
            if (aSize == 0){
                iAmplitude.add(aTempVal);
            }
            else {
                for (int j = 0; j < aSize; j++) {
                    if (aTempVal < iAmplitude.get(j) ){
                        iAmplitude.add(j,aTempVal);
                        break;
                    }
                }
                if (aSize == iAmplitude.size()){
                    iAmplitude.add(aTempVal);
                }
            }
            aSize = iAmplitude.size();

            if (aSize > 5) {
                if (aSize %2 == 0) {
                    aMedian = (iAmplitude.get(aSize/2) + iAmplitude.get(aSize/2 - 1))/2;
                }
                else {
                    aMedian = iAmplitude.get(aSize/2);
                }

                if (aTempVal > aMedian * 1.4) {
                    SignalProcUtils.ma_amplitudeFlag[aCtr][iChannel+1] = aTempVal;
                }
                if (iChannel == 0) {
                    SignalProcUtils.ma_amplitudeFlag[aCtr][0] = SignalProcConstants.MA_SHIFT * (i-1);
                    SignalProcUtils.ma_psdFlag[aCtr][0] = SignalProcConstants.MA_SHIFT * (i-1);
                }

                // PSD
                double[] aFFT_MA = fastfouriertransform_MA(aTempInput);
                
                double aSum1 = 0;
                for (int j = 1; j < 11; j++) {
                    aSum1 += (aFFT_MA[j]);
                }
                double aSum2 = 0;
                for (int j = 1; j < 1024; j++) {
                    aSum2 += (aFFT_MA[j]);
                }

                SignalProcUtils.ma_psdFlag[aCtr][iChannel+1] = aSum1 / aSum2;
                aCtr++;

            }






        }


    }

    public double checkMA(int[] iOverlap, int iChannel, double iEndLocation) throws Exception{

	    double[] aErrInd = new double[3];
	    int aPsdCtr = 0;
        double aTemp = 0;
	    if (SignalProcUtils.ma_amplitudeFlag[0][iChannel+1] > 0) {
	        aTemp = SignalProcUtils.ma_amplitudeFlag[0][0];
        }

        int aCtr = 0;

        for (int i = 1; i <= 8; i++) {
            if ( (SignalProcUtils.ma_amplitudeFlag[i-1][iChannel+1] != 0) && (SignalProcUtils.ma_amplitudeFlag[i][iChannel+1] != 0)) {
                aCtr++;
                if (aCtr >= SignalProcConstants.MA_COUNT_TH) {
                    aErrInd[0] = aTemp;
                    aErrInd[1] = SignalProcUtils.ma_amplitudeFlag[i][0] + SignalProcConstants.MA_DELTA;
                    aErrInd[2] = aCtr;
                    aPsdCtr = i;
                }
            }
            else {
                aTemp = SignalProcUtils.ma_amplitudeFlag[i][0] + SignalProcConstants.MA_SHIFT;
                aCtr = 0;
            }


        }

        int aFlag = 0;
        if (aPsdCtr > 0){
            iOverlap[aPsdCtr] = iOverlap[aPsdCtr] + 1;
            iOverlap[aPsdCtr+1] = iOverlap[aPsdCtr+1] + 1;

            double[] aPsd1 = new double[(int)aErrInd[2]];
            double[] aPsd2 = new double[(int)aErrInd[2]];
            double[] aPsd3 = new double[(int)aErrInd[2]];
            double[] aPsd4 = new double[(int)aErrInd[2]];

            int count = 0;
            for (int i = 1; i <= (int) aErrInd[2]; i++) {
                iOverlap[aPsdCtr-i] = iOverlap[aPsdCtr-i] + 1;
                aPsd1[count] = SignalProcUtils.ma_psdFlag[aPsdCtr-i+1][1];
                aPsd2[count] = SignalProcUtils.ma_psdFlag[aPsdCtr-i+1][2];
                aPsd3[count] = SignalProcUtils.ma_psdFlag[aPsdCtr-i+1][3];
                aPsd4[count] = SignalProcUtils.ma_psdFlag[aPsdCtr-i+1][4];
                count++;
            }

            if (findMedian(aPsd1) > SignalProcConstants.MA_PSD_TH){
                aFlag++;
            }
            if (findMedian(aPsd2) > SignalProcConstants.MA_PSD_TH){
                aFlag++;
            }
            if (findMedian(aPsd3) > SignalProcConstants.MA_PSD_TH){
                aFlag++;
            }
            if (findMedian(aPsd4) > SignalProcConstants.MA_PSD_TH){
                aFlag++;
            }

            if (aFlag >= 2) {
                if (aErrInd[1] > iEndLocation){
                    iEndLocation = (aErrInd[1]);
                }
            }

        }
        return iEndLocation;

    }

    public double checkOverlapMA(int[] iOverlap, double iEndLocation) {
	    int aIndOverlap = 0;

        for (int i = 0; i < 10; i++) {
            if (iOverlap[i] >= 2){
                if (iEndLocation < 5000 + i*SignalProcConstants.MA_SHIFT){
                    iEndLocation = 5000 + i*SignalProcConstants.MA_SHIFT;
                }
                aIndOverlap = i;
            }
        }

        if (aIndOverlap > 0 ){
            for (int i = aIndOverlap+1; i < 10; i++) {
                if (iOverlap[i] == 1 && iOverlap[i-1] == 2){
                    if (iEndLocation < 5000 + i*SignalProcConstants.MA_SHIFT){
                        iEndLocation = 5000 + i*SignalProcConstants.MA_SHIFT;
                    }
                }
                else if ( iOverlap[i] == 1 && iOverlap[i-1] == 1) {
                    if (iEndLocation < 5000 + i*SignalProcConstants.MA_SHIFT){
                        iEndLocation = 5000 + i*SignalProcConstants.MA_SHIFT;
                    }
                }
                else if (iOverlap[i] == 1 && iOverlap[i-1] == 0){
                    break;
                }
                else if (iOverlap[i] == 0 && iOverlap[i-1] == 0){
                    break;
                }
            }
        }
        return iEndLocation;
    }

    public int findVarianceMinLocation(int[] iQrs, int iNoOfRR){

		int aLengthVar = iQrs.length - iNoOfRR;
		int[] aTemp = new int[iNoOfRR];
		double[] aVarArr = new double[aLengthVar];
		int aMinLoc = -1;
		double aMinValue = 100000;

		double aMean;
		double aVar;

		for (int i = 0; i < aLengthVar; i++) {
			aMean = 0;
			aVar = 0;
			for (int j = 0; j < iNoOfRR; j++) {
				aTemp[j] = iQrs[i+j+1] - iQrs[i+j];
				aMean = aMean + aTemp[j];
			}
			aMean = aMean / iNoOfRR;
			for (int j = 0; j < iNoOfRR; j++) {
				aVar = aVar + Math.pow((aTemp[j] - aMean),2);
			}
			aVar = aVar / (iNoOfRR - 1);
			aVarArr[i] = aVar;
			if (aVar < aMinValue) {
			    aMinValue = aVar;
			    aMinLoc = i;
            }
		}
		return aMinLoc;

	}

	// Added by Aravind Prasad 9th March 2018
	// Continuous Data Quality Check function
	// Checks for baseline noise in between the selected FQRS locations
	public double[] CDQC_fetal(double[][] residue,int[] QrsfSelected,int iter){
		if(iter == 0){
			double[][] aresidue1 = new double[5000][4];
			double[][] aresidue2 = new double[5000][4];
			double[][] aresidue3 = new double[5000][4];
			ArrayList<Integer> QRSFinal1 = new ArrayList<>();
			ArrayList<Integer> QRSFinal2 = new ArrayList<>();
			ArrayList<Integer> QRSFinal3 = new ArrayList<>();

			double[] stdnoise1;
			double[] stdnoise2;
			double[] stdnoise3;
			double[] stdnoisemean = new double[4];
			double[] stdNoisearr;
			for (int j = 0; j < 5000; j++) {
				for (int i = 0; i < 4; i++) {
					aresidue1[j][i] = residue[j][i];
				}
			}
			for (int j = 5000; j < 10000; j++) {
				for (int i = 0; i < 4; i++) {
					aresidue2[j - 5000][i] = residue[j][i];
				}
			}
			for (int j = 10000; j < 15000; j++) {
				for (int i = 0; i < 4; i++) {
					aresidue3[j - 10000][i] = residue[j][i];
				}
			}
			int aLength = QrsfSelected.length;
			for (int i = 0; i < aLength; i++) {
				if((QrsfSelected[i] >=0) && (QrsfSelected[i]<5000)){
					QRSFinal1.add(QrsfSelected[i]);
				}
				if((QrsfSelected[i] >=5000) && (QrsfSelected[i]<10000)){
					QRSFinal2.add(QrsfSelected[i]-5000);

				}
				if((QrsfSelected[i] >=10000) && (QrsfSelected[i]<15000)){
					QRSFinal3.add(QrsfSelected[i]-10000);

				}
			}
			stdnoise1 = stdNoise_fiveSec(aresidue1,QRSFinal1);
			stdnoise2 = stdNoise_fiveSec(aresidue2,QRSFinal2);
			stdnoise3 = stdNoise_fiveSec(aresidue3,QRSFinal3);
			for (int j = 0; j < 4; j++) {
				stdnoisemean[j] = (stdnoise1[j]+stdnoise2[j]+stdnoise3[j])/3;
			}

			return stdnoisemean;

		}
		else{
			double[][] aresidue2 = new double[5000][4];
			double[][] aresidue3 = new double[5000][4];
			ArrayList<Integer> QRSFinal2 = new ArrayList<>();
			ArrayList<Integer> QRSFinal3 = new ArrayList<>();

			double[] stdnoise2;
			double[] stdnoise3;
			double[] stdnoisemean = new double[4];
			double[] stdNoisearr;
			for (int j = 5000; j < 10000; j++) {
				for (int i = 0; i < 4; i++) {
					aresidue2[j - 5000][i] = residue[j][i];
				}
			}
			for (int j = 10000; j < 15000; j++) {
				for (int i = 0; i < 4; i++) {
					aresidue3[j - 10000][i] = residue[j][i];
				}
			}
			int aLength = QrsfSelected.length;
			for (int i = 0; i < aLength; i++) {

				if((QrsfSelected[i] >=5000) && (QrsfSelected[i]<10000)){
					QRSFinal2.add(QrsfSelected[i]-5000);

				}
				if((QrsfSelected[i] >=10000) && (QrsfSelected[i]<15000)){
					QRSFinal3.add(QrsfSelected[i]-10000);

				}
			}
			stdnoise2 = stdNoise_fiveSec(aresidue2,QRSFinal2);
			stdnoise3 = stdNoise_fiveSec(aresidue3,QRSFinal3);
			for (int j = 0; j < 4; j++) {
				stdnoisemean[j] = (stdnoise2[j]+stdnoise3[j])/2;
			}
			return stdnoisemean;
		}


	}
	public double[] stdNoise_fiveSec(double[][] ecg_Residue,ArrayList<Integer> QRSf){

		ArrayList<Double> noiseChannel1 = new ArrayList<>();
		ArrayList<Double> noiseChannel2 = new ArrayList<>();
		ArrayList<Double> noiseChannel3 = new ArrayList<>();
		ArrayList<Double> noiseChannel4 = new ArrayList<>();
		Integer[] qrsf = QRSf.toArray(new Integer[QRSf.size()]);
		int lengthFilter = ecg_Residue.length;
		int minValue = qrsf.length;

		Double[] channel1 = new Double[lengthFilter];
		Double[] channel2 = new Double[lengthFilter];
		Double[] channel3 = new Double[lengthFilter];
		Double[] channel4 = new Double[lengthFilter];

		for (int i = 0; i < lengthFilter; i++) {
			channel1[i] = ecg_Residue[i][0];
			channel2[i] = ecg_Residue[i][1];
			channel3[i] = ecg_Residue[i][2];
			channel4[i] = ecg_Residue[i][3];
		}

		if(qrsf.length == 0){
			noiseChannel1 = new ArrayList<Double>(Arrays.asList(channel1));
			noiseChannel2 = new ArrayList<Double>(Arrays.asList(channel2));
			noiseChannel3 = new ArrayList<Double>(Arrays.asList(channel3));
			noiseChannel4 = new ArrayList<Double>(Arrays.asList(channel4));
		}
		else {
			for (int i = 0; i < minValue - 1; i++) {
				for (int j = qrsf[i]+10; j < qrsf[i+1]-10; j++) {
					noiseChannel1.add(channel1[j]);
					noiseChannel2.add(channel2[j]);
					noiseChannel3.add(channel3[j]);
					noiseChannel4.add(channel4[j]);
				}
			}
		}
		double sumChannel1 = 0;
		double sumChannel2 = 0;
		double sumChannel3 = 0;
		double sumChannel4 = 0;

		double varChannel1 = 0;
		double varChannel2 = 0;
		double varChannel3 = 0;
		double varChannel4 = 0;

		//    CHANNEL 1
		for(int i = 0; i < noiseChannel1.size(); i++){
			sumChannel1 += noiseChannel1.get(i); // this is the calculation for summing up all the values
		}
		double meanChannel1 = sumChannel1 / noiseChannel1.size(); // finding mean of the channels

		for (int i = 0; i < noiseChannel1.size(); i++) {
			varChannel1 += Math.pow((noiseChannel1.get(i) - meanChannel1),2) / noiseChannel1.size(); // varience of the channels
		}
		double standardDeviationChannel1 = Math.sqrt(varChannel1);  // standard deviation calculation

		//    CHANNEL 2
		for(int i = 0; i < noiseChannel2.size(); i++){
			sumChannel2 += noiseChannel2.get(i); // this is the calculation for summing up all the values
		}
		double meanChannel2 = sumChannel2 / noiseChannel2.size();

		for (int i = 0; i < noiseChannel2.size(); i++) {
			varChannel2 += Math.pow((noiseChannel2.get(i) - meanChannel2),2) / noiseChannel2.size();
		}
		double standardDeviationChannel2 = Math.sqrt(varChannel2);

		//    CHANNEL 3
		for(int i = 0; i < noiseChannel3.size(); i++){
			sumChannel3 += noiseChannel3.get(i); // this is the calculation for summing up all the values
		}
		double meanChannel3 = sumChannel3 / noiseChannel3.size();

		for (int i = 0; i < noiseChannel3.size(); i++) {
			varChannel3 += Math.pow((noiseChannel3.get(i) - meanChannel3),2) / noiseChannel3.size();
		}
		double standardDeviationChannel3 = Math.sqrt(varChannel3);

		//    CHANNEL 4
		for(int i = 0; i < noiseChannel4.size(); i++){
			sumChannel4 += noiseChannel4.get(i); // this is the calculation for summing up all the values
		}
		double meanChannel4 = sumChannel4 / noiseChannel4.size();

		for (int i = 0; i < noiseChannel4.size(); i++) {
			varChannel4 += Math.pow((noiseChannel4.get(i) - meanChannel4),2) / noiseChannel4.size();
		}
		double standardDeviationChannel4 = Math.sqrt(varChannel4);

		//double stdmean = (standardDeviationChannel1+standardDeviationChannel2+standardDeviationChannel3+standardDeviationChannel4)/4;
		double[] stdnosie = {standardDeviationChannel1,standardDeviationChannel2,standardDeviationChannel3,standardDeviationChannel4};

		return stdnosie;
	}


	
}// close class
