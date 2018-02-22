package SignalProc;

/**
 * LU Decomposition.
 * <p> For an m-by-n matrix A with {@literal m >= n}, the LU decomposition is an m-by-n
 unit lower triangular matrix L, an n-by-n upper triangular matrix U,
 and a permutation vector piv of length m so that A(piv,:) = L*U.
 If {@literal m < n}, then L is m-by-m and U is m-by-n.</p>
 <p>The LU decompostion with pivoting always exists, even if the matrix is
 singular, so the constructor will never fail.  The primary use of the
 LU decomposition is in the solution of square systems of simultaneous
 linear equations.  This will fail if isNonsingular() returns false. </p>
 * <p> Change Logs :</p>
 * <ul>
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
public class LUDecomposition {

/* ------------------------
   Class variables
 * ------------------------ */

   /** Array for internal storage of decomposition.
   @serial internal array storage.
   */
   private double[][] LU;

   /**
    * Row dimension of LU.
    */
   private int m;
   /**
    * Column dimension of LU.
    */
   private int n;
   /**
    * Pivot sign.
    */
   private int pivsign;

   /**
    * Internal storage of pivot vector.
    */
   private int[] piv;

/* ------------------------
   Constructor
 * ------------------------ */

   /** LU Decomposition
       Structure to access L, U and piv.
   @param  A Rectangular matrix
    */

   public LUDecomposition (double[][] A) {

   // Use a "left-looking", dot-product, Crout/Doolittle algorithm.

     
      m = A.length;
      n = A[0].length;
      
      LU = new double[m][n];
      
      for (int i =0; i<m; i++){
    	  for (int j =0; j<n; j++){
    		  LU[i][j] = A[i][j];
    	  }
      }
      
      piv = new int[m];
      for (int i = 0; i < m; i++) {
         piv[i] = i;
      }
      pivsign = 1;
      double[] LUrowi;
      double[] LUcolj = new double[m];

      // Outer loop.

      for (int j = 0; j < n; j++) {

         // Make a copy of the j-th column to localize references.

         for (int i = 0; i < m; i++) {
            LUcolj[i] = LU[i][j];
         }

         // Apply previous transformations.

         for (int i = 0; i < m; i++) {
            LUrowi = LU[i];

            // Most of the time is spent in the following dot product.

            int kmax = Math.min(i,j);
            double s = 0.0;
            for (int k = 0; k < kmax; k++) {
               s += LUrowi[k]*LUcolj[k];
            }

            LUrowi[j] = LUcolj[i] -= s;
         }
   
         // Find pivot and exchange if necessary.

         int p = j;
         for (int i = j+1; i < m; i++) {
            if (Math.abs(LUcolj[i]) > Math.abs(LUcolj[p])) {
               p = i;
            }
         }
         if (p != j) {
            for (int k = 0; k < n; k++) {
               double t = LU[p][k]; LU[p][k] = LU[j][k]; LU[j][k] = t;
            }
            int k = piv[p]; piv[p] = piv[j]; piv[j] = k;
            pivsign = -pivsign;
         }

         // Compute multipliers.
         
         if (j < m & LU[j][j] != 0.0) {
            for (int i = j+1; i < m; i++) {
               LU[i][j] /= LU[j][j];
            }
         }
      }
   }



/* ------------------------
   Public Methods
 * ------------------------ */

   /** Is the matrix nonsingular?
   @return true if LU is upper triangular, and hence A, is nonsingular.
   */

   public boolean isNonsingular () {
      for (int j = 0; j < n; j++) {
         if (LU[j][j] == 0)
            return false;
      }
      return true;
   }

   /**
    * Solve A*X = B
    * @param B A Matrix with as many rows as A and any number of columns.
    * @return X so that L*U*X = B(piv,:)
    * @throws Exception
    * <ul>
    *     <li> If LU has any diagonal entry 0.</li>
    *     <li> If (B.length != m)</li>
    *     <li> If B is not a square matrix.</li>
    * </ul>
    */
   public double[][] solve (double[][] B) throws Exception{
	   
	      if (B.length != m) {
	          throw new Exception("Matrix row dimensions must agree.");
	       }
	       if (!this.isNonsingular()) {
	          throw new Exception("Matrix is singular.");
	       }

	       // Copy right hand side with pivoting
	       int nx = B[0].length;
	       
	       double[][] X = new double[piv.length][nx];
	       try {
	          for (int i = 0; i < nx; i++) {
	             for (int j = 0; j < piv.length; j++) {
	                X[i][j] = B[i][piv[j]];
	             }
	          }
	       } catch(ArrayIndexOutOfBoundsException e) {
	          throw new ArrayIndexOutOfBoundsException("Submatrix indices");
	       }


      for (int k = 0; k < n; k++) {
         for (int i = k+1; i < n; i++) {
            for (int j = 0; j < nx; j++) {
               X[i][j] -= X[k][j]*LU[i][k];
            }
         }
      }
      // Solve U*X = Y;
      for (int k = n-1; k >= 0; k--) {
         for (int j = 0; j < nx; j++) {
            X[k][j] /= LU[k][k];
         }
         for (int i = 0; i < k; i++) {
            for (int j = 0; j < nx; j++) {
               X[i][j] -= X[k][j]*LU[i][k];
            }
         }
      }
      return X;
   }

}
