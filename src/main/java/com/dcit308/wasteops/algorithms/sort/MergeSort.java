package com.dcit308.wasteops.algorithms.sort;

import com.dcit308.wasteops.algorithms.SortAlgorithm;

/**
 * Owned by Issue #8. Implements SortAlgorithm so Issue #14's
 * performance harness can time this alongside the other three required
 * sorts identically.
 */
public class MergeSort<T extends Comparable<T>> implements SortAlgorithm<T> {

    @Override
    public void sort(T[] array) {
        throw new UnsupportedOperationException("TODO: Issue #8 \u2014 implement MergeSort, in place, ascending order.");
    }
}
