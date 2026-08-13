package com.dcit308.wasteops.domain;

/**
 * A dispatchable resource (truck+driver as one unit). Mirrors the
 * `resources` table. See docs/DECISION_LOG.md Decision D8 for why this
 * merges the original separate Truck/Driver entities into the brief's
 * single `resources` schema.
 *
 * Owned by Issue #1.
 */
public class Resource {

    public enum Type { GENERAL, HAZARDOUS, INDUSTRIAL }
    public enum Availability { AVAILABLE, IN_TRANSIT, COLLECTING, RETURNING }

    private final String resourceId;
    private final Type resourceType;
    private final String homeLocationId;
    private final int capacity;
    private Availability availabilityStatus;

    public Resource(String resourceId, Type resourceType, String homeLocationId, int capacity) {
        this.resourceId = resourceId;
        this.resourceType = resourceType;
        this.homeLocationId = homeLocationId;
        this.capacity = capacity;
        this.availabilityStatus = Availability.AVAILABLE;
    }

    public String getResourceId() { return resourceId; }
    public Type getResourceType() { return resourceType; }
    public String getHomeLocationId() { return homeLocationId; }
    public int getCapacity() { return capacity; }
    public Availability getAvailabilityStatus() { return availabilityStatus; }

    public void setAvailabilityStatus(Availability status) {
        this.availabilityStatus = status;
    }

    @Override
    public String toString() {
        return "Resource{" + resourceId + ", " + resourceType + ", capacity=" + capacity
                + ", status=" + availabilityStatus + "}";
    }
}
