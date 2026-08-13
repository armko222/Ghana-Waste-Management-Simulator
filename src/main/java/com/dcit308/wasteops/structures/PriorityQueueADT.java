package com.dcit308.wasteops.structures;

/**
 * Contract for a priority queue backed by a custom heap.
 *
 * OWNER (implements this): Issue #5 — BinaryHeap / CustomPriorityQueue.
 * CONSUMERS (code against this, do not wait for the real implementation):
 *   - Issue #12 (Dijkstra) — needs to repeatedly extract the
 *     not-yet-finalized vertex with the smallest known distance.
 *   - Issue #13 (Priority-tier dispatch) — needs to extract the
 *     highest-priority waiting request.
 *
 * Collaboration pattern: Issue #12 and #13 can start writing real,
 * testable code today against this interface, using a trivial stand-in
 * implementation (see structures/testutil/NaivePriorityQueue.java) instead
 * of waiting for Issue #5's real heap. Once Issue #5 merges a
 * BinaryHeap/CustomPriorityQueue that implements this same interface,
 * Issues #12 and #13 swap one constructor call — no other code changes.
 *
 * See Team_Handbook.docx, "Working With Each Other's Code," for the full
 * explanation of this pattern and which issues own vs. consume each contract.
 */
public interface PriorityQueueADT<T> {

    /** Inserts a value with the given priority (lower = more urgent, by convention). */
    void insert(int priority, T value);

    /** Removes and returns the value with the smallest priority. Throws if empty. */
    T extractMin();

    /** Returns (without removing) the value with the smallest priority. Throws if empty. */
    T peekMin();

    boolean isEmpty();

    int size();
}
