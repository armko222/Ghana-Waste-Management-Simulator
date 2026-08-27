package com.dcit308.wasteops.algorithms.graph;

import java.util.ArrayList;
import java.util.List;

import com.dcit308.wasteops.structures.GraphADT;

/**
 * Depth-first traversal of a graph.
 *
 * <p>DFS operates against GraphADT rather than a particular graph
 * representation. Therefore the same implementation can traverse both
 * GraphAdjacencyList and GraphAdjacencyMatrix.</p>
 *
 * <p>Owned by Issue #11.</p>
 */
public class DFS {

    /**
     * Simple hand-rolled stack node.
     *
     * <p>A custom stack is used instead of java.util.Stack or other
     * built-in stack structures, consistent with the project's
     * custom-data-structure requirements.</p>
     */
    private static class StackNode {
        final String vertexId;
        StackNode next;

        StackNode(String vertexId) {
            this.vertexId = vertexId;
        }
    }

    /**
     * Hand-rolled visited set.
     *
     * <p>This deliberately uses a linked list rather than
     * java.util.HashSet because the project requires custom
     * implementations of the relevant data structures.</p>
     */
    private static class VisitedSet {

        private static class Node {
            final String vertexId;
            Node next;

            Node(String vertexId) {
                this.vertexId = vertexId;
            }
        }

        private Node head;

        boolean contains(String vertexId) {
            Node current = head;

            while (current != null) {
                if (current.vertexId.equals(vertexId)) {
                    return true;
                }

                current = current.next;
            }

            return false;
        }

        void add(String vertexId) {
            Node node = new Node(vertexId);
            node.next = head;
            head = node;
        }
    }

    /**
     * Traverses the graph using depth-first search.
     *
     * @param graph graph to traverse
     * @param startVertexId starting vertex
     * @return vertices in DFS visit order
     */
    public List<String> traverse(
            GraphADT graph,
            String startVertexId) {

        if (graph == null) {
            throw new IllegalArgumentException(
                    "graph cannot be null");
        }

        if (startVertexId == null) {
            throw new IllegalArgumentException(
                    "startVertexId cannot be null");
        }

        // Ask the graph to validate that the starting vertex exists.
        if (!graph.getAllVertices().contains(startVertexId)) {
            throw new IllegalArgumentException(
                    "Unknown vertex: " + startVertexId);
        }

        List<String> visitOrder = new ArrayList<>();
        VisitedSet visited = new VisitedSet();

        StackNode stack = new StackNode(startVertexId);

        while (stack != null) {

            String currentId = stack.vertexId;
            stack = stack.next;

            if (visited.contains(currentId)) {
                continue;
            }

            visited.add(currentId);
            visitOrder.add(currentId);

            /*
             * Push neighbours onto the stack.
             *
             * Since this is a stack, the last neighbour returned by
             * getNeighbors() is processed first.
             */
            List<String> neighbors = graph.getNeighbors(currentId);

            for (String neighborId : neighbors) {
                if (!visited.contains(neighborId)) {
                    StackNode node = new StackNode(neighborId);
                    node.next = stack;
                    stack = node;
                }
            }
        }

        return visitOrder;
    }
}