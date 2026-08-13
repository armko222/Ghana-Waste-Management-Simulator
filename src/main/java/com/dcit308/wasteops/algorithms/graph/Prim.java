package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import java.util.List;

/**
 * Minimum-spanning-tree algorithm (heap-based). Owned by Issue #12.
 */
public class Prim {

    /** Result of an MST computation. */
    public static class MstResult {
        public final List<String> edgeDescriptions; // e.g. "L001-L002: 4.2"
        public final double totalCost;

        public MstResult(List<String> edgeDescriptions, double totalCost) {
            this.edgeDescriptions = edgeDescriptions;
            this.totalCost = totalCost;
        }
    }

    public MstResult minimumSpanningTree(GraphADT graph, PriorityQueueADT<String> priorityQueue) {
        throw new UnsupportedOperationException("TODO: Issue #12 \u2014 implement Prim's algorithm.");
    }
}
