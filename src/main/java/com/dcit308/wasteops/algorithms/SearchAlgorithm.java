package com.dcit308.wasteops.algorithms;

/**
 * Contract shared by both required search algorithms, so the performance
 * harness can time either one through the same loop instead of writing
 * separate timing code per algorithm.
 *
 * OWNERS (implement this): Issue #4 — LinearSearch.
 *                           Issue #5 — BinarySearch.
 * CONSUMER (codes against this): Issue #14 — the required search-
 * comparison performance experiment (brief Section 9) runs both
 * algorithms across the same input sizes and needs to call them
 * identically to produce a fair, like-for-like timing comparison.
 *
 * See Team_Handbook.docx, "Working With Each Other's Code."
 */
public interface SearchAlgorithm<T> {

    /** Returns the index of target in array, or -1 if not found. */
    int search(T[] array, T target);
}
