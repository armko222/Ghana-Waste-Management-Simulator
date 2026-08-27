package com.dcit308.wasteops.scheduling;

import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.structures.PriorityQueueADT;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Dispatch rule: order by priority tier (High -> Medium -> Low); within
 * a tier, by urgency descending; final tie-break by time_submitted
 * ascending. Built on Issue #5's PriorityQueueADT.
 *
 * PriorityQueueADT.insert() only accepts a single int, so tier, urgency,
 * and submission time are packed into one composite key:
 *
 *   key = tierRank * 1_000_000 + (5 - urgency) * 100_000 + minutesSinceReference
 *
 * extractMin() returns the smallest key first, so High tier (rank 0)
 * always beats Medium/Low regardless of the rest of the key, and within
 * a tier, higher urgency (smaller (5-urgency)) wins next.
 *
 * ASSUMPTION worth knowing: minutesSinceReference must stay below
 * 100,000 (about 69 days) for the tier/urgency terms to dominate
 * correctly. referenceTime should be set to the earliest time_submitted
 * in the whole dataset -- fine for a project on this timescale, but
 * would need revisiting for a dataset spanning several months.
 *
 * Owned by Issue #13.
 */
public class PriorityDispatcher {

    private final PriorityQueueADT<ServiceRequest> priorityQueue;
    private final LocalDateTime referenceTime;

    public PriorityDispatcher(PriorityQueueADT<ServiceRequest> priorityQueue, LocalDateTime referenceTime) {
        this.priorityQueue = priorityQueue;
        this.referenceTime = referenceTime;
    }

    public void loadPending(List<ServiceRequest> pendingRequests) {
        for (ServiceRequest request : pendingRequests) {
            priorityQueue.insert(computeKey(request), request);
        }
    }

    public ServiceRequest getNextRequest() {
        return priorityQueue.extractMin();
    }

    public boolean hasNext() {
        return !priorityQueue.isEmpty();
    }

    private int computeKey(ServiceRequest request) {
        int tierRank;
        switch (request.getPriority()) {
            case HIGH:   tierRank = 0; break;
            case MEDIUM: tierRank = 1; break;
            default:     tierRank = 2; break; // LOW
        }
        int urgencyTerm = 5 - request.getUrgency(); // higher urgency -> smaller term
        long minutesSinceReference = Duration.between(referenceTime, request.getTimeSubmitted()).toMinutes();

        return tierRank * 1_000_000 + urgencyTerm * 100_000 + (int) minutesSinceReference;
    }
}