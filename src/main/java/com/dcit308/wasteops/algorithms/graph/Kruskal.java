package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.DisjointSetADT;
import com.dcit308.wasteops.structures.GraphADT;

import java.util.ArrayList;
import java.util.List;

/**
 * Minimum-spanning-tree algorithm (disjoint-set-based Kruskal's). Uses
 * Issue #10's DisjointSetADT and Issue #10/#11's GraphADT — code against
 * the interfaces, not concrete implementations.
 *
 * <p>Collects all edges, sorts them by weight (ascending), and greedily
 * adds each edge unless it would form a cycle (checked via DisjointSetADT).
 *
 * Owned by Issue #12.
 */
public class Kruskal {

    /**
     * Internal representation of an edge for sorting.
     */
    private static class Edge implements Comparable<Edge> {
        final String from;
        final String to;
        final double weight;

        Edge(String from, String to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }

        @Override
        public int compareTo(Edge other) {
            return Double.compare(this.weight, other.weight);
        }
    }

    /**
     * Computes the minimum spanning tree of the given graph using
     * Kruskal's algorithm.
     *
     * <p>If the graph is empty, returns an MST with no edges and cost 0.
     * For disconnected graphs, the result is a minimum spanning forest
     * (all components are spanned).
     *
     * @param graph       the road-network graph
     * @param disjointSet an empty DisjointSetADT to use internally
     * @return the MST as a list of edge descriptions and total cost
     *         (same {@link Prim.MstResult} type for interchangeability)
     */
    public Prim.MstResult minimumSpanningTree(GraphADT graph, DisjointSetADT disjointSet) {
        List<String> allVertices = graph.getAllVertices();

        if (allVertices.isEmpty()) {
            return new Prim.MstResult(new ArrayList<>(), 0.0);
        }

        // Step 1: Make a set for each vertex
        for (String v : allVertices) {
            disjointSet.makeSet(v);
        }

        // Step 2: Collect all edges (avoid duplicates for undirected graphs
        // by only taking the edge where from < to lexicographically)
        List<Edge> edges = new ArrayList<>();
        for (String u : allVertices) {
            for (String neighbour : graph.getNeighbors(u)) {
                // For undirected graphs, avoid adding both (u,v) and (v,u)
                if (u.compareTo(neighbour) < 0) {
                    double w = graph.getWeight(u, neighbour);
                    edges.add(new Edge(u, neighbour, w));
                }
            }
        }

        // Step 3: Sort edges by weight (ascending)
        java.util.Collections.sort(edges);

        // Step 4: Greedily add edges that don't form a cycle
        List<String> edgeDescriptions = new ArrayList<>();
        double totalCost = 0.0;

        for (Edge e : edges) {
            if (!disjointSet.connected(e.from, e.to)) {
                disjointSet.union(e.from, e.to);
                totalCost += e.weight;
                edgeDescriptions.add(e.from + "-" + e.to + ": " + e.weight);
            }
        }

        return new Prim.MstResult(edgeDescriptions, totalCost);
    }
}
