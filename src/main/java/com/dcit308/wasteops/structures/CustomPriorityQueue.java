package com.dcit308.wasteops.structures;

public class CustomPriorityQueue<T> implements PriorityQueueADT<T> {
    private final BinaryHeap<T> heap;

    public CustomPriorityQueue() {
        this.heap = new BinaryHeap<>();
    }

    @Override
    public void insert(int priority, T value) {
        heap.insert(priority, value);
    }

    @Override
    public T extractMin() {
        return heap.extractMin();
    }

    @Override
    public T peekMin() {
        return heap.peekMin();
    }

    @Override
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    @Override
    public int size() {
        return heap.size();
    }
}