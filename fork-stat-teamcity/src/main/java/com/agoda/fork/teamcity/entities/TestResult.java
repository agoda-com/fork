package com.agoda.fork.teamcity.entities;

import java.util.List;

public class TestResult {
    private final int count;
    private final String href;
    private final List<Test> testOccurrence;

    public TestResult(int count, String href, List<Test> testOccurrence) {
        this.count = count;
        this.href = href;
        this.testOccurrence = testOccurrence;
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
}