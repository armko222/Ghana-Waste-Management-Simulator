package com.dcit308.wasteops.structures;

import java.util.List;

/**
 * Road network, adjacency-matrix representation -- the same underlying
 * network as Issue #10's GraphAdjacencyList, stored differently. Must
 * produce identical neighbour/weight results for the same queries --
 * coordinate a cross-check test with Issue #10.
 *
 * Owned by Issue #11. Implements GraphADT -- see GraphADT.java for who
 * depends on this without waiting for it.
 */
public class GraphAdjacencyMatrix implements GraphADT {

    @Override
    public void addVertex(String vertexId) {
        throw new UnsupportedOperationException("TODO: Issue #11 \u2014 implement addVertex.");
    }

    @Override
    public void addEdge(String fromVertexId, String toVertexId, double weight) {
        throw new UnsupportedOperationException("TODO: Issue #11 \u2014 implement addEdge.");
    }

    @Override
    public List<String> getNeighbors(String vertexId) {
        throw new UnsupportedOperationException("TODO: Issue #11 \u2014 implement getNeighbors.");
    }

    @Override
    public double getWeight(String fromVertexId, String toVertexId) {
        throw new UnsupportedOperationException("TODO: Issue #11 \u2014 implement getWeight. Must throw if no edge exists.");
    }

    @Override
    public List<String> getAllVertices() {
        throw new UnsupportedOperationException("TODO: Issue #11 \u2014 implement getAllVertices.");
    }
}
