package com.dcit308.wasteops.structures;

/**
 * Contract for a doubly linked list.
 *
 * OWNER (implements this): Issue #2 — DoublyLinkedList.
 * CONSUMER (codes against this): Issue #9, ONLY IF chaining is the
 * chosen collision strategy for CustomHashTable. If Issue #9 chooses
 * open addressing instead, this interface simply isn't used by them —
 * confirm the choice between the two issue owners early.
 *
 * See Team_Handbook.docx, "Working With Each Other's Code."
 */
public interface LinkedListADT<T> extends Iterable<T> {

    void addFirst(T value);

    void addLast(T value);

    /** Inserts value immediately after the first node matching ref. */
    void insertAfter(T ref, T value);

    /** Removes the first node matching value, if present. */
    void remove(T value);

    int size();

    boolean isEmpty();
}
