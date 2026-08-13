package com.dcit308.wasteops.db;

import com.dcit308.wasteops.domain.Resource;
import java.util.List;

/**
 * Basic CRUD + bulk load for the resource table. Ordinary
 * Java/JDBC tools are fine to use here (brief Section 8.ii exempts db/
 * from the no-built-ins rule).
 *
 * Owned by Issue #1.
 */
public class ResourceRepository {

    public void save(Resource item) {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement save.");
    }

    public Resource findById(String id) {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement findById.");
    }

    public List<Resource> findAll() {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement findAll.");
    }
}
