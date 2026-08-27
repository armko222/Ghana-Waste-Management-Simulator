package com.dcit308.wasteops.scheduling;

import com.dcit308.wasteops.domain.ServiceRequest;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UrgencyDispatcherTest {

    private ServiceRequest req(String id, int urgency, String time) {
        LocalDateTime t = LocalDateTime.parse(time);
        return new ServiceRequest(id, "L001", "L002", "General", urgency,
                ServiceRequest.Priority.MEDIUM, t, t.plusHours(2));
    }

    @Test
    void emptyListReturnsNull() {
        assertNull(new UrgencyDispatcher().getNextRequest(List.of()));
    }

    @Test
    void singleRequestIsReturned() {
        ServiceRequest only = req("Q1", 3, "2026-07-01T08:00:00");
        assertEquals(only, new UrgencyDispatcher().getNextRequest(List.of(only)));
    }

    @Test
    void picksHighestUrgency() {
        ServiceRequest low = req("Q1", 2, "2026-07-01T08:00:00");
        ServiceRequest high = req("Q2", 5, "2026-07-01T08:05:00");
        assertEquals(high, new UrgencyDispatcher().getNextRequest(List.of(low, high)));
    }

    @Test
    void tiesBreakByEarlierSubmission() {
        ServiceRequest earlier = req("Q1", 4, "2026-07-01T07:30:00");
        ServiceRequest later = req("Q2", 4, "2026-07-01T08:00:00");
        assertEquals(earlier, new UrgencyDispatcher().getNextRequest(List.of(later, earlier)),
                "same urgency -- earlier time_submitted must win");
    }
}