package com.dcit308.wasteops.structures;

/**
 * Contract shared by both required search-tree implementations, so they
 * can be used interchangeably wherever the system needs "a tree that can
 * insert, search, and report its own height" without caring which one.
 *
 * OWNERS (implement this): Issue #6 — BinarySearchTree.
 *                           Issue #7 — RedBlackTree.
 * CONSUMERS (code against this):
 *   - Issue #7 itself — needs to insert the same sequential data into
 *     both its own tree and Issue #6's BST and compare heights, which
 *     only works cleanly if both expose the same shape.
 *   - Issue #14 — the required "BST vs balanced tree" performance
 *     experiment (brief Section 9) times both tree types under the same
 *     loop, rather than needing separate bespoke code for each.
 *
 * See Team_Handbook.docx, "Working With Each Other's Code."
 */
public interface SearchTreeADT<K, V> {

    void insert(K key, V value);

    /** Returns the value for key, or null if not present. */
    V search(K key);

    /** Height of the tree — an empty tree has height 0. */
    int height();

    int size();
}
