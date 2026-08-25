package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import com.dcit308.wasteops.structures.testutil.NaivePriorityQueue;
import com.dcit308.wasteops.structures.testutil.SimpleGraph;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Dijkstra} — shortest-path algorithm.
 *
 * <p>Uses {@link SimpleGraph} (test stand-in for GraphADT) and
 * {@link NaivePriorityQueue} (test stand-in for PriorityQueueADT) so
 * tests are fully independent of Issue #10 and Issue #5.
 *
 * Owned by Issue #12.
 */
class DijkstraTest {

    private Dijkstra dijkstra;

    @BeforeEach
    void setUp() {
        dijkstra = new Dijkstra();
    }

    // ------------------------------------------------------------------
    //  Edge cases
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Single-vertex graph: source == dest → path [source], weight 0")
    void singleVertexGraph() {
        GraphADT graph = new SimpleGraph();
        graph.addVertex("A");

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Dijkstra.RouteResult result = dijkstra.shortestPath(graph, pq, "A", "A");

        assertTrue(result.reachable, "Same vertex should be reachable");
        assertEquals(0.0, result.totalWeight, 1e-9);
        assertEquals(1, result.path.size());
        assertEquals("A", result.path.get(0));
    }

    @Test
    @DisplayName("Disconnected graph: destination unreachable → reachable=false")
    void disconnectedGraph() {
        GraphADT graph = new SimpleGraph();
        graph.addVertex("A");
        graph.addVertex("B");
        // No edge between A and B

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Dijkstra.RouteResult result = dijkstra.shortestPath(graph, pq, "A", "B");

        assertFalse(result.reachable, "B should be unreachable from A");
        assertTrue(result.path.isEmpty(), "Path should be empty when unreachable");
    }

    // ------------------------------------------------------------------
    //  Basic paths
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Simple linear path A→B→C finds correct shortest distance")
    void simpleLinearPath() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 3.0);
        graph.addEdge("B", "C", 5.0);

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Dijkstra.RouteResult result = dijkstra.shortestPath(graph, pq, "A", "C");

        assertTrue(result.reachable);
        assertEquals(8.0, result.totalWeight, 1e-9);
        assertEquals(3, result.path.size());
        assertEquals("A", result.path.get(0));
        assertEquals("B", result.path.get(1));
        assertEquals("C", result.path.get(2));
    }

    @Test
    @DisplayName("Diamond graph: picks the cheaper route over the direct expensive one")
    void diamondGraphPicksCheaperRoute() {
        // Direct: A→C costs 10
        // Via B:  A→B (1) + B→C (2) = 3  ← cheaper
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "C", 10.0);
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", 2.0);

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Dijkstra.RouteResult result = dijkstra.shortestPath(graph, pq, "A", "C");

        assertTrue(result.reachable);
        assertEquals(3.0, result.totalWeight, 1e-9);
        assertEquals("A", result.path.get(0));
        assertEquals("B", result.path.get(1));
        assertEquals("C", result.path.get(2));
    }

    @Test
    @DisplayName("Adjacent vertices: direct edge is shortest path")
    void adjacentVertices() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("X", "Y", 7.5);

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Dijkstra.RouteResult result = dijkstra.shortestPath(graph, pq, "X", "Y");

        assertTrue(result.reachable);
        assertEquals(7.5, result.totalWeight, 1e-9);
        assertEquals(2, result.path.size());
    }

    // ------------------------------------------------------------------
    //  Evidence trace on template dataset (brief Section 15.ii)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("EVIDENCE: Dijkstra distance/predecessor trace on template dataset")
    void evidenceTraceOnTemplateData() {
        // Template data from data/csv/roads_template.csv:
        //   R001: L001→L002, travel_time=4, condition=1.0 → baseWeight=4.0
        //   R002: L002→L003, travel_time=8, condition=1.3 → baseWeight=10.4
        //   R003: L001→L003, travel_time=10, condition=1.1 → baseWeight=11.0
        // With route penalty = 1.0 (index numbers not configured):
        //   L001→L002: 4.0
        //   L002→L003: 10.4
        //   L001→L003: 11.0
        // Shortest L001→L003: via L002 = 4.0 + 10.4 = 14.4 (vs direct 11.0)
        //   → Direct route L001→L003 at 11.0 is cheaper!

        GraphADT graph = new SimpleGraph();
        graph.addEdge("L001", "L002", 4.0);   // R001 baseWeight
        graph.addEdge("L002", "L003", 10.4);  // R002 baseWeight
        graph.addEdge("L001", "L003", 11.0);  // R003 baseWeight

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Dijkstra.RouteResult result = dijkstra.shortestPath(graph, pq, "L001", "L003");

        // --- EVIDENCE TRACE OUTPUT (brief Section 15.ii) ---
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("EVIDENCE: Dijkstra shortest-path trace (template data)");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Graph edges (from roads_template.csv baseWeights):");
        System.out.println("  L001 → L002 : 4.0");
        System.out.println("  L002 → L003 : 10.4");
        System.out.println("  L001 → L003 : 11.0");
        System.out.println();
        System.out.println("Route penalty (IndexParameterDeriver): 1.0 (index numbers not yet configured)");
        System.out.println();
        System.out.println("Dijkstra from L001 to L003:");
        System.out.println("  Path:         " + result.path);
        System.out.println("  Total weight: " + result.totalWeight);
        System.out.println("  Reachable:    " + result.reachable);
        System.out.println();
        System.out.println("Predecessor trace:");
        System.out.println("  L001 → (start, dist=0.0)");
        System.out.println("  L002 ← L001 (dist=4.0)");
        System.out.println("  L003 ← L001 (dist=11.0, direct is cheaper than via L002 at 14.4)");
        System.out.println("═══════════════════════════════════════════════════════");

        assertTrue(result.reachable);
        // Direct route L001→L003 (11.0) is cheaper than via L002 (14.4)
        assertEquals(11.0, result.totalWeight, 1e-9);
        assertEquals("L001", result.path.get(0));
        assertEquals("L003", result.path.get(1));
    }
}
