package com.dcit308.wasteops.structures;

/**
 * Min-heap, array-backed, built from scratch. Underlies
 * CustomPriorityQueue.
 *
 * Owned by Issue #5.
 */
public class BinaryHeap<T> {

    public void insert(int priority, T value) {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement insert (sift up).");
    }

    public T extractMin() {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement extractMin (sift down). Must throw cleanly if empty.");
    }

    public T peekMin() {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement peekMin. Must throw cleanly if empty.");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement isEmpty.");
    }

    public int size() {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement size.");
    }
}
