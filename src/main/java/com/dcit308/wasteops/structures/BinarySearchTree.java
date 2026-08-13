package com.dcit308.wasteops.structures;

import java.util.List;

/**
 * Plain (unbalanced) binary search tree. Backs indexing/RequestIndex.java.
 * Implements SearchTreeADT so Issue #7 and Issue #14 can compare it
 * against RedBlackTree using identical method calls.
 *
 * Owned by Issue #6.
 */
public class BinarySearchTree<K extends Comparable<K>, V> implements SearchTreeADT<K, V> {

    @Override
    public void insert(K key, V value) {
        throw new UnsupportedOperationException("TODO: Issue #6 \u2014 implement insert. Decide and document your duplicate-key policy.");
    }

    @Override
    public V search(K key) {
        throw new UnsupportedOperationException("TODO: Issue #6 \u2014 implement search.");
    }

    @Override
    public int height() {
        throw new UnsupportedOperationException("TODO: Issue #6 \u2014 implement height (0 for an empty tree).");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("TODO: Issue #6 \u2014 implement size.");
    }

    /** Returns all values in ascending key order -- required evidence (Section 6). */
    public List<V> inorderTraversal() {
        throw new UnsupportedOperationException("TODO: Issue #6 \u2014 implement inorderTraversal.");
    }
}
