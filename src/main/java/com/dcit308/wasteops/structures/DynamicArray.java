package com.dcit308.wasteops.structures;

/**
 * Resizable array, built from scratch (no java.util.ArrayList as internal
 * storage -- use a plain Object[] and grow it yourself).
 *
 * Owned by Issue #1.
 */
public class DynamicArray<T> {

    public void insert(int index, T value) {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement insert.");
    }

    public T get(int index) {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement get.");
    }

    public void set(int index, T value) {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement set.");
    }

    public void remove(int index) {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement remove.");
    }

    public int size() {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement size.");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("TODO: Issue #1 \u2014 implement isEmpty.");
    }

    // TODO: Issue #1 -- private resize() helper, doubling the backing array
    // when it fills up. Called internally by insert().
}
