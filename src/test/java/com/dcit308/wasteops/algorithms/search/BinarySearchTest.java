package com.dcit308.wasteops.algorithms.search;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BinarySearchTest {

    @Test
    public void testEmptyArray() {
        BinarySearch<String> searcher = new BinarySearch<>();
        String[] empty = {};
        assertEquals(-1, searcher.search(empty, "Target"));
    }

    @Test
    public void testFoundAndNotFound() {
        BinarySearch<String> searcher = new BinarySearch<>();
        String[] locations = {"Agbogbloshie", "Cantonments", "Makola", "Osu", "Ridge"};
        assertEquals(2, searcher.search(locations, "Makola"));
        assertEquals(-1, searcher.search(locations, "Madina"));
    }

    @Test
    public void testUnsortedCounterexample() {
        BinarySearch<String> searcher = new BinarySearch<>();
        String[] unsorted = {"Ridge", "Agbogbloshie", "Osu"};
        assertThrows(IllegalStateException.class, () -> {
            searcher.search(unsorted, "Osu");
        });
    }
}