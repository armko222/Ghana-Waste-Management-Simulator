package algorithms.search;

/**
 * Binary search implementation with sorted-input validation.
 */
public class BinarySearch {

    public static <T extends Comparable<T>> boolean isSorted(T[] array) {
        if (array == null || array.length <= 1) {
            return true;
        }
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static <T extends Comparable<T>> int search(T[] array, T target) {
        if (!isSorted(array)) {
            throw new IllegalStateException("Precondition failed: Array must be sorted prior to executing binary search.");
        }

        int low = 0;
        int high = array.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int comparison = target.compareTo(array[mid]);

            if (comparison == 0) {
                return mid; 
            } else if (comparison < 0) {
                high = mid - 1; 
            } else {
                low = mid + 1; 
            }
        }
        return -1; 
    }
}