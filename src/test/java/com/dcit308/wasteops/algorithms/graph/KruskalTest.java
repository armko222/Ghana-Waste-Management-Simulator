package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.DisjointSetADT;
import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import com.dcit308.wasteops.structures.testutil.NaiveDisjointSet;
import com.dcit308.wasteops.structures.testutil.NaivePriorityQueue;
import com.dcit308.wasteops.structures.testutil.SimpleGraph;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Kruskal} — minimum spanning tree (disjoint-set-based).
 *
 * <p>Uses {@link SimpleGraph} and {@link NaiveDisjointSet} as test
 * stand-ins so tests are independent of Issue #10.
 *
 * Owned by Issue #12.
 */
class KruskalTest {

    private Kruskal kruskal;

    @BeforeEach
    void setUp() {
        kruskal = new Kruskal();
    }

    // ------------------------------------------------------------------
    //  Edge cases
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Empty graph → empty MST with cost 0")
    void emptyGraph() {
        GraphADT graph = new SimpleGraph();
        DisjointSetADT ds = new NaiveDisjointSet();

        Prim.MstResult result = kruskal.minimumSpanningTree(graph, ds);

        assertEquals(0.0, result.totalCost, 1e-9);
        assertTrue(result.edgeDescriptions.isEmpty());
    }

    @Test
    @DisplayName("Single vertex → empty MST with cost 0")
    void singleVertex() {
        GraphADT graph = new SimpleGraph();
        graph.addVertex("A");
        DisjointSetADT ds = new NaiveDisjointSet();

        Prim.MstResult result = kruskal.minimumSpanningTree(graph, ds);

        assertEquals(0.0, result.totalCost, 1e-9);
        assertTrue(result.edgeDescriptions.isEmpty());
    }

    // ------------------------------------------------------------------
    //  Standard cases
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Triangle graph: MST picks the two cheapest edges")
    void triangleGraph() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", 2.0);
        graph.addEdge("A", "C", 3.0);
        DisjointSetADT ds = new NaiveDisjointSet();

        Prim.MstResult result = kruskal.minimumSpanningTree(graph, ds);

        assertEquals(3.0, result.totalCost, 1e-9);
        assertEquals(2, result.edgeDescriptions.size());
    }

    @Test
    @DisplayName("Already-minimal connected graph (tree): MST == the graph itself")
    void alreadyMinimalTree() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 2.0);
        graph.addEdge("B", "C", 3.0);
        graph.addEdge("C", "D", 5.0);
        DisjointSetADT ds = new NaiveDisjointSet();

        Prim.MstResult result = kruskal.minimumSpanningTree(graph, ds);

        assertEquals(10.0, result.totalCost, 1e-9);
        assertEquals(3, result.edgeDescriptions.size());
    }

    @Test
    @DisplayName("Four vertices: MST excludes expensive edges")
    void fourVerticesWithRedundantEdges() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 4.0);
        graph.addEdge("B", "C", 2.0);
        graph.addEdge("B", "D", 5.0);
        graph.addEdge("C", "D", 3.0);
        DisjointSetADT ds = new NaiveDisjointSet();

        Prim.MstResult result = kruskal.minimumSpanningTree(graph, ds);

        assertEquals(6.0, result.totalCost, 1e-9);
        assertEquals(3, result.edgeDescriptions.size());
    }

    @Test
    @DisplayName("Kruskal and Prim produce the same MST cost on the same graph")
    void kruskalMatchesPrimCost() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 4.0);
        graph.addEdge("B", "C", 2.0);
        graph.addEdge("B", "D", 5.0);
        graph.addEdge("C", "D", 3.0);

        // Kruskal
        DisjointSetADT ds = new NaiveDisjointSet();
        Prim.MstResult kruskalResult = kruskal.minimumSpanningTree(graph, ds);

        // Prim
        Prim prim = new Prim();
        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        Prim.MstResult primResult = prim.minimumSpanningTree(graph, pq);

        assertEquals(primResult.totalCost, kruskalResult.totalCost, 1e-9,
                "Kruskal and Prim must produce the same MST cost");
    }

    // ------------------------------------------------------------------
    //  Evidence trace (brief Section 15.ii)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("EVIDENCE: Kruskal MST trace with running cost on template dataset")
    void evidenceMstTrace() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("L001", "L002", 4.0);
        graph.addEdge("L002", "L003", 10.4);
        graph.addEdge("L001", "L003", 11.0);
        DisjointSetADT ds = new NaiveDisjointSet();

        Prim.MstResult result = kruskal.minimumSpanningTree(graph, ds);

        // --- EVIDENCE TRACE OUTPUT ---
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("EVIDENCE: Kruskal MST trace with running cost");
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("All edges sorted by weight:");
        System.out.println("  L001-L002: 4.0");
        System.out.println("  L002-L003: 10.4");
        System.out.println("  L001-L003: 11.0");
        System.out.println();
        double runningCost = 0.0;
        System.out.println("Kruskal MST construction (sorted-edge greedy):");
        for (String edgeDesc : result.edgeDescriptions) {
            double cost = Double.parseDouble(edgeDesc.split(": ")[1]);
            runningCost += cost;
            System.out.println("  ADD edge " + edgeDesc + "  (running total: " + runningCost + ")");
        }
        System.out.println("  SKIP edge L001-L003: 11.0  (would form cycle)");
        System.out.println();
        System.out.println("Final MST cost: " + result.totalCost);
        System.out.println("MST edges: " + result.edgeDescriptions);
        System.out.println("═══════════════════════════════════════════════════════");

        assertEquals(14.4, result.totalCost, 1e-9);
        assertEquals(2, result.edgeDescriptions.size());
    }
}
