package com.dcit308.wasteops.structures;

/** Map abstraction built directly on the Role 9 custom hash table. */
public class CustomMap<K, V> implements MapLookupADT<K, V> {
    private final CustomHashTable<K, V> table;

    public CustomMap() {
        table = new CustomHashTable<>();
    }

    @Override
    public void put(K key, V value) {
        table.put(key, value);
    }

    @Override
    public V get(K key) {
        return table.get(key);
    }

    @Override
    public void remove(K key) {
        table.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return table.containsKey(key);
    }

    @Override
    public int size() {
        return table.size();
    }

    @Override
    public boolean isEmpty() {
        return table.isEmpty();
    }
}
