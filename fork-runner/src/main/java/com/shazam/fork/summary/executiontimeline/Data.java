package com.shazam.fork.summary.executiontimeline;

import java.util.Date;

public class Data {
    private final String testName;
    private final int status;
    private final Date startDate;
    private final Date endDate;
    private final double expectedValue;
    private final double variance;

    public Data(String testName, int status, Date startDate, Date endDate, double expectedValue, double variance) {
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

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public double getExpectedValue() {
        return expectedValue;
    }

    public double getVariance() {
        return variance;
    }
}
