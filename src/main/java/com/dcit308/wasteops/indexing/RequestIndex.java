package com.dcit308.wasteops.indexing;

import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.structures.DynamicArray;
import com.dcit308.wasteops.structures.SearchTreeADT;

/**
 * Fast lookup of ServiceRequests by deadline, backed by Issue #6's
 * BinarySearchTree (implements SearchTreeADT).
 *
 * DUPLICATE DEADLINES: several requests can share one deadline — nothing
 * in the schema forbids it (data/sql/schema.sql, `deadline TEXT NOT NULL`,
 * no unique constraint). The tree stores one value per key, so each key
 * maps to the *bucket* of requests due at that deadline rather than to a
 * single request. Keying on deadline + requestId would make keys unique
 * but would defeat the point of the index: you could then only look a
 * request up if you already held it, and lookup by deadline alone — the
 * whole reason this class exists — would be impossible.
 *
 * BUCKET STORAGE: buckets are Issue #1's DynamicArray rather than
 * java.util.ArrayList. This index is the one place Issue #6 needs a
 * growable collection, so it is where the team's own structure gets
 * demonstrated instead of the Java Collections Framework.
 *
 * Owned by Issue #6. Depends on Issue #1 for DynamicArray.
 */
public class RequestIndex {

    private final SearchTreeADT<String, DynamicArray<ServiceRequest>> tree;

    public RequestIndex(SearchTreeADT<String, DynamicArray<ServiceRequest>> tree) {
        this.tree = tree;
    }

    /**
     * Adds a request to the bucket for its deadline, creating the bucket
     * on first use. Requests keep insertion order within a bucket:
     * DynamicArray exposes no append method, so each new request goes in
     * at index size(), which is the end of the bucket.
     */
    public void indexByDeadline(ServiceRequest request) {
        String key = request.getDeadline().toString();
        DynamicArray<ServiceRequest> bucket = tree.search(key);

        if (bucket == null) {
            bucket = new DynamicArray<>();
            tree.insert(key, bucket);
        }
        bucket.insert(bucket.size(), request);
    }

    /**
     * All requests due at the given deadline, in the order they were
     * indexed. Returns an empty array — never null — when the deadline is
     * absent, so callers can read the result without a null check.
     *
     * The result is a copy of the bucket, not the bucket itself.
     * java.util.Collections.unmodifiableList() previously gave callers a
     * read-only view; DynamicArray has no equivalent wrapper, and adding
     * one is Issue #1's call, so the index copies instead. The guarantee
     * callers get is therefore slightly different: mutating the returned
     * array is permitted but does not affect the index.
     *
     * @param deadlineKey ISO-8601 deadline, i.e. LocalDateTime.toString()
     */
    public DynamicArray<ServiceRequest> findByDeadline(String deadlineKey) {
        DynamicArray<ServiceRequest> bucket = tree.search(deadlineKey);
        DynamicArray<ServiceRequest> copy = new DynamicArray<>();

        if (bucket != null) {
            for (int i = 0; i < bucket.size(); i++) {
                copy.insert(i, bucket.get(i));
            }
        }
        return copy;
    }
}
