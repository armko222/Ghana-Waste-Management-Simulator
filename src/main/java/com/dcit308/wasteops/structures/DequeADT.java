package com.dcit308.wasteops.structures;

/**
 * Contract for a double-ended queue.
 *
 * OWNER (implements this): Issue #5 — ArrayDeque (team's own
 * implementation, not java.util.ArrayDeque).
 * CONSUMER (codes against this): Issue #12 — needs to insert an urgent
 * request at the front of the dispatch line while everything else still
 * flows from the rear.
 *
 * See Team_Handbook.docx, "Working With Each Other's Code."
 */
public interface DequeADT<T> {

    void addFront(T value);

    void addRear(T value);

    T removeFront();

    T removeRear();

    boolean isEmpty();

    int size();
}
