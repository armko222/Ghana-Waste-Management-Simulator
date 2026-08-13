package com.dcit308.wasteops.structures;

/**
 * Contract for a key-based lookup structure.
 *
 * OWNER (implements this): Issue #9 — CustomHashTable.
 * CONSUMERS (code against this): Issue #13 (needs to look up a Resource
 * by ID when assigning a dispatch), Issue #14 (needs to look up records
 * by ID while assembling reports).
 *
 * Same collaboration pattern as the other *ADT interfaces — a consumer
 * can write and test real code today using a simple stand-in (a small,
 * deliberately slow implementation of this interface — see
 * structures/testutil/NaivePriorityQueue.java for what that looks like
 * for a different structure), instead of waiting for Issue #9's real
 * hash table. See Team_Handbook.docx, "Working With Each Other's Code."
 */
public interface MapLookupADT<K, V> {

    void put(K key, V value);

    /** Returns the value for key, or null if not present. */
    V get(K key);

    /** Removes the entry for key, if present. */
    void remove(K key);

    boolean containsKey(K key);

    int size();

    boolean isEmpty();
}
