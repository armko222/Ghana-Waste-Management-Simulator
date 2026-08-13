package com.dcit308.wasteops.indexing;

import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.structures.SearchTreeADT;

/**
 * Fast lookup of ServiceRequests by deadline, backed by Issue #6's
 * BinarySearchTree (implements SearchTreeADT).
 *
 * Owned by Issue #6.
 */
public class RequestIndex {

    private final SearchTreeADT<String, ServiceRequest> tree;

    public RequestIndex(SearchTreeADT<String, ServiceRequest> tree) {
        this.tree = tree;
    }

    public void indexByDeadline(ServiceRequest request) {
        throw new UnsupportedOperationException("TODO: Issue #6 \u2014 implement indexByDeadline.");
    }

    public ServiceRequest findByDeadlineKey(String deadlineKey) {
        throw new UnsupportedOperationException("TODO: Issue #6 \u2014 implement findByDeadlineKey.");
    }
}
