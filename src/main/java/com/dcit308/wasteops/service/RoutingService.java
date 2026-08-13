package com.dcit308.wasteops.service;

import com.dcit308.wasteops.algorithms.graph.Dijkstra;
import com.dcit308.wasteops.algorithms.graph.Prim;
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

    public Dijkstra.RouteResult fastestRoute(String sourceLocationId, String destLocationId) {
        throw new UnsupportedOperationException("TODO: Issue #12 \u2014 wire to Dijkstra.");
    }

    public List<String> reachableLocations(String fromLocationId) {
        throw new UnsupportedOperationException("TODO: Issue #12 \u2014 wire to BFS or DFS.");
    }

    public Prim.MstResult minimumConnectingNetwork() {
        throw new UnsupportedOperationException("TODO: Issue #12 \u2014 wire to Prim or Kruskal.");
    }
}
