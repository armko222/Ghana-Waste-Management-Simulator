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
        throw new UnsupportedOperationException("TODO: Issue #7 \u2014 implement SelectionSort, in place, ascending order.");
    }
}
