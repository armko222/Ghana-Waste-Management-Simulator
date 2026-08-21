package com.dcit308.wasteops.algorithms.sort;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SelectionSortTest {
    @Test
    public void testSortUnsortedArray() {
        SelectionSort<Integer> sorter = new SelectionSort<>();
        Integer[] array = {5, 2, 8, 1, 3};
        sorter.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 5, 8}, array);
    }
    @Test
    public void testSortAlreadySortedArray() {
        SelectionSort<Integer> sorter = new SelectionSort<>();
        Integer[] array = {1, 2, 3, 4, 5};
        sorter.sort(array);
        assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
    }
}