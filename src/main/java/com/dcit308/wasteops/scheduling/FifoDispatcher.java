package com.dcit308.wasteops.scheduling;

import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.structures.QueueADT;

import java.util.List;

/**
 * Dispatch rule: order strictly by time_submitted ascending, no other
 * factor. Assumes pendingRequests is already sorted by time_submitted
 * when passed in -- DispatchService is expected to fetch it that way
 * directly from the database (ORDER BY time_submitted ASC), rather than
 * sorting it again here. Built on Issue #4's QueueADT.
 *
 * Owned by Issue #13.
 */
public class FifoDispatcher {

    private final QueueADT<ServiceRequest> queue;

    public FifoDispatcher(QueueADT<ServiceRequest> queue) {
        this.queue = queue;
    }

    /** pendingRequests must already be in time_submitted ascending order. */
    public void loadPending(List<ServiceRequest> pendingRequests) {
        for (ServiceRequest request : pendingRequests) {
            queue.enqueue(request);
        }
    }

    public ServiceRequest getNextRequest() {
        return queue.dequeue();
    }

    public boolean hasNext() {
        return !queue.isEmpty();
    }
}