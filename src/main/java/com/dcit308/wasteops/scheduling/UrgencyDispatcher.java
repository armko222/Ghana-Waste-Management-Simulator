package com.dcit308.wasteops.scheduling;

import com.dcit308.wasteops.domain.ServiceRequest;
import java.util.List;

/**
 * Dispatch rule: order by urgency (1-5) descending; tie-break by earlier
 * time_submitted.
 *
 * Owned by Issue #13.
 */
public class UrgencyDispatcher {

    public ServiceRequest getNextRequest(List<ServiceRequest> waitingRequests) {
        throw new UnsupportedOperationException("TODO: Issue #13 \u2014 implement urgency-first selection with the documented tie-break.");
    }
}
