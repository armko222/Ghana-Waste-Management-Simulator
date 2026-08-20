package com.dcit308.wasteops.service;

import com.dcit308.wasteops.db.DatabaseManager;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link ExperimentService} — verifies that the experiment
 * runner gracefully handles unimplemented dependencies.
 *
 * <p>Since most algorithm/structure dependencies are stubs (owned by
 * other issues), these tests verify that ExperimentService catches
 * {@code UnsupportedOperationException} and continues without crashing.
 *
 * Owned by Issue #14.
 */
class ExperimentServiceTest {

    private DatabaseManager db;
    private ExperimentService service;

    @BeforeEach
    void setUp() {
        db = new TestDatabaseManager();
        db.connect();
        db.initSchemaIfNeeded();
        service = new ExperimentService(db);
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    @Test
    @DisplayName("runAllExperiments does not throw even when dependencies are stubs")
    void runAllExperimentsGraceful() {
        // All algorithm dependencies are stubs that throw
        // UnsupportedOperationException. The experiment service
        // should catch each one and continue.
        assertDoesNotThrow(() -> service.runAllExperiments(),
                "runAllExperiments should skip unimplemented categories gracefully");
    }

    @Test
    @DisplayName("ExperimentService can be constructed with a valid DatabaseManager")
    void constructionSucceeds() {
        assertNotNull(service, "ExperimentService should construct without errors");
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
                    conn = java.sql.DriverManager.getConnection("jdbc:sqlite::memory:");
                    conn.setAutoCommit(true);
                }
                return conn;
            } catch (java.sql.SQLException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void close() {
            try { if (conn != null && !conn.isClosed()) conn.close(); }
            catch (java.sql.SQLException ignored) { }
        }
    }
}
