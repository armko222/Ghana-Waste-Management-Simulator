package com.dcit308.wasteops.algorithms.sort;

import com.dcit308.wasteops.algorithms.SortAlgorithm;

import java.util.Arrays;

/**
 * Classic top-down merge sort: repeatedly split the array in half,
 * recursively sort each half, then merge the two sorted halves back
 * together in order. Ascending, stable (equal elements keep their
 * original relative order, via the {@code <=} check in {@link #merge}
 * favouring the left half on ties).
 *
 * <p>"In place" per {@link SortAlgorithm}'s contract means the caller's
 * array reference ends up sorted -- not that this runs in O(1) auxiliary
 * space. Merge sort's well-known trade-off against the other three
 * sorts in this project (InsertionSort/SelectionSort are truly O(1)
 * extra space but O(n^2) time; QuickSort is in-place partitioning but
 * worst-case O(n^2)) is exactly that it needs O(n) auxiliary space to
 * get guaranteed O(n log n) time. A single reusable buffer, allocated
 * once up front rather than once per merge call, keeps that overhead to
 * one array the size of the input.
 *
 * Owned by Issue #8. Implements SortAlgorithm so Issue #14's
 * performance harness can time this alongside the other three required
 * sorts identically.
 */
public class MergeSort<T extends Comparable<T>> implements SortAlgorithm<T> {

    @Override
    public void sort(T[] array) {
        if (array == null || array.length < 2) {
            return;
        }
        // Arrays.copyOf gives a same-length, same-runtime-type array
        // without an unchecked generic array creation -- its initial
        // contents are irrelevant since merge() overwrites the buffer
        // range it uses before reading it back.
        T[] buffer = Arrays.copyOf(array, array.length);
        mergeSort(array, buffer, 0, array.length - 1);
    }

    private void mergeSort(T[] array, T[] buffer, int lo, int hi) {
        if (lo >= hi) {
            return; // 0 or 1 elements: already sorted
        }
        int mid = lo + (hi - lo) / 2;
        mergeSort(array, buffer, lo, mid);
        mergeSort(array, buffer, mid + 1, hi);
        merge(array, buffer, lo, mid, hi);
    }

    /** Merges the two sorted runs array[lo..mid] and array[mid+1..hi]. */
    private void merge(T[] array, T[] buffer, int lo, int mid, int hi) {
        for (int k = lo; k <= hi; k++) {
            buffer[k] = array[k];
        }

        int left = lo;
        int right = mid + 1;
        for (int k = lo; k <= hi; k++) {
            if (left > mid) {
                array[k] = buffer[right++];
            } else if (right > hi) {
                array[k] = buffer[left++];
            } else if (buffer[left].compareTo(buffer[right]) <= 0) {
                array[k] = buffer[left++]; // tie goes to the left run -> stable
            } else {
                array[k] = buffer[right++];
            }
        }
    }
}
