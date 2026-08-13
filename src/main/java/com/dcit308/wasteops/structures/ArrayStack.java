package com.dcit308.wasteops.structures;

/**
 * Array-backed stack (no java.util.Stack). Powers the undo/audit log --
 * see AuditEvent and AuditEventRepository.
 *
 * Owned by Issue #3.
 */
public class ArrayStack<T> {

    public void push(T value) {
        throw new UnsupportedOperationException("TODO: Issue #3 \u2014 implement push.");
    }

    public T pop() {
        throw new UnsupportedOperationException("TODO: Issue #3 \u2014 implement pop. Must throw cleanly if empty.");
    }

    public T peek() {
        throw new UnsupportedOperationException("TODO: Issue #3 \u2014 implement peek. Must throw cleanly if empty.");
    }

    public boolean isEmpty() {
        throw new UnsupportedOperationException("TODO: Issue #3 \u2014 implement isEmpty.");
    }

    public int size() {
        throw new UnsupportedOperationException("TODO: Issue #3 \u2014 implement size.");
    }
}
