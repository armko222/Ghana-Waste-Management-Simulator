package com.dcit308.wasteops.algorithms.graph;

import com.dcit308.wasteops.structures.GraphADT;
import java.util.ArrayList;
import java.util.List;

/**
 * Breadth-first traversal -- answers "which locations are reachable from
 * this point." Owned by Issue #10.
 */
public class BFS {

    /** FIFO queue node holding one pending vertex id. */
    private static class QueueNode {
        final String vertexId;
        QueueNode next;

        QueueNode(String vertexId) {
            this.vertexId = vertexId;
        }
    }

    /**
     * Hand-rolled visited-membership set: a singly linked list with a
     * linear scan, same pattern as DisjointSet/GraphAdjacencyList's
     * internal lookups -- no java.util.HashSet.
     */
    private static class VisitedSet {
        private static class Node {
            final String id;
            Node next;

            Node(String id) {
                this.id = id;
            }
        }

        private Node head;

        boolean contains(String id) {
            Node current = head;
            while (current != null) {
                if (current.id.equals(id)) {
                    return true;
                }
                current = current.next;
            }
            return false;
        }

        void add(String id) {
            Node node = new Node(id);
            node.next = head;
            head = node;
        }
    }

    /**
     * Returns vertex IDs reachable from startVertexId, including itself,
     * in the order BFS visits them. Throws if startVertexId isn't in the
     * graph (mirrors GraphADT's own "unknown vertex" behaviour).
     */
    public List<String> traverse(GraphADT graph, String startVertexId) {
        if (graph == null) {
            throw new IllegalArgumentException("graph cannot be null");
        }
        if (startVertexId == null) {
            throw new IllegalArgumentException("startVertexId cannot be null");
        }

        List<String> visitOrder = new ArrayList<>();
        VisitedSet visited = new VisitedSet();

        QueueNode queueHead = new QueueNode(startVertexId);
        QueueNode queueTail = queueHead;
        visited.add(startVertexId);

        while (queueHead != null) {
            String currentId = queueHead.vertexId;
            queueHead = queueHead.next;
            if (queueHead == null) {
                queueTail = null;
            }

            visitOrder.add(currentId);

            for (String neighborId : graph.getNeighbors(currentId)) {
                if (!visited.contains(neighborId)) {
                    visited.add(neighborId);
                    QueueNode node = new QueueNode(neighborId);
                    if (queueTail == null) {
                        queueHead = node;
                        queueTail = node;
                    } else {
                        queueTail.next = node;
                        queueTail = node;
                    }
                }
            }
        }

        return visitOrder;
    }
}