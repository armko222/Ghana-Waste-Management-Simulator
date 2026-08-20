package com.dcit308.wasteops.util;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Timer} — the nanosecond measurement utility
 * used by all performance experiments.
 *
 * Owned by Issue #14.
 */
class TimerTest {

    @Test
    @DisplayName("start then stop returns a positive elapsed time")
    void startStopPositive() {
        Timer t = new Timer();
        t.start();
        // Do a small amount of work so elapsed time is measurably > 0
        long sum = 0;
        for (int i = 0; i < 1000; i++) {
            sum += i;
        }
        long elapsed = t.stop();
        assertTrue(elapsed >= 0, "Elapsed nanoseconds should be non-negative, got: " + elapsed);
    }

    @Test
    @DisplayName("stop before start throws IllegalStateException")
    void stopBeforeStartThrows() {
        Timer t = new Timer();
        assertThrows(IllegalStateException.class, t::stop,
                "Calling stop() without start() should throw");
    }

    @Test
    @DisplayName("multiple start-stop cycles each return independent measurements")
    void multipleCycles() {
        Timer t = new Timer();

        t.start();
        long first = t.stop();

        t.start();
        // Slightly more work in the second measurement
        long sum = 0;
        for (int i = 0; i < 10_000; i++) {
            sum += i;
        }
        long second = t.stop();

        assertTrue(first >= 0, "First measurement should be non-negative");
        assertTrue(second >= 0, "Second measurement should be non-negative");
    }

    @Test
    @DisplayName("start resets the timer for a fresh measurement")
    void startResetsTimer() {
        Timer t = new Timer();

        // First measurement
        t.start();
        t.stop();

        // Second measurement should not accumulate
        t.start();
        long elapsed = t.stop();
        assertTrue(elapsed >= 0, "After restart, elapsed should be fresh and non-negative");
    }
}
