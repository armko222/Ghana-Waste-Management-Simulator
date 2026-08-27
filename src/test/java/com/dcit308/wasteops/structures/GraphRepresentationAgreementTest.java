package com.dcit308.wasteops.structures;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Cross-checks the adjacency-list and adjacency-matrix representations.
 *
 * <p>The project requires both representations to expose the same
 * graph through GraphADT.</p>
 */
class GraphRepresentationAgreementTest {

    @Test
    @DisplayName("list and matrix contain the same vertices and edge weights")
    void representationsAgree() {

        GraphAdjacencyList list =
                new GraphAdjacencyList();

        GraphAdjacencyMatrix matrix =
                new GraphAdjacencyMatrix();

        List<String> vertices =
                List.of("L1", "L2", "L3", "L4", "L5");

        for (String vertex : vertices) {
            list.addVertex(vertex);
            matrix.addVertex(vertex);
        }

        addEdge(list, matrix, "L1", "L2", 5.0);
        addEdge(list, matrix, "L1", "L3", 2.5);
        addEdge(list, matrix, "L2", "L4", 7.0);
        addEdge(list, matrix, "L3", "L4", 3.0);
        addEdge(list, matrix, "L4", "L5", 4.5);

        assertEquals(
                list.getAllVertices(),
                matrix.getAllVertices()
        );

        for (String vertex : vertices) {

            Set<String> listNeighbors =
                    new HashSet<>(list.getNeighbors(vertex));

            Set<String> matrixNeighbors =
                    new HashSet<>(matrix.getNeighbors(vertex));

            assertEquals(
                    listNeighbors,
                    matrixNeighbors,
                    "Neighbours should agree for " + vertex
            );

            for (String neighbor : listNeighbors) {

                assertEquals(
                        list.getWeight(vertex, neighbor),
                        matrix.getWeight(vertex, neighbor),
                        0.000001,
                        "Weight should agree for "
                                + vertex + " -> " + neighbor
                );
            }
        }
    }

    private void addEdge(
            GraphAdjacencyList list,
            GraphAdjacencyMatrix matrix,
            String from,
            String to,
            double weight) {

        list.addEdge(from, to, weight);
        matrix.addEdge(from, to, weight);
    }
}