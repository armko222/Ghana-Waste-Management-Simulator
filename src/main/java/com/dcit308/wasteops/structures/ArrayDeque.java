package com.dcit308.wasteops.structures;

import java.util.NoSuchElementException;

public class ArrayDeque<T> implements DequeADT<T> {
    private T[] deque;
    private int front;
    private int rear;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public ArrayDeque() {
        deque = (T[]) new Object[INITIAL_CAPACITY];
        front = 0;
        rear = 0;
        size = 0;
    }

    @Override
    public void addFront(T item) {
        if (size == deque.length) resize();
        front = (front - 1 + deque.length) % deque.length;
        deque[front] = item;
        size++;
    }

    @Override
    public void addRear(T item) {
        if (size == deque.length) resize();
        deque[rear] = item;
        rear = (rear + 1) % deque.length;
        size++;
    }

    @Override
    public T removeFront() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        T item = deque[front];
        deque[front] = null; 
        front = (front + 1) % deque.length;
        size--;
        return item;
    }

    @Override
    public T removeRear() {
        if (isEmpty()) throw new NoSuchElementException("Deque is empty");
        rear = (rear - 1 + deque.length) % deque.length;
        T item = deque[rear];
        deque[rear] = null; 
        size--;
        return item;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        T[] newDeque = (T[]) new Object[deque.length * 2];
        for (int i = 0; i < size; i++) {
            newDeque[i] = deque[(front + i) % deque.length];
        }
        deque = newDeque;
        front = 0;
        rear = size;
    }
}