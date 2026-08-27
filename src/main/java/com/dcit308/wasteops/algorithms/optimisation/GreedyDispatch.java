package com.dcit308.wasteops.algorithms.optimisation;

import com.dcit308.wasteops.domain.ServiceRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Quick, practical budget-constrained selection: repeatedly take whichever
 * remaining request has the best value-per-weight ratio, until the budget
 * is used up or nothing else fits. Solves the SAME problem as KnapsackDP
 * (see RequestValuation) so the two can be directly compared -- that
 * comparison is where the required greedy-vs-DP counterexample comes from.
 *
 * Owned by Issue #13.
 */
public class GreedyDispatch {

    public static class Selection {
        public final List<ServiceRequest> chosen;
        public final int totalWeight;
        public final int totalValue;

        public Selection(List<ServiceRequest> chosen, int totalWeight, int totalValue) {
            this.chosen = chosen;
            this.totalWeight = totalWeight;
            this.totalValue = totalValue;
        }
    }

    public Selection selectRequests(List<ServiceRequest> candidates, int budget) {
        List<ServiceRequest> sorted = new ArrayList<>(candidates);
        sorted.sort((a, b) -> {
            double ratioA = (double) RequestValuation.value(a) / RequestValuation.weight(a);
            double ratioB = (double) RequestValuation.value(b) / RequestValuation.weight(b);
            return Double.compare(ratioB, ratioA); // descending
        });

        List<ServiceRequest> chosen = new ArrayList<>();
        int remainingBudget = budget;
        int totalValue = 0;

        for (ServiceRequest request : sorted) {
            int w = RequestValuation.weight(request);
            if (w <= remainingBudget) {
                chosen.add(request);
                remainingBudget -= w;
                totalValue += RequestValuation.value(request);
            }
        }

        return new Selection(chosen, budget - remainingBudget, totalValue);
    }
}