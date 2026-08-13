package com.dcit308.wasteops.db;

import com.dcit308.wasteops.domain.AuditEvent;
import java.util.List;

/**
 * Basic CRUD + bulk load for the auditevent table. Ordinary
 * Java/JDBC tools are fine to use here (brief Section 8.ii exempts db/
 * from the no-built-ins rule).
 *
 * Owned by Issue #3.
 */
public class AuditEventRepository {

    public void save(AuditEvent item) {
        throw new UnsupportedOperationException("TODO: Issue #3 \u2014 implement save.");
    }

    public AuditEvent findById(String id) {
        throw new UnsupportedOperationException("TODO: Issue #3 \u2014 implement findById.");
    }

    public List<AuditEvent> findAll() {
        throw new UnsupportedOperationException("TODO: Issue #3 \u2014 implement findAll.");
    }
}
