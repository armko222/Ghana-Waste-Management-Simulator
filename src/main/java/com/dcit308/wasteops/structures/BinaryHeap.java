package com.dcit308.wasteops.structures;

import java.util.NoSuchElementException;

public class BinaryHeap<T> {
    
    // Internal class to map priorities to generic values
    private static class HeapNode<T> {
        int priority;
        T value;

        HeapNode(int priority, T value) {
            this.priority = priority;
            this.value = value;
        }
    }

    private HeapNode<T>[] heap;
    private int size;
    private static final int INITIAL_CAPACITY = 10;

    @SuppressWarnings("unchecked")
    public BinaryHeap() {
        heap = new HeapNode[INITIAL_CAPACITY];
        size = 0;
    }

    public void insert(int priority, T value) {
        if (size == heap.length) resize();
        heap[size] = new HeapNode<>(priority, value);
        heapifyUp(size);
        size++;
    }

    public T extractMin() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        T min = heap[0].value;
        heap[0] = heap[size - 1];
        heap[size - 1] = null;
        size--;
        if (size > 0) heapifyDown(0);
        return min;
    }

    public T peekMin() {
        if (isEmpty()) throw new NoSuchElementException("Heap is empty");
        return heap[0].value;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    private void heapifyUp(int index) {
        int parentIndex = (index - 1) / 2;
        while (index > 0 && heap[index].priority < heap[parentIndex].priority) {
            swap(index, parentIndex);
            index = parentIndex;
            parentIndex = (index - 1) / 2;
        }
    }

    private void heapifyDown(int index) {
        int smallest = index;
        int leftChild = 2 * index + 1;
        int rightChild = 2 * index + 2;

        if (leftChild < size && heap[leftChild].priority < heap[smallest].priority) {
            smallest = leftChild;
        }
        if (rightChild < size && heap[rightChild].priority < heap[smallest].priority) {
            smallest = rightChild;
        }
        if (smallest != index) {
            swap(index, smallest);
            heapifyDown(smallest);
        }
    }

    private void swap(int i, int j) {
        HeapNode<T> temp = heap[i];
        heap[i] = heap[j];
        heap[j] = temp;
    }

    @SuppressWarnings("unchecked")
    private void resize() {
        HeapNode<T>[] newHeap = new HeapNode[heap.length * 2];
        System.arraycopy(heap, 0, newHeap, 0, size);
        heap = newHeap;
    }
}