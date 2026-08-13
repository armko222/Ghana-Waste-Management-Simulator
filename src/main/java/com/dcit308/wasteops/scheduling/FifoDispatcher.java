package com.dcit308.wasteops.scheduling;

import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.structures.QueueADT;

/**
 * Dispatch rule: order strictly by time_submitted ascending, no other
 * factor. Built on Issue #4's QueueADT -- code against the interface,
 * not CircularQueue directly.
 *
 * Owned by Issue #13.
 */
public class FifoDispatcher {

    private final QueueADT<ServiceRequest> queue;

    public FifoDispatcher(QueueADT<ServiceRequest> queue) {
        this.queue = queue;
    }

    public ServiceRequest getNextRequest() {
        throw new UnsupportedOperationException("TODO: Issue #13 \u2014 implement getNextRequest via the queue.");
    }
}
