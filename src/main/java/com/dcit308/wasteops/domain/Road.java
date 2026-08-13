package com.dcit308.wasteops.domain;

/**
 * A weighted edge in the road-network graph. Mirrors the `roads` table.
 * conditionWeight is a realistic-constraint multiplier (Section 2.ii of
 * the brief) and is also where the index-number-derived "route penalty"
 * parameter (Decision D10, Issue #11) gets applied at query time.
 *
 * Owned by Issue #1; consumed directly by Issue #10 (Graph structures)
 * and Issue #11 (Dijkstra/Prim/Kruskal).
 */
public class Road {

    private final String roadId;
    private final String fromLocationId;
    private final String toLocationId;
    private final double distanceKm;
    private final double travelTimeMin;
    private final double conditionWeight;

    public Road(String roadId, String fromLocationId, String toLocationId,
                double distanceKm, double travelTimeMin, double conditionWeight) {
        this.roadId = roadId;
        this.fromLocationId = fromLocationId;
        this.toLocationId = toLocationId;
        this.distanceKm = distanceKm;
        this.travelTimeMin = travelTimeMin;
        this.conditionWeight = conditionWeight;
    }

    public String getRoadId() { return roadId; }
    public String getFromLocationId() { return fromLocationId; }
    public String getToLocationId() { return toLocationId; }
    public double getDistanceKm() { return distanceKm; }
    public double getTravelTimeMin() { return travelTimeMin; }
    public double getConditionWeight() { return conditionWeight; }

    /**
     * The effective edge weight used by graph algorithms: travel time
     * scaled by road condition. Issue #11 applies the index-number-derived
     * route-penalty parameter on top of this base weight, not inside it,
     * so this class stays independent of any team-specific parameter.
     */
    public double baseWeight() {
        return travelTimeMin * conditionWeight;
    }

    @Override
    public String toString() {
        return "Road{" + roadId + ": " + fromLocationId + " -> " + toLocationId
                + ", weight=" + baseWeight() + "}";
    }
}
