package com.dcit308.wasteops.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages the SQLite database connection and schema initialization.
 *
 * The database file is created automatically if it does not exist.
 *
 * The schema is loaded from:
 *
 * src/main/resources/data/sql/schema.sql
 *
 * Owned by Issue #14.
 */
public class DatabaseManager {

    private static final String DATABASE_URL =
            "jdbc:sqlite:data/wasteops.db";

    private static final String SCHEMA_RESOURCE =
            "data/sql/schema.sql";

    /**
     * Opens a connection to the SQLite database.
     *
     * SQLite creates the database file automatically if it
     * does not already exist.
     *
     * The schema is also initialized automatically.
     */
    public Connection connect() {

        try {

            Connection connection =
                    DriverManager.getConnection(DATABASE_URL);

            // Enable foreign-key enforcement.
            try (Statement statement =
                         connection.createStatement()) {

                statement.execute(
                        "PRAGMA foreign_keys = ON"
                );
            }

            // Initialize tables automatically.
            initializeSchema(connection);

            return connection;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to connect to SQLite database.",
                    e
            );
        }
    }

    /**
     * Initializes the database schema.
     *
     * This method is public so it can also be called explicitly
     * by application startup code if required.
     */
    public void initSchemaIfNeeded() {

        try (Connection connection =
                     DriverManager.getConnection(DATABASE_URL)) {

            try (Statement statement =
                         connection.createStatement()) {

                statement.execute(
                        "PRAGMA foreign_keys = ON"
                );

                initializeSchema(connection);
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Failed to initialize database schema.",
                    e
            );
        }
    }

    /**
     * Loads schema.sql and executes each SQL statement.
     */
    private void initializeSchema(Connection connection)
            throws SQLException {

        String schema = loadSchema();

        try (Statement statement =
                     connection.createStatement()) {

            executeSchema(statement, schema);
        }
    }

    /**
     * Loads schema.sql from the application resources.
     */
    private String loadSchema() {

        try (InputStream inputStream =
                     getClass()
                             .getClassLoader()
                             .getResourceAsStream(
                                     SCHEMA_RESOURCE
                             )) {

            if (inputStream == null) {

                throw new IllegalStateException(
                        "Could not find database schema: "
                                + SCHEMA_RESOURCE
                                + ". Make sure schema.sql is located at "
                                + "src/main/resources/data/sql/schema.sql"
                );
            }

            return new String(
                    inputStream.readAllBytes(),
                    StandardCharsets.UTF_8
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to read database schema.",
                    e
            );
        }
    }

    /**
     * Executes individual SQL statements from schema.sql.
     *
     * SQLite JDBC does not reliably execute an entire
     * multi-statement SQL script with one execute() call.
     */
    private void executeSchema(
            Statement statement,
            String schema
    ) throws SQLException {

        StringBuilder currentStatement =
                new StringBuilder();

        String[] lines = schema.split("\\R");

        for (String line : lines) {

            String trimmedLine = line.trim();

            // Ignore SQL comments.
            if (trimmedLine.startsWith("--")) {
                continue;
            }

            currentStatement
                    .append(line)
                    .append('\n');

            if (trimmedLine.endsWith(";")) {

                String sql =
                        currentStatement
                                .toString()
                                .trim();

                if (!sql.isEmpty()) {
                    statement.execute(sql);
                }

                currentStatement.setLength(0);
            }
        }

        // Handle a final statement without semicolon.
        String remaining =
                currentStatement
                        .toString()
                        .trim();

        if (!remaining.isEmpty()) {
            statement.execute(remaining);
        }
    }
}