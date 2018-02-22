package SignalProc;

import java.util.Arrays;

/**
 * Find index of array entries in sorted array.
 */
    public final class ArrayUtils {
        public static int[] argsort(final double[] a) {
            return argsort(a, true);
        }

        public static int[] argsort(final double[] a, final boolean ascending) {
            Integer[] indexes = new Integer[a.length];
            for (int i = 0; i < indexes.length; i++) {
                indexes[i] = i;
            }
            Arrays.sort(indexes, (i1, i2) -> (ascending ? 1 : -1) * Double.compare(a[i1], a[i2]));
            return asArray(indexes);
        }

        public static <T extends Number> int[] asArray(final T... a) {
            int[] b = new int[a.length];
            for (int i = 0; i < b.length; i++) {
                b[i] = a[i].intValue();
            }
            return b;
        }


    private ArrayUtils() {
    }
}