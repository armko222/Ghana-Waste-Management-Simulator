package com.dcit308.wasteops.service;

import com.dcit308.wasteops.domain.ServiceRequest;

/**
 * Exposes all three dispatch modes (FIFO / urgency / priority) plus
 * greedy and DP selection as callable operations -- the piece the
 * console menu (Issue #13's minor task, extended by Issue #14) actually
 * calls into.
 *
 * Owned by Issue #13.
 */
public class DispatchService {

    public ServiceRequest dispatchNextFifo() {
        throw new UnsupportedOperationException("TODO: Issue #13 \u2014 wire to FifoDispatcher.");
    }

    public ServiceRequest dispatchNextByUrgency() {
        throw new UnsupportedOperationException("TODO: Issue #13 \u2014 wire to UrgencyDispatcher.");
    }

    public ServiceRequest dispatchNextByPriority() {
        throw new UnsupportedOperationException("TODO: Issue #13 \u2014 wire to PriorityDispatcher.");
    }

    // TODO: Issue #13 -- expose greedy dispatch and the DP budget-selection
    // operation here too, once GreedyDispatch and KnapsackDP exist.
}
