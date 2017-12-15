package com.shazam.fork.summary.executiontimeline;

import java.util.List;

public class ExecutionResult {
    private final int passedTests;
    private final int failedTests;
    private final ExecutionStats executionStats;
    private final List<Measure> measures;

    public ExecutionResult(int passedTests, int failedTests, ExecutionStats executionStats, List<Measure> measures) {
        this.passedTests = passedTests;
        this.failedTests = failedTests;
        this.executionStats = executionStats;
        this.measures = measures;
    }

    public int getPassedTests() {
        return passedTests;
    }

    public int getFailedTests() {
        return failedTests;
    }

    public ExecutionStats getExecutionStats() {
        return executionStats;
    }

    public List<Measure> getMeasures() {
        return measures;
    }
}
