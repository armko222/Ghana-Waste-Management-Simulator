package com.dcit308.wasteops.algorithms.sort;

import com.dcit308.wasteops.algorithms.SortAlgorithm;

/**
 * Owned by Issue #7. Implements SortAlgorithm so Issue #14's
 * performance harness can time this alongside the other three required
 * sorts identically.
 */
public class SelectionSort<T extends Comparable<T>> implements SortAlgorithm<T> {

    @Override
    public void sort(T[] array) {
        if (array == null || array.length <=1) {
            return;
        }
        int n = array.length;
        for (int i = 0; i < n-1; i++) {
            int minIndex = i;
            for (int j=i+1; j<n; j++) {
                if (array[j].compareTo(array[minIndex]) <0 ){minIndex = j;}
            }
            if (minIndex != i) {
                T temp = array[i];
                array[i] = array[minIndex];
                array[minIndex] = temp;
        }
    }
}
}