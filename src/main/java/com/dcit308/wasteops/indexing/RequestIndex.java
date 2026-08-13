package com.dcit308.wasteops.indexing;

import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.structures.SearchTreeADT;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Fast lookup of ServiceRequests by deadline, backed by Issue #6's
 * BinarySearchTree (implements SearchTreeADT).
 *
 * DUPLICATE DEADLINES: several requests can share one deadline — nothing
 * in the schema forbids it (data/sql/schema.sql, `deadline TEXT NOT NULL`,
 * no unique constraint). The tree stores one value per key, so each key
 * maps to the *list* of requests due at that deadline rather than to a
 * single request. Keying on deadline + requestId would make keys unique
 * but would defeat the point of the index: you could then only look a
 * request up if you already held it, and lookup by deadline alone — the
 * whole reason this class exists — would be impossible.
 *
 * Owned by Issue #6.
 */
public class RequestIndex {

    private final SearchTreeADT<String, List<ServiceRequest>> tree;

    public RequestIndex(SearchTreeADT<String, List<ServiceRequest>> tree) {
        this.tree = tree;
    }

    /**
     * Adds a request to the bucket for its deadline, creating the bucket
     * on first use. Requests keep insertion order within a bucket.
     */
    public void indexByDeadline(ServiceRequest request) {
        String key = request.getDeadline().toString();
        List<ServiceRequest> bucket = tree.search(key);

        if (bucket == null) {
            bucket = new ArrayList<>();
            tree.insert(key, bucket);
        }
        bucket.add(request);
    }

    /**
     * All requests due at the given deadline, in the order they were
     * indexed. Returns an empty list — never null — when the deadline is
     * absent, so callers can iterate the result without a null check.
     *
     * @param deadlineKey ISO-8601 deadline, i.e. LocalDateTime.toString()
     */
    public List<ServiceRequest> findByDeadline(String deadlineKey) {
        List<ServiceRequest> bucket = tree.search(deadlineKey);
        return bucket == null ? Collections.emptyList() : Collections.unmodifiableList(bucket);
    }
}
