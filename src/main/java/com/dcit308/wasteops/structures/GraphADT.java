package com.dcit308.wasteops.structures;

import java.util.List;

/**
 * Contract for the road-network graph, satisfied by BOTH required
 * representations (brief Section 6 asks for adjacency list AND matrix).
 *
 * OWNER (implements this, twice): Issue #10 (GraphAdjacencyList) and
 * Issue #11 (GraphAdjacencyMatrix).
 * CONSUMER (codes against this, does not wait for either implementation):
 *   Issue #12 — Dijkstra, Prim, Kruskal only need to ask "what are this
 *   vertex's neighbours and at what weight" — they don't care which
 *   representation is underneath.
 *
 * Collaboration pattern: Issue #12 can write and unit-test Dijkstra today
 * against a tiny hand-built test graph implementing this interface,
 * instead of waiting for Issue #10/#11's real graph classes. Once either
 * merges, Issue #12 swaps in the real GraphAdjacencyList (or matrix) —
 * no changes to Dijkstra/Prim/Kruskal's own code.
 *
 * See Team_Handbook.docx, "Working With Each Other's Code."
 */
public interface GraphADT {

    void addVertex(String vertexId);

    void addEdge(String fromVertexId, String toVertexId, double weight);

    /** All vertex IDs directly reachable from the given vertex. */
    List<String> getNeighbors(String vertexId);

    /** Edge weight between two directly-connected vertices. Throws if no edge exists. */
    double getWeight(String fromVertexId, String toVertexId);

    List<String> getAllVertices();
}
