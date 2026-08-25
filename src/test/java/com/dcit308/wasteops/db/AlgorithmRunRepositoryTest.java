package com.dcit308.wasteops.db;

import com.dcit308.wasteops.domain.AlgorithmRun;
import org.junit.jupiter.api.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link AlgorithmRunRepository} — the database CRUD layer
 * owned by Issue #14.
 *
 * <p>Uses an in-memory SQLite database for isolation: each test method
 * gets a fresh schema so tests never interfere with each other or with
 * the on-disk development database.
 */
class AlgorithmRunRepositoryTest {

    private DatabaseManager db;
    private AlgorithmRunRepository repo;

    @BeforeEach
    void setUp() {
        // Use a unique temp DB for each test for full isolation
        db = new TestDatabaseManager();
        db.connect();
        db.initSchemaIfNeeded();
        repo = new AlgorithmRunRepository(db);
    }

    @AfterEach
    void tearDown() {
        db.close();
    }

    // ------------------------------------------------------------------
    // save + findById
    // ------------------------------------------------------------------

    @Test
    @DisplayName("save then findById returns the same record")
    void saveAndFindById() {
        AlgorithmRun run = new AlgorithmRun(
                "run-001", "MergeSort", 1000, 456789L, 128, "2026-08-20T10:00:00");

        repo.save(run);
        AlgorithmRun found = repo.findById("run-001");

        assertNotNull(found, "findById should return the saved record");
        assertEquals("run-001", found.getRunId());
        assertEquals("MergeSort", found.getAlgorithmName());
        assertEquals(1000, found.getInputSize());
        assertEquals(456789L, found.getTimeNanos());
        assertEquals(128, found.getMemoryKb());
        assertEquals("2026-08-20T10:00:00", found.getDateRun());
    }

    @Test
    @DisplayName("findById returns null for non-existent ID")
    void findByIdNonExistent() {
        AlgorithmRun found = repo.findById("does-not-exist");
        assertNull(found, "findById should return null for unknown ID");
    }

    @Test
    @DisplayName("save with null memoryKb stores and retrieves null")
    void saveNullMemory() {
        AlgorithmRun run = new AlgorithmRun(
                "run-null-mem", "QuickSort", 500, 123L, null, "2026-08-20T11:00:00");

        repo.save(run);
        AlgorithmRun found = repo.findById("run-null-mem");

        assertNotNull(found);
        assertNull(found.getMemoryKb(), "memoryKb should be null when stored as null");
    }

    // ------------------------------------------------------------------
    // saveAll + findAll
    // ------------------------------------------------------------------

    @Test
    @DisplayName("saveAll then findAll returns all records")
    void saveAllAndFindAll() {
        List<AlgorithmRun> runs = Arrays.asList(
                new AlgorithmRun("batch-1", "LinearSearch", 100, 100L, null, "2026-08-20T12:00:00"),
                new AlgorithmRun("batch-2", "BinarySearch", 100, 50L, null, "2026-08-20T12:00:00"),
                new AlgorithmRun("batch-3", "LinearSearch", 500, 400L, null, "2026-08-20T12:00:01")
        );

        repo.saveAll(runs);
        List<AlgorithmRun> all = repo.findAll();

        assertEquals(3, all.size(), "findAll should return all 3 batch-saved records");
    }

    @Test
    @DisplayName("findAll on empty table returns empty list")
    void findAllEmpty() {
        List<AlgorithmRun> all = repo.findAll();
        assertNotNull(all);
        assertTrue(all.isEmpty(), "findAll should return empty list on empty table");
    }

    @Test
    @DisplayName("saveAll with empty list is a safe no-op")
    void saveAllEmpty() {
        assertDoesNotThrow(() -> repo.saveAll(List.of()),
                "saveAll with empty list should not throw");
    }

    @Test
    @DisplayName("saveAll with null is a safe no-op")
    void saveAllNull() {
        assertDoesNotThrow(() -> repo.saveAll(null),
                "saveAll with null should not throw");
    }

    // ------------------------------------------------------------------
    // save overwrites (INSERT OR REPLACE)
    // ------------------------------------------------------------------

    @Test
    @DisplayName("save with duplicate run_id overwrites the existing record")
    void saveOverwrite() {
        repo.save(new AlgorithmRun("dup-1", "Algo1", 100, 999L, null, "2026-08-20T13:00:00"));
        repo.save(new AlgorithmRun("dup-1", "Algo2", 200, 888L, 64, "2026-08-20T14:00:00"));

        AlgorithmRun found = repo.findById("dup-1");
        assertNotNull(found);
        assertEquals("Algo2", found.getAlgorithmName(), "second save should overwrite");
        assertEquals(200, found.getInputSize());
        assertEquals(888L, found.getTimeNanos());
    }

    // ------------------------------------------------------------------
    // Test helper — in-memory SQLite database
    // ------------------------------------------------------------------

    /**
     * A DatabaseManager that connects to an in-memory SQLite database
     * rather than the on-disk file, providing test isolation.
     */
    private static class TestDatabaseManager extends DatabaseManager {
        private java.sql.Connection testConnection;

        @Override
        public java.sql.Connection connect() {
            try {
                if (testConnection == null || testConnection.isClosed()) {
                    testConnection = java.sql.DriverManager.getConnection("jdbc:sqlite::memory:");
                    testConnection.setAutoCommit(true);
                }
                return testConnection;
            } catch (java.sql.SQLException e) {
                throw new RuntimeException("Failed to open in-memory test database", e);
            }
        }

        @Override
        public void close() {
            try {
                if (testConnection != null && !testConnection.isClosed()) {
                    testConnection.close();
                }
            } catch (java.sql.SQLException ignored) { }
        }
    }
}
