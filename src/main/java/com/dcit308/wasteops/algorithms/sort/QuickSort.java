package com.dcit308.wasteops.algorithms.sort;

import com.dcit308.wasteops.algorithms.SortAlgorithm;

/**
 * In-place QuickSort implementation.
 *
 * Average time complexity: O(n log n)
 * Worst-case time complexity: O(n^2)
 *
 * Owned by Issue #9.
 */
public class QuickSort<T extends Comparable<T>> implements SortAlgorithm<T> {

    @Override
    public void sort(T[] array) {
        if (array == null || array.length < 2) {
            return;
        }

        quickSort(array, 0, array.length - 1);
    }

    private void quickSort(T[] array, int low, int high) {
        if (low >= high) {
            return;
        }

        int pivotIndex = partition(array, low, high);

        quickSort(array, low, pivotIndex - 1);
        quickSort(array, pivotIndex + 1, high);
    }

    private int partition(T[] array, int low, int high) {
        T pivot = array[high];
        int smallerIndex = low - 1;

        for (int current = low; current < high; current++) {
            if (array[current].compareTo(pivot) <= 0) {
                smallerIndex++;
                swap(array, smallerIndex, current);
            }
        }

        swap(array, smallerIndex + 1, high);

        return smallerIndex + 1;
    }

    private void swap(T[] array, int first, int second) {
        T temporary = array[first];
        array[first] = array[second];
        array[second] = temporary;
    }
}