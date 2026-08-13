package com.dcit308.wasteops.util;

/**
 * Timing utility for the performance harness (Issue #14's
 * ExperimentService). Wraps System.nanoTime() consistently so every
 * experiment measures the same way.
 *
 * Owned by Issue #14.
 */
public class Timer {

    public void start() {
        throw new UnsupportedOperationException("TODO: Issue #14 \u2014 implement start.");
    }

    /** Returns elapsed nanoseconds since start() was called. */
    public long stop() {
        throw new UnsupportedOperationException("TODO: Issue #14 \u2014 implement stop.");
    }
}
