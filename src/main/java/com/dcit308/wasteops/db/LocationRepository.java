package com.dcit308.wasteops.db;

import com.dcit308.wasteops.domain.Location;
import java.util.List;

/**
 * Basic CRUD + bulk load for the location table. Ordinary
 * Java/JDBC tools are fine to use here (brief Section 8.ii exempts db/
 * from the no-built-ins rule).
 *
 * Owned by Issue #1.
 */
public class LocationRepository {

    public void save(Location item) {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement save.");
    }

    public Location findById(String id) {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement findById.");
    }

    public List<Location> findAll() {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement findAll.");
    }
}
