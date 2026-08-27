package com.dcit308.wasteops.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * B-tree with real node splitting (not the lighter "index-page
 * simulation" the brief also allows -- this is the actual structure).
 * Each node holds up to {@code 2 * minDegree - 1} keys, mirroring how a
 * database index page holds several keys per disk block rather than one
 * key per node the way a binary tree does; that is what keeps the tree
 * shallow as it grows. Backs indexing/ResourceIndex.java.
 *
 * <p>Duplicate-key policy: inserting a key that is already present
 * overwrites its value in place and does not change {@link #size()} --
 * same "insert = upsert" convention used elsewhere in this codebase
 * (see BinarySearchTree's documented policy).
 *
 * Owned by Issue #8.
 */
public class BTree<K extends Comparable<K>, V> {

    /** Default minimum degree. Max keys/node = 2t-1 = 5, min (non-root) = t-1 = 2. */
    private static final int DEFAULT_MIN_DEGREE = 3;

    private final int t; // minimum degree
    private Node<K, V> root;
    private int size;

    public BTree() {
        this(DEFAULT_MIN_DEGREE);
    }

    /**
     * @param minDegree minimum degree t (t &gt;= 2). A node holds between
     *                  t-1 and 2t-1 keys (root may hold fewer than t-1).
     *                  Smaller t splits more often -- useful for
     *                  demonstrating splitting behaviour with small
     *                  datasets (see BTreeTest.NodeSplit, which uses t=2).
     */
    public BTree(int minDegree) {
        if (minDegree < 2) {
            throw new IllegalArgumentException("minDegree must be >= 2, was " + minDegree);
        }
        this.t = minDegree;
        this.root = new Node<>(true);
        this.size = 0;
    }

    public void insert(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("key cannot be null");
        }
        Node<K, V> r = root;
        if (isFull(r)) {
            Node<K, V> newRoot = new Node<>(false);
            newRoot.children.add(r);
            splitChild(newRoot, 0);
            root = newRoot;
            insertNonFull(root, key, value);
        } else {
            insertNonFull(r, key, value);
        }
    }

    public V search(K key) {
        if (key == null) {
            return null;
        }
        return searchNode(root, key);
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** Minimum degree this tree was built with -- exposed for trace/evidence output. */
    public int minDegree() {
        return t;
    }

    // ------------------------------------------------------------------
    // Internals
    // ------------------------------------------------------------------

    private V searchNode(Node<K, V> node, K key) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
            i++;
        }
        if (i < node.keys.size() && key.compareTo(node.keys.get(i)) == 0) {
            return node.values.get(i);
        }
        if (node.leaf) {
            return null;
        }
        return searchNode(node.children.get(i), key);
    }

    private boolean isFull(Node<K, V> node) {
        return node.keys.size() == 2 * t - 1;
    }

    /**
     * Inserts (key, value) into the subtree rooted at node, which must
     * not be full when this is called.
     */
    private void insertNonFull(Node<K, V> node, K key, V value) {
        int i = 0;
        while (i < node.keys.size() && key.compareTo(node.keys.get(i)) > 0) {
            i++;
        }
        if (i < node.keys.size() && key.compareTo(node.keys.get(i)) == 0) {
            node.values.set(i, value); // upsert: key already present
            return;
        }

        if (node.leaf) {
            node.keys.add(i, key);
            node.values.add(i, value);
            size++;
            return;
        }

        if (isFull(node.children.get(i))) {
            splitChild(node, i);
            if (key.compareTo(node.keys.get(i)) == 0) {
                node.values.set(i, value); // the key that moved up was the one we're inserting
                return;
            }
            if (key.compareTo(node.keys.get(i)) > 0) {
                i++;
            }
        }
        insertNonFull(node.children.get(i), key, value);
    }

    /**
     * Splits the full child at parent.children[index] into two nodes,
     * pushing the median key/value up into parent at position index.
     * This is the core B-tree operation: it is what keeps every leaf at
     * the same depth as the tree grows, instead of letting one branch
     * run away the way an unbalanced BST can.
     */
    private void splitChild(Node<K, V> parent, int index) {
        Node<K, V> child = parent.children.get(index);
        int mid = t - 1; // median index within a full (2t-1)-key node

        Node<K, V> sibling = new Node<>(child.leaf);
        for (int j = mid + 1; j < child.keys.size(); j++) {
            sibling.keys.add(child.keys.get(j));
            sibling.values.add(child.values.get(j));
        }
        if (!child.leaf) {
            for (int j = mid + 1; j < child.children.size(); j++) {
                sibling.children.add(child.children.get(j));
            }
            trim(child.children, mid + 1);
        }

        K medianKey = child.keys.get(mid);
        V medianValue = child.values.get(mid);
        trim(child.keys, mid);
        trim(child.values, mid);

        parent.keys.add(index, medianKey);
        parent.values.add(index, medianValue);
        parent.children.add(index + 1, sibling);
    }

    private static <E> void trim(List<E> list, int fromIndexInclusive) {
        while (list.size() > fromIndexInclusive) {
            list.remove(list.size() - 1);
        }
    }

    private static final class Node<K, V> {
        final List<K> keys = new ArrayList<>();
        final List<V> values = new ArrayList<>();
        final List<Node<K, V>> children = new ArrayList<>();
        final boolean leaf;

        Node(boolean leaf) {
            this.leaf = leaf;
        }
    }
}
