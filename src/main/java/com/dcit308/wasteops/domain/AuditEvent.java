package com.dcit308.wasteops.domain;

import java.time.LocalDateTime;

/**
 * A single entry in the stack-backed undo/audit log. Mirrors the
 * `audit_events` table. See docs/DECISION_LOG.md Decision D4 for why
 * Stack's operational role is this audit trail.
 *
 * Owned by Issue #1 (domain object); pushed/popped by Issue #3's
 * ArrayStack in structures/.
 */
public class AuditEvent {

    public enum EventType { DISPATCH, STATUS_CHANGE, IMPORT }

    private final String eventId;
    private final EventType eventType;
    private final String relatedRequestId; // nullable
    private final String description;
    private final LocalDateTime timestamp;

    public AuditEvent(String eventId, EventType eventType, String relatedRequestId,
                       String description, LocalDateTime timestamp) {
        this.eventId = eventId;
        this.eventType = eventType;
        this.relatedRequestId = relatedRequestId;
        this.description = description;
        this.timestamp = timestamp;
    }

    public String getEventId() { return eventId; }
    public EventType getEventType() { return eventType; }
    public String getRelatedRequestId() { return relatedRequestId; }
    public String getDescription() { return description; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        return "AuditEvent{" + eventType + ", " + description + ", " + timestamp + "}";
    }
}
