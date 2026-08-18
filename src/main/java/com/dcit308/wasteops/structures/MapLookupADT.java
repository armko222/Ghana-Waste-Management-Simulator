package com.dcit308.wasteops.structures;

public interface MapLookupADT<K, V> {
    void put(K key, V value);
    V get(K key);
    void remove(K key);
    boolean containsKey(K key);
    int size();
    boolean isEmpty();
}
