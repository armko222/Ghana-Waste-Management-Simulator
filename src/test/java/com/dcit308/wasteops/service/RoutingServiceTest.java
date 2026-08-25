package com.dcit308.wasteops.service;

import com.dcit308.wasteops.algorithms.graph.Dijkstra;
import com.dcit308.wasteops.algorithms.graph.Prim;
import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.testutil.SimpleGraph;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link RoutingService} — the service layer that wires
 * Dijkstra, inline BFS, and Prim to the road-network graph.
 *
 * <p>Uses {@link SimpleGraph} as the GraphADT test stand-in.
 *
 * Owned by Issue #12.
 */
class RoutingServiceTest {

    // ------------------------------------------------------------------
    //  fastestRoute
    // ------------------------------------------------------------------

    @Test
    @DisplayName("fastestRoute returns correct path and weight")
    void fastestRouteCorrectResult() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 2.0);
        graph.addEdge("B", "C", 3.0);
        graph.addEdge("A", "C", 10.0);

        RoutingService service = new RoutingService(graph);
        Dijkstra.RouteResult result = service.fastestRoute("A", "C");

        assertTrue(result.reachable);
        assertEquals(5.0, result.totalWeight, 1e-9, "A→B→C (2+3=5) cheaper than A→C (10)");
        assertEquals("A", result.path.get(0));
        assertEquals("C", result.path.get(result.path.size() - 1));
    }

    @Test
    @DisplayName("fastestRoute on disconnected graph → unreachable")
    void fastestRouteDisconnected() {
        GraphADT graph = new SimpleGraph();
        graph.addVertex("A");
        graph.addVertex("Z");

        RoutingService service = new RoutingService(graph);
        Dijkstra.RouteResult result = service.fastestRoute("A", "Z");

        assertFalse(result.reachable, "Z should be unreachable from A");
    }

    @Test
    @DisplayName("fastestRoute same source and destination")
    void fastestRouteSameVertex() {
        GraphADT graph = new SimpleGraph();
        graph.addVertex("A");

        RoutingService service = new RoutingService(graph);
        Dijkstra.RouteResult result = service.fastestRoute("A", "A");

        assertTrue(result.reachable);
        assertEquals(0.0, result.totalWeight, 1e-9);
    }

    // ------------------------------------------------------------------
    //  reachableLocations (inline BFS — see RoutingService Javadoc)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("reachableLocations returns all connected vertices")
    void reachableLocationsAllConnected() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", 1.0);
        graph.addEdge("C", "D", 1.0);

        RoutingService service = new RoutingService(graph);
        List<String> reachable = service.reachableLocations("A");

        assertEquals(4, reachable.size());
        assertTrue(reachable.contains("A"), "Source itself is reachable");
        assertTrue(reachable.contains("B"));
        assertTrue(reachable.contains("C"));
        assertTrue(reachable.contains("D"));
    }

    @Test
    @DisplayName("reachableLocations on disconnected graph → only connected component")
    void reachableLocationsDisconnected() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 1.0);
        graph.addVertex("C");  // isolated vertex
        graph.addVertex("D");  // isolated vertex

        RoutingService service = new RoutingService(graph);
        List<String> reachable = service.reachableLocations("A");

        assertEquals(2, reachable.size(), "Only A and B are reachable");
        assertTrue(reachable.contains("A"));
        assertTrue(reachable.contains("B"));
        assertFalse(reachable.contains("C"), "C is not connected to A");
        assertFalse(reachable.contains("D"), "D is not connected to A");
    }

    @Test
    @DisplayName("reachableLocations on single vertex → only itself")
    void reachableLocationsSingleVertex() {
        GraphADT graph = new SimpleGraph();
        graph.addVertex("SOLO");

        RoutingService service = new RoutingService(graph);
        List<String> reachable = service.reachableLocations("SOLO");

        assertEquals(1, reachable.size());
        assertTrue(reachable.contains("SOLO"));
    }

    // ------------------------------------------------------------------
    //  minimumConnectingNetwork
    // ------------------------------------------------------------------

    @Test
    @DisplayName("minimumConnectingNetwork returns valid MST")
    void minimumConnectingNetworkValid() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", 2.0);
        graph.addEdge("A", "C", 3.0);

        RoutingService service = new RoutingService(graph);
        Prim.MstResult mst = service.minimumConnectingNetwork();

        assertEquals(3.0, mst.totalCost, 1e-9, "MST: A-B(1) + B-C(2) = 3");
        assertEquals(2, mst.edgeDescriptions.size());
    }

    @Test
    @DisplayName("minimumConnectingNetwork on empty graph → cost 0")
    void minimumConnectingNetworkEmpty() {
        GraphADT graph = new SimpleGraph();

        RoutingService service = new RoutingService(graph);
        Prim.MstResult mst = service.minimumConnectingNetwork();

        assertEquals(0.0, mst.totalCost, 1e-9);
        assertTrue(mst.edgeDescriptions.isEmpty());
    }

    @Test
    @DisplayName("minimumConnectingNetwork on template dataset")
    void minimumConnectingNetworkTemplateData() {
        GraphADT graph = new SimpleGraph();
        graph.addEdge("L001", "L002", 4.0);
        graph.addEdge("L002", "L003", 10.4);
        graph.addEdge("L001", "L003", 11.0);

        RoutingService service = new RoutingService(graph);
        Prim.MstResult mst = service.minimumConnectingNetwork();

        // MST: L001-L002 (4.0) + L002-L003 (10.4) = 14.4
        assertEquals(14.4, mst.totalCost, 1e-9);
        assertEquals(2, mst.edgeDescriptions.size());
    }
}
