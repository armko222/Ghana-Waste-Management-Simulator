package com.dcit308.wasteops.service;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.dcit308.wasteops.db.DatabaseManager;

/**
 * Tests for {@link ReportingService} — verifies that the operational
 * report generates correctly on both empty and populated databases.
 *
 * Owned by Issue #14.
 */
class ReportingServiceTest {

    private DatabaseManager db;
    private ReportingService service;

    @BeforeEach
    void setUp() {
        db = new TestDatabaseManager();
        db.connect();
        db.initSchemaIfNeeded();
        service = new ReportingService(db);
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    @DisplayName("generateOperationalReport on empty database does not crash")
    void reportOnEmptyDb() {
        String report = service.generateOperationalReport();

        assertNotNull(report, "Report should not be null");
        assertTrue(
                report.contains("OPERATIONAL REPORT"),
                "Report should contain the header"
        );
        assertTrue(
                report.contains("Requests by Status"),
                "Report should contain status section"
        );
        assertTrue(
                report.contains("Requests by Category"),
                "Report should contain category section"
        );
        assertTrue(
                report.contains("Requests by Priority"),
                "Report should contain priority section"
        );
        assertTrue(
                report.contains("Resource Utilisation"),
                "Report should contain utilisation section"
        );
        assertTrue(
                report.contains("Deadline Comparison"),
                "Report should contain deadline comparison section"
        );
    }

    @Test
    @DisplayName("report shows '(no completed requests yet)' when no completed requests")
    void reportShowsNoCompletedRequests() {
        String report = service.generateOperationalReport();

        assertTrue(
                report.contains("no completed requests yet"),
                "Deadline section should indicate no completed requests on empty DB"
        );
    }

    @Test
    @DisplayName("report includes data when service_requests are seeded")
    void reportWithSeededData() {

        // Seed locations first because service_requests has foreign keys
        // referencing the locations table.
        try (var stmt = db.connect().createStatement()) {

            stmt.execute(
                    "INSERT INTO locations " +
                    "(location_id, name, area, location_type, x_coord, y_coord) " +
                    "VALUES " +
                    "('L001', 'Test Location', 'Area1', 'House', 5.0, 0.0)"
            );

            stmt.execute(
                    "INSERT INTO locations " +
                    "(location_id, name, area, location_type, x_coord, y_coord) " +
                    "VALUES " +
                    "('L002', 'Test Location 2', 'Area1', 'School', 5.1, 0.1)"
            );

            stmt.execute(
                    "INSERT INTO service_requests " +
                    "(request_id, source_location_id, destination_location_id, " +
                    "category, urgency, priority, time_submitted, deadline, status) " +
                    "VALUES " +
                    "('Q001', 'L001', 'L002', 'General', 3, 'MEDIUM', " +
                    "'2026-08-01T08:00', '2026-08-01T10:00', 'NEW')"
            );

        } catch (Exception e) {
            fail("Failed to seed test data: " + e.getMessage());
        }

        String report = service.generateOperationalReport();

        assertTrue(
                report.contains("NEW"),
                "Report should show NEW status count"
        );
        assertTrue(
                report.contains("General"),
                "Report should show General category"
        );
        assertTrue(
                report.contains("MEDIUM"),
                "Report should show MEDIUM priority"
        );
    }

    @Test
    @DisplayName("report shows completed request with deadline comparison")
    void reportWithCompletedRequest() {

        // Seed locations first because service_requests has foreign keys
        // referencing the locations table.
        try (var stmt = db.connect().createStatement()) {

            stmt.execute(
                    "INSERT INTO locations " +
                    "(location_id, name, area, location_type, x_coord, y_coord) " +
                    "VALUES " +
                    "('L001', 'Loc A', 'Area1', 'House', 5.0, 0.0)"
            );

            stmt.execute(
                    "INSERT INTO locations " +
                    "(location_id, name, area, location_type, x_coord, y_coord) " +
                    "VALUES " +
                    "('L002', 'Loc B', 'Area2', 'Office', 5.1, 0.1)"
            );

            stmt.execute(
                    "INSERT INTO service_requests " +
                    "(request_id, source_location_id, destination_location_id, " +
                    "category, urgency, priority, time_submitted, deadline, status) " +
                    "VALUES " +
                    "('Q100', 'L001', 'L002', 'Hazardous', 5, 'HIGH', " +
                    "'2026-08-01T08:00', '2026-08-01T12:00', 'COMPLETED')"
            );

        } catch (Exception e) {
            fail("Failed to seed test data: " + e.getMessage());
        }

        String report = service.generateOperationalReport();

        assertTrue(
                report.contains("Q100"),
                "Report should include the completed request"
        );

        assertTrue(
                report.contains("ON TIME") || report.contains("OVERDUE"),
                "Deadline section should show a verdict for completed requests"
        );
    }

    // ------------------------------------------------------------------
    // In-memory database for test isolation
    // ------------------------------------------------------------------

    private static class TestDatabaseManager extends DatabaseManager {

        private java.sql.Connection conn;

        @Override
        public java.sql.Connection connect() {
            try {
                if (conn == null || conn.isClosed()) {
                    conn = java.sql.DriverManager.getConnection(
                            "jdbc:sqlite::memory:"
                    );
                    conn.setAutoCommit(true);
                }

                return conn;

            } catch (java.sql.SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (java.sql.SQLException ignored) {
                // Nothing to do during test cleanup.
            }
        }
    }
}