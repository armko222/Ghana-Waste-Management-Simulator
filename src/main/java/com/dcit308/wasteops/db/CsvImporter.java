package com.dcit308.wasteops.db;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

import com.dcit308.wasteops.domain.Location;
import com.dcit308.wasteops.domain.Resource;
import com.dcit308.wasteops.domain.Road;
import com.dcit308.wasteops.domain.ServiceRequest;

/**
 * Reads data/csv/*.csv, validates rows, and writes into the database
 * through the repository classes.
 *
 * Malformed rows are rejected clearly rather than skipped silently.
 *
 * Owned by Issue #1.
 */
public class CsvImporter {

    private final LocationRepository locationRepository;
    private final RoadRepository roadRepository;
    private final ServiceRequestRepository serviceRequestRepository;
    private final ResourceRepository resourceRepository;

    public CsvImporter() {
        this.locationRepository = new LocationRepository();
        this.roadRepository = new RoadRepository();
        this.serviceRequestRepository = new ServiceRequestRepository();
        this.resourceRepository = new ResourceRepository();
    }

    /**
     * Imports all four CSV files.
     *
     * Import order:
     *
     * 1. Locations
     * 2. Roads
     * 3. Resources
     * 4. Service requests
     *
     * Locations must be imported first because the other tables
     * contain foreign keys referring to locations.
     */
    public void importAll(String csvDirectoryPath) {

        Path directory = Path.of(csvDirectoryPath);

        if (!Files.exists(directory)) {
            throw new IllegalArgumentException(
                    "CSV directory does not exist: " + csvDirectoryPath
            );
        }

        if (!Files.isDirectory(directory)) {
            throw new IllegalArgumentException(
                    "CSV path is not a directory: " + csvDirectoryPath
            );
        }

        importLocations(directory.resolve("locations.csv"));

        importRoads(directory.resolve("roads.csv"));

        importResources(directory.resolve("resources.csv"));

        importServiceRequests(
                directory.resolve("service_requests.csv")
        );
    }

    // ============================================================
    // LOCATIONS
    // ============================================================

    /**
     * Imports locations.csv.
     *
     * Expected columns:
     *
     * location_id,
     * name,
     * area,
     * location_type,
     * x_coord,
     * y_coord
     */
    private void importLocations(Path file) {

        readFile(
                file,
                "locations.csv",
                6,
                (fields, rowNumber) -> {

                    String locationId =
                            required(fields[0], "location_id");

                    String name =
                            required(fields[1], "name");

                    String area =
                            required(fields[2], "area");

                    String locationType =
                            required(fields[3], "location_type");

                    double xCoord =
                            parseDouble(
                                    fields[4],
                                    "x_coord",
                                    file,
                                    rowNumber
                            );

                    double yCoord =
                            parseDouble(
                                    fields[5],
                                    "y_coord",
                                    file,
                                    rowNumber
                            );

                    Location location = new Location(
                            locationId,
                            name,
                            area,
                            locationType,
                            xCoord,
                            yCoord
                    );

                    locationRepository.save(location);
                }
        );
    }

    // ============================================================
    // ROADS
    // ============================================================

    /**
     * Imports roads.csv.
     *
     * Expected columns:
     *
     * road_id,
     * from_location_id,
     * to_location_id,
     * distance_km,
     * travel_time_min,
     * condition_weight
     */
    private void importRoads(Path file) {

        readFile(
                file,
                "roads.csv",
                6,
                (fields, rowNumber) -> {

                    String roadId =
                            required(fields[0], "road_id");

                    String fromLocationId =
                            required(
                                    fields[1],
                                    "from_location_id"
                            );

                    String toLocationId =
                            required(
                                    fields[2],
                                    "to_location_id"
                            );

                    double distanceKm =
                            parseDouble(
                                    fields[3],
                                    "distance_km",
                                    file,
                                    rowNumber
                            );

                    double travelTimeMin =
                            parseDouble(
                                    fields[4],
                                    "travel_time_min",
                                    file,
                                    rowNumber
                            );

                    double conditionWeight =
                            parseDouble(
                                    fields[5],
                                    "condition_weight",
                                    file,
                                    rowNumber
                            );

                    if (distanceKm <= 0) {
                        throw malformed(
                                file,
                                rowNumber,
                                "distance_km must be greater than zero"
                        );
                    }

                    if (travelTimeMin <= 0) {
                        throw malformed(
                                file,
                                rowNumber,
                                "travel_time_min must be greater than zero"
                        );
                    }

                    if (conditionWeight <= 0) {
                        throw malformed(
                                file,
                                rowNumber,
                                "condition_weight must be greater than zero"
                        );
                    }

                    Road road = new Road(
                            roadId,
                            fromLocationId,
                            toLocationId,
                            distanceKm,
                            travelTimeMin,
                            conditionWeight
                    );

                    roadRepository.save(road);
                }
        );
    }

    // ============================================================
    // RESOURCES
    // ============================================================

    /**
     * Imports resources.csv.
     *
     * Expected columns:
     *
     * resource_id,
     * resource_type,
     * home_location_id,
     * capacity
     */
    private void importResources(Path file) {

        readFile(
                file,
                "resources.csv",
                4,
                (fields, rowNumber) -> {

                    String resourceId =
                            required(
                                    fields[0],
                                    "resource_id"
                            );

                    Resource.Type resourceType =
                            parseResourceType(
                                    fields[1],
                                    file,
                                    rowNumber
                            );

                    String homeLocationId =
                            required(
                                    fields[2],
                                    "home_location_id"
                            );

                    int capacity =
                            parseInt(
                                    fields[3],
                                    "capacity",
                                    file,
                                    rowNumber
                            );

                    if (capacity <= 0) {
                        throw malformed(
                                file,
                                rowNumber,
                                "capacity must be greater than zero"
                        );
                    }

                    Resource resource = new Resource(
                            resourceId,
                            resourceType,
                            homeLocationId,
                            capacity
                    );

                    resourceRepository.save(resource);
                }
        );
    }

    // ============================================================
    // SERVICE REQUESTS
    // ============================================================

    /**
     * Imports service_requests.csv.
     *
     * Expected columns:
     *
     * request_id,
     * source_location_id,
     * destination_location_id,
     * category,
     * urgency,
     * priority,
     * time_submitted,
     * deadline
     */
    private void importServiceRequests(Path file) {

        readFile(
                file,
                "service_requests.csv",
                8,
                (fields, rowNumber) -> {

                    String requestId =
                            required(
                                    fields[0],
                                    "request_id"
                            );

                    String sourceLocationId =
                            required(
                                    fields[1],
                                    "source_location_id"
                            );

                    String destinationLocationId =
                            required(
                                    fields[2],
                                    "destination_location_id"
                            );

                    String category =
                            required(
                                    fields[3],
                                    "category"
                            );

                    int urgency =
                            parseInt(
                                    fields[4],
                                    "urgency",
                                    file,
                                    rowNumber
                            );

                    if (urgency < 1 || urgency > 5) {
                        throw malformed(
                                file,
                                rowNumber,
                                "urgency must be between 1 and 5"
                        );
                    }

                    ServiceRequest.Priority priority =
                            parsePriority(
                                    fields[5],
                                    file,
                                    rowNumber
                            );

                    LocalDateTime timeSubmitted =
                            parseDateTime(
                                    fields[6],
                                    "time_submitted",
                                    file,
                                    rowNumber
                            );

                    LocalDateTime deadline =
                            parseDateTime(
                                    fields[7],
                                    "deadline",
                                    file,
                                    rowNumber
                            );

                    if (deadline.isBefore(timeSubmitted)) {
                        throw malformed(
                                file,
                                rowNumber,
                                "deadline cannot be before "
                                        + "time_submitted"
                        );
                    }

                    ServiceRequest request =
                            new ServiceRequest(
                                    requestId,
                                    sourceLocationId,
                                    destinationLocationId,
                                    category,
                                    urgency,
                                    priority,
                                    timeSubmitted,
                                    deadline
                            );

                    serviceRequestRepository.save(request);
                }
        );
    }

    // ============================================================
    // GENERIC CSV READER
    // ============================================================

    /**
     * Reads a CSV file and validates each row.
     *
     * The first non-empty row is treated as the header.
     */
    private void readFile(
            Path file,
            String fileName,
            int expectedColumns,
            CsvRowHandler handler
    ) {

        if (!Files.exists(file)) {
            throw new IllegalArgumentException(
                    "Required CSV file does not exist: " + file
            );
        }

        if (!Files.isRegularFile(file)) {
            throw new IllegalArgumentException(
                    "CSV path is not a regular file: " + file
            );
        }

        try (BufferedReader reader =
                     Files.newBufferedReader(file)) {

            String line;

            int rowNumber = 0;

            boolean headerRead = false;

            while ((line = reader.readLine()) != null) {

                rowNumber++;

                // Ignore blank lines.
                if (line.trim().isEmpty()) {
                    continue;
                }

                // First non-empty line is the header.
                if (!headerRead) {
                    headerRead = true;
                    continue;
                }

                String[] fields = parseCsvLine(line);

                // Validate number of columns.
                if (fields.length != expectedColumns) {

                    throw malformed(
                            file,
                            rowNumber,
                            "expected "
                                    + expectedColumns
                                    + " columns but found "
                                    + fields.length
                    );
                }

                try {

                    handler.handle(
                            fields,
                            rowNumber
                    );

                } catch (IllegalArgumentException e) {

                    throw e;

                } catch (Exception e) {

                    throw malformed(
                            file,
                            rowNumber,
                            e.getMessage()
                    );
                }
            }

            if (!headerRead) {

                throw new IllegalArgumentException(
                        "CSV file is empty: " + fileName
                );
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to read CSV file: " + file,
                    e
            );
        }
    }

    // ============================================================
    // CSV PARSER
    // ============================================================

    /**
     * Parses one CSV line.
     *
     * Supports:
     *
     * - normal comma-separated values
     * - quoted values
     * - commas inside quoted values
     * - escaped quotes ("")
     *
     * No ArrayList is used.
     */
    private String[] parseCsvLine(String line) {

        /*
         * First determine the number of fields.
         */
        int fieldCount = 1;

        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);

            if (c == '"') {

                /*
                 * Two quotes inside a quoted field represent
                 * one escaped quote.
                 */
                if (insideQuotes
                        && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {

                    i++;

                } else {

                    insideQuotes = !insideQuotes;
                }

            } else if (c == ',' && !insideQuotes) {

                fieldCount++;
            }
        }

        if (insideQuotes) {

            throw new IllegalArgumentException(
                    "Unclosed quoted field"
            );
        }

        /*
         * Create an array with exactly the required size.
         */
        String[] fields =
                new String[fieldCount];

        StringBuilder current =
                new StringBuilder();

        insideQuotes = false;

        int fieldIndex = 0;

        /*
         * Parse the fields.
         */
        for (int i = 0; i < line.length(); i++) {

            char c = line.charAt(i);

            if (c == '"') {

                if (insideQuotes
                        && i + 1 < line.length()
                        && line.charAt(i + 1) == '"') {

                    current.append('"');

                    i++;

                } else {

                    insideQuotes = !insideQuotes;
                }

            } else if (c == ',' && !insideQuotes) {

                fields[fieldIndex] =
                        current.toString().trim();

                fieldIndex++;

                current.setLength(0);

            } else {

                current.append(c);
            }
        }

        /*
         * Add the final field.
         */
        fields[fieldIndex] =
                current.toString().trim();

        return fields;
    }

    // ============================================================
    // VALIDATION HELPERS
    // ============================================================

    /**
     * Ensures a required string field is not empty.
     */
    private String required(
            String value,
            String fieldName
    ) {

        if (value == null
                || value.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    fieldName + " cannot be empty"
            );
        }

        return value.trim();
    }

    /**
     * Parses a double value.
     */
    private double parseDouble(
            String value,
            String fieldName,
            Path file,
            int rowNumber
    ) {

        try {

            return Double.parseDouble(
                    required(
                            value,
                            fieldName
                    )
            );

        } catch (NumberFormatException e) {

            throw malformed(
                    file,
                    rowNumber,
                    fieldName
                            + " must be a valid number: "
                            + value
            );
        }
    }

    /**
     * Parses an integer value.
     */
    private int parseInt(
            String value,
            String fieldName,
            Path file,
            int rowNumber
    ) {

        try {

            return Integer.parseInt(
                    required(
                            value,
                            fieldName
                    )
            );

        } catch (NumberFormatException e) {

            throw malformed(
                    file,
                    rowNumber,
                    fieldName
                            + " must be a valid integer: "
                            + value
            );
        }
    }

    /**
     * Parses an ISO-8601 LocalDateTime.
     */
    private LocalDateTime parseDateTime(
            String value,
            String fieldName,
            Path file,
            int rowNumber
    ) {

        try {

            return LocalDateTime.parse(
                    required(
                            value,
                            fieldName
                    )
            );

        } catch (Exception e) {

            throw malformed(
                    file,
                    rowNumber,
                    fieldName
                            + " must be ISO-8601 date/time, "
                            + "for example "
                            + "2026-08-18T08:00:00"
            );
        }
    }

    /**
     * Converts CSV priority into the ServiceRequest enum.
     */
    private ServiceRequest.Priority parsePriority(
            String value,
            Path file,
            int rowNumber
    ) {

        String cleanedValue;

        try {

            cleanedValue =
                    required(
                            value,
                            "priority"
                    ).toUpperCase();

            return ServiceRequest.Priority.valueOf(
                    cleanedValue
            );

        } catch (IllegalArgumentException e) {

            throw malformed(
                    file,
                    rowNumber,
                    "priority must be HIGH, MEDIUM, or LOW"
            );
        }
    }

    /**
     * Converts CSV resource type into the Resource enum.
     */
    private Resource.Type parseResourceType(
            String value,
            Path file,
            int rowNumber
    ) {

        String cleanedValue;

        try {

            cleanedValue =
                    required(
                            value,
                            "resource_type"
                    ).toUpperCase();

            return Resource.Type.valueOf(
                    cleanedValue
            );

        } catch (IllegalArgumentException e) {

            throw malformed(
                    file,
                    rowNumber,
                    "resource_type must be GENERAL, "
                            + "HAZARDOUS, or INDUSTRIAL"
            );
        }
    }

    /**
     * Creates a clear error for malformed CSV rows.
     */
    private IllegalArgumentException malformed(
            Path file,
            int rowNumber,
            String message
    ) {

        return new IllegalArgumentException(
                "Malformed CSV row: "
                        + file
                        + " at row "
                        + rowNumber
                        + " - "
                        + message
        );
    }

    // ============================================================
    // FUNCTIONAL INTERFACE
    // ============================================================

    @FunctionalInterface
    private interface CsvRowHandler {

        void handle(
                String[] fields,
                int rowNumber
        );
    }
}