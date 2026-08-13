package com.dcit308.wasteops.structures;

/**
 * Contract for a disjoint-set (union-find) structure.
 *
 * OWNER (implements this): Issue #10 — DisjointSet.
 * CONSUMER (codes against this): Issue #12 — Kruskal's algorithm needs to
 * repeatedly check "are these two vertices already connected?" while
 * building the minimum spanning tree.
 *
 * Same collaboration pattern as PriorityQueueADT and GraphADT — Issue #12
 * can write Kruskal against this interface and a trivial stand-in before
 * Issue #10's real implementation exists. See Team_Handbook.docx,
 * "Working With Each Other's Code."
 */
public interface DisjointSetADT {

    void makeSet(String element);

    /** Returns the representative (root) of the set containing element. */
    String find(String element);

    /** Merges the sets containing a and b. No-op if already in the same set. */
    void union(String a, String b);

    /** Convenience: true if a and b are already in the same set. */
    default boolean connected(String a, String b) {
        return find(a).equals(find(b));
    }
}
