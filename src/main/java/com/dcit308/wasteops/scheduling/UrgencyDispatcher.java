package com.dcit308.wasteops.scheduling;

import java.util.List;

import com.dcit308.wasteops.domain.ServiceRequest;

/**
 * Dispatch rule: order by urgency (1-5) descending; tie-break by earlier
 * time_submitted. Kept as a plain scan rather than its own heap -- a
 * deliberate simplicity choice, since it's called once per dispatch
 * decision rather than continuously.
 *
 * Owned by Issue #13.
 */
public class UrgencyDispatcher {

    public ServiceRequest getNextRequest(List<ServiceRequest> waitingRequests) {
        if (waitingRequests.isEmpty()) {
            return null;
        }

        ServiceRequest best = waitingRequests.get(0);
        for (ServiceRequest candidate : waitingRequests) {
            if (isBetter(candidate, best)) {
                best = candidate;
            }
        }
        return best;
    }

    private boolean isBetter(ServiceRequest candidate, ServiceRequest currentBest) {
        if (candidate.getUrgency() != currentBest.getUrgency()) {
            return candidate.getUrgency() > currentBest.getUrgency();
        }
        // Tie-break: earlier time_submitted wins.
        return candidate.getTimeSubmitted().isBefore(currentBest.getTimeSubmitted());
    }
}