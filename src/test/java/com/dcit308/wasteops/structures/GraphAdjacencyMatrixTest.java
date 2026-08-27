package com.dcit308.wasteops.structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #11's adjacency-matrix graph.
 */
class GraphAdjacencyMatrixTest {

    @Nested
    @DisplayName("Empty graph")
    class EmptyGraph {

        @Test
        @DisplayName("fresh graph contains no vertices")
        void freshGraphIsEmpty() {
            GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

            assertTrue(graph.getAllVertices().isEmpty());
        }

        @Test
        @DisplayName("unknown vertex lookup throws")
        void unknownVertexThrows() {
            GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

            assertThrows(
                    IllegalArgumentException.class,
                    () -> graph.getNeighbors("Ghost")
            );
        }

        @Test
        @DisplayName("null vertex is rejected")
        void nullVertexRejected() {
            GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

            assertThrows(
                    IllegalArgumentException.class,
                    () -> graph.addVertex(null)
            );
        }
    }

    @Nested
    @DisplayName("Single vertex")
    class SingleVertex {

        @Test
        @DisplayName("single vertex has no neighbours")
        void isolatedVertex() {
            GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

            graph.addVertex("A");

            assertEquals(
                    List.of("A"),
                    graph.getAllVertices()
            );

            assertTrue(
                    graph.getNeighbors("A").isEmpty()
            );
        }

        @Test
        @DisplayName("self-loop is stored correctly")
        void selfLoop() {
            GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

            graph.addVertex("A");
            graph.addEdge("A", "A", 7.5);

            assertEquals(
                    List.of("A"),
                    graph.getNeighbors("A")
            );

            assertEquals(
                    7.5,
                    graph.getWeight("A", "A")
            );
        }
    }

    @Nested
    @DisplayName("Edges")
    class Edges {

        @Test
        @DisplayName("directed edge stores its weight")
        void directedEdge() {
            GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

            graph.addVertex("A");
            graph.addVertex("B");

            graph.addEdge("A", "B", 5.0);

            assertEquals(
                    List.of("B"),
                    graph.getNeighbors("A")
            );

            assertEquals(
                    5.0,
                    graph.getWeight("A", "B")
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> graph.getWeight("B", "A")
            );
        }

        @Test
        @DisplayName("adding the same edge updates its weight")
        void duplicateEdgeUpdatesWeight() {
            GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

            graph.addVertex("A");
            graph.addVertex("B");

            graph.addEdge("A", "B", 5.0);
            graph.addEdge("A", "B", 12.0);

            assertEquals(
                    12.0,
                    graph.getWeight("A", "B")
            );

            assertEquals(
                    List.of("B"),
                    graph.getNeighbors("A")
            );
        }

        @Test
        @DisplayName("edge requires both vertices to exist")
        void edgeRequiresExistingVertices() {
            GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

            graph.addVertex("A");

            assertThrows(
                    IllegalArgumentException.class,
                    () -> graph.addEdge("A", "B", 1.0)
            );

            assertThrows(
                    IllegalArgumentException.class,
                    () -> graph.addEdge("B", "A", 1.0)
            );
        }

        @Test
        @DisplayName("missing edge weight throws")
        void missingEdgeThrows() {
            GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

            graph.addVertex("A");
            graph.addVertex("B");

            assertThrows(
                    IllegalArgumentException.class,
                    () -> graph.getWeight("A", "B")
            );
        }
    }

    @Nested
    @DisplayName("Disconnected graph")
    class DisconnectedGraph {

        @Test
        @DisplayName("neighbours remain within their components")
        void componentsRemainSeparate() {
            GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

            for (String vertex : List.of("A", "B", "C", "D")) {
                graph.addVertex(vertex);
            }

            graph.addEdge("A", "B", 2.0);
            graph.addEdge("C", "D", 3.0);

            assertEquals(
                    List.of("B"),
                    graph.getNeighbors("A")
            );

            assertEquals(
                    List.of("D"),
                    graph.getNeighbors("C")
            );

            assertTrue(
                    graph.getNeighbors("B").isEmpty()
            );

            assertTrue(
                    graph.getNeighbors("D").isEmpty()
            );
        }
    }

    @Test
    @DisplayName("adding an existing vertex is idempotent")
    void duplicateVertexDoesNotCreateDuplicate() {
        GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

        graph.addVertex("A");
        graph.addVertex("A");

        assertEquals(
                List.of("A"),
                graph.getAllVertices()
        );
    }

    @Test
    @DisplayName("matrix grows beyond initial capacity")
    void matrixResizes() {
        GraphAdjacencyMatrix graph = new GraphAdjacencyMatrix();

        for (int i = 0; i < 10; i++) {
            graph.addVertex("V" + i);
        }

        graph.addEdge("V0", "V9", 25.0);

        assertEquals(10, graph.getAllVertices().size());
        assertEquals(25.0, graph.getWeight("V0", "V9"));
    }
}