package com.dcit308.wasteops.structures;

/**
 * Union-find with path compression and union by rank/size. Feeds
 * Kruskal's algorithm (Issue #12).
 *
 * Owned by Issue #10. Implements DisjointSetADT -- see
 * DisjointSetADT.java for who depends on this without waiting for it.
 */
public class DisjointSet implements DisjointSetADT {

    /**
     * One tracked element. `parent` points at another DSNode to form the
     * union-find tree (a node whose parent is itself is a root). `next`
     * has nothing to do with set membership -- it only threads every
     * DSNode together into a singly linked list so findNode() can look
     * elements up by id without java.util.HashMap.
     */
    private static class DSNode {
        final String id;
        DSNode parent;
        int rank;
        DSNode next;

        DSNode(String id) {
            this.id = id;
            this.parent = this;
            this.rank = 0;
        }
    }

    private DSNode entriesHead;
    private DSNode entriesTail;

    private DSNode findNode(String element) {
        DSNode current = entriesHead;
        while (current != null) {
            if (current.id.equals(element)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public void makeSet(String element) {
        if (element == null) {
            throw new IllegalArgumentException("element cannot be null");
        }
        if (findNode(element) != null) {
            return; // already its own set -- idempotent
        }
        DSNode node = new DSNode(element);
        if (entriesHead == null) {
            entriesHead = node;
            entriesTail = node;
        } else {
            entriesTail.next = node;
            entriesTail = node;
        }
    }

    /**
     * Walks up to the root, then makes a second pass repointing every
     * node visited straight at that root (path compression).
     */
    private DSNode findRoot(DSNode node) {
        DSNode root = node;
        while (root.parent != root) {
            root = root.parent;
        }
        DSNode current = node;
        while (current.parent != root) {
            DSNode next = current.parent;
            current.parent = root;
            current = next;
        }
        return root;
    }

    @Override
    public String find(String element) {
        DSNode node = findNode(element);
        if (node == null) {
            throw new IllegalArgumentException(
                    "Unknown element (call makeSet first): " + element);
        }
        return findRoot(node).id;
    }

    @Override
    public void union(String a, String b) {
        DSNode nodeA = findNode(a);
        DSNode nodeB = findNode(b);
        if (nodeA == null) {
            throw new IllegalArgumentException("Unknown element (call makeSet first): " + a);
        }
        if (nodeB == null) {
            throw new IllegalArgumentException("Unknown element (call makeSet first): " + b);
        }

        DSNode rootA = findRoot(nodeA);
        DSNode rootB = findRoot(nodeB);
        if (rootA == rootB) {
            return; // already connected -- no-op
        }

        // Union by rank: attach the shorter tree under the taller one to
        // keep future findRoot() walks short.
        if (rootA.rank < rootB.rank) {
            rootA.parent = rootB;
        } else if (rootA.rank > rootB.rank) {
            rootB.parent = rootA;
        } else {
            rootB.parent = rootA;
            rootA.rank++;
        }
    }
}