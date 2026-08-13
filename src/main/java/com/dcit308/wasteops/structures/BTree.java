package com.dcit308.wasteops.structures;

/**
 * B-tree with node splitting, or a documented database-index-page
 * simulation (brief Section 6 explicitly allows the lighter option).
 * Backs indexing/ResourceIndex.java.
 *
 * Owned by Issue #8.
 */
public class BTree<K extends Comparable<K>, V> {

    public void insert(K key, V value) {
        throw new UnsupportedOperationException("TODO: Issue #8 \u2014 implement insert, including node splitting when a node overflows.");
    }

    public V search(K key) {
        throw new UnsupportedOperationException("TODO: Issue #8 \u2014 implement search.");
    }

    public int size() {
        throw new UnsupportedOperationException("TODO: Issue #8 \u2014 implement size.");
    }
}
