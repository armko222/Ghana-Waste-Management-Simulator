package com.dcit308.wasteops.structures;

/**
 * Set implemented on top of the custom hash table. Each distinct value is
 * stored as a key and maps to one shared marker object.
 */
public class CustomSet<T> {
    private static final Object PRESENT = new Object();
    private final CustomHashTable<T, Object> table;

    public CustomSet() {
        table = new CustomHashTable<>();
    }

    public void add(T value) {
        table.put(value, PRESENT);
    }

    public boolean contains(T value) {
        return table.containsKey(value);
    }

    public void remove(T value) {
        table.remove(value);
    }

    public int size() {
        return table.size();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }
}
