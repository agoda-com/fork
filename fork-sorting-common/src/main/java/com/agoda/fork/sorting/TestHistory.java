package com.agoda.fork.sorting;

import java.util.List;

public class TestHistory {
    private final String testClass;
    private final String testMethod;
    private final List<TestExecution> executions;
    private final TestMetric testMetric;

    public TestHistory(String testClass, String testMethod,
                       List<TestExecution> executions, TestMetric testMetric) {
        this.testClass = testClass;
        this.testMethod = testMethod;
        this.executions = executions;
        this.testMetric = testMetric;
    }

    public String getTestClass() {
        return testClass;
    }

    public String getTestMethod() {
        return testMethod;
    }

    public List<TestExecution> getExecutions() {
        return executions;
    }

    public TestMetric getTestMetric() {
        return testMetric;
    }
}