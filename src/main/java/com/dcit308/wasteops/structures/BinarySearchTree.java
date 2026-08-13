package com.dcit308.wasteops.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Plain (unbalanced) binary search tree. Backs indexing/RequestIndex.java.
 * Implements SearchTreeADT so Issue #7 and Issue #14 can compare it
 * against RedBlackTree using identical method calls.
 *
 * Owned by Issue #6.
 */
public class BinarySearchTree<K extends Comparable<K>, V> implements SearchTreeADT<K, V> {

    private Node root;
    private int size;

    private class Node {
        K key;
        V value;
        Node left, right;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    /**
     * DUPLICATE-KEY POLICY: inserting a key that is already present
     * overwrites that node's value and leaves size() unchanged — the tree
     * never holds two nodes for the same key, so no duplicate branch is
     * created and the shape of the tree does not change.
     *
     * Chosen over the alternatives (rejecting the insert, or storing
     * duplicates in a right subtree) because this tree backs
     * indexing/RequestIndex.java, which relies on being able to read a
     * key's current value, mutate it, and have the tree keep serving that
     * same object. Callers needing to keep every value for a key should
     * do what RequestIndex does: use a collection as V.
     */
    @Override
    public void insert(K key, V value) {
        root = insertRecord(root, key, value);
    }

    private Node insertRecord(Node node, K key, V value) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }
        int cmp = key.compareTo(node.key);

        if (cmp < 0) {
            node.left = insertRecord(node.left, key, value);
        } else if (cmp > 0) {
            node.right = insertRecord(node.right, key, value);
        } else {
            node.value = value;
        }
        return node;
    }

    @Override
    public V search(K key) {
        Node node = root;

        while (node != null) {
            int cmp = key.compareTo(node.key);
            if (cmp == 0) return node.value;
            node = cmp < 0 ? node.left : node.right;
        }
        return null;
    }

    @Override
    public int height() {
        return heightRecord(root);
    }

    private int heightRecord(Node node) {
        if (node == null) return 0;
        return 1 + Math.max(heightRecord(node.left), heightRecord(node.right));
    }

    @Override
    public int size() {
        return size;
    }

    /** Returns all values in ascending key order -- required evidence (Section 6). */
    public List<V> inorderTraversal() {
        List<V> result = new ArrayList<>();
        inorderRecord(root, result);
        return result;
    }

    private void inorderRecord(Node node, List<V> result) {
        if (node == null) return;
        inorderRecord(node.left, result);
        result.add(node.value);
        inorderRecord(node.right, result);
    }
}
