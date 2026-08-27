package com.dcit308.wasteops;

import com.dcit308.wasteops.db.AlgorithmRunRepository;
import com.dcit308.wasteops.db.DatabaseManager;
import com.dcit308.wasteops.domain.AlgorithmRun;
import com.dcit308.wasteops.service.ExperimentService;
import com.dcit308.wasteops.service.ReportingService;
import com.dcit308.wasteops.ui.console.ConsoleMenu;
import org.junit.jupiter.api.*;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration tests for Issue #14.
 *
 * <p>Verifies the full path: database initialisation → schema created →
 * repository operations → service layer → console construction.
 *
 * <p>Uses an in-memory SQLite database for test isolation.
 *
 * Owned by Issue #14.
 */
class IntegrationTest {

    private DatabaseManager db;

    @BeforeEach
    void setUp() {
        db = new InMemoryDatabaseManager();
        db.connect();
        db.initSchemaIfNeeded();
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    // ------------------------------------------------------------------
    // Database initialisation
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Database schema creates all required tables")
    void schemaCreatesAllTables() throws Exception {
        String[] tables = {
                "locations",
                "roads",
                "resources",
                "service_requests",
                "algorithm_runs",
                "audit_events"
        };

        try (Statement stmt = db.connect().createStatement()) {
            for (String table : tables) {
                ResultSet rs = stmt.executeQuery(
                        "SELECT name FROM sqlite_master " +
                        "WHERE type='table' AND name='" + table + "'"
                );

                assertTrue(
                        rs.next(),
                        "Table '" + table + "' should exist after schema init"
                );

                rs.close();
            }
        }
    }

    @Test
    @DisplayName("Schema initialisation is idempotent — safe to call twice")
    void schemaIdempotent() {
        assertDoesNotThrow(
                () -> db.initSchemaIfNeeded(),
                "Calling initSchemaIfNeeded a second time should be a no-op"
        );
    }

    // ------------------------------------------------------------------
    // AlgorithmRunRepository round-trip
    // ------------------------------------------------------------------

    @Test
    @DisplayName("AlgorithmRun save → findById → findAll round-trip")
    void algorithmRunRoundTrip() {
        AlgorithmRunRepository repo = new AlgorithmRunRepository(db);

        AlgorithmRun run = new AlgorithmRun(
                "integ-1",
                "TestAlgo",
                42,
                9999L,
                null,
                "2026-08-20T00:00:00"
        );

        repo.save(run);

        AlgorithmRun found = repo.findById("integ-1");

        assertNotNull(found);
        assertEquals("TestAlgo", found.getAlgorithmName());

        List<AlgorithmRun> all = repo.findAll();

        assertEquals(1, all.size());
    }

    // ------------------------------------------------------------------
    // ReportingService on seeded data
    // ------------------------------------------------------------------

    @Test
    @DisplayName("ReportingService produces a non-empty report")
    void reportingServiceProducesReport() {
        ReportingService reporting = new ReportingService(db);

        String report = reporting.generateOperationalReport();

        assertNotNull(report);
        assertFalse(report.isEmpty(), "Report should not be empty");
        assertTrue(report.contains("OPERATIONAL REPORT"));
    }

    // ------------------------------------------------------------------
    // ExperimentService graceful degradation
    // ------------------------------------------------------------------

    @Test
    @DisplayName("ExperimentService.runAllExperiments() completes without crashing")
    void experimentServiceRuns() {
        ExperimentService experiments = new ExperimentService(db);

        assertDoesNotThrow(
                experiments::runAllExperiments,
                "All experiments should either run or be skipped gracefully"
        );
    }

    // ------------------------------------------------------------------
    // ConsoleMenu construction
    // ------------------------------------------------------------------

    @Test
    @DisplayName("ConsoleMenu can be constructed with DatabaseManager")
    void consoleMenuConstruction() {
        assertDoesNotThrow(
                () -> new ConsoleMenu(db),
                "ConsoleMenu should construct successfully with a valid DB"
        );
    }

    // ------------------------------------------------------------------
    // Full pipeline: seed data → generate report → verify output
    // ------------------------------------------------------------------

    @Test
    @DisplayName("Full pipeline: seed requests → operational report includes them")
    void fullPipeline() throws Exception {

        // Seed locations
        try (Statement stmt = db.connect().createStatement()) {

            stmt.execute(
                    "INSERT INTO locations VALUES " +
                    "('L001','Balme Library','Legon','Library',5.65,0.19)"
            );

            stmt.execute(
                    "INSERT INTO locations VALUES " +
                    "('L002','CS Dept','Legon','Academic',5.651,0.188)"
            );
        }

        // Seed service requests
        try (Statement stmt = db.connect().createStatement()) {

            stmt.execute(
                    "INSERT INTO service_requests " +
                    "(request_id,source_location_id,destination_location_id,category," +
                    "urgency,priority,time_submitted,deadline,status) " +
                    "VALUES " +
                    "('Q001','L001','L002','General',3,'MEDIUM'," +
                    "'2026-08-01T08:00','2026-08-01T10:00','NEW')"
            );

            stmt.execute(
                    "INSERT INTO service_requests " +
                    "(request_id,source_location_id,destination_location_id,category," +
                    "urgency,priority,time_submitted,deadline,status) " +
                    "VALUES " +
                    "('Q002','L002','L001','Hazardous',5,'HIGH'," +
                    "'2026-08-01T08:15','2026-08-01T09:00','COMPLETED')"
            );
        }

        // Seed a resource
        try (Statement stmt = db.connect().createStatement()) {

            stmt.execute(
                    "INSERT INTO resources " +
                    "(resource_id,resource_type,home_location_id,capacity,availability_status) " +
                    "VALUES ('V001','General','L001',4,'AVAILABLE')"
            );
        }

        // Generate report
        ReportingService reporting = new ReportingService(db);

        String report = reporting.generateOperationalReport();

        // Verify all sections have data
        assertTrue(report.contains("NEW"), 
                "Report should show NEW status");

        assertTrue(report.contains("COMPLETED"), 
                "Report should show COMPLETED status");

        assertTrue(report.contains("General"), 
                "Report should show General category");

        assertTrue(report.contains("Hazardous"), 
                "Report should show Hazardous category");

        assertTrue(report.contains("HIGH"), 
                "Report should show HIGH priority");

        assertTrue(report.contains("MEDIUM"), 
                "Report should show MEDIUM priority");

        assertTrue(report.contains("AVAILABLE"), 
                "Report should show AVAILABLE resource status");

        assertTrue(report.contains("Q002"), 
                "Report should show completed request in deadline section");
    }

    // ------------------------------------------------------------------
    // In-memory database helper
    // ------------------------------------------------------------------

    private static class InMemoryDatabaseManager extends DatabaseManager {

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
                // Ignore cleanup errors during tests
            }
        }
    }
}