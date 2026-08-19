package com.dcit308.wasteops.structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #8's BTree.
 *
 * Same package as the class under test (different source root), so
 * package-private members stay reachable if later tests need them.
 */
class BTreeTest {

    @Nested
    @DisplayName("Empty tree")
    class EmptyTree {

        @Test
        @DisplayName("reports size 0 and isEmpty true")
        void reportsZeroSize() {
            BTree<Integer, String> tree = new BTree<>();

            assertEquals(0, tree.size());
            assertTrue(tree.isEmpty());
        }

        @Test
        @DisplayName("search returns null for any key")
        void searchReturnsNull() {
            BTree<Integer, String> tree = new BTree<>();

            assertNull(tree.search(1));
            assertNull(tree.search(-999));
        }

        @Test
        @DisplayName("rejects a null key on insert")
        void rejectsNullKeyOnInsert() {
            BTree<Integer, String> tree = new BTree<>();

            assertThrows(IllegalArgumentException.class, () -> tree.insert(null, "x"));
        }

        @Test
        @DisplayName("rejects a minimum degree below 2")
        void rejectsInvalidMinDegree() {
            assertThrows(IllegalArgumentException.class, () -> new BTree<Integer, String>(1));
        }
    }

    @Nested
    @DisplayName("Single key")
    class SingleKey {

        @Test
        @DisplayName("the inserted key is found by search")
        void insertedKeyIsFound() {
            BTree<Integer, String> tree = new BTree<>();

            tree.insert(42, "answer");

            assertEquals("answer", tree.search(42));
        }

        @Test
        @DisplayName("size becomes 1 and isEmpty becomes false")
        void sizeIsOne() {
            BTree<Integer, String> tree = new BTree<>();

            tree.insert(42, "answer");

            assertEquals(1, tree.size());
            assertTrue(!tree.isEmpty());
        }

        @Test
        @DisplayName("a different, absent key returns null")
        void absentKeyReturnsNull() {
            BTree<Integer, String> tree = new BTree<>();

            tree.insert(42, "answer");

            assertNull(tree.search(43));
        }
    }

    @Nested
    @DisplayName("Duplicate keys")
    class DuplicateKeys {

        @Test
        @DisplayName("re-inserting an existing key overwrites its value without growing size")
        void reinsertingUpdatesValue() {
            BTree<Integer, String> tree = new BTree<>();

            tree.insert(5, "first");
            tree.insert(5, "second");

            assertEquals(1, tree.size());
            assertEquals("second", tree.search(5));
        }

        @Test
        @DisplayName("upsert still works for a key that moved up during a split")
        void upsertOfMedianKeyAfterSplit() {
            // t=2 -> max 3 keys/node; forces a split partway through this sequence.
            BTree<Integer, String> tree = new BTree<>(2);
            for (int k : new int[]{10, 20, 30, 40}) {
                tree.insert(k, "v" + k);
            }
            // 20 was the median that moved up into the new root when 40 was inserted.
            tree.insert(20, "updated");

            assertEquals(4, tree.size());
            assertEquals("updated", tree.search(20));
        }
    }

    @Nested
    @DisplayName("Node split")
    class NodeSplit {

        @Test
        @DisplayName("forcing a split keeps every key searchable afterwards")
        void forcingSplitKeepsAllKeysSearchable() {
            // t=2 -> a node overflows and splits once it holds 4 keys (max is 3).
            BTree<Integer, String> tree = new BTree<>(2);
            int[] keys = {10, 20, 30, 40, 50, 60, 70};

            for (int k : keys) {
                tree.insert(k, "R" + k);
            }

            assertEquals(keys.length, tree.size());
            for (int k : keys) {
                assertEquals("R" + k, tree.search(k));
            }
            assertNull(tree.search(999)); // never inserted
        }

        @Test
        @DisplayName("splitting happens more than once as more keys are added")
        void multipleSplitsStillSearchable() {
            BTree<Integer, String> tree = new BTree<>(2);
            for (int k = 0; k < 50; k++) {
                tree.insert(k, "v" + k);
            }

            assertEquals(50, tree.size());
            for (int k = 0; k < 50; k++) {
                assertEquals("v" + k, tree.search(k));
            }
        }

        @Test
        @DisplayName("descending insertion order also splits correctly")
        void descendingInsertionOrder() {
            BTree<Integer, String> tree = new BTree<>(2);
            for (int k = 20; k >= 1; k--) {
                tree.insert(k, "v" + k);
            }

            assertEquals(20, tree.size());
            for (int k = 1; k <= 20; k++) {
                assertEquals("v" + k, tree.search(k));
            }
        }
    }
}
