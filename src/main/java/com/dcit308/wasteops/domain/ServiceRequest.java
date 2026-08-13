package com.dcit308.wasteops.domain;

import java.time.LocalDateTime;

/**
 * A waste-collection request. Mirrors the `service_requests` table.
 *
 * Status is derived from elapsed simulated travel time when queried, not
 * set manually by any menu action — see docs/DECISION_LOG.md Decision D2
 * for why this replaced the original autonomous-clock design, and
 * Decision D9 for why both a tiered `priority` (High/Medium/Low, for the
 * heap-based dispatcher) and a numeric `urgency` (1-5, for the
 * urgency-ranked dispatcher) exist as separate fields.
 *
 * Owned by Issue #1. Status transition logic (computeStatus) is a natural
 * extension point for Issue #13 (Service Layer) once dispatch timestamps
 * exist.
 */
public class ServiceRequest {

    public enum Status { NEW, ASSIGNED, IN_TRANSIT, COMPLETED }
    public enum Priority { HIGH, MEDIUM, LOW }

    private final String requestId;
    private final String sourceLocationId;
    private final String destinationLocationId;
    private final String category;
    private final int urgency; // 1-5
    private final Priority priority;
    private final LocalDateTime timeSubmitted;
    private final LocalDateTime deadline;
    private Status status;
    private String assignedResourceId; // nullable until dispatched
    private LocalDateTime dispatchTime; // nullable until dispatched

    public ServiceRequest(String requestId, String sourceLocationId, String destinationLocationId,
                           String category, int urgency, Priority priority,
                           LocalDateTime timeSubmitted, LocalDateTime deadline) {
        if (urgency < 1 || urgency > 5) {
            throw new IllegalArgumentException("urgency must be between 1 and 5, got: " + urgency);
        }
        this.requestId = requestId;
        this.sourceLocationId = sourceLocationId;
        this.destinationLocationId = destinationLocationId;
        this.category = category;
        this.urgency = urgency;
        this.priority = priority;
        this.timeSubmitted = timeSubmitted;
        this.deadline = deadline;
        this.status = Status.NEW;
    }

    public String getRequestId() { return requestId; }
    public String getSourceLocationId() { return sourceLocationId; }
    public String getDestinationLocationId() { return destinationLocationId; }
    public String getCategory() { return category; }
    public int getUrgency() { return urgency; }
    public Priority getPriority() { return priority; }
    public LocalDateTime getTimeSubmitted() { return timeSubmitted; }
    public LocalDateTime getDeadline() { return deadline; }
    public Status getStatus() { return status; }
    public String getAssignedResourceId() { return assignedResourceId; }
    public LocalDateTime getDispatchTime() { return dispatchTime; }

    /**
     * Called once by the dispatch service (Issue #12/#13) when a resource
     * is assigned. Does not itself flip status to IN_TRANSIT/COMPLETED —
     * that is derived on read via computeStatus(), per Decision D2.
     */
    public void assignResource(String resourceId, LocalDateTime dispatchTime) {
        this.assignedResourceId = resourceId;
        this.dispatchTime = dispatchTime;
        this.status = Status.ASSIGNED;
    }

    /**
     * Derives the current status from elapsed simulated time, given the
     * travel time computed by Dijkstra for this request's route (Issue
     * #11 / #13 supply travelTimeMinutes). This replaces a background
     * clock thread — see Decision D2 in docs/DECISION_LOG.md.
     *
     * @param now                current simulated time
     * @param travelTimeMinutes  route travel time from source to destination
     * @return the derived status; does not mutate internal state — callers
     *         decide whether/when to persist the transition
     */
    public Status computeStatus(LocalDateTime now, double travelTimeMinutes) {
        if (status == Status.NEW || dispatchTime == null) {
            return status;
        }
        double elapsedMinutes = java.time.Duration.between(dispatchTime, now).toMinutes();
        if (elapsedMinutes < travelTimeMinutes) {
            return Status.IN_TRANSIT;
        }
        return Status.COMPLETED;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ServiceRequest{" + requestId + ", " + category + ", urgency=" + urgency
                + ", priority=" + priority + ", status=" + status + "}";
    }
}
