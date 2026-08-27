package com.dcit308.wasteops.structures;

import java.util.ArrayList;
import java.util.List;

/**
 * Road network, adjacency-matrix representation.
 *
 * <p>This is the second required graph representation for the project.
 * It implements the same GraphADT contract as GraphAdjacencyList so that
 * graph algorithms such as DFS and Dijkstra can operate on either
 * representation without knowing how the graph is stored internally.</p>
 *
 * <p>Edges are directed, matching GraphAdjacencyList and the roads table.
 * A repeated edge updates its existing weight rather than creating a
 * duplicate edge.</p>
 *
 * <p>Owned by Issue #11.</p>
 */
public class GraphAdjacencyMatrix implements GraphADT {

    private static final double NO_EDGE = Double.POSITIVE_INFINITY;

    private String[] vertexIds;
    private double[][] matrix;
    private int size;

    /**
     * Creates an empty graph.
     */
    public GraphAdjacencyMatrix() {
        vertexIds = new String[4];
        matrix = new double[4][4];
        size = 0;

        initialiseMatrix(matrix);
    }

    /**
     * Fills a matrix with the value representing no edge.
     */
    private void initialiseMatrix(double[][] target) {
        for (int i = 0; i < target.length; i++) {
            for (int j = 0; j < target.length; j++) {
                target[i][j] = NO_EDGE;
            }
        }
    }

    /**
     * Finds the array index of a vertex.
     *
     * @return the vertex index, or -1 if it does not exist
     */
    private int findVertexIndex(String vertexId) {
        for (int i = 0; i < size; i++) {
            if (vertexIds[i].equals(vertexId)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Ensures that there is enough space for another vertex.
     */
    private void ensureCapacity() {
        if (size < vertexIds.length) {
            return;
        }

        int newCapacity = vertexIds.length * 2;

        String[] newVertexIds = new String[newCapacity];
        double[][] newMatrix = new double[newCapacity][newCapacity];

        initialiseMatrix(newMatrix);

        for (int i = 0; i < size; i++) {
            newVertexIds[i] = vertexIds[i];

            for (int j = 0; j < size; j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }

        vertexIds = newVertexIds;
        matrix = newMatrix;
    }

    @Override
    public void addVertex(String vertexId) {
        if (vertexId == null) {
            throw new IllegalArgumentException("vertexId cannot be null");
        }

        // Same behaviour as GraphAdjacencyList:
        // adding an existing vertex is harmless.
        if (findVertexIndex(vertexId) != -1) {
            return;
        }

        ensureCapacity();

        vertexIds[size] = vertexId;

        // The new row/column were already initialised to NO_EDGE
        // when the matrix was created/resized.
        size++;
    }

    @Override
    public void addEdge(
            String fromVertexId,
            String toVertexId,
            double weight) {

        int fromIndex = findVertexIndex(fromVertexId);
        int toIndex = findVertexIndex(toVertexId);

        if (fromIndex == -1) {
            throw new IllegalArgumentException(
                    "Unknown vertex (call addVertex first): "
                            + fromVertexId);
        }

        if (toIndex == -1) {
            throw new IllegalArgumentException(
                    "Unknown vertex (call addVertex first): "
                            + toVertexId);
        }

        matrix[fromIndex][toIndex] = weight;
    }

    @Override
    public List<String> getNeighbors(String vertexId) {
        int vertexIndex = findVertexIndex(vertexId);

        if (vertexIndex == -1) {
            throw new IllegalArgumentException(
                    "Unknown vertex: " + vertexId);
        }

        List<String> neighbors = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            if (matrix[vertexIndex][i] != NO_EDGE) {
                neighbors.add(vertexIds[i]);
            }
        }

        return neighbors;
    }

    @Override
    public double getWeight(
            String fromVertexId,
            String toVertexId) {

        int fromIndex = findVertexIndex(fromVertexId);
        int toIndex = findVertexIndex(toVertexId);

        if (fromIndex == -1) {
            throw new IllegalArgumentException(
                    "Unknown vertex: " + fromVertexId);
        }

        if (toIndex == -1) {
            throw new IllegalArgumentException(
                    "Unknown vertex: " + toVertexId);
        }

        if (matrix[fromIndex][toIndex] == NO_EDGE) {
            throw new IllegalArgumentException(
                    "No edge from "
                            + fromVertexId
                            + " to "
                            + toVertexId);
        }

        return matrix[fromIndex][toIndex];
    }

    @Override
    public List<String> getAllVertices() {
        List<String> ids = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            ids.add(vertexIds[i]);
        }

        return ids;
    }
}