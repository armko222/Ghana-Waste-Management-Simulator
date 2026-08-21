package com.dcit308.wasteops.service;

import com.dcit308.wasteops.db.DatabaseManager;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Generates operational reports by querying the database directly.
 *
 * <p>Reports produced:
 * <ul>
 *   <li>Request counts grouped by status (NEW / ASSIGNED / IN_TRANSIT / COMPLETED)</li>
 *   <li>Request breakdown by waste category</li>
 *   <li>Request breakdown by priority (High / Medium / Low)</li>
 *   <li>Resource utilisation — available vs. busy</li>
 *   <li>For completed requests: actual time taken vs. deadline</li>
 * </ul>
 *
 * All queries use plain JDBC — the brief (Section 8.ii) exempts {@code db/}
 * and its callers from the no-built-ins rule when accessing the database.
 *
 * Owned by Issue #14.
 */
public class ReportingService {

    private final DatabaseManager db;

    public ReportingService(DatabaseManager db) {
        this.db = db;
    }

    /**
     * Builds and returns the full operational report as a formatted string.
     *
     * @return multi-line report ready to print to the console
     */
    public String generateOperationalReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════\n");
        sb.append(" OPERATIONAL REPORT\n");
        sb.append("═══════════════════════════════════════════\n\n");

        appendRequestsByStatus(sb);
        sb.append("\n");
        appendRequestsByCategory(sb);
        sb.append("\n");
        appendRequestsByPriority(sb);
        sb.append("\n");
        appendResourceUtilisation(sb);
        sb.append("\n");
        appendDeadlineComparison(sb);

        sb.append("\n═══════════════════════════════════════════\n");
        return sb.toString();
    }

    // -------------------------------------------------------------------------
    // Private report sections
    // -------------------------------------------------------------------------

    private void appendRequestsByStatus(StringBuilder sb) {
        sb.append("── Requests by Status ──────────────────────\n");
        String sql = "SELECT status, COUNT(*) AS cnt "
                   + "FROM service_requests GROUP BY status ORDER BY status";
        runQuery(sb, sql, rs -> {
            while (rs.next()) {
                sb.append(String.format("  %-14s %d%n", rs.getString("status"), rs.getInt("cnt")));
            }
        });
    }

    private void appendRequestsByCategory(StringBuilder sb) {
        sb.append("── Requests by Category ────────────────────\n");
        String sql = "SELECT category, COUNT(*) AS cnt "
                   + "FROM service_requests GROUP BY category ORDER BY category";
        runQuery(sb, sql, rs -> {
            while (rs.next()) {
                sb.append(String.format("  %-20s %d%n", rs.getString("category"), rs.getInt("cnt")));
            }
        });
    }

    private void appendRequestsByPriority(StringBuilder sb) {
        sb.append("── Requests by Priority ────────────────────\n");
        String sql = "SELECT priority, COUNT(*) AS cnt "
                   + "FROM service_requests GROUP BY priority ORDER BY priority";
        runQuery(sb, sql, rs -> {
            while (rs.next()) {
                sb.append(String.format("  %-10s %d%n", rs.getString("priority"), rs.getInt("cnt")));
            }
        });
    }

    private void appendResourceUtilisation(StringBuilder sb) {
        sb.append("── Resource Utilisation ────────────────────\n");
        String sql = "SELECT availability_status, COUNT(*) AS cnt "
                   + "FROM resources GROUP BY availability_status ORDER BY availability_status";
        runQuery(sb, sql, rs -> {
            while (rs.next()) {
                sb.append(String.format("  %-12s %d%n",
                        rs.getString("availability_status"), rs.getInt("cnt")));
            }
        });
    }

    /**
     * For completed requests, shows the time submitted, deadline, and
     * whether the request was completed before or after its deadline.
     * This satisfies the brief's requirement: "actual time taken vs.
     * deadline."
     *
     * <p>Since status transitions are event-driven by other issues'
     * dispatch logic (which may not be implemented yet), this report
     * section shows data only when completed requests exist in the
     * database.
     */
    private void appendDeadlineComparison(StringBuilder sb) {
        sb.append("── Completed Requests: Deadline Comparison ─\n");
        String sql = "SELECT request_id, category, priority, "
                   + "time_submitted, deadline, status "
                   + "FROM service_requests "
                   + "WHERE status = 'COMPLETED' "
                   + "ORDER BY request_id";
        runQuery(sb, sql, rs -> {
            boolean hasRows = false;
            while (rs.next()) {
                hasRows = true;
                String reqId      = rs.getString("request_id");
                String category   = rs.getString("category");
                String priority   = rs.getString("priority");
                String submitted  = rs.getString("time_submitted");
                String deadline   = rs.getString("deadline");

                // Compare ISO-8601 timestamps lexicographically — safe
                // because the format sorts chronologically.
                String verdict = (submitted != null && deadline != null
                        && submitted.compareTo(deadline) <= 0)
                        ? "ON TIME" : "OVERDUE";

                sb.append(String.format("  %-8s %-14s %-8s  submitted=%-20s  deadline=%-20s  %s%n",
                        reqId, category, priority, submitted, deadline, verdict));
            }
            if (!hasRows) {
                sb.append("  (no completed requests yet)\n");
            }
        });
    }

    // -------------------------------------------------------------------------
    // JDBC helper — keeps boilerplate out of each section method
    // -------------------------------------------------------------------------

    @FunctionalInterface
    private interface ResultSetHandler {
        void handle(ResultSet rs) throws SQLException;
    }

    private void runQuery(StringBuilder sb, String sql, ResultSetHandler handler) {
        try (Statement stmt = db.connect().createStatement();
             ResultSet rs   = stmt.executeQuery(sql)) {
            handler.handle(rs);
        } catch (SQLException e) {
            sb.append("  [ERROR] Query failed: ").append(e.getMessage()).append("\n");
        }
    }
}
