package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        List<String> allVertices = graph.getAllVertices();

        if (allVertices.isEmpty()) {
            return new MstResult(new ArrayList<>(), 0.0);
        }

        // Track which vertices are in MST
        boolean[] inMST = new boolean[allVertices.size()];
        Map<String, Integer> vertexToIndex = new HashMap<>();
        for (int i = 0; i < allVertices.size(); i++) {
            vertexToIndex.put(allVertices.get(i), i);
        }

        // Start from first vertex
        int startIndex = 0;
        inMST[startIndex] = true;

        // Add all edges from start vertex to priority queue
        String startVertex = allVertices.get(startIndex);
        for (String neighbor : graph.getNeighbors(startVertex)) {
            double weight = graph.getWeight(startVertex, neighbor);
            priorityQueue.insert((int) (weight * 1000), startVertex + "|" + neighbor);
        }

        List<String> edgeDescriptions = new ArrayList<>();
        double totalCost = 0.0;
        int edgesAdded = 0;

        while (!priorityQueue.isEmpty() && edgesAdded < allVertices.size() - 1) {
            String edgeKey = priorityQueue.extractMin();
            String[] parts = edgeKey.split("\\|");
            String from = parts[0];
            String to = parts[1];

            int toIndex = vertexToIndex.get(to);

            // Skip if destination already in MST
            if (inMST[toIndex]) {
                continue;
            }

            // Add edge to MST
            double weight = graph.getWeight(from, to);
            edgeDescriptions.add(from + "-" + to + ": " + weight);
            totalCost += weight;
            edgesAdded++;

            // Mark vertex as in MST
            inMST[toIndex] = true;

            // Add new edges from this vertex
            for (String neighbor : graph.getNeighbors(to)) {
                int neighborIndex = vertexToIndex.get(neighbor);
                if (!inMST[neighborIndex]) {
                    double edgeWeight = graph.getWeight(to, neighbor);
                    priorityQueue.insert((int) (edgeWeight * 1000), to + "|" + neighbor);
                }
            }
        }

        return new MstResult(edgeDescriptions, totalCost);
    }
}
