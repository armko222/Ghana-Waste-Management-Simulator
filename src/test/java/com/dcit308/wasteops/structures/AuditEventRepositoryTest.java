package com.dcit308.wasteops.structures;

import com.dcit308.wasteops.db.AuditEventRepository;
import com.dcit308.wasteops.domain.AuditEvent;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AuditEventRepositoryTest {

    private Connection connection;
    private AuditEventRepository repository;

    @BeforeEach
    void setUp() throws Exception {
        // Connect to an in-memory SQLite database for test isolation
        connection = DriverManager.getConnection("jdbc:sqlite::memory:");

        // Create table matching our AuditEvent schema
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE auditevent (" +
                    "event_id VARCHAR(50) PRIMARY KEY, " +
                    "event_type VARCHAR(20) NOT NULL, " +
                    "related_request_id VARCHAR(50), " +
                    "description TEXT NOT NULL, " +
                    "timestamp TIMESTAMP NOT NULL" +
                    ")");
        }

        repository = new AuditEventRepository(connection);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    @Test
    @DisplayName("Should save and find AuditEvent by ID")
    void testSaveAndFindById() {
        LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        AuditEvent event = new AuditEvent("E-100", AuditEvent.EventType.DISPATCH, "REQ-55", "Resource dispatched", now);

        repository.save(event);

        AuditEvent retrieved = repository.findById("E-100");

        assertNotNull(retrieved);
        assertEquals("E-100", retrieved.getEventId());
        assertEquals(AuditEvent.EventType.DISPATCH, retrieved.getEventType());
        assertEquals("REQ-55", retrieved.getRelatedRequestId());
        assertEquals("Resource dispatched", retrieved.getDescription());
        assertEquals(now, retrieved.getTimestamp());
    }

    @Test
    @DisplayName("Should handle null relatedRequestId correctly")
    void testSaveWithNullRelatedRequestId() {
        AuditEvent event = new AuditEvent("E-101", AuditEvent.EventType.IMPORT, null, "Bulk CSV import", LocalDateTime.now());

        repository.save(event);

        AuditEvent retrieved = repository.findById("E-101");

        assertNotNull(retrieved);
        assertNull(retrieved.getRelatedRequestId());
    }

    @Test
    @DisplayName("Should return all saved events ordered by timestamp DESC")
    void testFindAll() {
        LocalDateTime time1 = LocalDateTime.now().minusMinutes(10).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime time2 = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

        AuditEvent event1 = new AuditEvent("E-1", AuditEvent.EventType.IMPORT, null, "Import", time1);
        AuditEvent event2 = new AuditEvent("E-2", AuditEvent.EventType.STATUS_CHANGE, "REQ-1", "Updated status", time2);

        repository.save(event1);
        repository.save(event2);

        List<AuditEvent> events = repository.findAll();

        assertEquals(2, events.size());
        assertEquals("E-2", events.get(0).getEventId(), "Most recent event should come first");
        assertEquals("E-1", events.get(1).getEventId());
    }
}
