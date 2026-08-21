package com.dcit308.wasteops.util;

/**
 * Lightweight wall-clock timer for performance experiments.
 * Wraps {@link System#nanoTime()} so every experiment measures consistently.
 *
 * <p>Usage:
 * <pre>
 *   Timer t = new Timer();
 *   t.start();
 *   // ... work ...
 *   long ns = t.stop();
 * </pre>
 *
 * Owned by Issue #14.
 */
public class Timer {

    private long startNanos;

    /** Records the current time as the start of a measurement. */
    public void start() {
        startNanos = System.nanoTime();
    }

    /**
     * Returns elapsed nanoseconds since {@link #start()} was called.
     *
     * @return elapsed time in nanoseconds
     * @throws IllegalStateException if {@link #start()} was never called
     */
    public long stop() {
        if (startNanos == 0) {
            throw new IllegalStateException("Timer.stop() called before start()");
        }
        return System.nanoTime() - startNanos;
    }
}
