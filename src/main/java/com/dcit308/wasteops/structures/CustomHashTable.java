package com.dcit308.wasteops.structures;

/**
 * Hash table with a documented collision strategy (chaining -- possibly
 * via Issue #2's DoublyLinkedList -- or open addressing; your choice,
 * but document it). Implements MapLookupADT so Issue #13 and Issue #14
 * can look things up by ID without depending on this class directly.
 *
 * Owned by Issue #9.
 */
public class CustomHashTable<K, V> implements MapLookupADT<K, V> {

    @Override
    public void put(K key, V value) {
        throw new UnsupportedOperationException("TODO: Issue #9 \u2014 implement put.");
    }

    @Override
    public V get(K key) {
        throw new UnsupportedOperationException("TODO: Issue #9 \u2014 implement get.");
    }

    @Override
    public void remove(K key) {
        throw new UnsupportedOperationException("TODO: Issue #9 \u2014 implement remove.");
    }

    @Override
    public boolean containsKey(K key) {
        throw new UnsupportedOperationException("TODO: Issue #9 \u2014 implement containsKey.");
    }

    @Override
    public int size() {
        throw new UnsupportedOperationException("TODO: Issue #9 \u2014 implement size.");
    }

    @Override
    public boolean isEmpty() {
        throw new UnsupportedOperationException("TODO: Issue #9 \u2014 implement isEmpty.");
    }
}
