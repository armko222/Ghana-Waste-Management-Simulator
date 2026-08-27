package com.dcit308.wasteops.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dcit308.wasteops.domain.Resource;

/**
 * Basic CRUD + bulk load for the resources table.
 *
 * Ordinary Java/JDBC tools are allowed in the db package.
 *
 * Owned by Issue #1.
 */
public class ResourceRepository {

    private final DatabaseManager databaseManager;

    public ResourceRepository() {
        this.databaseManager = new DatabaseManager();
    }

    /**
     * Saves a resource.
     *
     * If a resource with the same ID already exists,
     * its values are updated.
     */
    public void save(Resource item) {

        String sql = """
                INSERT INTO resources (
                    resource_id,
                    resource_type,
                    home_location_id,
                    capacity,
                    availability_status
                )
                VALUES (?, ?, ?, ?, ?)
                ON CONFLICT(resource_id) DO UPDATE SET
                    resource_type = excluded.resource_type,
                    home_location_id = excluded.home_location_id,
                    capacity = excluded.capacity,
                    availability_status = excluded.availability_status
                """;

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, item.getResourceId());

            statement.setString(
                    2,
                    item.getResourceType().name()
            );

            statement.setString(
                    3,
                    item.getHomeLocationId()
            );

            statement.setInt(
                    4,
                    item.getCapacity()
            );

            statement.setString(
                    5,
                    item.getAvailabilityStatus().name()
            );

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to save resource: " + item.getResourceId(),
                    e
            );
        }
    }

    /**
     * Finds a resource by its ID.
     *
     * @param id resource ID
     * @return Resource if found, otherwise null
     */
    public Resource findById(String id) {

        String sql = """
                SELECT
                    resource_id,
                    resource_type,
                    home_location_id,
                    capacity,
                    availability_status
                FROM resources
                WHERE resource_id = ?
                """;

        try (Connection connection = databaseManager.connect();
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
                    "Failed to find resource: " + id,
                    e
            );
        }
    }

    /**
     * Returns all resources.
     */
    public List<Resource> findAll() {

        String sql = """
                SELECT
                    resource_id,
                    resource_type,
                    home_location_id,
                    capacity,
                    availability_status
                FROM resources
                ORDER BY resource_id
                """;

        List<Resource> resources = new ArrayList<>();

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                resources.add(mapRow(resultSet));
            }

            return resources;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to retrieve resources.",
                    e
            );
        }
    }

    /**
     * Converts one database row into a Resource object.
     */
    private Resource mapRow(ResultSet resultSet) throws SQLException {

        Resource.Type resourceType =
                Resource.Type.valueOf(
                        resultSet.getString("resource_type")
                                .toUpperCase()
                );

        Resource resource = new Resource(
                resultSet.getString("resource_id"),
                resourceType,
                resultSet.getString("home_location_id"),
                resultSet.getInt("capacity")
        );

        Resource.Availability availability =
                Resource.Availability.valueOf(
                        resultSet.getString("availability_status")
                                .toUpperCase()
                );

        resource.setAvailabilityStatus(availability);

        return resource;
    }
}