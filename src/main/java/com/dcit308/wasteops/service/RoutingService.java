package com.dcit308.wasteops.service;

import com.dcit308.wasteops.algorithms.graph.BFS;
import com.dcit308.wasteops.algorithms.graph.Dijkstra;
import com.dcit308.wasteops.algorithms.graph.Kruskal;
import com.dcit308.wasteops.algorithms.graph.Prim;
import com.dcit308.wasteops.db.DatabaseManager;
import com.dcit308.wasteops.db.LocationRepository;
import com.dcit308.wasteops.db.RoadRepository;
import com.dcit308.wasteops.structures.DisjointSet;
import com.dcit308.wasteops.structures.GraphADT;
import com.dcit308.wasteops.structures.GraphAdjacencyList;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import com.dcit308.wasteops.structures.CustomPriorityQueue;
import java.util.List;

/**
 * Exposes: fastest route between two locations, reachable locations from
 * a point, minimum connecting network. This is what answers the brief's
 * Section 3 routing questions -- the console menu calls into this, not
 * into Dijkstra/BFS/Kruskal directly.
 *
 * Owned by Issue #12.
 */
public class RoutingService {

    private final DatabaseManager db;
    private final LocationRepository locationRepository;
    private final RoadRepository roadRepository;
    private GraphADT graph;

    public RoutingService(DatabaseManager db) {
        this.db = db;
        this.locationRepository = new LocationRepository();
        this.roadRepository = new RoadRepository();
        this.graph = null;
    }

    /**
     * Builds the graph from the database if not already built.
     */
    private void ensureGraphBuilt() {
        if (graph != null) {
            return;
        }

        graph = new GraphAdjacencyList();

        // Add all locations as vertices
        List<com.dcit308.wasteops.domain.Location> locations = locationRepository.findAll();
        for (com.dcit308.wasteops.domain.Location loc : locations) {
            graph.addVertex(loc.getLocationId());
        }

        // Add all roads as edges (directed)
        List<com.dcit308.wasteops.domain.Road> roads = roadRepository.findAll();
        for (com.dcit308.wasteops.domain.Road road : roads) {
            // Use travel_time_min as weight for routing
            graph.addEdge(road.getFromLocationId(), road.getToLocationId(), road.getTravelTimeMin());
        }
    }

    public Dijkstra.RouteResult fastestRoute(String sourceLocationId, String destLocationId) {
        ensureGraphBuilt();

        PriorityQueueADT<String> pq = new CustomPriorityQueue<>();
        Dijkstra dijkstra = new Dijkstra();
        return dijkstra.shortestPath(graph, pq, sourceLocationId, destLocationId);
    }

    public List<String> reachableLocations(String fromLocationId) {
        ensureGraphBuilt();

        BFS bfs = new BFS();
        return bfs.traverse(graph, fromLocationId);
    }

    public Prim.MstResult minimumConnectingNetwork() {
        ensureGraphBuilt();

        // Use Prim's algorithm for MST
        PriorityQueueADT<String> pq = new CustomPriorityQueue<>();
        Prim prim = new Prim();
        return prim.minimumSpanningTree(graph, pq);
    }
}