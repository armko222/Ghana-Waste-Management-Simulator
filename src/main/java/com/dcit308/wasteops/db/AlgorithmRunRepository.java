package com.dcit308.wasteops.db;

import com.dcit308.wasteops.domain.AlgorithmRun;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * CRUD + batch operations for the {@code algorithm_runs} table.
 * Ordinary Java/JDBC tools are fine here (brief Section 8.ii exempts
 * {@code db/} from the no-built-ins rule).
 *
 * Owned by Issue #14.
 */
public class AlgorithmRunRepository {

    private final DatabaseManager db;

    public AlgorithmRunRepository(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Inserts a single algorithm run into the database.
     *
     * @param item the run to persist
     * @throws RuntimeException if the SQL execution fails
     */
    public void save(AlgorithmRun item) {
        String sql = "INSERT OR REPLACE INTO algorithm_runs "
                   + "(run_id, algorithm_name, input_size, time_ns, memory_kb, date_run) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = db.connect().prepareStatement(sql)) {
            ps.setString(1, item.getRunId());
            ps.setString(2, item.getAlgorithmName());
            ps.setInt(3, item.getInputSize());
            ps.setLong(4, item.getTimeNanos());
            if (item.getMemoryKb() != null) {
                ps.setInt(5, item.getMemoryKb());
            } else {
                ps.setNull(5, java.sql.Types.INTEGER);
            }
            ps.setString(6, item.getDateRun());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save AlgorithmRun: " + e.getMessage(), e);
        }
    }

    /**
     * Inserts multiple algorithm runs in a single transaction for
     * efficiency — used by {@code ExperimentService} after each
     * experiment category completes.
     *
     * @param items the runs to persist
     * @throws RuntimeException if the SQL execution fails
     */
    public void saveAll(List<AlgorithmRun> items) {
        if (items == null || items.isEmpty()) {
            return;
        }
        String sql = "INSERT OR REPLACE INTO algorithm_runs "
                   + "(run_id, algorithm_name, input_size, time_ns, memory_kb, date_run) "
                   + "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = db.connect();
        try {
            conn.setAutoCommit(false);
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                for (AlgorithmRun item : items) {
                    ps.setString(1, item.getRunId());
                    ps.setString(2, item.getAlgorithmName());
                    ps.setInt(3, item.getInputSize());
                    ps.setLong(4, item.getTimeNanos());
                    if (item.getMemoryKb() != null) {
                        ps.setInt(5, item.getMemoryKb());
                    } else {
                        ps.setNull(5, java.sql.Types.INTEGER);
                    }
                    ps.setString(6, item.getDateRun());
                    ps.addBatch();
                }
                ps.executeBatch();
            }
            conn.commit();
        } catch (SQLException e) {
            try { conn.rollback(); } catch (SQLException ignored) { }
            throw new RuntimeException("Failed to batch-save AlgorithmRuns: " + e.getMessage(), e);
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ignored) { }
        }
    }

    /**
     * Retrieves a single run by its primary key.
     *
     * @param id the {@code run_id}
     * @return the matching {@link AlgorithmRun}, or {@code null} if not found
     */
    public AlgorithmRun findById(String id) {
        String sql = "SELECT run_id, algorithm_name, input_size, time_ns, memory_kb, date_run "
                   + "FROM algorithm_runs WHERE run_id = ?";
        try (PreparedStatement ps = db.connect().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find AlgorithmRun by id: " + e.getMessage(), e);
        }
    }

    /**
     * Returns every algorithm run stored in the database.
     *
     * @return all rows, possibly empty
     */
    public List<AlgorithmRun> findAll() {
        String sql = "SELECT run_id, algorithm_name, input_size, time_ns, memory_kb, date_run "
                   + "FROM algorithm_runs ORDER BY algorithm_name, input_size, date_run";
        List<AlgorithmRun> results = new ArrayList<>();
        try (PreparedStatement ps = db.connect().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                results.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list AlgorithmRuns: " + e.getMessage(), e);
        }
        return results;
    }

    // ------------------------------------------------------------------
    // Internal helper
    // ------------------------------------------------------------------

    private AlgorithmRun mapRow(ResultSet rs) throws SQLException {
        int memoryKbRaw = rs.getInt("memory_kb");
        Integer memoryKb = rs.wasNull() ? null : memoryKbRaw;
        return new AlgorithmRun(
                rs.getString("run_id"),
                rs.getString("algorithm_name"),
                rs.getInt("input_size"),
                rs.getLong("time_ns"),
                memoryKb,
                rs.getString("date_run")
        );
    }
}
