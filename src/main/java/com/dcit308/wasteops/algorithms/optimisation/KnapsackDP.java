package com.dcit308.wasteops.algorithms.optimisation;

import java.util.ArrayList;
import java.util.List;

import com.dcit308.wasteops.domain.ServiceRequest;

/**
 * Budget-constrained request selection, dynamic programming, guarantees
 * the optimal total value within budget -- the 0/1 knapsack problem.
 * Weight and value come from RequestValuation, so this solves the exact
 * same problem GreedyDispatch does, which is what makes a fair
 * greedy-vs-DP comparison possible.
 *
 * Owned by Issue #13.
 */
public class KnapsackDP {

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

    /**
     * Exposed after selectRequests() runs, purely so the required DP
     * trace table (brief Section 10) can be printed/inspected. Not used
     * by any other production logic.
     */
    public int[][] lastComputedTable;

    public Selection selectRequests(List<ServiceRequest> candidates, int budget) {
        int n = candidates.size();
        int[] weights = new int[n];
        int[] values = new int[n];
        for (int i = 0; i < n; i++) {
            weights[i] = RequestValuation.weight(candidates.get(i));
            values[i] = RequestValuation.value(candidates.get(i));
        }

        // table[i][b] = best total value achievable using the first i
        // requests with budget b.
        int[][] table = new int[n + 1][budget + 1];

        for (int i = 1; i <= n; i++) {
            for (int b = 0; b <= budget; b++) {
                int withoutItem = table[i - 1][b];
                if (weights[i - 1] <= b) {
                    int withItem = table[i - 1][b - weights[i - 1]] + values[i - 1];
                    table[i][b] = Math.max(withoutItem, withItem);
                } else {
                    table[i][b] = withoutItem;
                }
            }
        }
        this.lastComputedTable = table;

        // Reconstruction: walk backwards to find which items were used.
        List<ServiceRequest> chosen = new ArrayList<>();
        int remainingBudget = budget;
        for (int i = n; i >= 1; i--) {
            if (table[i][remainingBudget] != table[i - 1][remainingBudget]) {
                chosen.add(candidates.get(i - 1));
                remainingBudget -= weights[i - 1];
            }
        }

        int totalValue = table[n][budget];
        int totalWeight = budget - remainingBudget;
        return new Selection(chosen, totalWeight, totalValue);
    }
}