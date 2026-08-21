package com.dcit308.wasteops.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.dcit308.wasteops.domain.AlgorithmRun;

/**
 * Basic CRUD + bulk load for the algorithm_runs table.
 *
 * Ordinary Java/JDBC tools are fine to use here.
 *
 * Owned by Issue #14.
 */
public class AlgorithmRunRepository {

    /**
     * Saves an algorithm run.
     *
     * If a run with the same ID already exists,
     * its values are updated.
     */
    public void save(AlgorithmRun item) {

        String sql = """
                INSERT INTO algorithm_runs (
                    run_id,
                    algorithm_name,
                    input_size,
                    time_ns,
                    memory_kb,
                    date_run
                )
                VALUES (?, ?, ?, ?, ?, ?)
                ON CONFLICT(run_id) DO UPDATE SET
                    algorithm_name = excluded.algorithm_name,
                    input_size = excluded.input_size,
                    time_ns = excluded.time_ns,
                    memory_kb = excluded.memory_kb,
                    date_run = excluded.date_run
                """;

        try (Connection connection = new DatabaseManager().connect();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, item.getRunId());
            statement.setString(2, item.getAlgorithmName());
            statement.setInt(3, item.getInputSize());
            statement.setLong(4, item.getTimeNanos());

            // memoryKb is nullable.
            if (item.getMemoryKb() == null) {
                statement.setNull(5, java.sql.Types.INTEGER);
            } else {
                statement.setInt(5, item.getMemoryKb());
            }

            statement.setString(6, item.getDateRun());

            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to save algorithm run: " + item.getRunId(),
                    e
            );
        }
    }

    /**
     * Finds an algorithm run by its ID.
     *
     * @param id the run ID
     * @return the AlgorithmRun if found, otherwise null
     */
    public AlgorithmRun findById(String id) {

        String sql = """
                SELECT
                    run_id,
                    algorithm_name,
                    input_size,
                    time_ns,
                    memory_kb,
                    date_run
                FROM algorithm_runs
                WHERE run_id = ?
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
                    "Failed to find algorithm run: " + id,
                    e
            );
        }
    }

    /**
     * Returns all algorithm runs.
     */
    public List<AlgorithmRun> findAll() {

        String sql = """
                SELECT
                    run_id,
                    algorithm_name,
                    input_size,
                    time_ns,
                    memory_kb,
                    date_run
                FROM algorithm_runs
                ORDER BY date_run
                """;

        List<AlgorithmRun> runs = new ArrayList<>();

        try (Connection connection = new DatabaseManager().connect();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                runs.add(mapRow(resultSet));
            }

            return runs;

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to retrieve algorithm runs.",
                    e
            );
        }
    }

    /**
     * Converts a database row into an AlgorithmRun object.
     */
    private AlgorithmRun mapRow(ResultSet resultSet) throws SQLException {

        Integer memoryKb =
                resultSet.getObject("memory_kb", Integer.class);

        return new AlgorithmRun(
                resultSet.getString("run_id"),
                resultSet.getString("algorithm_name"),
                resultSet.getInt("input_size"),
                resultSet.getLong("time_ns"),
                memoryKb,
                resultSet.getString("date_run")
        );
    }
}