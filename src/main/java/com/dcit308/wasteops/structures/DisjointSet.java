package com.dcit308.wasteops.structures;

/**
 * Union-find with path compression and union by rank/size. Feeds
 * Kruskal's algorithm (Issue #12).
 *
 * Owned by Issue #10. Implements DisjointSetADT -- see
 * DisjointSetADT.java for who depends on this without waiting for it.
 */
public class DisjointSet implements DisjointSetADT {

    @Override
    public void makeSet(String element) {
        throw new UnsupportedOperationException("TODO: Issue #10 \u2014 implement makeSet.");
    }

    @Override
    public String find(String element) {
        throw new UnsupportedOperationException("TODO: Issue #10 \u2014 implement find, with path compression.");
    }

    @Override
    public void union(String a, String b) {
        throw new UnsupportedOperationException("TODO: Issue #10 \u2014 implement union, by rank or size.");
    }
}
