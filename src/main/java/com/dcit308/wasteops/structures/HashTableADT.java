package com.dcit308.wa;

/**
 * Hash Table ADT Interface - Custom implementation without Java Collections
 * @param <K> Key type
 * @param <V> Value type
 */
public interface HashTableADT<K, V> {
    
    /**
     * Inserts a key-value pair into the hash table
     * If key already exists, updates the value
     */
    void put(K key, V value);
    
    /**
     * Retrieves the value associated with the key
     * @return value or null if key not found
     */
    V get(K key);
    
    /**
     * Removes the key-value pair from the hash table
     * @return the removed value, or null if key not found
     */
    V remove(K key);
    
    /**
     * Checks if the hash table contains the specified key
     */
    boolean containsKey(K key);
    
    /**
     * Returns the number of key-value pairs in the hash table
     */
    int size();
    
    /**
     * Checks if the hash table is empty
     */
    boolean isEmpty();
    
    /**
     * Returns an array of all keys
     */
    Object[] keys();
    
    /**
     * Returns an array of all values
     */
    Object[] values();
    
    /**
     * Removes all entries from the hash table
     */
    void clear();
    
    /**
     * Returns the current load factor
     */
    double getLoadFactor();
}
