package com.dcit308.wasteops.structures.testutil;

import com.dcit308.wasteops.structures.GraphADT;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * A deliberately simple, HashMap-backed GraphADT implementation for tests.
 *
 * This exists ONLY so that Issue #12 (Dijkstra, Prim, Kruskal) can write
 * and run real, passing tests before Issue #10's actual GraphAdjacencyList
 * exists. Same stand-in pattern as NaivePriorityQueue — see
 * Team_Handbook.docx, "Working With Each Other's Code."
 *
 * NOT for production use.
 */
public class SimpleGraph implements GraphADT {

    // vertexId -> { neighbourId -> weight }
    private final Map<String, Map<String, Double>> adjacency = new HashMap<>();

    @Override
    public void addVertex(String vertexId) {
        adjacency.putIfAbsent(vertexId, new HashMap<>());
    }

    @Override
    public void addEdge(String fromVertexId, String toVertexId, double weight) {
        addVertex(fromVertexId);
        addVertex(toVertexId);
        adjacency.get(fromVertexId).put(toVertexId, weight);
        // Undirected: add reverse edge as well (roads are two-way)
        adjacency.get(toVertexId).put(fromVertexId, weight);
    }

    @Override
    public List<String> getNeighbors(String vertexId) {
        Map<String, Double> neighbours = adjacency.get(vertexId);
        if (neighbours == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(neighbours.keySet());
    }

    @Override
    public double getWeight(String fromVertexId, String toVertexId) {
        Map<String, Double> neighbours = adjacency.get(fromVertexId);
        if (neighbours == null || !neighbours.containsKey(toVertexId)) {
            throw new NoSuchElementException(
                "No edge from " + fromVertexId + " to " + toVertexId);
        }
        return neighbours.get(toVertexId);
    }

    @Override
    public List<String> getAllVertices() {
        return new ArrayList<>(adjacency.keySet());
    }
}
