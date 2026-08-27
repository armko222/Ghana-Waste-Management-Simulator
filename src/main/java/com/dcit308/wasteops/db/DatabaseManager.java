package com.dcit308.wasteops.db;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Single point of entry for the SQLite database.
 *
 * <p>The database file is created automatically at
 * {@code data/wasteops.db}. The schema is loaded from
 * {@code data/sql/schema.sql}.
 *
 * <p>Owned by Issue #1.
 */
public class DatabaseManager {

    private static final String DB_URL =
            "jdbc:sqlite:data/wasteops.db";

    private static final Path SCHEMA_PATH =
            Path.of("data", "sql", "schema.sql");

    private Connection connection;

    /**
     * Opens the database connection and initializes the schema.
     */
    public Connection connect() {

        try {

            if (connection == null || connection.isClosed()) {

                connection = DriverManager.getConnection(DB_URL);
                connection.setAutoCommit(true);

                try (Statement statement =
                             connection.createStatement()) {

                    statement.execute(
                            "PRAGMA foreign_keys = ON"
                    );
                }

                initSchemaIfNeeded();
            }

            return connection;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to open database: " + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Loads and executes data/sql/schema.sql.
     */
    public void initSchemaIfNeeded() {

        String schema = loadSchema();

        try (Statement statement =
                     connect().createStatement()) {

            executeSchema(statement, schema);

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Schema initialisation failed: "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Reads the schema file from the project.
     */
    private String loadSchema() {

        try {

            if (!Files.exists(SCHEMA_PATH)) {

                throw new IllegalStateException(
                        "Could not find "
                                + SCHEMA_PATH.toAbsolutePath()
                                + " -- run the application from "
                                + "the project root."
                );
            }

            return Files.readString(
                    SCHEMA_PATH,
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to read schema file: "
                            + e.getMessage(),
                    e
            );
        }
    }

    /**
     * Executes each SQL statement individually.
     */
    private void executeSchema(
            Statement statement,
            String schema
    ) throws SQLException {

        StringBuilder current =
                new StringBuilder();

        for (String line : schema.split("\\R")) {

            String trimmed = line.trim();

            if (trimmed.startsWith("--")
                    || trimmed.isEmpty()) {
                continue;
            }

            current.append(line).append('\n');

            if (trimmed.endsWith(";")) {

                statement.execute(
                        current.toString().trim()
                );

                current.setLength(0);
            }
        }

        String remaining =
                current.toString().trim();

        if (!remaining.isEmpty()) {
            statement.execute(remaining);
        }
    }

    /**
     * Closes the database connection.
     */
    public void close() {

        try {

            if (connection != null
                    && !connection.isClosed()) {

                connection.close();
            }

        } catch (SQLException e) {

            System.err.println(
                    "[WARN] Failed to close database connection: "
                            + e.getMessage()
            );
        }
    }
}