package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import java.util.List;

/**
 * Shortest-path algorithm. Uses Issue #5's PriorityQueueADT and Issue
 * #10/#11's GraphADT -- code against these interfaces, not against
 * BinaryHeap/GraphAdjacencyList directly, so you can start before either
 * merges (see Team_Handbook.docx, "Working With Each Other's Code").
 *
 * Owned by Issue #12.
 */
public class Dijkstra {

    /** Result of a shortest-path query. */
    public static class RouteResult {
        public final List<String> path;
        public final double totalWeight;
        public final boolean reachable;

        public RouteResult(List<String> path, double totalWeight, boolean reachable) {
            this.path = path;
            this.totalWeight = totalWeight;
            this.reachable = reachable;
        }
    }

    public RouteResult shortestPath(GraphADT graph, PriorityQueueADT<String> priorityQueue,
                                     String sourceVertexId, String destVertexId) {
        throw new UnsupportedOperationException(
            "TODO: Issue #12 \u2014 implement Dijkstra. If destVertexId is unreachable, " +
            "return a RouteResult with reachable = false, not an exception.");
    }
}
