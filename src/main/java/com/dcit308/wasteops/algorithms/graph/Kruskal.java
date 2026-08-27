package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.DisjointSetADT;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Minimum-spanning-tree algorithm (disjoint-set-based). Uses Issue #10's
 * DisjointSetADT -- code against the interface, not DisjointSet directly.
 * Owned by Issue #12.
 */
public class Kruskal {

    public Prim.MstResult minimumSpanningTree(GraphADT graph, DisjointSetADT disjointSet) {
        List<String> allVertices = graph.getAllVertices();

        if (allVertices.isEmpty()) {
            return new Prim.MstResult(new ArrayList<>(), 0.0);
        }

        // Initialize disjoint sets for all vertices
        for (String vertex : allVertices) {
            disjointSet.makeSet(vertex);
        }

        // Collect all edges
        List<Edge> edges = new ArrayList<>();
        for (String from : allVertices) {
            for (String to : graph.getNeighbors(from)) {
                double weight = graph.getWeight(from, to);
                edges.add(new Edge(from, to, weight));
            }
        }

        // Sort edges by weight
        edges.sort(Comparator.comparingDouble(e -> e.weight));

        // Kruskal's algorithm
        List<String> edgeDescriptions = new ArrayList<>();
        double totalCost = 0.0;
        int edgesAdded = 0;

        for (Edge edge : edges) {
            String rootFrom = disjointSet.find(edge.from);
            String rootTo = disjointSet.find(edge.to);

            if (!rootFrom.equals(rootTo)) {
                // Add edge to MST
                disjointSet.union(edge.from, edge.to);
                edgeDescriptions.add(edge.from + "-" + edge.to + ": " + edge.weight);
                totalCost += edge.weight;
                edgesAdded++;

                if (edgesAdded == allVertices.size() - 1) {
                    break; // MST complete
                }
            }
        }

        return new Prim.MstResult(edgeDescriptions, totalCost);
    }

    private static class Edge {
        final String from;
        final String to;
        final double weight;

        Edge(String from, String to, double weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
}
