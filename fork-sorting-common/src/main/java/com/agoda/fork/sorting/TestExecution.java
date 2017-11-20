package com.agoda.fork.sorting;

public class TestExecution {
    private final int duration;
    private final int retryCount;

    public TestExecution(int duration, int retryCount) {
        this.duration = duration;
        this.retryCount = retryCount;
    }

    public int getDuration() {
        return duration;
    }

    public int getRetryCount() {
        return retryCount;
    }
}