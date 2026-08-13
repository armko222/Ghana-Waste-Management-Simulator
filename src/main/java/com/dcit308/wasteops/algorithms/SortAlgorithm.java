package com.dcit308.wasteops.algorithms;

/**
 * Contract shared by all four required sorting algorithms, so the
 * performance harness can time any of them through the same loop instead
 * of writing separate timing code per algorithm.
 *
 * OWNERS (implement this): Issue #6 — InsertionSort.
 *                           Issue #7 — SelectionSort.
 *                           Issue #8 — MergeSort.
 *                           Issue #9 — QuickSort.
 * CONSUMER (codes against this): Issue #14 — the required sorting-
 * comparison performance experiment (brief Section 9) runs all four
 * algorithms across the same input sizes and needs to call them
 * identically to produce a fair, like-for-like timing comparison.
 *
 * See Team_Handbook.docx, "Working With Each Other's Code."
 */
public interface SortAlgorithm<T extends Comparable<T>> {

    /** Sorts array in place, ascending order. */
    void sort(T[] array);
}
