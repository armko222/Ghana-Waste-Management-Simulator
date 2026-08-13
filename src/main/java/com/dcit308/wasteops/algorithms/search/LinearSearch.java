package com.dcit308.wasteops.algorithms.search;

import com.dcit308.wasteops.algorithms.SearchAlgorithm;

/**
 * Owned by Issue #4. Implements SearchAlgorithm so Issue #14's
 * performance harness can time this alongside BinarySearch identically.
 */
public class LinearSearch<T> implements SearchAlgorithm<T> {

    @Override
    public int search(T[] array, T target) {
        throw new UnsupportedOperationException("TODO: Issue #4 \u2014 implement linear search. Return -1 if not found.");
    }
}
