package com.dcit308.wasteops.algorithms.optimisation;

import com.dcit308.wasteops.domain.ServiceRequest;
import java.util.List;

/**
 * Budget-constrained request selection, dynamic programming, guarantees
 * the optimal answer within budget. Budget parameter must be one of the
 * team's index-number-derived parameters (see
 * util/IndexParameterDeriver.java).
 *
 * Owned by Issue #13.
 */
public class KnapsackDP {

    public static class Selection {
        public final List<ServiceRequest> chosen;
        public final int totalValue;

        public Selection(List<ServiceRequest> chosen, int totalValue) {
            this.chosen = chosen;
            this.totalValue = totalValue;
        }
    }

    public Selection selectRequests(List<ServiceRequest> requests, int budget) {
        throw new UnsupportedOperationException(
            "TODO: Issue #13 \u2014 implement the DP solution. Produce the memoisation/" +
            "tabulation table and the reconstruction step as required evidence.");
    }
}
