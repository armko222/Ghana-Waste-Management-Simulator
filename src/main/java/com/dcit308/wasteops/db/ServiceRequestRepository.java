package com.dcit308.wasteops.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.dcit308.wasteops.domain.ServiceRequest;

/**
 * Basic CRUD + bulk load for the service_requests table.
 *
 * <p>
 * Issue #13 uses this repository to obtain pending requests for the
 * different dispatch strategies.
 *
 * <p>
 * The repository is responsible only for retrieving/storing data.
 * The actual dispatch rules remain inside the scheduling package.
 *
 * Owned by Issue #1.
 */
public class ServiceRequestRepository {

    private final DatabaseManager databaseManager;

    public ServiceRequestRepository() {
        this.databaseManager = new DatabaseManager();
    }

    /**
     * Allows the repository to share an existing DatabaseManager.
     *
     * This is useful for the console application because Main already
     * creates and opens a DatabaseManager.
     */
    public ServiceRequestRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    /**
     * Saves a service request.
     *
     * If a request with the same ID already exists,
     * its values are updated.
     */
    public void save(ServiceRequest item) {

        String sql = """
                INSERT INTO service_requests (
                    request_id,
                    source_location_id,
                    destination_location_id,
                    category,
                    urgency,
                    priority,
                    time_submitted,
                    deadline,
                    status,
                    assigned_resource_id
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                ON CONFLICT(request_id) DO UPDATE SET
                    source_location_id = excluded.source_location_id,
                    destination_location_id = excluded.destination_location_id,
                    category = excluded.category,
                    urgency = excluded.urgency,
                    priority = excluded.priority,
                    time_submitted = excluded.time_submitted,
                    deadline = excluded.deadline,
                    status = excluded.status,
                    assigned_resource_id = excluded.assigned_resource_id
                """;

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, item.getRequestId());
            statement.setString(2, item.getSourceLocationId());
            statement.setString(3, item.getDestinationLocationId());
            statement.setString(4, item.getCategory());
            statement.setInt(5, item.getUrgency());
            statement.setString(6, item.getPriority().name());
            statement.setString(7, item.getTimeSubmitted().toString());
            statement.setString(8, item.getDeadline().toString());
            statement.setString(9, item.getStatus().name());

            if (item.getAssignedResourceId() == null) {
                statement.setNull(10, java.sql.Types.VARCHAR);
            } else {
                statement.setString(10, item.getAssignedResourceId());
            }

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save service request: "
                            + item.getRequestId(),
                    e
            );
        }
    }

    /**
     * Finds a service request by its ID.
     *
     * @param id request ID
     * @return ServiceRequest if found, otherwise null
     */
    public ServiceRequest findById(String id) {

        String sql = """
                SELECT
                    request_id,
                    source_location_id,
                    destination_location_id,
                    category,
                    urgency,
                    priority,
                    time_submitted,
                    deadline,
                    status,
                    assigned_resource_id
                FROM service_requests
                WHERE request_id = ?
                """;

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapRow(resultSet);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find service request: " + id,
                    e
            );
        }
    }

    /**
     * Returns all service requests.
     */
    public List<ServiceRequest> findAll() {

        String sql = """
                SELECT
                    request_id,
                    source_location_id,
                    destination_location_id,
                    category,
                    urgency,
                    priority,
                    time_submitted,
                    deadline,
                    status,
                    assigned_resource_id
                FROM service_requests
                ORDER BY request_id
                """;

        List<ServiceRequest> requests = new ArrayList<>();

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                requests.add(mapRow(resultSet));
            }

            return requests;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to retrieve service requests.",
                    e
            );
        }
    }

    /**
     * Returns all NEW/pending service requests ordered by submission time.
     *
     * <p>
     * This ordering is specifically required by Issue #13 for FIFO.
     * The database performs the ordering so FifoDispatcher itself does
     * not need to sort the requests.
     *
     * @return pending requests ordered by time_submitted ascending
     */
    public List<ServiceRequest> findPending() {

        String sql = """
                SELECT
                    request_id,
                    source_location_id,
                    destination_location_id,
                    category,
                    urgency,
                    priority,
                    time_submitted,
                    deadline,
                    status,
                    assigned_resource_id
                FROM service_requests
                WHERE status = 'NEW'
                ORDER BY time_submitted ASC
                """;

        List<ServiceRequest> requests = new ArrayList<>();

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                requests.add(mapRow(resultSet));
            }

            return requests;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to retrieve pending service requests.",
                    e
            );
        }
    }

    /**
     * Converts a database row into a ServiceRequest object.
     */
    private ServiceRequest mapRow(ResultSet resultSet)
            throws SQLException {

        ServiceRequest.Priority priority =
                ServiceRequest.Priority.valueOf(
                        resultSet
                                .getString("priority")
                                .toUpperCase()
                );

        LocalDateTime timeSubmitted =
                LocalDateTime.parse(
                        resultSet.getString("time_submitted")
                );

        LocalDateTime deadline =
                LocalDateTime.parse(
                        resultSet.getString("deadline")
                );

        ServiceRequest request = new ServiceRequest(
                resultSet.getString("request_id"),
                resultSet.getString("source_location_id"),
                resultSet.getString("destination_location_id"),
                resultSet.getString("category"),
                resultSet.getInt("urgency"),
                priority,
                timeSubmitted,
                deadline
        );

        /*
         * Restore persisted status.
         */
        ServiceRequest.Status status =
                ServiceRequest.Status.valueOf(
                        resultSet
                                .getString("status")
                                .toUpperCase()
                );

        request.setStatus(status);

        /*
         * assigned_resource_id is currently not restored because
         * ServiceRequest.assignResource() also requires a dispatch time,
         * while the current database schema does not persist dispatch_time.
         *
         * This is outside the core scope of Issue #13 and can be addressed
         * later when dispatch/resource assignment is integrated.
         */
        return request;
    }
}