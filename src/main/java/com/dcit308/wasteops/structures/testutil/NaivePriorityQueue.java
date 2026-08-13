package com.dcit308.wasteops.structures.testutil;

import com.dcit308.wasteops.structures.PriorityQueueADT;
import java.util.ArrayList;
import java.util.List;

/**
 * A deliberately naive, inefficient PriorityQueueADT implementation.
 *
 * This exists ONLY so that Issue #12 (Dijkstra) and Issue #13
 * (priority-tier dispatch) can write and run real, passing tests before
 * Issue #5's actual BinaryHeap exists. It is O(n) per operation (it just
 * scans a list) — that is fine here, because it is never used in the
 * final submission and is never part of anything graded. It exists purely
 * to unblock parallel work.
 *
 * HOW TO USE THIS PATTERN:
 *   1. Write your code (e.g. Dijkstra) against the PriorityQueueADT
 *      interface, never against a concrete class.
 *   2. In your own tests, construct a NaivePriorityQueue instead of the
 *      real BinaryHeap.
 *   3. Once Issue #5 merges BinaryHeap (which also implements
 *      PriorityQueueADT), change ONE line in your production code —
 *      the constructor call — to use BinaryHeap instead. Your tests,
 *      your algorithm logic, and everyone else's code are unaffected.
 *   4. Delete your reliance on this class once the real one is available;
 *      this class stays in testutil/ only as a reference/teaching example
 *      and, optionally, as a correctness oracle to compare BinaryHeap's
 *      output against in tests (two independent implementations agreeing
 *      is good evidence of correctness).
 *
 * See Team_Handbook.docx, "Working With Each Other's Code," for the full explanation.
 */
public class NaivePriorityQueue<T> implements PriorityQueueADT<T> {

    private static class Entry<T> {
        int priority;
        T value;
        Entry(int priority, T value) { this.priority = priority; this.value = value; }
    }

    private final List<Entry<T>> entries = new ArrayList<>();

    @Override
    public void insert(int priority, T value) {
        entries.add(new Entry<>(priority, value));
    }

    @Override
    public T extractMin() {
        if (isEmpty()) {
            throw new IllegalStateException("extractMin() called on an empty queue");
        }
        int minIndex = 0;
        for (int i = 1; i < entries.size(); i++) {
            if (entries.get(i).priority < entries.get(minIndex).priority) {
                minIndex = i;
            }
        }
        return entries.remove(minIndex).value;
    }

    @Override
    public T peekMin() {
        if (isEmpty()) {
            throw new IllegalStateException("peekMin() called on an empty queue");
        }
        Entry<T> min = entries.get(0);
        for (Entry<T> e : entries) {
            if (e.priority < min.priority) min = e;
        }
        return min.value;
    }

    @Override
    public boolean isEmpty() {
        return entries.isEmpty();
    }

    @Override
    public int size() {
        return entries.size();
    }
}
