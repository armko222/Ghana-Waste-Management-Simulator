package com.dcit308.wasteops.util;

/**
 * Derives the three algorithm parameters required by brief Section 2.iii:
 * "Each team must derive at least three algorithm parameters from member
 * index numbers, for example priority weight, route penalty, hash-table
 * size, random seed or budget constraint."
 *
 * See docs/DECISION_LOG.md Decision D10 and docs/DATA_DICTIONARY.md for
 * the reasoning behind which three parameters were chosen and where each
 * is consumed.
 *
 * TODO (Issue #13, blocked on real team member index numbers):
 *   1. Replace MEMBER_INDEX_NUMBERS below with the actual 13 index numbers.
 *   2. Confirm/adjust the derivation formulas below with the team —
 *      current formulas are the placeholders proposed in Decision D10,
 *      not final.
 *   3. Once finalized, reference the exact formulas here in
 *      docs/DECISION_LOG.md so the report's Section 2 (dataset/
 *      parameters) and Section 15.ii (index-number-derived trace outputs)
 *      can cite this class directly.
 *
 * Consumed by:
 *   - Issue #9  (CustomHashTable initial size)
 *   - Issue #11 (Dijkstra route penalty weight)
 *   - Issue #12 (KnapsackDP budget constraint)
 */
public final class IndexParameterDeriver {

    // TODO: replace with the team's actual 13 index numbers.
    private static final int[] MEMBER_INDEX_NUMBERS = {
        // e.g. 10912345, 10912346, ...
    };

    private IndexParameterDeriver() {
        // utility class, no instances
    }

    /**
     * Hash table initial size: sum of all member index numbers, reduced
     * modulo a reasonable table-size range, rounded up to the next prime
     * (primes reduce clustering for common collision-handling strategies).
     * Placeholder formula — confirm with team once real numbers exist.
     */
    public static int deriveHashTableSize() {
        requireIndexNumbers();
        long sum = 0;
        for (int idx : MEMBER_INDEX_NUMBERS) {
            sum += idx;
        }
        int base = (int) (sum % 500) + 101; // keep in a sensible range, e.g. 101-600
        return nextPrime(base);
    }

    /**
     * Route penalty weight: derived from a specific member's index number
     * (e.g. the project leader's), applied as a road-condition multiplier
     * on top of Road.baseWeight() in Dijkstra (Issue #11).
     * Placeholder formula — confirm which member's number to use.
     */
    public static double deriveRoutePenalty() {
        requireIndexNumbers();
        int leaderIndex = MEMBER_INDEX_NUMBERS[0]; // TODO: confirm which member
        int lastDigit = leaderIndex % 10;
        return 1.0 + (lastDigit * 0.05); // e.g. 1.00 - 1.45 multiplier range
    }

    /**
     * DP budget constraint: aggregate of index numbers, scaled to a
     * sensible capacity range for the knapsack-style request selection
     * (Issue #12).
     * Placeholder formula — confirm scaling with team once real numbers
     * and realistic resource-capacity ranges are known.
     */
    public static int deriveBudgetConstraint() {
        requireIndexNumbers();
        long sum = 0;
        for (int idx : MEMBER_INDEX_NUMBERS) {
            sum += (idx % 1000);
        }
        return (int) (sum % 5000) + 1000; // e.g. 1000-6000 budget units
    }

    private static void requireIndexNumbers() {
        if (MEMBER_INDEX_NUMBERS.length == 0) {
            throw new IllegalStateException(
                "MEMBER_INDEX_NUMBERS is empty — replace the placeholder in "
                + "IndexParameterDeriver.java with the team's actual index numbers "
                + "before running any experiment that depends on these parameters."
            );
        }
    }

    private static int nextPrime(int from) {
        int candidate = Math.max(from, 2);
        while (!isPrime(candidate)) {
            candidate++;
        }
        return candidate;
    }

    private static boolean isPrime(int n) {
        if (n < 2) return false;
        for (int i = 2; (long) i * i <= n; i++) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
