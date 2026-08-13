package com.dcit308.wasteops.domain;

/**
 * A vertex in the road-network graph. Mirrors the `locations` table.
 * See docs/DATA_DICTIONARY.md for field definitions and
 * docs/DECISION_LOG.md Decision D7 for why there is no separate
 * Customer or WasteBin entity — every request originates from a Location.
 *
 * Owned by Issue #1 (Domain Model, Database Schema & CSV Import).
 */
public class Location {

    private final String locationId;
    private final String name;
    private final String area;
    private final String locationType; // House | School | Hospital | Factory | Market | Office | ...
    private final double xCoord;
    private final double yCoord;

    public Location(String locationId, String name, String area,
                     String locationType, double xCoord, double yCoord) {
        this.locationId = locationId;
        this.name = name;
        this.area = area;
        this.locationType = locationType;
        this.xCoord = xCoord;
        this.yCoord = yCoord;
    }

    public String getLocationId() { return locationId; }
    public String getName() { return name; }
    public String getArea() { return area; }
    public String getLocationType() { return locationType; }
    public double getXCoord() { return xCoord; }
    public double getYCoord() { return yCoord; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Location)) return false;
        return locationId.equals(((Location) o).locationId);
    }

    @Override
    public int hashCode() {
        // NOTE: this uses Java's built-in hashCode as an object-identity
        // convenience only (equals/hashCode contract for domain objects).
        // It is NOT a substitute for the custom CustomHashTable required
        // by Issue #9 / brief Section 6 — that structure must be built
        // from scratch and is used for ID lookups system-wide.
        return locationId.hashCode();
    }

    @Override
    public String toString() {
        return "Location{" + locationId + ", " + name + ", " + area + "}";
    }
}
