package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import com.dcit308.wasteops.util.IndexParameterDeriver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Shortest-path algorithm. Uses Issue #5's PriorityQueueADT and Issue
 * #10/#11's GraphADT -- code against these interfaces, not against
 * BinaryHeap/GraphAdjacencyList directly, so you can start before either
 * merges (see Team_Handbook.docx, "Working With Each Other's Code").
 *
 * Applies the index-number-derived route-penalty parameter (from
 * {@link IndexParameterDeriver#deriveRoutePenalty()}) to edge weights.
 * Falls back to a 1.0 multiplier if index numbers have not yet been
 * configured by the team.
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

    /**
     * Computes the shortest path from {@code sourceVertexId} to
     * {@code destVertexId} using Dijkstra's algorithm.
     *
     * <p>Edge weights are multiplied by the index-number-derived route
     * penalty (see {@link IndexParameterDeriver}). If that parameter is
     * not yet available (index numbers empty), a penalty of 1.0 is used.
     *
     * <p>The PriorityQueueADT uses {@code int} priorities, so double
     * weights are scaled by 1000 and cast to int. This gives
     * sub-millisecond precision which is more than sufficient for the
     * travel-time domain.
     *
     * @param graph            the road-network graph
     * @param priorityQueue    an empty PriorityQueueADT to use internally
     * @param sourceVertexId   starting vertex
     * @param destVertexId     target vertex
     * @return a RouteResult; if destVertexId is unreachable,
     *         {@code reachable} is false and path is empty
     */
    public RouteResult shortestPath(GraphADT graph, PriorityQueueADT<String> priorityQueue,
                                     String sourceVertexId, String destVertexId) {

        double routePenalty = resolveRoutePenalty();

        // dist[v] = best known distance from source to v
        Map<String, Double> dist = new HashMap<>();
        // prev[v] = predecessor of v on the shortest path
        Map<String, String> prev = new HashMap<>();
        // finalized vertices (already extracted from PQ)
        Set<String> visited = new HashSet<>();

        // Initialise source distance
        dist.put(sourceVertexId, 0.0);
        priorityQueue.insert(0, sourceVertexId);

        while (!priorityQueue.isEmpty()) {
            String u = priorityQueue.extractMin();

            // Skip if already finalized (PQ may contain stale entries)
            if (visited.contains(u)) {
                continue;
            }
            visited.add(u);

            // Early exit if we've finalized the destination
            if (u.equals(destVertexId)) {
                break;
            }

            double distU = dist.getOrDefault(u, Double.MAX_VALUE);

            for (String neighbour : graph.getNeighbors(u)) {
                if (visited.contains(neighbour)) {
                    continue;
                }

                // Apply route penalty to the raw edge weight
                double edgeWeight = graph.getWeight(u, neighbour) * routePenalty;
                double newDist = distU + edgeWeight;

                if (newDist < dist.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    dist.put(neighbour, newDist);
                    prev.put(neighbour, u);
                    // Scale double to int for the PQ (×1000 for precision)
                    priorityQueue.insert((int) (newDist * 1000), neighbour);
                }
            }
        }

        // Reconstruct path
        if (!dist.containsKey(destVertexId)) {
            // Destination was never reached
            return new RouteResult(Collections.emptyList(), Double.MAX_VALUE, false);
        }

        List<String> path = new ArrayList<>();
        String current = destVertexId;
        while (current != null) {
            path.add(current);
            current = prev.get(current);
        }
        Collections.reverse(path);

        return new RouteResult(path, dist.get(destVertexId), true);
    }

    /**
     * Resolves the route penalty from IndexParameterDeriver. Falls back
     * to 1.0 if the team's index numbers have not been populated yet.
     */
    private double resolveRoutePenalty() {
        try {
            return IndexParameterDeriver.deriveRoutePenalty();
        } catch (IllegalStateException e) {
            // Index numbers not yet configured — use no penalty
            return 1.0;
        }
    }
}
