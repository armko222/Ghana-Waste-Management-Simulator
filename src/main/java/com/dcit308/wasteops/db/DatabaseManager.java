package com.dcit308.wasteops.db;

import java.sql.Connection;

/**
 * SQLite connection and automatic schema initialisation from
 * data/sql/schema.sql. Ordinary Java/JDBC tools are fine to use in this
 * package (brief Section 8.ii exempts db/ from the no-built-ins rule).
 *
 * Owned by Issue #14.
 */
public class DatabaseManager {

    public Connection connect() {
        throw new UnsupportedOperationException("TODO: Issue #14 \u2014 open (and if needed create) the SQLite connection.");
    }

    public void initSchemaIfNeeded() {
        throw new UnsupportedOperationException("TODO: Issue #14 \u2014 run data/sql/schema.sql if the database file is new.");
    }
}
