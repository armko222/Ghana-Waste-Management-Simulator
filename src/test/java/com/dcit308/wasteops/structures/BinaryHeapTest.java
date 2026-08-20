package com.dcit308.wasteops.structures;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

public class BinaryHeapTest {

    @Test
    public void testEmptyHeap() {
        BinaryHeap<String> heap = new BinaryHeap<>();
        assertTrue(heap.isEmpty());
        assertEquals(0, heap.size());
        assertThrows(NoSuchElementException.class, heap::extractMin);
    }

    @Test
    public void testSingleElement() {
        BinaryHeap<String> heap = new BinaryHeap<>();
        heap.insert(42, "Request A");
        assertFalse(heap.isEmpty());
        assertEquals(1, heap.size());
        assertEquals("Request A", heap.extractMin());
    }

    @Test
    public void testHeapProperty() {
        BinaryHeap<String> heap = new BinaryHeap<>();
        // Format: insert(Priority Integer, Value String)
        heap.insert(8, "Req8");
        heap.insert(1, "Req1");
        heap.insert(10, "Req10");
        heap.insert(3, "Req3");
        heap.insert(5, "Req5");
        
        // Verifies elements are extracted in priority order (lowest number first)
        assertEquals("Req1", heap.extractMin());
        assertEquals("Req3", heap.extractMin());
        assertEquals("Req5", heap.extractMin());
        assertEquals("Req8", heap.extractMin());
        assertEquals("Req10", heap.extractMin());
    }
}