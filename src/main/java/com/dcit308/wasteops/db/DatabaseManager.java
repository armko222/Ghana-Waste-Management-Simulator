package com.dcit308.wasteops.db;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Single point of entry for the SQLite database.
 *
 * <p>The database file is created automatically on first run in the working
 * directory ({@code waste_management.db}). The schema is applied once via
 * {@link #initSchemaIfNeeded()}; subsequent runs are no-ops because every
 * {@code CREATE} statement in schema.sql uses {@code IF NOT EXISTS}.
 *
 * <p>Ordinary JDBC is used here — the brief (Section 8.ii) explicitly exempts
 * the {@code db/} package from the no-built-ins rule.
 *
 * Owned by Issue #14.
 */
public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:waste_management.db";

    private Connection connection;

    /**
     * Opens (and if necessary creates) the SQLite database file.
     *
     * @return the active {@link Connection}
     * @throws RuntimeException if the JDBC driver fails to connect
     */
    public Connection connect() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(DB_URL);
                connection.setAutoCommit(true);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to open database: " + e.getMessage(), e);
        }
    }

    /**
     * Runs {@code data/sql/schema.sql} against the open connection.
     * Safe to call on every startup — all statements use {@code IF NOT EXISTS}.
     *
     * @throws RuntimeException if the schema file is missing or execution fails
     */
    public void initSchemaIfNeeded() {
        try (InputStream in = getClass().getResourceAsStream("/schema.sql")) {
            if (in == null) {
                throw new RuntimeException("schema.sql not found on classpath");
            }
            String sql = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            try (Statement stmt = connect().createStatement()) {
                for (String statement : sql.split(";")) {
                    String trimmed = statement.strip();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("--")) {
                        stmt.execute(trimmed);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read schema file: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new RuntimeException("Schema initialisation failed: " + e.getMessage(), e);
        }
    }

    /**
     * Closes the database connection. Call on application shutdown.
     */
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("[WARN] Failed to close database connection: " + e.getMessage());
        }
    }
}
