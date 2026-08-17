package com.dcit308.wasteops.structures;

import com.dcit308.wasteops.domain.AuditEvent;
import com.dcit308.wasteops.structures.ArrayStack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ArrayStackTest{

    private ArrayStack<AuditEvent> stack;

    @BeforeEach
    void setUp() {
        stack = new ArrayStack<>(3); // Initializing with a small capacity of 3
    }

    @Test
    @DisplayName("Should push items onto stack and retain LIFO order")
    void testPushAndPop() {
        AuditEvent event1 = new AuditEvent("E1", AuditEvent.EventType.IMPORT, null, "Imported file", LocalDateTime.now());
        AuditEvent event2 = new AuditEvent("E2", AuditEvent.EventType.DISPATCH, "REQ-101", "Dispatched truck", LocalDateTime.now());

        stack.push(event1);
        stack.push(event2);

        assertEquals(2, stack.getSize());
        assertEquals(event2, stack.peek(), "Peek should return the most recent item");
        assertEquals(event2, stack.pop(), "Pop should return items in Last-In, First-Out order");
        assertEquals(event1, stack.pop());
        assertTrue(stack.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when pushing to a full stack (Stack Overflow)")
    void testStackOverflow() {
        stack.push(new AuditEvent("E1", AuditEvent.EventType.IMPORT, null, "Desc 1", LocalDateTime.now()));
        stack.push(new AuditEvent("E2", AuditEvent.EventType.STATUS_CHANGE, "R1", "Desc 2", LocalDateTime.now()));
        stack.push(new AuditEvent("E3", AuditEvent.EventType.DISPATCH, "R2", "Desc 3", LocalDateTime.now()));

        assertTrue(stack.isFull());

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> stack.push(new AuditEvent("E4", AuditEvent.EventType.DISPATCH, "R3", "Overflow item", LocalDateTime.now()))
        );

        assertEquals("Stack overflow", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when popping or peeking an empty stack (Stack Underflow)")
    void testStackUnderflow() {
        assertTrue(stack.isEmpty());

        assertThrows(IllegalStateException.class, () -> stack.pop());
        assertThrows(IllegalStateException.class, () -> stack.peek());
    }
}