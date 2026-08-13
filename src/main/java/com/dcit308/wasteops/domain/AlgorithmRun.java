package com.dcit308.wasteops.domain;

/**
 * A single recorded run of a timed performance experiment. Mirrors the
 * `algorithm_runs` table. Populated by Issue #14's ExperimentService,
 * never hand-entered.
 *
 * Owned by Issue #14.
 */
public class AlgorithmRun {

    private final String runId;
    private final String algorithmName;
    private final int inputSize;
    private final long timeNanos;
    private final Integer memoryKb; // nullable -- optional per brief
    private final String dateRun; // ISO 8601

    public AlgorithmRun(String runId, String algorithmName, int inputSize,
                         long timeNanos, Integer memoryKb, String dateRun) {
        this.runId = runId;
        this.algorithmName = algorithmName;
        this.inputSize = inputSize;
        this.timeNanos = timeNanos;
        this.memoryKb = memoryKb;
        this.dateRun = dateRun;
    }

    public String getRunId() { return runId; }
    public String getAlgorithmName() { return algorithmName; }
    public int getInputSize() { return inputSize; }
    public long getTimeNanos() { return timeNanos; }
    public Integer getMemoryKb() { return memoryKb; }
    public String getDateRun() { return dateRun; }

    @Override
    public String toString() {
        return "AlgorithmRun{" + algorithmName + ", n=" + inputSize + ", " + timeNanos + "ns}";
    }
}
