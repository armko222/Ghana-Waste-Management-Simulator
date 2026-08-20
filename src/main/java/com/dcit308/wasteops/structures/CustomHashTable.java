package com.dcit308.wasteops.structures;

/**
 * Generic hash table using open addressing with linear probing.
 *
 * Collision strategy: linear probing. When two keys map to the same slot,
 * the table checks the following slots cyclically until it finds the key,
 * an empty slot, or a deleted slot. Deleted entries are represented by a
 * tombstone so searches are not broken by removals.
 *
 * The table grows before an insertion would push the load factor above
 * MAX_LOAD_FACTOR. Rehashing places live entries into a fresh table.
 *
 * Core operations are expected O(1) average time; a collision-heavy or
 * adversarial input can make an individual operation O(n).
 */
public class CustomHashTable<K, V> implements MapLookupADT<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double MAX_LOAD_FACTOR = 0.75;

    private Entry<K, V>[] table;
    private int size;
    private int usedSlots;
    private long collisionCount;
    private long probeCount;

    private static final class Entry<K, V> {
        private final K key;
        private V value;
        private boolean deleted;

        private Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    public CustomHashTable() {
        this(DEFAULT_CAPACITY);
    }

    @SuppressWarnings("unchecked")
    public CustomHashTable(int initialCapacity) {
        if (initialCapacity < 1) {
            throw new IllegalArgumentException("Initial capacity must be positive.");
        }
        table = (Entry<K, V>[]) new Entry[nextPowerOfTwo(initialCapacity)];
    }

    private static int nextPowerOfTwo(int value) {
        int capacity = 1;
        while (capacity < value && capacity < (1 << 30)) {
            capacity <<= 1;
        }
        return capacity;
    }

    private int indexFor(K key, int capacity) {
        return (key == null ? 0 : key.hashCode() & 0x7fffffff) % capacity;
    }

    private boolean keysEqual(K a, K b) {
        return a == b || (a != null && a.equals(b));
    }

    @Override
    public void put(K key, V value) {
        int existing = findIndex(key);
        if (existing >= 0) {
            table[existing].value = value;
            return;
        }

        if ((usedSlots + 1.0) / table.length > MAX_LOAD_FACTOR) {
            resize(table.length * 2);
        }

        insertNew(key, value);
    }

    private void insertNew(K key, V value) {
        int index = indexFor(key, table.length);
        int firstDeleted = -1;

        for (int probes = 0; probes < table.length; probes++) {
            Entry<K, V> entry = table[index];

            if (entry == null) {
                int target = firstDeleted >= 0 ? firstDeleted : index;
                if (firstDeleted >= 0) {
                    table[target] = new Entry<>(key, value);
                    table[target].deleted = false;
                } else {
                    table[index] = new Entry<>(key, value);
                    usedSlots++;
                }
                size++;
                return;
            }

            if (entry.deleted) {
                if (firstDeleted < 0) {
                    firstDeleted = index;
                }
            } else if (keysEqual(entry.key, key)) {
                entry.value = value;
                return;
            } else {
                collisionCount++;
                probeCount++;
            }

            index = (index + 1) % table.length;
        }

        resize(table.length * 2);
        insertNew(key, value);
    }

    @Override
    public V get(K key) {
        int index = findIndex(key);
        return index < 0 ? null : table[index].value;
    }

    @Override
    public void remove(K key) {
        int index = findIndex(key);
        if (index < 0) {
            return;
        }

        table[index].deleted = true;
        table[index].value = null;
        size--;

        // If the table becomes sparse, rebuild it to clean tombstones.
        if (size == 0) {
            clear();
        } else if (size < table.length / 4 && table.length > DEFAULT_CAPACITY) {
            resize(Math.max(DEFAULT_CAPACITY, table.length / 2));
        }
    }

    @Override
    public boolean containsKey(K key) {
        return findIndex(key) >= 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    /** Returns the current physical table capacity for performance evidence. */
    public int capacity() {
        return table.length;
    }

    /** Returns the current live-entry load factor. */
    public double loadFactor() {
        return (double) size / table.length;
    }

    /** Returns the number of occupied-slot collisions recorded since construction/reset. */
    public long collisionCount() {
        return collisionCount;
    }

    /** Returns the number of extra probe steps caused by collisions. */
    public long probeCount() {
        return probeCount;
    }

    /** Resets collision/probe counters without changing stored data. */
    public void resetCollisionStatistics() {
        collisionCount = 0;
        probeCount = 0;
    }

    private int findIndex(K key) {
        int index = indexFor(key, table.length);

        for (int probes = 0; probes < table.length; probes++) {
            Entry<K, V> entry = table[index];
            if (entry == null) {
                return -1;
            }
            if (!entry.deleted && keysEqual(entry.key, key)) {
                return index;
            }
            index = (index + 1) % table.length;
        }

        return -1;
    }

    @SuppressWarnings("unchecked")
    private void resize(int newCapacity) {
        Entry<K, V>[] old = table;
        table = (Entry<K, V>[]) new Entry[nextPowerOfTwo(newCapacity)];
        int oldSize = size;
        size = 0;
        usedSlots = 0;

        for (Entry<K, V> entry : old) {
            if (entry != null && !entry.deleted) {
                insertWithoutStatistics(entry.key, entry.value);
            }
        }

        if (size != oldSize) {
            throw new IllegalStateException("Hash table rehash lost an entry.");
        }
    }

    private void insertWithoutStatistics(K key, V value) {
        int index = indexFor(key, table.length);
        while (table[index] != null) {
            index = (index + 1) % table.length;
        }
        table[index] = new Entry<>(key, value);
        size++;
        usedSlots++;
    }

    @SuppressWarnings("unchecked")
    private void clear() {
        table = (Entry<K, V>[]) new Entry[DEFAULT_CAPACITY];
        size = 0;
        usedSlots = 0;
    }
}
