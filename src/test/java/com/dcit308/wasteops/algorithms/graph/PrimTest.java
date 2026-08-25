package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import com.dcit308.wasteops.structures.testutil.NaivePriorityQueue;
import com.dcit308.wasteops.structures.testutil.SimpleGraph;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Prim} — minimum spanning tree (heap-based).
 *
 * <p>Uses {@link SimpleGraph} and {@link NaivePriorityQueue} as test
 * stand-ins so tests are independent of Issue #10 and Issue #5.
 *
 * Owned by Issue #12.
 */
class PrimTest {

    private Prim prim;

    @BeforeEach
    void setUp() {
        prim = new Prim();
    }

    // ------------------------------------------------------------------
    //  Edge cases
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Empty graph → empty MST with cost 0")
    void emptyGraph() {
        GraphADT graph = new SimpleGraph();

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Prim.MstResult result = prim.minimumSpanningTree(graph, pq);

        assertEquals(0.0, result.totalCost, 1e-9);
        assertTrue(result.edgeDescriptions.isEmpty());
    }

    @Test
    @DisplayName("Single vertex → empty MST with cost 0")
    void singleVertex() {
        GraphADT graph = new SimpleGraph();
        graph.addVertex("A");

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Prim.MstResult result = prim.minimumSpanningTree(graph, pq);

        assertEquals(0.0, result.totalCost, 1e-9);
        assertTrue(result.edgeDescriptions.isEmpty());
    }

    // ------------------------------------------------------------------
    //  Standard cases
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Triangle graph: MST picks the two cheapest edges")
    void triangleGraph() {
        // Edges: A-B: 1, B-C: 2, A-C: 3
        // MST should pick A-B (1) and B-C (2), total = 3
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", 2.0);
        graph.addEdge("A", "C", 3.0);

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Prim.MstResult result = prim.minimumSpanningTree(graph, pq);

        assertEquals(3.0, result.totalCost, 1e-9);
        assertEquals(2, result.edgeDescriptions.size(), "MST of 3 vertices has 2 edges");
    }

    @Test
    @DisplayName("Already-minimal connected graph (tree): MST == the graph itself")
    void alreadyMinimalTree() {
        // A linear chain: A-B-C-D with weights 2, 3, 5
        // This is already a tree — MST cost = 2 + 3 + 5 = 10
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 2.0);
        graph.addEdge("B", "C", 3.0);
        graph.addEdge("C", "D", 5.0);

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Prim.MstResult result = prim.minimumSpanningTree(graph, pq);

        assertEquals(10.0, result.totalCost, 1e-9);
        assertEquals(3, result.edgeDescriptions.size(), "MST of 4-vertex tree has 3 edges");
    }

    @Test
    @DisplayName("Four vertices with redundant edges: MST excludes expensive edges")
    void fourVerticesWithRedundantEdges() {
        // A-B: 1, A-C: 4, B-C: 2, B-D: 5, C-D: 3
        // MST: A-B (1), B-C (2), C-D (3) = total 6
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 4.0);
        graph.addEdge("B", "C", 2.0);
        graph.addEdge("B", "D", 5.0);
        graph.addEdge("C", "D", 3.0);

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Prim.MstResult result = prim.minimumSpanningTree(graph, pq);

        assertEquals(6.0, result.totalCost, 1e-9);
        assertEquals(3, result.edgeDescriptions.size());
    }

    // ------------------------------------------------------------------
    //  Evidence trace (brief Section 15.ii)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("EVIDENCE: Prim MST trace with running cost on template dataset")
    void evidenceMstTrace() {
        // Template data edges (baseWeights):
        //   L001-L002: 4.0
        //   L002-L003: 10.4
        //   L001-L003: 11.0
        // MST: L001-L002 (4.0) + L002-L003 (10.4) = 14.4
        //   (L001-L003 at 11.0 is excluded — would form cycle)

        GraphADT graph = new SimpleGraph();
        graph.addEdge("L001", "L002", 4.0);
        graph.addEdge("L002", "L003", 10.4);
        graph.addEdge("L001", "L003", 11.0);

        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Prim.MstResult result = prim.minimumSpanningTree(graph, pq);

        // --- EVIDENCE TRACE OUTPUT ---
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("EVIDENCE: Prim MST trace with running cost");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("Graph edges (from roads_template.csv baseWeights):");
        System.out.println("  L001 — L002 : 4.0");
        System.out.println("  L002 — L003 : 10.4");
        System.out.println("  L001 — L003 : 11.0");
        System.out.println();
        double runningCost = 0.0;
        System.out.println("Prim MST construction (starting from first vertex):");
        for (String edgeDesc : result.edgeDescriptions) {
            // Parse cost from "from-to: cost"
            double cost = Double.parseDouble(edgeDesc.split(": ")[1]);
            runningCost += cost;
            System.out.println("  ADD edge " + edgeDesc + "  (running total: " + runningCost + ")");
        }
        System.out.println();
        System.out.println("Final MST cost: " + result.totalCost);
        System.out.println("MST edges: " + result.edgeDescriptions);
        System.out.println("═══════════════════════════════════════════════════════");

        assertEquals(14.4, result.totalCost, 1e-9);
        assertEquals(2, result.edgeDescriptions.size());
    }
}
