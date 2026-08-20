package com.dcit308.wasteops.structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #6's BinarySearchTree.
 *
 * Same package as the class under test (different source root), so
 * package-private members stay reachable if later tests need them.
 */
class BinarySearchTreeTest {

    @Nested
    @DisplayName("Empty tree")
    class EmptyTree {

        @Test
        @DisplayName("reports height 0 and size 0")
        void reportsZeroHeightAndSize() {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();

            assertEquals(0, tree.height(), "SearchTreeADT specifies height 0 for an empty tree");
            assertEquals(0, tree.size());
        }

        @Test
        @DisplayName("returns null for any search and an empty traversal")
        void searchesReturnNull() {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();

            assertNull(tree.search(42));
            assertTrue(tree.inorderTraversal().isEmpty());
        }
    }

    @Nested
    @DisplayName("Single node")
    class SingleNode {

        @Test
        @DisplayName("reports height 1 and size 1")
        void reportsHeightAndSizeOfOne() {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
            tree.insert(10, "Q001");

            assertEquals(1, tree.height());
            assertEquals(1, tree.size());
        }

        @Test
        @DisplayName("finds its only key and nothing else")
        void findsOnlyKey() {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
            tree.insert(10, "Q001");

            assertEquals("Q001", tree.search(10));
            assertNull(tree.search(9));
            assertNull(tree.search(11));
        }
    }

    @Nested
    @DisplayName("Search")
    class Search {

        private BinarySearchTree<Integer, String> populated() {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
            tree.insert(50, "Q050");
            tree.insert(30, "Q030");
            tree.insert(70, "Q070");
            tree.insert(20, "Q020");
            tree.insert(40, "Q040");
            return tree;
        }

        @Test
        @DisplayName("returns the value for a key that is present")
        void findsPresentKey() {
            assertEquals("Q040", populated().search(40), "40 sits on the left subtree's right branch");
            assertEquals("Q050", populated().search(50), "root");
            assertEquals("Q070", populated().search(70), "right child");
        }

        @Test
        @DisplayName("returns null for a key that is absent")
        void returnsNullForAbsentKey() {
            assertNull(populated().search(45), "absent, would sit between 40 and 50");
            assertNull(populated().search(99), "absent, beyond every key");
            assertNull(populated().search(1), "absent, below every key");
        }
    }

    @Nested
    @DisplayName("Duplicate keys")
    class DuplicateKeys {

        @Test
        @DisplayName("overwrite the existing value and leave size unchanged")
        void overwriteValueAndKeepSize() {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
            tree.insert(10, "first");
            tree.insert(10, "second");

            assertEquals("second", tree.search(10), "documented policy: last write wins");
            assertEquals(1, tree.size(), "a duplicate key must not add a node");
        }

        @Test
        @DisplayName("do not add a duplicate branch to the tree")
        void doNotChangeTreeShape() {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
            tree.insert(10, "first");
            int heightBefore = tree.height();

            tree.insert(10, "second");

            assertEquals(heightBefore, tree.height(), "re-inserting a key must not deepen the tree");
            assertEquals(List.of("second"), tree.inorderTraversal(), "the key appears exactly once");
        }
    }

    @Nested
    @DisplayName("Inorder traversal")
    class InorderTraversal {

        @Test
        @DisplayName("returns values in ascending key order regardless of insertion order")
        void returnsSortedOutput() {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
            // Deliberately unsorted insertion order.
            tree.insert(50, "fifty");
            tree.insert(30, "thirty");
            tree.insert(70, "seventy");
            tree.insert(20, "twenty");
            tree.insert(40, "forty");
            tree.insert(60, "sixty");
            tree.insert(80, "eighty");

            assertEquals(
                    List.of("twenty", "thirty", "forty", "fifty", "sixty", "seventy", "eighty"),
                    tree.inorderTraversal(),
                    "inorder traversal of a BST is the sorted sequence — Section 6 evidence");
        }

        @Test
        @DisplayName("works with String keys, which RequestIndex uses for ISO-8601 deadlines")
        void sortsStringKeysLexicographically() {
            BinarySearchTree<String, String> tree = new BinarySearchTree<>();
            tree.insert("2026-07-01T11:30", "Q002");
            tree.insert("2026-07-01T09:00", "Q001");
            tree.insert("2026-07-01T14:15", "Q003");

            assertEquals(List.of("Q001", "Q002", "Q003"), tree.inorderTraversal(),
                    "ISO-8601 sorts correctly as plain text, which is why RequestIndex keys on it");
        }
    }

    @Nested
    @DisplayName("Height")
    class Height {

        @Test
        @DisplayName("degrades to the node count when keys arrive in sorted order")
        void degeneratesOnSortedInput() {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
            for (int key = 1; key <= 5; key++) {
                tree.insert(key, "Q" + key);
            }

            // Every insert goes right, so the tree is really a linked list.
            // This is the weakness Issue #7's RedBlackTree is measured against.
            assertEquals(5, tree.height(), "sorted input produces a degenerate, unbalanced tree");
            assertEquals(5, tree.size());
        }

        @Test
        @DisplayName("stays shallow when keys arrive balanced")
        void staysShallowWhenBalanced() {
            BinarySearchTree<Integer, String> tree = new BinarySearchTree<>();
            tree.insert(50, "a");
            tree.insert(30, "b");
            tree.insert(70, "c");

            assertEquals(2, tree.height(), "same node count as a 3-key degenerate tree, but half the height");
            assertEquals(3, tree.size());
        }
    }
}
