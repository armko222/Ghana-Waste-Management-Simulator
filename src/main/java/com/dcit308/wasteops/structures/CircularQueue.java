package com.dcit308.wasteops.structures;

/**
 * Fixed-size, array-backed queue with wrap-around (not java.util.ArrayDeque
 * or LinkedList). Powers FifoDispatcher.
 *
 * Owned by Issue #4. Implements QueueADT -- see QueueADT.java for who
 * depends on this without waiting for it.
 */
public class CircularQueue<T> implements QueueADT<T> {

    public CircularQueue(int capacity) {
        // TODO: Issue #4 -- allocate a fixed-size backing array of this capacity.
    }

    @Override
    public void enqueue(T value) {
        throw new UnsupportedOperationException("TODO: Issue #4 \u2014 implement enqueue. Must handle wrap-around.");
    }

    @Override
    public T dequeue() {
        throw new UnsupportedOperationException("TODO: Issue #4 \u2014 implement dequeue. Must throw cleanly if empty.");
    }

    @Override
    public T peekFront() {
        throw new UnsupportedOperationException("TODO: Issue #4 \u2014 implement peekFront.");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("TODO: Issue #4 \u2014 implement isEmpty.");
    }

    @Override
    public boolean isFull() {
        throw new UnsupportedOperationException("TODO: Issue #4 \u2014 implement isFull.");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("TODO: Issue #4 \u2014 implement size.");
    }
}
