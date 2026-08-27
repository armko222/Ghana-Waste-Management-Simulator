package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // Validate inputs
        if (graph == null || priorityQueue == null || sourceVertexId == null || destVertexId == null) {
            throw new IllegalArgumentException("Graph, priorityQueue, source, and destination cannot be null");
        }

        List<String> allVertices = graph.getAllVertices();
        if (!allVertices.contains(sourceVertexId) || !allVertices.contains(destVertexId)) {
            return new RouteResult(new ArrayList<>(), 0.0, false);
        }

        // Maps to store distances and predecessors
        Map<String, Double> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();

        // Initialize distances
        for (String vertex : allVertices) {
            distances.put(vertex, Double.POSITIVE_INFINITY);
        }
        distances.put(sourceVertexId, 0.0);

        // Insert source into priority queue with distance 0
        priorityQueue.insert(0, sourceVertexId);

        // Track visited vertices
        boolean[] visited = new boolean[allVertices.size()];
        Map<String, Integer> vertexToIndex = new HashMap<>();
        for (int i = 0; i < allVertices.size(); i++) {
            vertexToIndex.put(allVertices.get(i), i);
        }

        while (!priorityQueue.isEmpty()) {
            String currentVertex = priorityQueue.extractMin();
            int currentIndex = vertexToIndex.get(currentVertex);

            // Skip if already visited
            if (visited[currentIndex]) {
                continue;
            }
            visited[currentIndex] = true;

            // Early exit if we reached destination
            if (currentVertex.equals(destVertexId)) {
                break;
            }

            // Relax edges
            double currentDistance = distances.get(currentVertex);
            List<String> neighbors = graph.getNeighbors(currentVertex);

            for (String neighbor : neighbors) {
                if (visited[vertexToIndex.get(neighbor)]) {
                    continue;
                }

                double edgeWeight = graph.getWeight(currentVertex, neighbor);
                double newDistance = currentDistance + edgeWeight;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    predecessors.put(neighbor, currentVertex);
                    priorityQueue.insert((int) (newDistance * 1000), neighbor); // Scale for integer priority
                }
            }
        }

        // Check if destination is reachable
        if (distances.get(destVertexId) == Double.POSITIVE_INFINITY) {
            return new RouteResult(new ArrayList<>(), 0.0, false);
        }

        // Reconstruct path
        List<String> path = new ArrayList<>();
        String current = destVertexId;
        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
        }

        return new RouteResult(path, distances.get(destVertexId), true);
    }
}
