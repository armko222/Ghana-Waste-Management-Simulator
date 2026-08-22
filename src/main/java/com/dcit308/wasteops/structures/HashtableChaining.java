package com.dcit308.wa;

/**
 * Custom Hash Table implementation using Separate Chaining
 * No Java Collections used - implements from scratch
 */
public class HashTableChaining<K, V> implements HashTableADT<K, V> {
    
    // Node class for linked list in each bucket
    private static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;
        
        Node(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
        }
    }
    
    private Node<K, V>[] buckets;
    private int capacity;
    private int size;
    private static final double LOAD_FACTOR_THRESHOLD = 0.75;
    private static final int INITIAL_CAPACITY = 16;
    
    /**
     * Constructor with default initial capacity
     */
    @SuppressWarnings("unchecked")
    public HashTableChaining() {
        this.capacity = INITIAL_CAPACITY;
        this.buckets = (Node<K, V>[]) new Node[capacity];
        this.size = 0;
    }
    
    /**
     * Constructor with custom initial capacity
     */
    @SuppressWarnings("unchecked")
    public HashTableChaining(int initialCapacity) {
        if (initialCapacity <= 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.capacity = initialCapacity;
        this.buckets = (Node<K, V>[]) new Node[capacity];
        this.size = 0;
    }
    
    /**
     * Custom hash function - combines hashCode with scrambling for better distribution
     */
    private int hash(K key) {
        if (key == null) {
            return 0;
        }
        int h = key.hashCode();
        h = h ^ (h >>> 16);
        return Math.abs(h) % capacity;
    }
    
    @Override
    public void put(K key, V value) {
        if (key == null) {
            throw new IllegalArgumentException("Null keys not allowed");
        }
        
        if ((double) size / capacity >= LOAD_FACTOR_THRESHOLD) {
            resize();
        }
        
        int index = hash(key);
        Node<K, V> current = buckets[index];
        
        while (current != null) {
            if (current.key.equals(key)) {
                current.value = value;
                return;
            }
            current = current.next;
        }
        
        Node<K, V> newNode = new Node<>(key, value);
        newNode.next = buckets[index];
        buckets[index] = newNode;
        size++;
    }
    
    @Override
    public V get(K key) {
        if (key == null) {
            return null;
        }
        
        int index = hash(key);
        Node<K, V> current = buckets[index];
        
        while (current != null) {
            if (current.key.equals(key)) {
                return current.value;
            }
            current = current.next;
        }
        
        return null;
    }
    
    @Override
    public V remove(K key) {
        if (key == null) {
            return null;
        }
        
        int index = hash(key);
        Node<K, V> current = buckets[index];
        Node<K, V> previous = null;
        
        while (current != null) {
            if (current.key.equals(key)) {
                if (previous == null) {
                    buckets[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return current.value;
            }
            previous = current;
            current = current.next;
        }
        
        return null;
    }
    
    @Override
    public boolean containsKey(K key) {
        return get(key) != null;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public boolean isEmpty() {
        return size == 0;
    }
    
    @Override
    public Object[] keys() {
        Object[] keysArray = new Object[size];
        int index = 0;
        
        for (int i = 0; i < capacity; i++) {
            Node<K, V> current = buckets[i];
            while (current != null) {
                keysArray[index++] = current.key;
                current = current.next;
            }
        }
        
        return keysArray;
    }
    
    @Override
    public Object[] values() {
        Object[] valuesArray = new Object[size];
        int index = 0;
        
        for (int i = 0; i < capacity; i++) {
            Node<K, V> current = buckets[i];
            while (current != null) {
                valuesArray[index++] = current.value;
                current = current.next;
            }
        }
        
        return valuesArray;
    }
    
    @Override
    public void clear() {
        @SuppressWarnings("unchecked")
        Node<K, V>[] newBuckets = (Node<K, V>[]) new Node[capacity];
        this.buckets = newBuckets;
        this.size = 0;
    }
    
    @Override
    public double getLoadFactor() {
        return (double) size / capacity;
    }
    
    /**
     * Resizes the hash table when load factor exceeds threshold
     */
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = capacity * 2;
        Node<K, V>[] oldBuckets = buckets;
        
        buckets = (Node<K, V>[]) new Node[newCapacity];
        int oldCapacity = capacity;
        capacity = newCapacity;
        size = 0;
        
        for (int i = 0; i < oldCapacity; i++) {
            Node<K, V> current = oldBuckets[i];
            while (current != null) {
                put(current.key, current.value);
                current = current.next;
            }
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        
        for (int i = 0; i < capacity; i++) {
            Node<K, V> current = buckets[i];
            while (current != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(current.key).append("=").append(current.value);
                first = false;
                current = current.next;
            }
        }
        
        sb.append("}");
        return sb.toString();
    }
}
