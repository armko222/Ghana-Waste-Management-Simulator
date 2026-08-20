package com.dcit308.wasteops.algorithms.sort;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuickSortTest {

    @Test
    void shouldSortUnorderedArray() {
        Integer[] array = {5, 2, 8, 1, 3};

        QuickSort<Integer> quickSort = new QuickSort<>();
        quickSort.sort(array);

        assertArrayEquals(
                new Integer[]{1, 2, 3, 5, 8},
                array
        );
    }

    @Test
    void shouldSortAlreadySortedArray() {
        Integer[] array = {1, 2, 3, 4, 5};

        QuickSort<Integer> quickSort = new QuickSort<>();
        quickSort.sort(array);

        assertArrayEquals(
                new Integer[]{1, 2, 3, 4, 5},
                array
        );
    }

    @Test
    void shouldSortArrayWithDuplicates() {
        Integer[] array = {4, 2, 4, 1, 2, 4};

        QuickSort<Integer> quickSort = new QuickSort<>();
        quickSort.sort(array);

        assertArrayEquals(
                new Integer[]{1, 2, 2, 4, 4, 4},
                array
        );
    }

    @Test
    void shouldHandleSingleElementArray() {
        Integer[] array = {7};

        QuickSort<Integer> quickSort = new QuickSort<>();
        quickSort.sort(array);

        assertArrayEquals(
                new Integer[]{7},
                array
        );
    }

    @Test
    void shouldHandleEmptyArray() {
        Integer[] array = {};

        QuickSort<Integer> quickSort = new QuickSort<>();
        quickSort.sort(array);

        assertArrayEquals(
                new Integer[]{},
                array
        );
    }

    @Test
    void shouldHandleNullArray() {
        QuickSort<Integer> quickSort = new QuickSort<>();

        assertDoesNotThrow(() -> quickSort.sort(null));
    }
}