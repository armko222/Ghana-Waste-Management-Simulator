package com.dcit308.wasteops.service;

import com.dcit308.wasteops.algorithms.graph.Dijkstra;
import com.dcit308.wasteops.algorithms.graph.Prim;
import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import com.dcit308.wasteops.structures.testutil.NaivePriorityQueue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

/**
 * Exposes: fastest route between two locations, reachable locations from
 * a point, minimum connecting network. This is what answers the brief's
 * Section 3 routing questions -- the console menu calls into this, not
 * into Dijkstra/BFS/Kruskal directly.
 *
 * <p>Note on reachableLocations: BFS.java is owned by Issue #10 and may
 * not yet be implemented. Rather than depending on the BFS stub, this
 * service implements an inline iterative BFS using
 * {@link GraphADT#getNeighbors}. The issue description allows "wire to
 * BFS or DFS" — this inline BFS satisfies that requirement while keeping
 * Issue #12 independent of Issue #10's timeline.
 *
 * <p>Once Issue #5 (BinaryHeap/CustomPriorityQueue) merges, swap
 * {@code NaivePriorityQueue} for {@code CustomPriorityQueue} — one line
 * change, no other code affected.
 *
 * Owned by Issue #12.
 */
public class RoutingService {

    private final GraphADT graph;

    /**
     * Creates a RoutingService wired to the given road-network graph.
     *
     * @param graph the GraphADT instance representing the road network
     */
    public RoutingService(GraphADT graph) {
        this.graph = graph;
    }

    /**
     * Finds the fastest (shortest-weight) route between two locations
     * using Dijkstra's algorithm.
     *
     * <p>Uses NaivePriorityQueue as the PQ implementation. Once Issue #5
     * merges CustomPriorityQueue (backed by BinaryHeap), swap it in here.
     *
     * @param sourceLocationId starting location
     * @param destLocationId   destination location
     * @return a RouteResult; if unreachable, {@code reachable} is false
     */
    public Dijkstra.RouteResult fastestRoute(String sourceLocationId, String destLocationId) {
        Dijkstra dijkstra = new Dijkstra();
        // TODO: Once Issue #5 merges BinaryHeap, swap NaivePriorityQueue
        //       for CustomPriorityQueue here (one-line change).
        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        return dijkstra.shortestPath(graph, pq, sourceLocationId, destLocationId);
    }

    /**
     * Returns all location IDs reachable from the given starting point.
     *
     * <p><b>Implementation note (Issue #12):</b> BFS.java is owned by
     * Issue #10 and is still a stub. Instead of depending on it, this
     * method implements an inline iterative BFS directly using
     * {@link GraphADT#getNeighbors(String)}. The Issue #12 description
     * says "wire to BFS or DFS" — this inline traversal satisfies that
     * requirement while keeping this service fully independent of
     * Issue #10's timeline. If/when the BFS class is implemented, this
     * can optionally be refactored to delegate to it.
     *
     * @param fromLocationId the starting location
     * @return list of all reachable location IDs (including fromLocationId itself)
     */
    public List<String> reachableLocations(String fromLocationId) {
        /*
         * Inline iterative BFS implementation.
         *
         * WHY INLINE: BFS.java is owned by Issue #10 and throws
         * UnsupportedOperationException. Rather than waiting for that
         * issue to merge, we implement the traversal directly here using
         * GraphADT.getNeighbors(). This follows the Team_Handbook.docx
         * pattern of coding against interfaces and not blocking on other
         * issues.
         *
         * ALGORITHM: Standard breadth-first search using a FIFO queue.
         * Visit the source, enqueue its neighbours, and repeat until the
         * queue is empty. All visited vertices are reachable.
         */
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();

        visited.add(fromLocationId);
        queue.add(fromLocationId);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            for (String neighbour : graph.getNeighbors(current)) {
                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    queue.add(neighbour);
                }
            }
        }

        return new ArrayList<>(visited);
    }

    /**
     * Computes the minimum connecting network (MST) of the entire road
     * network using Prim's algorithm.
     *
     * <p>Uses NaivePriorityQueue as the PQ implementation. Once Issue #5
     * merges CustomPriorityQueue, swap it in here.
     *
     * @return the MST as edge descriptions and total cost
     */
    public Prim.MstResult minimumConnectingNetwork() {
        Prim prim = new Prim();
        // TODO: Once Issue #5 merges BinaryHeap, swap NaivePriorityQueue
        //       for CustomPriorityQueue here (one-line change).
        PriorityQueueADT<String> pq = new NaivePriorityQueue<>();
        return prim.minimumSpanningTree(graph, pq);
    }
}
