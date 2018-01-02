package com.shazam.fork.summary.executiontimeline;

import com.shazam.fork.stat.TestExecution;

public class Data {
    private final String testName;
    private final TestExecution.Status status;
    private final long startDate;
    private final long endDate;
    private final double expectedValue;
    private final double variance;

    public Data(String testName, TestExecution.Status status, long startDate, long endDate, double expectedValue, double variance) {
        this.testName = testName;
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
        this.expectedValue = expectedValue;
        this.variance = variance;
    }

    public String getTestName() {
        return testName;
    }

    public TestExecution.Status getStatus() {
        return status;
    }

    public long getStartDate() {
        return startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public double getExpectedValue() {
        return expectedValue;
    }

    public double getVariance() {
        return variance;
    }
}
