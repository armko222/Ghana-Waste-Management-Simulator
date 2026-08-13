package com.dcit308.wasteops.scheduling;

import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.structures.PriorityQueueADT;

/**
 * Dispatch rule: order by priority tier (High -> Medium -> Low); within
 * a tier, by urgency descending; final tie-break by time_submitted
 * ascending. Built on Issue #5's PriorityQueueADT.
 *
 * Owned by Issue #13.
 */
public class PriorityDispatcher {

    private final PriorityQueueADT<ServiceRequest> priorityQueue;

    public PriorityDispatcher(PriorityQueueADT<ServiceRequest> priorityQueue) {
        this.priorityQueue = priorityQueue;
    }

    public ServiceRequest getNextRequest() {
        throw new UnsupportedOperationException("TODO: Issue #13 \u2014 implement getNextRequest via the priority queue, with the documented tie-break rules.");
    }
}
