package com.dcit308.wasteops.structures;

/**
 * Self-balancing binary search tree (rotation + recolouring), or a
 * clearly documented simplified balanced-tree alternative if the team
 * agrees that trade-off. Implements SearchTreeADT so it can be compared
 * directly against Issue #6's plain BST using identical method calls.
 *
 * Owned by Issue #7.
 */
public class RedBlackTree<K extends Comparable<K>, V> implements SearchTreeADT<K, V> {

    @Override
    public void insert(K key, V value) {
        throw new UnsupportedOperationException("TODO: Issue #7 \u2014 implement insert with rotation/recolouring.");
    }

    @Override
    public V search(K key) {
        throw new UnsupportedOperationException("TODO: Issue #7 \u2014 implement search.");
    }

    @Override
    public int height() {
        throw new UnsupportedOperationException("TODO: Issue #7 \u2014 implement height (0 for an empty tree).");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("TODO: Issue #7 \u2014 implement size.");
    }
}
