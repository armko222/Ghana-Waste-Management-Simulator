package com.dcit308.wasteops.structures;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Doubly linked list (no java.util.LinkedList internally).
 * Implements LinkedListADT so Issue #9 can use it for hash-table
 * collision chaining if that is the chosen collision strategy.
 *
 * Owned by Issue #2.
 */
public class DoublyLinkedList<T> implements LinkedListADT<T> {

    private Node<T> head;
    private Node<T> tail;
    private int size;

    private static class Node<T> {
        T value;
        Node<T> prev;
        Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

    @Override
    public void addFirst(T value) {
        Node<T> node = new Node<>(value);
        if (head == null) {
            head = node;
            tail = node;
        } else {
            node.next = head;
            head.prev = node;
            head = node;
        }
        size++;
    }

    @Override
    public void addLast(T value) {
        Node<T> node = new Node<>(value);
        if (tail == null) {
            head = node;
            tail = node;
        } else {
            node.prev = tail;
            tail.next = node;
            tail = node;
        }
        size++;
    }

    /**
     * REF-NOT-FOUND POLICY: if no node matches ref, this is a silent no-op
     * -- mirroring remove()'s "if present" contract instead of throwing, so
     * callers can treat both methods as consistently forgiving of a value
     * that turns out not to be in the list.
     */
    @Override
    public void insertAfter(T ref, T value) {
        Node<T> refNode = findFirst(ref);
        if (refNode == null) {
            return;
        }

        Node<T> node = new Node<>(value);
        node.prev = refNode;
        node.next = refNode.next;

        if (refNode.next != null) {
            refNode.next.prev = node;
        } else {
            tail = node;
        }
        refNode.next = node;
        size++;
    }

    @Override
    public void remove(T value) {
        Node<T> target = findFirst(value);
        if (target == null) {
            return;
        }

        if (target.prev != null) {
            target.prev.next = target.next;
        } else {
            head = target.next;
        }

        if (target.next != null) {
            target.next.prev = target.prev;
        } else {
            tail = target.prev;
        }

        target.prev = null;
        target.next = null;
        size--;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new ForwardIterator();
    }

    private Node<T> findFirst(T value) {
        Node<T> current = head;
        while (current != null) {
            if (valuesEqual(current.value, value)) {
                return current;
            }
            current = current.next;
        }
        return null;
    }

    private boolean valuesEqual(T a, T b) {
        return (a == null) ? (b == null) : a.equals(b);
    }

    private class ForwardIterator implements Iterator<T> {
        private Node<T> current = head;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            if (current == null) {
                throw new NoSuchElementException("No more elements in DoublyLinkedList.");
            }
            T value = current.value;
            current = current.next;
            return value;
        }
    }
}
