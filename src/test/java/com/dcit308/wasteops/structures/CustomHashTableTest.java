package com.dcit308.wasteops.structures;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomHashTableTest {

    @Test
    void emptyTableShouldBehaveCorrectly() {
        CustomHashTable<String, Integer> table = new CustomHashTable<>();

        assertTrue(table.isEmpty());
        assertEquals(0, table.size());
        assertFalse(table.containsKey("missing"));
        assertNull(table.get("missing"));
    }

    @Test
    void singleEntryShouldSupportPutGetAndContains() {
        CustomHashTable<String, Integer> table = new CustomHashTable<>();

        table.put("A", 10);

        assertEquals(1, table.size());
        assertFalse(table.isEmpty());
        assertTrue(table.containsKey("A"));
        assertEquals(10, table.get("A"));
    }

    @Test
    void updatingExistingKeyShouldReplaceValue() {
        CustomHashTable<String, Integer> table = new CustomHashTable<>();

        table.put("A", 10);
        table.put("A", 25);

        assertEquals(1, table.size());
        assertEquals(25, table.get("A"));
    }

    @Test
    void removingExistingKeyShouldRemoveIt() {
        CustomHashTable<String, Integer> table = new CustomHashTable<>();

        table.put("A", 10);
        table.remove("A");

        assertEquals(0, table.size());
        assertFalse(table.containsKey("A"));
        assertNull(table.get("A"));
        assertTrue(table.isEmpty());
    }

    @Test
    void removingNonExistentKeyShouldNotChangeTable() {
        CustomHashTable<String, Integer> table = new CustomHashTable<>();

        table.put("A", 10);
        table.remove("missing");

        assertEquals(1, table.size());
        assertEquals(10, table.get("A"));
    }

    @Test
    void forcedCollisionShouldBeHandled() {
        CustomHashTable<CollisionKey, String> table =
                new CustomHashTable<>(4);

        CollisionKey first = new CollisionKey("first");
        CollisionKey second = new CollisionKey("second");
        CollisionKey third = new CollisionKey("third");

        table.put(first, "one");
        table.put(second, "two");
        table.put(third, "three");

        assertEquals("one", table.get(first));
        assertEquals("two", table.get(second));
        assertEquals("three", table.get(third));

        assertTrue(table.collisionCount() > 0);
        assertTrue(table.probeCount() > 0);
    }

    @Test
    void collisionStatisticsShouldReset() {
        CustomHashTable<CollisionKey, Integer> table =
                new CustomHashTable<>(4);

        table.put(new CollisionKey("A"), 1);
        table.put(new CollisionKey("B"), 2);

        assertTrue(table.collisionCount() > 0);

        table.resetCollisionStatistics();

        assertEquals(0, table.collisionCount());
        assertEquals(0, table.probeCount());
    }

    @Test
    void setShouldSupportBasicOperations() {
        CustomSet<String> set = new CustomSet<>();

        assertTrue(set.isEmpty());

        set.add("Ghana");
        set.add("Ghana");
        set.add("Accra");

        assertEquals(2, set.size());
        assertTrue(set.contains("Ghana"));
        assertTrue(set.contains("Accra"));

        set.remove("Ghana");

        assertFalse(set.contains("Ghana"));
        assertEquals(1, set.size());
    }

    @Test
    void mapShouldSupportBasicOperations() {
        CustomMap<String, Integer> map = new CustomMap<>();

        assertTrue(map.isEmpty());

        map.put("Accra", 100);
        map.put("Kumasi", 200);

        assertEquals(2, map.size());
        assertTrue(map.containsKey("Accra"));
        assertEquals(100, map.get("Accra"));

        map.remove("Accra");

        assertFalse(map.containsKey("Accra"));
        assertNull(map.get("Accra"));
        assertEquals(1, map.size());
    }

    private static final class CollisionKey {

        private final String value;

        private CollisionKey(String value) {
            this.value = value;
        }

        @Override
        public int hashCode() {
            return 42;
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof CollisionKey)) {
                return false;
            }

            CollisionKey otherKey = (CollisionKey) other;
            return value.equals(otherKey.value);
        }
    }
}