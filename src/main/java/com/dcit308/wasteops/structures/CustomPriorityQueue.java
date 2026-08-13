package com.dcit308.wasteops.structures;

/**
 * Thin wrapper exposing BinaryHeap through the PriorityQueueADT contract
 * that Issue #12 (Dijkstra) and Issue #13 (priority dispatch) build
 * against.
 *
 * Owned by Issue #5.
 */
public class CustomPriorityQueue<T> implements PriorityQueueADT<T> {

    private final BinaryHeap<T> heap = new BinaryHeap<>();

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
