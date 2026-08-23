package com.dcit308.wasteops.structures;

/**
 * Resizable array, built from scratch.
 *
 * No java.util.ArrayList is used as internal storage.
 * Uses a plain Object[] and doubles its capacity when full.
 *
 * Owned by Issue #1.
 */
public class DynamicArray<T> {

    private Object[] elements;
    private int size;

    private static final int DEFAULT_CAPACITY = 10;

    /**
     * Creates an empty DynamicArray with the default capacity.
     */
    public DynamicArray() {
        elements = new Object[DEFAULT_CAPACITY];
        size = 0;
    }

    /**
     * Inserts a value at the specified index.
     *
     * Existing elements from index onward are shifted one position
     * to the right.
     *
     * Valid indexes are from 0 to size inclusive.
     */
    public void insert(int index, T value) {

        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size: " + size
            );
        }

        // If the array is full, increase its capacity.
        if (size == elements.length) {
            resize();
        }

        // Shift elements to the right.
        for (int i = size; i > index; i--) {
            elements[i] = elements[i - 1];
        }

        elements[index] = value;
        size++;
    }

    /**
     * Returns the element at the specified index.
     */
    @SuppressWarnings("unchecked")
    public T get(int index) {

        checkElementIndex(index);

        return (T) elements[index];
    }

    /**
     * Replaces the element at the specified index.
     */
    public void set(int index, T value) {

        checkElementIndex(index);

        elements[index] = value;
    }

    /**
     * Removes the element at the specified index.
     *
     * Elements after the removed element are shifted left.
     */
    public void remove(int index) {

        checkElementIndex(index);

        // Shift elements to the left.
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        // Remove reference to the last element.
        elements[size - 1] = null;

        size--;
    }

    /**
     * Returns the number of elements currently stored.
     */
    public int size() {
        return size;
    }

    /**
     * Returns true if the array contains no elements.
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Doubles the capacity of the backing array.
     */
    private void resize() {

        int newCapacity = elements.length * 2;

        Object[] newElements = new Object[newCapacity];

        for (int i = 0; i < size; i++) {
            newElements[i] = elements[i];
        }

        elements = newElements;
    }

    /**
     * Checks whether an index refers to an existing element.
     */
    private void checkElementIndex(int index) {

        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException(
                    "Index: " + index + ", Size: " + size
            );
        }
    }
}