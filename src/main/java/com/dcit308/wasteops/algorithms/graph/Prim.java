package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.PriorityQueueADT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Minimum-spanning-tree algorithm (heap-based Prim's). Uses Issue #5's
 * PriorityQueueADT and Issue #10/#11's GraphADT — code against the
 * interfaces, not concrete implementations.
 *
 * <p>Starts from an arbitrary vertex (the first in
 * {@link GraphADT#getAllVertices()}) and greedily adds the cheapest
 * edge that connects a new vertex to the growing MST.
 *
 * <p>The PriorityQueueADT uses {@code int} priorities, so double weights
 * are scaled by 1000 and cast to int (same convention as Dijkstra).
 *
 * Owned by Issue #12.
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

    /**
     * Computes the minimum spanning tree of the given graph.
     *
     * <p>If the graph is empty, returns an MST with no edges and cost 0.
     * If the graph is disconnected, the MST covers only the component
     * reachable from the starting vertex.
     *
     * @param graph         the road-network graph
     * @param priorityQueue an empty PriorityQueueADT to use internally
     * @return the MST as a list of edge descriptions and total cost
     */
    public MstResult minimumSpanningTree(GraphADT graph, PriorityQueueADT<String> priorityQueue) {
        List<String> allVertices = graph.getAllVertices();

        if (allVertices.isEmpty()) {
            return new MstResult(new ArrayList<>(), 0.0);
        }

        List<String> edgeDescriptions = new ArrayList<>();
        double totalCost = 0.0;

        // Track which vertices are already in the MST
        Set<String> inMst = new HashSet<>();
        // For each vertex not yet in MST, track which MST vertex connects
        // to it with the cheapest edge, and at what cost.
        // We encode this as "fromVertex:toVertex" in the PQ value, with
        // weight as priority. But PQ stores String, so we use a helper
        // approach: store "from|to" as the PQ value, and track best-known
        // cost separately.

        // Actually, Prim's with this PQ interface is simpler if we track
        // cheapest[v] = best edge weight to connect v to the MST, and
        // cheapestFrom[v] = which MST vertex that edge comes from.
        // We re-insert into the PQ whenever we find a cheaper connection.

        java.util.Map<String, Double> cheapest = new java.util.HashMap<>();
        java.util.Map<String, String> cheapestFrom = new java.util.HashMap<>();

        // Start from the first vertex
        String start = allVertices.get(0);
        inMst.add(start);

        // Add all neighbours of start to the PQ
        for (String neighbour : graph.getNeighbors(start)) {
            double w = graph.getWeight(start, neighbour);
            cheapest.put(neighbour, w);
            cheapestFrom.put(neighbour, start);
            priorityQueue.insert((int) (w * 1000), neighbour);
        }

        while (!priorityQueue.isEmpty()) {
            String v = priorityQueue.extractMin();

            // Skip if already in MST (stale PQ entry)
            if (inMst.contains(v)) {
                continue;
            }

            inMst.add(v);

            // Record the edge that brought v into the MST
            String from = cheapestFrom.get(v);
            double cost = cheapest.get(v);
            totalCost += cost;
            edgeDescriptions.add(from + "-" + v + ": " + cost);

            // Update neighbours of v
            for (String neighbour : graph.getNeighbors(v)) {
                if (inMst.contains(neighbour)) {
                    continue;
                }
                double w = graph.getWeight(v, neighbour);
                if (w < cheapest.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    cheapest.put(neighbour, w);
                    cheapestFrom.put(neighbour, v);
                    priorityQueue.insert((int) (w * 1000), neighbour);
                }
            }
        }

        return new MstResult(edgeDescriptions, totalCost);
    }
}
