package com.dcit308.wasteops.structures;

/**
 * Map built on top of CustomHashTable (or another permitted custom
 * structure). Implements MapLookupADT for consistency with
 * CustomHashTable.
 *
 * Owned by Issue #9.
 */
public class CustomMap<K, V> implements MapLookupADT<K, V> {

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
