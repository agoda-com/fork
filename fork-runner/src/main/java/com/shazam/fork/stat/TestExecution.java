package com.shazam.fork.stat;

import com.android.ddmlib.testrunner.TestIdentifier;
import org.jetbrains.annotations.NotNull;

public class TestExecution {

    public enum Status {
        FAILED,
        PASSED,
        IGNORED
    }

    private final TestIdentifier test;
    private final long startTime;
    private final long endTime;
    private final Status status;

    public TestExecution(TestIdentifier test, long startTime, long endTime, Status status) {
        this.test = test;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
    }

    public TestIdentifier getTest() {
        return test;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public Status getStatus() {
        return status;
    }
}
