package com.dcit308.wasteops.structures;

/**
 * Contract for a strict first-in-first-out queue.
 *
 * OWNER (implements this): Issue #4 — CircularQueue.
 * CONSUMER (codes against this): Issue #12 — the FIFO dispatch strategy
 * just needs "add to the back, take from the front" — it doesn't need to
 * know about circular wrap-around internals.
 *
 * See Team_Handbook.docx, "Working With Each Other's Code."
 */
public interface QueueADT<T> {

    void enqueue(T value);

    T dequeue();

    T peekFront();

    boolean isEmpty();

    boolean isFull();

    int size();
}
