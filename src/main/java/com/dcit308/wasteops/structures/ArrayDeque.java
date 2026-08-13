package com.dcit308.wasteops.structures;

/**
 * Double-ended queue, team's own implementation (not java.util.ArrayDeque).
 * Lets an urgent request jump to the front of the dispatch line.
 *
 * Owned by Issue #5. Implements DequeADT -- see DequeADT.java for who
 * depends on this without waiting for it.
 */
public class ArrayDeque<T> implements DequeADT<T> {

    @Override
    public void addFront(T value) {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement addFront.");
    }

    @Override
    public void addRear(T value) {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement addRear.");
    }

    @Override
    public T removeFront() {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement removeFront.");
    }

    @Override
    public T removeRear() {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement removeRear.");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement isEmpty.");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("TODO: Issue #5 \u2014 implement size.");
    }
}
