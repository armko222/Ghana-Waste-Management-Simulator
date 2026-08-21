package com.dcit308.wasteops.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dcit308.wasteops.domain.AuditEvent;

/**
 * Basic CRUD + bulk load for the audit_events table.
 *
 * Ordinary Java/JDBC tools are fine to use here.
 *
 * Owned by Issue #3.
 */
public class AuditEventRepository {

    /**
     * Saves an audit event.
     *
     * If an event with the same ID already exists,
     * its values are updated.
     */
    public void save(AuditEvent item) {

        String sql = """
                INSERT INTO audit_events (
                    event_id,
                    event_type,
                    related_request_id,
                    description,
                    timestamp
                )
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT(event_id) DO UPDATE SET
                    event_type = excluded.event_type,
                    related_request_id = excluded.related_request_id,
                    description = excluded.description,
                    timestamp = excluded.timestamp
                """;

        try (Connection connection = new DatabaseManager().connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, item.getEventId());

            // Store the enum as text.
            statement.setString(2, item.getEventType().name());

            // relatedRequestId is nullable.
            if (item.getRelatedRequestId() == null) {
                statement.setNull(3, java.sql.Types.VARCHAR);
            } else {
                statement.setString(3, item.getRelatedRequestId());
            }

            statement.setString(4, item.getDescription());

            // Store LocalDateTime as ISO-8601 text.
            statement.setString(5, item.getTimestamp().toString());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save audit event: " + item.getEventId(),
                    e
            );
        }
    }

    /**
     * Finds an audit event by its ID.
     *
     * @param id event ID
     * @return the AuditEvent if found, otherwise null
     */
    public AuditEvent findById(String id) {

        String sql = """
                SELECT
                    event_id,
                    event_type,
                    related_request_id,
                    description,
                    timestamp
                FROM audit_events
                WHERE event_id = ?
                """;

        try (Connection connection = new DatabaseManager().connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find audit event: " + id,
                    e
            );
        }
    }

    /**
     * Returns all audit events.
     */
    public List<AuditEvent> findAll() {

        String sql = """
                SELECT
                    event_id,
                    event_type,
                    related_request_id,
                    description,
                    timestamp
                FROM audit_events
                ORDER BY timestamp
                """;

        List<AuditEvent> events = new ArrayList<>();

        try (Connection connection = new DatabaseManager().connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                events.add(mapRow(resultSet));
            }

            return events;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to retrieve audit events.",
                    e
            );
        }
    }

    /**
     * Converts a database row into an AuditEvent object.
     */
    private AuditEvent mapRow(ResultSet resultSet) throws SQLException {

        String relatedRequestId =
                resultSet.getString("related_request_id");

        String eventType =
                resultSet.getString("event_type");

        String timestamp =
                resultSet.getString("timestamp");

        return new AuditEvent(
                resultSet.getString("event_id"),
                AuditEvent.EventType.valueOf(eventType),
                relatedRequestId,
                resultSet.getString("description"),
                LocalDateTime.parse(timestamp)
        );
    }
}