package com.dcit308.wasteops.structures;

import java.util.ArrayList;
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

    /** One edge out of a vertex. `next` threads a vertex's own edge list. */
    private static class Edge {
        final String toVertexId;
        double weight;
        Edge next;

        Edge(String toVertexId, double weight) {
            this.toVertexId = toVertexId;
            this.weight = weight;
        }
    }

    /**
     * One vertex and the head of its own edge list. `next` here has
     * nothing to do with edges -- it threads every Vertex together into
     * a separate list so findVertex() can look vertices up by id
     * without java.util.HashMap.
     */
    private static class Vertex {
        final String id;
        Edge edgesHead;
        Vertex next;

        Vertex(String id) {
            this.id = id;
        }
    }

    private Vertex verticesHead;
    private Vertex verticesTail;

    private Vertex findVertex(String vertexId) {
        Vertex current = verticesHead;
        while (current != null) {
            if (current.id.equals(vertexId)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    @Override
    public void addVertex(String vertexId) {
        if (vertexId == null) {
            throw new IllegalArgumentException("vertexId cannot be null");
        }
        if (findVertex(vertexId) != null) {
            return; // already present -- idempotent, same as DisjointSet.makeSet
        }
        Vertex v = new Vertex(vertexId);
        if (verticesHead == null) {
            verticesHead = v;
            verticesTail = v;
        } else {
            verticesTail.next = v;
            verticesTail = v;
        }
    }

    /**
     * Adds a directed edge fromVertexId -> toVertexId. This mirrors the
     * `roads` table directly (Road has a single fromLocationId/
     * toLocationId), so a two-way street must be loaded as two addEdge
     * calls, one in each direction. Calling this again for the same
     * (from, to) pair updates the existing edge's weight rather than
     * creating a duplicate.
     */
    @Override
    public void addEdge(String fromVertexId, String toVertexId, double weight) {
        Vertex from = findVertex(fromVertexId);
        Vertex to = findVertex(toVertexId);
        if (from == null) {
            throw new IllegalArgumentException("Unknown vertex (call addVertex first): " + fromVertexId);
        }
        if (to == null) {
            throw new IllegalArgumentException("Unknown vertex (call addVertex first): " + toVertexId);
        }

        Edge existing = from.edgesHead;
        while (existing != null) {
            if (existing.toVertexId.equals(toVertexId)) {
                existing.weight = weight;
                return;
            }
            existing = existing.next;
        }

        Edge edge = new Edge(toVertexId, weight);
        edge.next = from.edgesHead;
        from.edgesHead = edge;
    }

    @Override
    public List<String> getNeighbors(String vertexId) {
        Vertex v = findVertex(vertexId);
        if (v == null) {
            throw new IllegalArgumentException("Unknown vertex: " + vertexId);
        }
        List<String> neighbors = new ArrayList<>();
        Edge current = v.edgesHead;
        while (current != null) {
            neighbors.add(current.toVertexId);
            current = current.next;
        }
        return neighbors;
    }

    @Override
    public double getWeight(String fromVertexId, String toVertexId) {
        Vertex from = findVertex(fromVertexId);
        if (from == null) {
            throw new IllegalArgumentException("Unknown vertex: " + fromVertexId);
        }
        Edge current = from.edgesHead;
        while (current != null) {
            if (current.toVertexId.equals(toVertexId)) {
                return current.weight;
            }
            current = current.next;
        }
        throw new IllegalArgumentException(
                "No edge from " + fromVertexId + " to " + toVertexId);
    }

    @Override
    public List<String> getAllVertices() {
        List<String> ids = new ArrayList<>();
        Vertex current = verticesHead;
        while (current != null) {
            ids.add(current.id);
            current = current.next;
        }
        return ids;
    }
}