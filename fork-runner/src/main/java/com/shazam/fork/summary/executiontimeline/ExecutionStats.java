package com.shazam.fork.summary.executiontimeline;

public class ExecutionStats {
    private long idleTimeMillis;
    private long averageTestExecutionTimeMillis;

    public ExecutionStats(long idleTimeMillis, long averageTestExecutionTimeMillis) {
        this.idleTimeMillis = idleTimeMillis;
        this.averageTestExecutionTimeMillis = averageTestExecutionTimeMillis;
    }

    public long getIdleTimeMillis() {
        return idleTimeMillis;
    }

    public long getAverageTestExecutionTimeMillis() {
        return averageTestExecutionTimeMillis;
    }
}
