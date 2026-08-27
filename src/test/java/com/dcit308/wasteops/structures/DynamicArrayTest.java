package com.dcit308.wasteops.structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #1's DynamicArray.
 *
 * Covers:
 * - Empty case
 * - Single element
 * - Resize trigger
 * - Out-of-bounds access
 */
class DynamicArrayTest {

    @Nested
    @DisplayName("Empty array")
    class EmptyArray {

        @Test
        @DisplayName("reports size 0 and isEmpty true")
        void reportsZeroSize() {
            DynamicArray<String> array = new DynamicArray<>();

            assertEquals(0, array.size());
            assertTrue(array.isEmpty());
        }

        @Test
        @DisplayName("get on empty array throws IndexOutOfBoundsException")
        void getOnEmptyThrows() {
            DynamicArray<String> array = new DynamicArray<>();

            assertThrows(IndexOutOfBoundsException.class, () -> array.get(0));
        }

        @Test
        @DisplayName("set on empty array throws IndexOutOfBoundsException")
        void setOnEmptyThrows() {
            DynamicArray<String> array = new DynamicArray<>();

            assertThrows(IndexOutOfBoundsException.class, () -> array.set(0, "x"));
        }

        @Test
        @DisplayName("remove on empty array throws IndexOutOfBoundsException")
        void removeOnEmptyThrows() {
            DynamicArray<String> array = new DynamicArray<>();

            assertThrows(IndexOutOfBoundsException.class, () -> array.remove(0));
        }
    }

    @Nested
    @DisplayName("Single element")
    class SingleElement {

        @Test
        @DisplayName("insert at index 0 stores the value")
        void insertAtZero() {
            DynamicArray<String> array = new DynamicArray<>();
            array.insert(0, "A");

            assertEquals(1, array.size());
            assertEquals("A", array.get(0));
        }

        @Test
        @DisplayName("set updates the existing element")
        void setUpdatesElement() {
            DynamicArray<String> array = new DynamicArray<>();
            array.insert(0, "A");
            array.set(0, "B");

            assertEquals("B", array.get(0));
            assertEquals(1, array.size());
        }

        @Test
        @DisplayName("remove leaves the array empty")
        void removeLeavesEmpty() {
            DynamicArray<String> array = new DynamicArray<>();
            array.insert(0, "A");
            array.remove(0);

            assertEquals(0, array.size());
            assertTrue(array.isEmpty());
        }
    }

    @Nested
    @DisplayName("Resize trigger")
    class ResizeTrigger {

        @Test
        @DisplayName("inserts beyond default capacity resize the backing array")
        void resizesWhenFull() {
            DynamicArray<Integer> array = new DynamicArray<>();

            for (int i = 0; i < 20; i++) {
                array.insert(i, i);
            }

            assertEquals(20, array.size());

            for (int i = 0; i < 20; i++) {
                assertEquals(i, array.get(i));
            }
        }

        @Test
        @DisplayName("insert at end appends and triggers resize")
        void insertAtEndAppends() {
            DynamicArray<String> array = new DynamicArray<>();

            array.insert(0, "A");
            array.insert(1, "B");
            array.insert(2, "C");

            assertEquals(3, array.size());
            assertEquals("A", array.get(0));
            assertEquals("B", array.get(1));
            assertEquals("C", array.get(2));
        }

        @Test
        @DisplayName("insert in middle shifts elements right")
        void insertInMiddleShifts() {
            DynamicArray<String> array = new DynamicArray<>();

            array.insert(0, "A");
            array.insert(1, "C");
            array.insert(1, "B");

            assertEquals(3, array.size());
            assertEquals("A", array.get(0));
            assertEquals("B", array.get(1));
            assertEquals("C", array.get(2));
        }
    }

    @Nested
    @DisplayName("Out-of-bounds access")
    class OutOfBounds {

        @Test
        @DisplayName("get with negative index throws")
        void getNegativeThrows() {
            DynamicArray<String> array = new DynamicArray<>();
            array.insert(0, "A");

            assertThrows(IndexOutOfBoundsException.class, () -> array.get(-1));
        }

        @Test
        @DisplayName("get with index >= size throws")
        void getTooLargeThrows() {
            DynamicArray<String> array = new DynamicArray<>();
            array.insert(0, "A");

            assertThrows(IndexOutOfBoundsException.class, () -> array.get(1));
        }

        @Test
        @DisplayName("insert with negative index throws")
        void insertNegativeThrows() {
            DynamicArray<String> array = new DynamicArray<>();

            assertThrows(IndexOutOfBoundsException.class, () -> array.insert(-1, "x"));
        }

        @Test
        @DisplayName("insert with index > size throws")
        void insertTooLargeThrows() {
            DynamicArray<String> array = new DynamicArray<>();
            array.insert(0, "A");

            assertThrows(IndexOutOfBoundsException.class, () -> array.insert(2, "x"));
        }

        @Test
        @DisplayName("remove with negative index throws")
        void removeNegativeThrows() {
            DynamicArray<String> array = new DynamicArray<>();
            array.insert(0, "A");

            assertThrows(IndexOutOfBoundsException.class, () -> array.remove(-1));
        }

        @Test
        @DisplayName("remove with index >= size throws")
        void removeTooLargeThrows() {
            DynamicArray<String> array = new DynamicArray<>();
            array.insert(0, "A");

            assertThrows(IndexOutOfBoundsException.class, () -> array.remove(1));
        }
    }

    @Nested
    @DisplayName("Remove")
    class Remove {

        @Test
        @DisplayName("remove shifts remaining elements left")
        void removeShiftsLeft() {
            DynamicArray<String> array = new DynamicArray<>();

            array.insert(0, "A");
            array.insert(1, "B");
            array.insert(2, "C");
            array.remove(1);

            assertEquals(2, array.size());
            assertEquals("A", array.get(0));
            assertEquals("C", array.get(1));
        }

        @Test
        @DisplayName("remove first element updates order")
        void removeFirstElement() {
            DynamicArray<String> array = new DynamicArray<>();

            array.insert(0, "A");
            array.insert(1, "B");
            array.remove(0);

            assertEquals(1, array.size());
            assertEquals("B", array.get(0));
        }

        @Test
        @DisplayName("remove last element updates order")
        void removeLastElement() {
            DynamicArray<String> array = new DynamicArray<>();

            array.insert(0, "A");
            array.insert(1, "B");
            array.remove(1);

            assertEquals(1, array.size());
            assertEquals("A", array.get(0));
        }
    }
}
