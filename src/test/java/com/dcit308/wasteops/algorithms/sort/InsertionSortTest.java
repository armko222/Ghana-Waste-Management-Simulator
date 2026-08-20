package com.dcit308.wasteops.algorithms.sort;

import com.dcit308.wasteops.algorithms.SortAlgorithm;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests for Issue #6's InsertionSort.
 *
 * SortAlgorithm.sort is specified to sort in place, so every test asserts
 * against the array that was passed in rather than a returned copy.
 */
class InsertionSortTest {

    private final SortAlgorithm<Integer> sorter = new InsertionSort<>();

    @Test
    @DisplayName("an empty array is left untouched")
    void sortsEmptyArray() {
        Integer[] input = {};

        sorter.sort(input);

        assertArrayEquals(new Integer[] {}, input);
    }

    @Test
    @DisplayName("a single-element array is left untouched")
    void sortsSingleElement() {
        Integer[] input = {7};

        sorter.sort(input);

        assertArrayEquals(new Integer[] {7}, input);
    }

    @Test
    @DisplayName("an already-sorted array keeps its order (best case, O(n))")
    void sortsAlreadySortedArray() {
        Integer[] input = {1, 2, 3, 4, 5};

        sorter.sort(input);

        assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, input);
    }

    @Test
    @DisplayName("a reverse-sorted array is fully reordered (worst case, O(n^2))")
    void sortsReverseSortedArray() {
        Integer[] input = {5, 4, 3, 2, 1};

        sorter.sort(input);

        assertArrayEquals(new Integer[] {1, 2, 3, 4, 5}, input);
    }

    @Test
    @DisplayName("an unsorted array is ordered ascending")
    void sortsUnsortedArray() {
        Integer[] input = {29, 10, 14, 37, 13};

        sorter.sort(input);

        assertArrayEquals(new Integer[] {10, 13, 14, 29, 37}, input);
    }

    @Test
    @DisplayName("duplicate values are all kept, not collapsed")
    void keepsDuplicates() {
        Integer[] input = {3, 1, 3, 2, 1};

        sorter.sort(input);

        assertArrayEquals(new Integer[] {1, 1, 2, 3, 3}, input);
    }

    @Test
    @DisplayName("negative and zero values sort correctly")
    void sortsNegativesAndZero() {
        Integer[] input = {0, -5, 3, -1};

        sorter.sort(input);

        assertArrayEquals(new Integer[] {-5, -1, 0, 3}, input);
    }

    @Test
    @DisplayName("sorts in place — the same array object is mutated")
    void sortsInPlace() {
        Integer[] input = {3, 1, 2};
        Integer[] reference = input;

        sorter.sort(input);

        assertSame(reference, input, "SortAlgorithm.sort must not swap in a new array");
        assertArrayEquals(new Integer[] {1, 2, 3}, reference);
    }

    @Test
    @DisplayName("works for any Comparable, not just Integer")
    void sortsStrings() {
        String[] input = {"Van", "Rider", "Truck"};

        new InsertionSort<String>().sort(input);

        assertArrayEquals(new String[] {"Rider", "Truck", "Van"}, input);
    }
}
