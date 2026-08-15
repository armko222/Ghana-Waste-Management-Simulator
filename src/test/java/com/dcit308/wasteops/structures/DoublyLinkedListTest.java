package com.dcit308.wasteops.structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #2's DoublyLinkedList.
 *
 * Same package as the class under test (different source root), so
 * package-private members stay reachable if later tests need them.
 */
class DoublyLinkedListTest {

    /** Drains an iterator into a plain list, purely for assertion convenience. */
    private static <T> List<T> drain(Iterable<T> list) {
        List<T> result = new ArrayList<>();
        for (T value : list) {
            result.add(value);
        }
        return result;
    }

    @Nested
    @DisplayName("Empty list")
    class EmptyList {

        @Test
        @DisplayName("reports size 0 and isEmpty true")
        void reportsZeroSize() {
            DoublyLinkedList<String> list = new DoublyLinkedList<>();

            assertEquals(0, list.size());
            assertTrue(list.isEmpty());
        }

        @Test
        @DisplayName("iterator has no elements")
        void iteratorHasNoElements() {
            DoublyLinkedList<String> list = new DoublyLinkedList<>();

            Iterator<String> it = list.iterator();
            assertFalse(it.hasNext());
            assertThrows(NoSuchElementException.class, it::next);
        }

        @Test
        @DisplayName("remove and insertAfter on an absent value are no-ops")
        void removeAndInsertAfterAreNoOps() {
            DoublyLinkedList<String> list = new DoublyLinkedList<>();

            list.remove("ghost");
            list.insertAfter("ghost", "value");

            assertEquals(0, list.size());
            assertTrue(list.isEmpty());
        }
    }

    @Nested
    @DisplayName("Single node")
    class SingleNode {

        @Test
        @DisplayName("addFirst on an empty list sets size 1 and is the only element")
        void addFirstOnEmptyList() {
            DoublyLinkedList<String> list = new DoublyLinkedList<>();
            list.addFirst("A");

            assertEquals(1, list.size());
            assertFalse(list.isEmpty());
            assertEquals(List.of("A"), drain(list));
        }

        @Test
        @DisplayName("addLast on an empty list sets size 1 and is the only element")
        void addLastOnEmptyList() {
            DoublyLinkedList<String> list = new DoublyLinkedList<>();
            list.addLast("A");

            assertEquals(1, list.size());
            assertEquals(List.of("A"), drain(list));
        }

        @Test
        @DisplayName("removing the only node empties the list")
        void removingOnlyNodeEmptiesList() {
            DoublyLinkedList<String> list = new DoublyLinkedList<>();
            list.addFirst("A");

            list.remove("A");

            assertEquals(0, list.size());
            assertTrue(list.isEmpty());
            assertFalse(list.iterator().hasNext());
        }
    }

    @Nested
    @DisplayName("Multiple nodes")
    class MultipleNodes {

        private DoublyLinkedList<String> populated() {
            DoublyLinkedList<String> list = new DoublyLinkedList<>();
            list.addLast("A");
            list.addLast("B");
            list.addLast("C");
            return list;
        }

        @Test
        @DisplayName("addLast repeatedly builds insertion order")
        void addLastBuildsInsertionOrder() {
            assertEquals(List.of("A", "B", "C"), drain(populated()));
        }

        @Test
        @DisplayName("addFirst repeatedly builds reverse-insertion order")
        void addFirstBuildsReverseOrder() {
            DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
            list.addFirst(1);
            list.addFirst(2);
            list.addFirst(3);

            assertEquals(List.of(3, 2, 1), drain(list));
            assertEquals(3, list.size());
        }

        @Test
        @DisplayName("insertAfter places the new value directly after ref")
        void insertAfterPlacesValueAfterRef() {
            DoublyLinkedList<String> list = populated();

            list.insertAfter("B", "B2");

            assertEquals(List.of("A", "B", "B2", "C"), drain(list));
            assertEquals(4, list.size());
        }

        @Test
        @DisplayName("insertAfter on the tail extends the list and updates tail")
        void insertAfterOnTailExtendsList() {
            DoublyLinkedList<String> list = populated();

            list.insertAfter("C", "D");

            assertEquals(List.of("A", "B", "C", "D"), drain(list));

            // Confirm tail bookkeeping is correct, not just forward iteration:
            // addLast after this must append at the true end.
            list.addLast("E");
            assertEquals(List.of("A", "B", "C", "D", "E"), drain(list));
        }

        @Test
        @DisplayName("insertAfter with a ref that isn't present is a no-op")
        void insertAfterWithAbsentRefIsNoOp() {
            DoublyLinkedList<String> list = populated();

            list.insertAfter("Z", "should-not-appear");

            assertEquals(List.of("A", "B", "C"), drain(list));
            assertEquals(3, list.size());
        }

        @Test
        @DisplayName("removing an existing value unlinks it and closes the gap")
        void removingExistingValueClosesGap() {
            DoublyLinkedList<String> list = populated();

            list.remove("B");

            assertEquals(List.of("A", "C"), drain(list));
            assertEquals(2, list.size());
        }

        @Test
        @DisplayName("removing the head updates head bookkeeping")
        void removingHeadUpdatesHead() {
            DoublyLinkedList<String> list = populated();

            list.remove("A");

            assertEquals(List.of("B", "C"), drain(list));
            // addFirst after this must land at the true new head.
            list.addFirst("Z");
            assertEquals(List.of("Z", "B", "C"), drain(list));
        }

        @Test
        @DisplayName("removing the tail updates tail bookkeeping")
        void removingTailUpdatesTail() {
            DoublyLinkedList<String> list = populated();

            list.remove("C");

            assertEquals(List.of("A", "B"), drain(list));
            // addLast after this must land at the true new tail.
            list.addLast("Z");
            assertEquals(List.of("A", "B", "Z"), drain(list));
        }

        @Test
        @DisplayName("removing a value that isn't present is a no-op")
        void removingAbsentValueIsNoOp() {
            DoublyLinkedList<String> list = populated();

            list.remove("Z");

            assertEquals(List.of("A", "B", "C"), drain(list));
            assertEquals(3, list.size());
        }

        @Test
        @DisplayName("removing only the first match leaves duplicates further down the list")
        void removingOnlyFirstMatch() {
            DoublyLinkedList<String> list = new DoublyLinkedList<>();
            list.addLast("A");
            list.addLast("B");
            list.addLast("A");

            list.remove("A");

            assertEquals(List.of("B", "A"), drain(list), "only the first matching node is removed");
            assertEquals(2, list.size());
        }
    }

    @Nested
    @DisplayName("Iteration")
    class Iteration {

        @Test
        @DisplayName("visits nodes in insertion order")
        void visitsInsertionOrder() {
            DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
            for (int i = 1; i <= 5; i++) {
                list.addLast(i);
            }

            assertEquals(List.of(1, 2, 3, 4, 5), drain(list));
        }

        @Test
        @DisplayName("a fresh iterator reflects removals made since the previous iteration")
        void reflectsRemovalsOnFreshIterator() {
            DoublyLinkedList<Integer> list = new DoublyLinkedList<>();
            list.addLast(1);
            list.addLast(2);
            list.addLast(3);
            assertEquals(List.of(1, 2, 3), drain(list));

            list.remove(2);

            assertEquals(List.of(1, 3), drain(list), "iterating again after remove() must skip the removed node");
        }

        @Test
        @DisplayName("exhausting the iterator throws NoSuchElementException on the next call")
        void exhaustingIteratorThrows() {
            DoublyLinkedList<String> list = new DoublyLinkedList<>();
            list.addLast("only");

            Iterator<String> it = list.iterator();
            assertEquals("only", it.next());
            assertFalse(it.hasNext());
            assertThrows(NoSuchElementException.class, it::next);
        }

        @Test
        @DisplayName("supports the enhanced for-loop via Iterable")
        void supportsEnhancedForLoop() {
            DoublyLinkedList<String> list = new DoublyLinkedList<>();
            list.addLast("A");
            list.addLast("B");

            StringBuilder result = new StringBuilder();
            for (String value : list) {
                result.append(value);
            }

            assertEquals("AB", result.toString());
        }
    }
}
