package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphAdjacencyList;
import com.dcit308.wasteops.structures.GraphAdjacencyMatrix;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #11's DFS traversal.
 *
 * <p>The tests deliberately run DFS against both graph representations
 * because DFS is written against GraphADT.</p>
 */
class DFSTest {

    private final DFS dfs = new DFS();

    @Nested
    @DisplayName("Empty graph")
    class EmptyGraph {

        @Test
        @DisplayName("traversing an empty graph throws")
        void emptyGraphThrows() {
            GraphAdjacencyMatrix graph =
                    new GraphAdjacencyMatrix();

            assertThrows(
                    IllegalArgumentException.class,
                    () -> dfs.traverse(graph, "Ghost")
            );
        }

        @Test
        @DisplayName("null graph is rejected")
        void nullGraphRejected() {
            assertThrows(
                    IllegalArgumentException.class,
                    () -> dfs.traverse(null, "A")
            );
        }

        @Test
        @DisplayName("null start vertex is rejected")
        void nullStartRejected() {
            GraphAdjacencyMatrix graph =
                    new GraphAdjacencyMatrix();

            assertThrows(
                    IllegalArgumentException.class,
                    () -> dfs.traverse(graph, null)
            );
        }
    }

    @Nested
    @DisplayName("Single vertex")
    class SingleVertex {

        @Test
        @DisplayName("DFS visits the only vertex")
        void visitsSingleVertex() {
            GraphAdjacencyMatrix graph =
                    new GraphAdjacencyMatrix();

            graph.addVertex("A");

            assertEquals(
                    List.of("A"),
                    dfs.traverse(graph, "A")
            );
        }

        @Test
        @DisplayName("self-loop does not revisit vertex")
        void selfLoopDoesNotRevisit() {
            GraphAdjacencyMatrix graph =
                    new GraphAdjacencyMatrix();

            graph.addVertex("A");
            graph.addEdge("A", "A", 1.0);

            assertEquals(
                    List.of("A"),
                    dfs.traverse(graph, "A")
            );
        }
    }

    @Nested
    @DisplayName("Disconnected graph")
    class DisconnectedGraph {

        @Test
        @DisplayName("DFS does not cross into another component")
        void disconnectedComponents() {
            GraphAdjacencyMatrix graph =
                    new GraphAdjacencyMatrix();

            for (String vertex :
                    List.of("A", "B", "C", "D", "E")) {
                graph.addVertex(vertex);
            }

            graph.addEdge("A", "B", 1.0);
            graph.addEdge("B", "C", 1.0);

            graph.addEdge("D", "E", 1.0);

            List<String> result =
                    dfs.traverse(graph, "A");

            assertEquals(
                    List.of("A", "B", "C"),
                    result
            );

            assertTrue(!result.contains("D"));
            assertTrue(!result.contains("E"));
        }

        @Test
        @DisplayName("second component is independently traversable")
        void secondComponent() {
            GraphAdjacencyMatrix graph =
                    new GraphAdjacencyMatrix();

            for (String vertex :
                    List.of("A", "B", "C", "D", "E")) {
                graph.addVertex(vertex);
            }

            graph.addEdge("A", "B", 1.0);
            graph.addEdge("B", "C", 1.0);

            graph.addEdge("D", "E", 1.0);

            assertEquals(
                    List.of("D", "E"),
                    dfs.traverse(graph, "D")
            );
        }
    }

    @Test
    @DisplayName("DFS visits every reachable vertex exactly once")
    void visitsEveryVertexOnce() {
        GraphAdjacencyMatrix graph =
                new GraphAdjacencyMatrix();

        for (String vertex : List.of("A", "B", "C", "D")) {
            graph.addVertex(vertex);
        }

        graph.addEdge("A", "B", 1.0);
        graph.addEdge("A", "C", 1.0);
        graph.addEdge("B", "D", 1.0);
        graph.addEdge("C", "D", 1.0);

        List<String> result =
                dfs.traverse(graph, "A");

        assertEquals(4, result.size());
        assertEquals(
                1,
                result.stream()
                        .filter("A"::equals)
                        .count()
        );
        assertEquals(
                1,
                result.stream()
                        .filter("B"::equals)
                        .count()
        );
        assertEquals(
                1,
                result.stream()
                        .filter("C"::equals)
                        .count()
        );
        assertEquals(
                1,
                result.stream()
                        .filter("D"::equals)
                        .count()
        );
    }

    @Test
    @DisplayName("DFS works with adjacency-list representation")
    void worksWithAdjacencyList() {
        GraphAdjacencyList graph =
                new GraphAdjacencyList();

        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");

        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", 1.0);

        assertEquals(
                List.of("A", "B", "C"),
                dfs.traverse(graph, "A")
        );
    }

    @Test
    @DisplayName("DFS works with adjacency-matrix representation")
    void worksWithAdjacencyMatrix() {
        GraphAdjacencyMatrix graph =
                new GraphAdjacencyMatrix();

        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");

        graph.addEdge("A", "B", 1.0);
        graph.addEdge("B", "C", 1.0);

        assertEquals(
                List.of("A", "B", "C"),
                dfs.traverse(graph, "A")
        );
    }
}