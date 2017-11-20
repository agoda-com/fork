package com.agoda.fork.teamcity.entities;

import java.util.List;

public class TestResult {
    private final int count;
    private final String href;
    private final List<Test> testOccurrence;
    private final List<String> retries;

    public TestResult(int count, String href, List<Test> testOccurrence, List<String> retries) {
        this.count = count;
        this.href = href;
        this.testOccurrence = testOccurrence;
        this.retries = retries;
    }

    public int getCount() {
        return count;
    }

    public String getHref() {
        return href;
    }

    public List<Test> getTestOccurrence() {
        return testOccurrence;
    }

    public List<String> getRetries() {
        return retries;
    }
}