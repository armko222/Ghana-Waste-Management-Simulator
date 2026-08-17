package com.dcit308.wasteops.db;

import com.dcit308.wasteops.domain.AuditEvent;
import java.util.List;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Basic CRUD + bulk load for the auditevent table. Ordinary
 * Java/JDBC tools are fine to use here (brief Section 8.ii exempts db/
 * from the no-built-ins rule)[cite: 1].
 *
 * Owned by Issue #3.
 */
public class AuditEventRepository {

    private final Connection connection;

    public AuditEventRepository(Connection connection) {
        this.connection = connection;
    }

    public void save(AuditEvent item) {
        String sql = "INSERT INTO auditevent (event_id, event_type, related_request_id, description, timestamp) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, item.getEventId());
            stmt.setString(2, item.getEventType().name());

            // Handle nullable relatedRequestId cleanly
            if (item.getRelatedRequestId() != null) {
                stmt.setString(3, item.getRelatedRequestId());
            } else {
                stmt.setNull(3, Types.VARCHAR);
            }

            stmt.setString(4, item.getDescription());
            stmt.setTimestamp(5, Timestamp.valueOf(item.getTimestamp()));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error saving AuditEvent to database", e);
        }
    }

    public AuditEvent findById(String id) {
        String sql = "SELECT event_id, event_type, related_request_id, description, timestamp " +
                "FROM auditevent WHERE event_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToAuditEvent(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error finding AuditEvent by ID: " + id, e);
        }
        return null;
    }

    public List<AuditEvent> findAll() {
        List<AuditEvent> events = new ArrayList<>();
        String sql = "SELECT event_id, event_type, related_request_id, description, timestamp " +
                "FROM auditevent ORDER BY timestamp DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                events.add(mapRowToAuditEvent(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all AuditEvents", e);
        }
        return events;
    }

    // Helper method to map a ResultSet row to an AuditEvent instance
    private AuditEvent mapRowToAuditEvent(ResultSet rs) throws SQLException {
        String eventId = rs.getString("event_id");
        AuditEvent.EventType eventType = AuditEvent.EventType.valueOf(rs.getString("event_type"));
        String relatedRequestId = rs.getString("related_request_id");
        String description = rs.getString("description");
        LocalDateTime timestamp = rs.getTimestamp("timestamp").toLocalDateTime();

        return new AuditEvent(eventId, eventType, relatedRequestId, description, timestamp);
    }
}