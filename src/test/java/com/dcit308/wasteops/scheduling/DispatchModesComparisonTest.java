package com.dcit308.wasteops.scheduling;

import com.dcit308.wasteops.domain.ServiceRequest;
import com.dcit308.wasteops.structures.PriorityQueueADT;
import com.dcit308.wasteops.structures.QueueADT;
import com.dcit308.wasteops.structures.testutil.NaivePriorityQueue;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Confirms FIFO, Urgency, and Priority dispatch produce genuinely
 * different results on the same input -- required by Issue #13's test
 * list. Uses the same four-request fixture across all three modes so
 * the comparison is fair.
 */
class DispatchModesComparisonTest {

    /** Test-only QueueADT stand-in -- see FifoDispatcherTest for the same pattern. */
    static class ListBackedQueue<T> implements QueueADT<T> {
        private final List<T> items = new ArrayList<>();
        public void enqueue(T value) { items.add(value); }
        public T dequeue() {
            if (items.isEmpty()) throw new NoSuchElementException("queue is empty");
            return items.remove(0);
        }
        public T peekFront() { return items.get(0); }
        public boolean isEmpty() { return items.isEmpty(); }
        public boolean isFull() { return false; }
        public int size() { return items.size(); }
    }

    private ServiceRequest req(String id, String time, int urgency, ServiceRequest.Priority priority) {
        LocalDateTime t = LocalDateTime.parse(time);
        return new ServiceRequest(id, "L001", "L002", "General", urgency, priority, t, t.plusHours(2));
    }

    @Test
    void allThreeModesPickADifferentRequestFirst() {
        ServiceRequest r1 = req("R1", "2026-07-01T08:00:00", 2, ServiceRequest.Priority.LOW);
        ServiceRequest r2 = req("R2", "2026-07-01T07:30:00", 4, ServiceRequest.Priority.HIGH);
        ServiceRequest r3 = req("R3", "2026-07-01T07:45:00", 5, ServiceRequest.Priority.MEDIUM);
        ServiceRequest r4 = req("R4", "2026-07-01T07:15:00", 1, ServiceRequest.Priority.LOW);
        List<ServiceRequest> all = List.of(r1, r2, r3, r4);

        // FIFO: earliest time_submitted wins -> R4 (07:15)
        FifoDispatcher fifo = new FifoDispatcher(new ListBackedQueue<>());
        List<ServiceRequest> byTime = new ArrayList<>(all);
        byTime.sort((a, b) -> a.getTimeSubmitted().compareTo(b.getTimeSubmitted()));
        fifo.loadPending(byTime);
        ServiceRequest fifoFirst = fifo.getNextRequest();

        // Urgency: highest urgency wins -> R3 (urgency 5)
        UrgencyDispatcher urgency = new UrgencyDispatcher();
        ServiceRequest urgencyFirst = urgency.getNextRequest(all);

        // Priority: HIGH tier wins regardless of urgency -> R2
        PriorityQueueADT<ServiceRequest> pq = new NaivePriorityQueue<>();
        PriorityDispatcher priority = new PriorityDispatcher(pq, LocalDateTime.parse("2026-07-01T07:00:00"));
        priority.loadPending(all);
        ServiceRequest priorityFirst = priority.getNextRequest();

        assertEquals(r4, fifoFirst, "FIFO must pick the earliest submitted request");
        assertEquals(r3, urgencyFirst, "Urgency must pick the highest urgency score");
        assertEquals(r2, priorityFirst, "Priority must pick the HIGH tier request regardless of urgency");

        assertNotEquals(fifoFirst, urgencyFirst);
        assertNotEquals(urgencyFirst, priorityFirst);
        assertNotEquals(fifoFirst, priorityFirst);
    }
}