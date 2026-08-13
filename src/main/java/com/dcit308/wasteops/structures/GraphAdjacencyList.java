package com.dcit308.wasteops.structures;

import java.util.List;

/**
 * Road network, adjacency-list representation. Built from Locations and
 * Roads (Issue #1).
 *
 * Owned by Issue #10. Implements GraphADT -- see GraphADT.java for who
 * depends on this without waiting for it. Must produce identical
 * neighbour/weight results to Issue #11's GraphAdjacencyMatrix for the
 * same data -- coordinate a cross-check test with them.
 */
public class GraphAdjacencyList implements GraphADT {

    @Override
    public void addVertex(String vertexId) {
        throw new UnsupportedOperationException("TODO: Issue #10 \u2014 implement addVertex.");
    }

    @Override
    public void addEdge(String fromVertexId, String toVertexId, double weight) {
        throw new UnsupportedOperationException("TODO: Issue #10 \u2014 implement addEdge.");
    }

    @Override
    public List<String> getNeighbors(String vertexId) {
        throw new UnsupportedOperationException("TODO: Issue #10 \u2014 implement getNeighbors.");
    }

    @Override
    public double getWeight(String fromVertexId, String toVertexId) {
        throw new UnsupportedOperationException("TODO: Issue #10 \u2014 implement getWeight. Must throw if no edge exists.");
    }

    @Override
    public List<String> getAllVertices() {
        throw new UnsupportedOperationException("TODO: Issue #10 \u2014 implement getAllVertices.");
    }
}
