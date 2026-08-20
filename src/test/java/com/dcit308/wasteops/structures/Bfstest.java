package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphAdjacencyList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #10's BFS, run against the real GraphAdjacencyList
 * (also Issue #10) rather than a stand-in -- both pieces are owned by
 * the same role here, so there's no need for the
 * write-your-own-rough-version pattern from the handbook.
 */
class BFSTest {

    private final BFS bfs = new BFS();

    @Nested
    @DisplayName("Empty graph")
    class EmptyGraph {

        @Test
        @DisplayName("a freshly built graph has no vertices")
        void freshGraphHasNoVertices() {
            GraphAdjacencyList graph = new GraphAdjacencyList();

            assertTrue(graph.getAllVertices().isEmpty());
        }

        @Test
        @DisplayName("traversing from a vertex that was never added throws")
        void unknownStartVertexThrows() {
            GraphAdjacencyList graph = new GraphAdjacencyList();

            assertThrows(IllegalArgumentException.class, () -> bfs.traverse(graph, "Ghost"));
        }

        @Test
        @DisplayName("a null graph or null start vertex is rejected")
        void nullArgumentsRejected() {
            GraphAdjacencyList graph = new GraphAdjacencyList();

            assertThrows(IllegalArgumentException.class, () -> bfs.traverse(null, "A"));
            assertThrows(IllegalArgumentException.class, () -> bfs.traverse(graph, null));
        }
    }

    @Nested
    @DisplayName("Single vertex")
    class SingleVertexGraph {

        @Test
        @DisplayName("a vertex with no edges visits only itself")
        void isolatedVertexVisitsOnlyItself() {
            GraphAdjacencyList graph = new GraphAdjacencyList();
            graph.addVertex("Solo");

            List<String> order = bfs.traverse(graph, "Solo");

            assertEquals(List.of("Solo"), order);
        }

        @Test
        @DisplayName("a self-loop does not cause the vertex to be revisited")
        void selfLoopDoesNotRevisit() {
            GraphAdjacencyList graph = new GraphAdjacencyList();
            graph.addVertex("Solo");
            graph.addEdge("Solo", "Solo", 1.0);

            List<String> order = bfs.traverse(graph, "Solo");

            assertEquals(List.of("Solo"), order);
        }
    }

    @Nested
    @DisplayName("Disconnected graph")
    class DisconnectedGraph {

        /**
         * Two separate components:
         *   L1 -> L2 -> L3   (one component)
         *   L4 -> L5         (a second, unreachable component)
         */
        private GraphAdjacencyList twoComponents() {
            GraphAdjacencyList graph = new GraphAdjacencyList();
            for (String v : List.of("L1", "L2", "L3", "L4", "L5")) {
                graph.addVertex(v);
            }
            graph.addEdge("L1", "L2", 5.0);
            graph.addEdge("L2", "L3", 2.0);
            graph.addEdge("L4", "L5", 1.0);
            return graph;
        }

        @Test
        @DisplayName("BFS from one component never reaches the other")
        void onlyReachesOwnComponent() {
            GraphAdjacencyList graph = twoComponents();

            List<String> fromL1 = bfs.traverse(graph, "L1");

            assertEquals(List.of("L1", "L2", "L3"), fromL1);
            assertFalse(fromL1.contains("L4"));
            assertFalse(fromL1.contains("L5"));
        }

        @Test
        @DisplayName("the other component is independently reachable from its own start vertex")
        void otherComponentIsIndependentlyReachable() {
            GraphAdjacencyList graph = twoComponents();

            List<String> fromL4 = bfs.traverse(graph, "L4");

            assertEquals(List.of("L4", "L5"), fromL4);
        }

        @Test
        @DisplayName("a fully isolated vertex added alongside other components visits only itself")
        void fullyIsolatedVertexInLargerGraph() {
            GraphAdjacencyList graph = twoComponents();
            graph.addVertex("Island");

            List<String> order = bfs.traverse(graph, "Island");

            assertEquals(List.of("Island"), order);
        }

        @Test
        @DisplayName("a diamond-shaped component visits every vertex exactly once")
        void diamondShapeVisitsEachVertexOnce() {
            GraphAdjacencyList graph = new GraphAdjacencyList();
            for (String v : List.of("A", "B", "C", "D")) {
                graph.addVertex(v);
            }
            // A reaches D via two different paths (through B and through C).
            graph.addEdge("A", "B", 1.0);
            graph.addEdge("A", "C", 1.0);
            graph.addEdge("B", "D", 1.0);
            graph.addEdge("C", "D", 1.0);

            List<String> order = bfs.traverse(graph, "A");

            assertEquals(4, order.size(), "every vertex should appear exactly once");
            assertEquals(1, order.stream().filter("D"::equals).count(),
                    "D is reachable via two paths but BFS must not revisit it");
            assertEquals("A", order.get(0), "traversal must start at the given vertex");
        }
    }
}