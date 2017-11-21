package com.shazam.fork.summary.executiontimeline;

import java.util.List;

public class ExecutionResult {
    private final int passedTests;
    private final int failedTests;
    private final List<Measure> measures;

    public ExecutionResult(int passedTests, int failedTests, List<Measure> measures) {
        this.passedTests = passedTests;
        this.failedTests = failedTests;
        this.measures = measures;
    }

    public int getFailedTests() {
        return failedTests;
    }

    public int getPassedTests() {
        return passedTests;
    }

    public List<Measure> getMeasures() {
        return measures;
    }
}
