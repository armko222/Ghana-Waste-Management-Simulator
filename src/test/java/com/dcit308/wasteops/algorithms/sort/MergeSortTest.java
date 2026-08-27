package com.dcit308.wasteops.algorithms.sort;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #8's MergeSort.
 */
class MergeSortTest {

    private final MergeSort<Integer> mergeSort = new MergeSort<>();

    @Nested
    @DisplayName("Boundary cases")
    class BoundaryCases {

        @Test
        @DisplayName("null array does not throw")
        void nullArrayDoesNotThrow() {
            mergeSort.sort(null);
        }

        @Test
        @DisplayName("empty array stays empty")
        void emptyArray() {
            Integer[] array = {};

            mergeSort.sort(array);

            assertArrayEquals(new Integer[]{}, array);
        }

        @Test
        @DisplayName("single-element array is unchanged")
        void singleElement() {
            Integer[] array = {7};

            mergeSort.sort(array);

            assertArrayEquals(new Integer[]{7}, array);
        }
    }

    @Nested
    @DisplayName("Ordering cases")
    class OrderingCases {

        @Test
        @DisplayName("already-sorted input stays sorted")
        void alreadySorted() {
            Integer[] array = {1, 2, 3, 4, 5};

            mergeSort.sort(array);

            assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
        }

        @Test
        @DisplayName("reverse-sorted input is fully reversed")
        void reverseSorted() {
            Integer[] array = {5, 4, 3, 2, 1};

            mergeSort.sort(array);

            assertArrayEquals(new Integer[]{1, 2, 3, 4, 5}, array);
        }

        @Test
        @DisplayName("the textbook worked example from the trace table sorts correctly")
        void tracedExample() {
            Integer[] array = {38, 27, 43, 3, 9, 82, 10};

            mergeSort.sort(array);

            assertArrayEquals(new Integer[]{3, 9, 10, 27, 38, 43, 82}, array);
        }

        @Test
        @DisplayName("duplicate values are all preserved")
        void duplicateValues() {
            Integer[] array = {5, 3, 5, 1, 3, 5};

            mergeSort.sort(array);

            assertArrayEquals(new Integer[]{1, 3, 3, 5, 5, 5}, array);
        }

        @Test
        @DisplayName("matches java.util.Arrays.sort on random input")
        void matchesReferenceSortOnRandomInput() {
            Random random = new Random(42);
            Integer[] array = new Integer[500];
            for (int i = 0; i < array.length; i++) {
                array[i] = random.nextInt(10_000);
            }
            Integer[] expected = array.clone();
            Arrays.sort(expected);

            mergeSort.sort(array);

            assertArrayEquals(expected, array);
        }
    }

    @Nested
    @DisplayName("Stability")
    class Stability {

        /** Wraps an int with an origin tag so we can tell equal-valued elements apart after sorting. */
        private static final class Tagged implements Comparable<Tagged> {
            final int value;
            final String tag;

            Tagged(int value, String tag) {
                this.value = value;
                this.tag = tag;
            }

            @Override
            public int compareTo(Tagged other) {
                return Integer.compare(this.value, other.value);
            }

            @Override
            public String toString() {
                return value + tag;
            }
        }

        @Test
        @DisplayName("equal keys keep their original relative order")
        void equalKeysStayInOriginalOrder() {
            MergeSort<Tagged> tagSort = new MergeSort<>();
            Tagged[] array = {
                    new Tagged(2, "a"), new Tagged(1, "a"), new Tagged(2, "b"), new Tagged(1, "b")
            };

            tagSort.sort(array);

            // Both value-1 elements come before both value-2 elements, each pair in
            // original relative order: 1a before 1b, 2a before 2b.
            assertTrue(array[0].toString().equals("1a"));
            assertTrue(array[1].toString().equals("1b"));
            assertTrue(array[2].toString().equals("2a"));
            assertTrue(array[3].toString().equals("2b"));
        }
    }
}
