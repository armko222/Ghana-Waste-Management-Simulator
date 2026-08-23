package com.dcit308.wasteops.algorithms.optimisation;

import com.dcit308.wasteops.domain.ServiceRequest;

/**
 * Turns a ServiceRequest into the weight/value numbers GreedyDispatch and
 * KnapsackDP both need. Shared here so the two algorithms are guaranteed
 * to be solving the exact same problem on the exact same numbers -- if
 * they used different valuations, a greedy-vs-DP comparison wouldn't be
 * a fair counterexample, it would just be two algorithms answering two
 * different questions.
 *
 * Weight: derived from category. Hazardous requests are treated as
 * needing more specialised capacity than General or Industrial ones.
 * Value: derived from priority tier (dominant) plus urgency (fine-grained).
 *
 * Owned by Issue #13.
 */
public final class RequestValuation {

    private RequestValuation() {
        // static helper only
    }

    public static int weight(ServiceRequest request) {
        switch (request.getCategory()) {
            case "Hazardous": return 3;
            case "Industrial": return 2;
            case "General": return 1;
            default:
                // Should never happen -- categories are fixed at General/
                // Hazardous/Industrial (see Team_Handbook.docx). Falling
                // back to 1 rather than throwing keeps a bad CSV row from
                // crashing a whole dispatch cycle.
                return 1;
        }
    }

    public static int value(ServiceRequest request) {
        int priorityValue;
        switch (request.getPriority()) {
            case HIGH:   priorityValue = 100; break;
            case MEDIUM: priorityValue = 50;  break;
            default:     priorityValue = 10;  break; // LOW
        }
        return priorityValue + request.getUrgency();
    }
}