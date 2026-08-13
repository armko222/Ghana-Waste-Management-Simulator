package com.dcit308.wasteops.structures;

import java.util.Iterator;

/**
 * Doubly linked list (no java.util.LinkedList internally).
 * Implements LinkedListADT so Issue #9 can use it for hash-table
 * collision chaining if that is the chosen collision strategy.
 *
 * Owned by Issue #2.
 */
public class DoublyLinkedList<T> implements LinkedListADT<T> {

    @Override
    public void addFirst(T value) {
        throw new UnsupportedOperationException("TODO: Issue #2 \u2014 implement addFirst.");
    }

    @Override
    public void addLast(T value) {
        throw new UnsupportedOperationException("TODO: Issue #2 \u2014 implement addLast.");
    }

    @Override
    public void insertAfter(T ref, T value) {
        throw new UnsupportedOperationException("TODO: Issue #2 \u2014 implement insertAfter.");
    }

    @Override
    public void remove(T value) {
        throw new UnsupportedOperationException("TODO: Issue #2 \u2014 implement remove.");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("TODO: Issue #2 \u2014 implement size.");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("TODO: Issue #2 \u2014 implement isEmpty.");
    }

    @Override
    public Iterator<T> iterator() {
        throw new UnsupportedOperationException("TODO: Issue #2 \u2014 implement iterator.");
    }
}
