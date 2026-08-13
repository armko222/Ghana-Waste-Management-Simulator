package com.dcit308.wasteops.algorithms.search;

import com.dcit308.wasteops.algorithms.SearchAlgorithm;

/**
 * Owned by Issue #5. Requires sorted input -- this precondition must be
 * documented, and a test must show what happens if it's violated (one
 * of the two required counterexamples, brief Section 10). Implements
 * SearchAlgorithm so Issue #14's performance harness can time this
 * alongside LinearSearch identically.
 */
public class BinarySearch<T extends Comparable<T>> implements SearchAlgorithm<T> {

    /** Precondition: array must already be sorted ascending. */
    @Override
    public int search(T[] array, T target) {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement binary search. Return -1 if not found.");
    }
}
