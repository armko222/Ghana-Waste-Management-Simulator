package com.dcit308.wa;

/**
 * Map Implementation using HashTable
 * No Java Collections used
 */
public class MapImpl<K, V> implements MapADT<K, V> {
    
    private HashTableChaining<K, V> table;
    
    /**
     * Constructor with default capacity
     */
    public MapImpl() {
        this.table = new HashTableChaining<>();
    }
    
    /**
     * Constructor with custom capacity
     */
    public MapImpl(int initialCapacity) {
        this.table = new HashTableChaining<>(initialCapacity);
    }
    
    @Override
    public V put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Null keys not allowed");
        }
        V oldValue = table.get(key);
        table.put(key, value);
        return oldValue;
    }
    
    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }
        return table.get(key);
    }
    
    @Override
    public V remove(K key) {
        if (key == null) {
            return null;
        }
        return table.remove(key);
    }
    
    @Override
    public boolean containsKey(K key) {
        if (key == null) {
            return false;
        }
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
    
    @Override
    public void clear() {
        table.clear();
    }
    
    @Override
    public SetADT<K> keySet() {
        SetImpl<K> keySet = new SetImpl<>();
        Object[] keys = table.keys();
        for (Object key : keys) {
            @SuppressWarnings("unchecked")
            K keyObj = (K) key;
            keySet.add(keyObj);
        }
        return keySet;
    }
    
    @Override
    public Object[] values() {
        return table.values();
    }
    
    @Override
    public Object[][] entrySet() {
        Object[] keys = table.keys();
        Object[][] entries = new Object[keys.length][2];
        
        for (int i = 0; i < keys.length; i++) {
            entries[i][0] = keys[i];
            entries[i][1] = table.get((K) keys[i]);
        }
        
        return entries;
    }
    
    @Override
    public String toString() {
        return table.toString();
    }
}