package com.dcit308.wasteops.structures;

import org.junit.jupiter.api.Test;
import java.util.NoSuchElementException;
import static org.junit.jupiter.api.Assertions.*;

public class ArrayDequeTest {

    @Test
    public void testEmptyDeque() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        assertTrue(deque.isEmpty());
        assertThrows(NoSuchElementException.class, deque::removeFront);
    }

    @Test
    public void testSingleElement() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addFront("REQ-101");
        assertFalse(deque.isEmpty());
        assertEquals("REQ-101", deque.removeRear());
        assertTrue(deque.isEmpty());
    }

    @Test
    public void testWrapAroundAndUrgency() {
        ArrayDeque<String> deque = new ArrayDeque<>();
        deque.addRear("REQ-102");
        deque.addRear("REQ-103");
        // Urgent insertion simulating a hazardous waste spill bypassing FIFO
        deque.addFront("REQ-999 (URGENT)"); 
        
        assertEquals("REQ-999 (URGENT)", deque.removeFront());
        assertEquals("REQ-102", deque.removeFront());
    }
}