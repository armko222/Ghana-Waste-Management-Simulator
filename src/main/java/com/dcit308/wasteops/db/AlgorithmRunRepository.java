package com.dcit308.wasteops.db;

import com.dcit308.wasteops.domain.AlgorithmRun;
import java.util.List;

/**
 * Basic CRUD + bulk load for the algorithmrun table. Ordinary
 * Java/JDBC tools are fine to use here (brief Section 8.ii exempts db/
 * from the no-built-ins rule).
 *
 * Owned by Issue #14.
 */
public class AlgorithmRunRepository {

    public void save(AlgorithmRun item) {
        throw new UnsupportedOperationException("TODO: Issue #14 \u2014 implement save.");
    }

    public AlgorithmRun findById(String id) {
        throw new UnsupportedOperationException("TODO: Issue #14 \u2014 implement findById.");
    }

    public List<AlgorithmRun> findAll() {
        throw new UnsupportedOperationException("TODO: Issue #14 \u2014 implement findAll.");
    }
}
