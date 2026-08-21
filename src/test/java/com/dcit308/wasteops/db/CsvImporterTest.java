package com.dcit308.wasteops.db;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import com.dcit308.wasteops.domain.Location;
import com.dcit308.wasteops.domain.Road;
import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.domain.Resource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for Issue #1's CsvImporter.
 *
 * Covers:
 * - Valid input imported correctly
 * - Malformed input rejected clearly
 */
class CsvImporterTest {

    /**
     * Each test shares the fixed data/wasteops.db path used by
     * DatabaseManager, so the database is reset before every test to
     * keep assertions about row counts isolated.
     */
    @BeforeEach
    void resetDatabase() {
        for (String suffix : new String[] { "", "-wal", "-shm" }) {
            Path dbFile = Paths.get("data", "wasteops.db" + suffix);
            try {
                Files.deleteIfExists(dbFile);
            } catch (IOException ignored) {
                // Best-effort cleanup; SQLite recreates the file as needed.
            }
        }
    }

    @Test
    @DisplayName("imports valid locations CSV")
    void importsValidLocations() throws IOException {
        Path tempDir = Files.createTempDirectory("csv_test");
        Path locations = tempDir.resolve("locations.csv");
        Path roads = tempDir.resolve("roads.csv");
        Path resources = tempDir.resolve("resources.csv");
        Path requests = tempDir.resolve("service_requests.csv");

        Files.writeString(locations, "location_id,name,area,location_type,x_coord,y_coord\nL001,Test,Area,Library,5.6,-0.19\n");
        Files.writeString(roads, "road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight\nR001,L001,L001,1.0,5,1.0\n");
        Files.writeString(resources, "resource_id,resource_type,home_location_id,capacity\nR001,GENERAL,L001,500\n");
        Files.writeString(requests, "request_id,source_location_id,destination_location_id,category,urgency,priority,time_submitted,deadline\nQ001,L001,L001,Medical,1,LOW,2026-07-01T08:00,2026-07-01T09:00\n");

        CsvImporter importer = new CsvImporter();
        importer.importAll(tempDir.toString());

        LocationRepository repo = new LocationRepository();
        List<Location> locationsList = repo.findAll();

        assertEquals(1, locationsList.size());
        assertEquals("L001", locationsList.get(0).getLocationId());
    }

    @Test
    @DisplayName("imports valid roads CSV")
    void importsValidRoads() throws IOException {
        Path tempDir = Files.createTempDirectory("csv_test");
        Path locations = tempDir.resolve("locations.csv");
        Path roads = tempDir.resolve("roads.csv");
        Path resources = tempDir.resolve("resources.csv");
        Path requests = tempDir.resolve("service_requests.csv");

        Files.writeString(locations, "location_id,name,area,location_type,x_coord,y_coord\nL001,Test,Area,Library,5.6,-0.19\nL002,Test2,Area2,Office,5.7,-0.20\n");
        Files.writeString(roads, "road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight\nR001,L001,L002,1.0,5,1.0\n");
        Files.writeString(resources, "resource_id,resource_type,home_location_id,capacity\nR001,GENERAL,L001,500\n");
        Files.writeString(requests, "request_id,source_location_id,destination_location_id,category,urgency,priority,time_submitted,deadline\nQ001,L001,L002,Medical,1,LOW,2026-07-01T08:00,2026-07-01T09:00\n");

        CsvImporter importer = new CsvImporter();
        importer.importAll(tempDir.toString());

        RoadRepository repo = new RoadRepository();
        List<Road> roadsList = repo.findAll();

        assertEquals(1, roadsList.size());
        assertEquals("R001", roadsList.get(0).getRoadId());
    }

    @Test
    @DisplayName("imports valid resources CSV")
    void importsValidResources() throws IOException {
        Path tempDir = Files.createTempDirectory("csv_test");
        Path locations = tempDir.resolve("locations.csv");
        Path roads = tempDir.resolve("roads.csv");
        Path resources = tempDir.resolve("resources.csv");
        Path requests = tempDir.resolve("service_requests.csv");

        Files.writeString(locations, "location_id,name,area,location_type,x_coord,y_coord\nL001,Test,Area,Library,5.6,-0.19\n");
        Files.writeString(roads, "road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight\nR001,L001,L001,1.0,5,1.0\n");
        Files.writeString(resources, "resource_id,resource_type,home_location_id,capacity\nR001,GENERAL,L001,500\n");
        Files.writeString(requests, "request_id,source_location_id,destination_location_id,category,urgency,priority,time_submitted,deadline\nQ001,L001,L001,Medical,1,LOW,2026-07-01T08:00,2026-07-01T09:00\n");

        CsvImporter importer = new CsvImporter();
        importer.importAll(tempDir.toString());

        ResourceRepository repo = new ResourceRepository();
        List<Resource> resourcesList = repo.findAll();

        assertEquals(1, resourcesList.size());
        assertEquals("R001", resourcesList.get(0).getResourceId());
    }

    @Test
    @DisplayName("imports valid service requests CSV")
    void importsValidRequests() throws IOException {
        Path tempDir = Files.createTempDirectory("csv_test");
        Path locations = tempDir.resolve("locations.csv");
        Path roads = tempDir.resolve("roads.csv");
        Path resources = tempDir.resolve("resources.csv");
        Path requests = tempDir.resolve("service_requests.csv");

        Files.writeString(locations, "location_id,name,area,location_type,x_coord,y_coord\nL001,Test,Area,Library,5.6,-0.19\nL002,Test2,Area2,Office,5.7,-0.20\n");
        Files.writeString(roads, "road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight\nR001,L001,L002,1.0,5,1.0\n");
        Files.writeString(resources, "resource_id,resource_type,home_location_id,capacity\nR001,GENERAL,L001,500\n");
        Files.writeString(requests, "request_id,source_location_id,destination_location_id,category,urgency,priority,time_submitted,deadline\nQ001,L001,L002,Medical,1,LOW,2026-07-01T08:00,2026-07-01T09:00\n");

        CsvImporter importer = new CsvImporter();
        importer.importAll(tempDir.toString());

        ServiceRequestRepository repo = new ServiceRequestRepository();
        List<ServiceRequest> requestsList = repo.findAll();

        assertEquals(1, requestsList.size());
        assertEquals("Q001", requestsList.get(0).getRequestId());
    }

    @Test
    @DisplayName("rejects missing CSV file")
    void rejectsMissingFile() {
        CsvImporter importer = new CsvImporter();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importAll("/nonexistent/directory")
        );

        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    @DisplayName("rejects CSV with wrong number of columns")
    void rejectsWrongColumnCount() throws IOException {
        Path tempDir = Files.createTempDirectory("csv_test");
        Path locations = tempDir.resolve("locations.csv");
        Path roads = tempDir.resolve("roads.csv");
        Path resources = tempDir.resolve("resources.csv");
        Path requests = tempDir.resolve("service_requests.csv");

        Files.writeString(locations, "location_id,name,area,location_type,x_coord\nL001,Test,Area,Library,5.6\n");
        Files.writeString(roads, "road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight\nR001,L001,L001,1.0,5,1.0\n");
        Files.writeString(resources, "resource_id,resource_type,home_location_id,capacity\nR001,GENERAL,L001,500\n");
        Files.writeString(requests, "request_id,source_location_id,destination_location_id,category,urgency,priority,time_submitted,deadline\nQ001,L001,L001,Medical,1,LOW,2026-07-01T08:00,2026-07-01T09:00\n");

        CsvImporter importer = new CsvImporter();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importAll(tempDir.toString())
        );

        assertTrue(exception.getMessage().contains("expected 6 columns but found 5"));
    }

    @Test
    @DisplayName("rejects malformed urgency value")
    void rejectsMalformedUrgency() throws IOException {
        Path tempDir = Files.createTempDirectory("csv_test");
        Path locations = tempDir.resolve("locations.csv");
        Path roads = tempDir.resolve("roads.csv");
        Path resources = tempDir.resolve("resources.csv");
        Path requests = tempDir.resolve("service_requests.csv");

        Files.writeString(locations, "location_id,name,area,location_type,x_coord,y_coord\nL001,Test,Area,Library,5.6,-0.19\n");
        Files.writeString(roads, "road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight\nR001,L001,L001,1.0,5,1.0\n");
        Files.writeString(resources, "resource_id,resource_type,home_location_id,capacity\nR001,GENERAL,L001,500\n");
        Files.writeString(requests, "request_id,source_location_id,destination_location_id,category,urgency,priority,time_submitted,deadline\nQ001,L001,L001,Medical,invalid,LOW,2026-07-01T08:00,2026-07-01T09:00\n");

        CsvImporter importer = new CsvImporter();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importAll(tempDir.toString())
        );

        assertTrue(exception.getMessage().contains("Malformed CSV row"));
        assertTrue(exception.getMessage().contains("urgency must be a valid integer"));
    }

    @Test
    @DisplayName("rejects malformed date time")
    void rejectsMalformedDateTime() throws IOException {
        Path tempDir = Files.createTempDirectory("csv_test");
        Path locations = tempDir.resolve("locations.csv");
        Path roads = tempDir.resolve("roads.csv");
        Path resources = tempDir.resolve("resources.csv");
        Path requests = tempDir.resolve("service_requests.csv");

        Files.writeString(locations, "location_id,name,area,location_type,x_coord,y_coord\nL001,Test,Area,Library,5.6,-0.19\n");
        Files.writeString(roads, "road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight\nR001,L001,L001,1.0,5,1.0\n");
        Files.writeString(resources, "resource_id,resource_type,home_location_id,capacity\nR001,GENERAL,L001,500\n");
        Files.writeString(requests, "request_id,source_location_id,destination_location_id,category,urgency,priority,time_submitted,deadline\nQ001,L001,L001,Medical,1,LOW,not-a-date,2026-07-01T09:00\n");

        CsvImporter importer = new CsvImporter();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importAll(tempDir.toString())
        );

        assertTrue(exception.getMessage().contains("Malformed CSV row"));
        assertTrue(exception.getMessage().contains("time_submitted must be ISO-8601"));
    }

    @Test
    @DisplayName("rejects invalid priority value")
    void rejectsInvalidPriority() throws IOException {
        Path tempDir = Files.createTempDirectory("csv_test");
        Path locations = tempDir.resolve("locations.csv");
        Path roads = tempDir.resolve("roads.csv");
        Path resources = tempDir.resolve("resources.csv");
        Path requests = tempDir.resolve("service_requests.csv");

        Files.writeString(locations, "location_id,name,area,location_type,x_coord,y_coord\nL001,Test,Area,Library,5.6,-0.19\n");
        Files.writeString(roads, "road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight\nR001,L001,L001,1.0,5,1.0\n");
        Files.writeString(resources, "resource_id,resource_type,home_location_id,capacity\nR001,GENERAL,L001,500\n");
        Files.writeString(requests, "request_id,source_location_id,destination_location_id,category,urgency,priority,time_submitted,deadline\nQ001,L001,L001,Medical,1,INVALID,2026-07-01T08:00,2026-07-01T09:00\n");

        CsvImporter importer = new CsvImporter();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importAll(tempDir.toString())
        );

        assertTrue(exception.getMessage().contains("Malformed CSV row"));
        assertTrue(exception.getMessage().contains("priority must be HIGH, MEDIUM, or LOW"));
    }

    @Test
    @DisplayName("rejects invalid resource type")
    void rejectsInvalidResourceType() throws IOException {
        Path tempDir = Files.createTempDirectory("csv_test");
        Path locations = tempDir.resolve("locations.csv");
        Path roads = tempDir.resolve("roads.csv");
        Path resources = tempDir.resolve("resources.csv");
        Path requests = tempDir.resolve("service_requests.csv");

        Files.writeString(locations, "location_id,name,area,location_type,x_coord,y_coord\nL001,Test,Area,Library,5.6,-0.19\n");
        Files.writeString(roads, "road_id,from_location_id,to_location_id,distance_km,travel_time_min,condition_weight\nR001,L001,L001,1.0,5,1.0\n");
        Files.writeString(resources, "resource_id,resource_type,home_location_id,capacity\nR001,INVALID,L001,500\n");
        Files.writeString(requests, "request_id,source_location_id,destination_location_id,category,urgency,priority,time_submitted,deadline\nQ001,L001,L001,Medical,1,LOW,2026-07-01T08:00,2026-07-01T09:00\n");

        CsvImporter importer = new CsvImporter();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> importer.importAll(tempDir.toString())
        );

        assertTrue(exception.getMessage().contains("Malformed CSV row"));
        assertTrue(exception.getMessage().contains("resource_type must be GENERAL, HAZARDOUS, or INDUSTRIAL"));
    }
}
