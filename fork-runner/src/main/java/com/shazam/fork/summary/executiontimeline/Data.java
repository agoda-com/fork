package com.shazam.fork.summary.executiontimeline;

public class Data {
    private final String testName;
    private final int status;
    private final long startDate;
    private final long endDate;
    private final double expectedValue;
    private final double variance;

    public Data(String testName, int status, long startDate, long endDate, double expectedValue, double variance) {
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

    public int getStatus() {
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
