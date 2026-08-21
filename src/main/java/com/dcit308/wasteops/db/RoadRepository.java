package com.dcit308.wasteops.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dcit308.wasteops.domain.Road;

/**
 * Basic CRUD + bulk load for the roads table.
 *
 * Ordinary Java/JDBC tools are allowed in the db package.
 *
 * Owned by Issue #1.
 */
public class RoadRepository {

    private final DatabaseManager databaseManager;

    public RoadRepository() {
        this.databaseManager = new DatabaseManager();
    }

    /**
     * Saves a road.
     *
     * If a road with the same ID already exists,
     * its values are updated.
     */
    public void save(Road item) {

        String sql = """
                INSERT INTO roads (
                    road_id,
                    from_location_id,
                    to_location_id,
                    distance_km,
                    travel_time_min,
                    condition_weight
                )
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(road_id) DO UPDATE SET
                    from_location_id = excluded.from_location_id,
                    to_location_id = excluded.to_location_id,
                    distance_km = excluded.distance_km,
                    travel_time_min = excluded.travel_time_min,
                    condition_weight = excluded.condition_weight
                """;

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, item.getRoadId());
            statement.setString(2, item.getFromLocationId());
            statement.setString(3, item.getToLocationId());
            statement.setDouble(4, item.getDistanceKm());
            statement.setDouble(5, item.getTravelTimeMin());
            statement.setDouble(6, item.getConditionWeight());

            statement.executeUpdate();

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to save road: " + item.getRoadId(),
                    e
            );
        }
    }

    /**
     * Finds a road by its ID.
     *
     * @param id road ID
     * @return Road if found, otherwise null
     */
    public Road findById(String id) {

        String sql = """
                SELECT
                    road_id,
                    from_location_id,
                    to_location_id,
                    distance_km,
                    travel_time_min,
                    condition_weight
                FROM roads
                WHERE road_id = ?
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
                    "Failed to find road: " + id,
                    e
            );
        }
    }

    /**
     * Returns all roads.
     */
    public List<Road> findAll() {

        String sql = """
                SELECT
                    road_id,
                    from_location_id,
                    to_location_id,
                    distance_km,
                    travel_time_min,
                    condition_weight
                FROM roads
                ORDER BY road_id
                """;

        List<Road> roads = new ArrayList<>();

        try (Connection connection = databaseManager.connect();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                roads.add(mapRow(resultSet));
            }

            return roads;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to retrieve roads.",
                    e
            );
        }
    }

    /**
     * Converts one database row into a Road object.
     */
    private Road mapRow(ResultSet resultSet) throws SQLException {

        return new Road(
                resultSet.getString("road_id"),
                resultSet.getString("from_location_id"),
                resultSet.getString("to_location_id"),
                resultSet.getDouble("distance_km"),
                resultSet.getDouble("travel_time_min"),
                resultSet.getDouble("condition_weight")
        );
    }
}