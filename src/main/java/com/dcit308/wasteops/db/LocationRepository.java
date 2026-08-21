package com.dcit308.wasteops.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dcit308.wasteops.domain.Location;

/**
 * Basic CRUD + bulk load for the locations table.
 *
 * Ordinary Java/JDBC tools are allowed in the db package.
 *
 * Owned by Issue #1.
 */
public class LocationRepository {

    private final DatabaseManager databaseManager;

    public LocationRepository() {
        this.databaseManager = new DatabaseManager();
    }

    /**
     * Saves a location.
     *
     * If a location with the same ID already exists,
     * its values are updated.
     */
    public void save(Location item) {

        String sql = """
                INSERT INTO locations (
                    location_id,
                    name,
                    area,
                    location_type,
                    x_coord,
                    y_coord
                )
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(location_id) DO UPDATE SET
                    name = excluded.name,
                    area = excluded.area,
                    location_type = excluded.location_type,
                    x_coord = excluded.x_coord,
                    y_coord = excluded.y_coord
                """;

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, item.getLocationId());
            statement.setString(2, item.getName());
            statement.setString(3, item.getArea());
            statement.setString(4, item.getLocationType());
            statement.setDouble(5, item.getXCoord());
            statement.setDouble(6, item.getYCoord());

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to save location: " + item.getLocationId(),
                    e
            );
        }
    }

    /**
     * Finds a location by its ID.
     *
     * @param id location ID
     * @return Location if found, otherwise null
     */
    public Location findById(String id) {

        String sql = """
                SELECT
                    location_id,
                    name,
                    area,
                    location_type,
                    x_coord,
                    y_coord
                FROM locations
                WHERE location_id = ?
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
                    "Failed to find location: " + id,
                    e
            );
        }
    }

    /**
     * Returns all locations.
     */
    public List<Location> findAll() {

        String sql = """
                SELECT
                    location_id,
                    name,
                    area,
                    location_type,
                    x_coord,
                    y_coord
                FROM locations
                ORDER BY location_id
                """;

        List<Location> locations = new ArrayList<>();

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                locations.add(mapRow(resultSet));
            }

            return locations;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to retrieve locations.",
                    e
            );
        }
    }

    /**
     * Converts one database row into a Location object.
     */
    private Location mapRow(ResultSet resultSet) throws SQLException {

        return new Location(
                resultSet.getString("location_id"),
                resultSet.getString("name"),
                resultSet.getString("area"),
                resultSet.getString("location_type"),
                resultSet.getDouble("x_coord"),
                resultSet.getDouble("y_coord")
        );
    }
}
