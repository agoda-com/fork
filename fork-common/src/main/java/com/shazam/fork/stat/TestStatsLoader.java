package com.shazam.fork.stat;

import com.agoda.fork.stat.TestHistory;
import com.agoda.fork.stat.TestMetric;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.shazam.fork.model.TestCaseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class TestStatsLoader {

    StatServiceLoader loader;

    public TestStatsLoader(StatServiceLoader loader) {
        this.loader = loader;
    }

    private final List<TestHistory> testHistories = new ArrayList<>();
    private final Map<String, TestMetric> testMetrics = new HashMap<>();

    public void load() {
        testHistories.addAll(loader.load());
        testMetrics.putAll(testHistories
                .stream()
                .collect(toMap(this::calculateTestKey, TestHistory::getTestMetric)));
    }

    public TestMetric findMetric(String className, String methodName) {
        return testMetrics.getOrDefault(calculateTestKey(className, methodName), TestMetric.empty());
    }

    public TestMetric findMetric(TestIdentifier testIdentifier) {
        return testMetrics.getOrDefault(calculateTestKey(testIdentifier), TestMetric.empty());
    }

    private String calculateTestKey(TestIdentifier testIdentifier) {
        return calculateTestKey(testIdentifier.getClassName(),testIdentifier.getTestName());
    }

    private String calculateTestKey(TestHistory history) {
        return calculateTestKey(history.getTestClass(), history.getTestMethod());
    }

    private String calculateTestKey(TestCaseEvent event) {
        return calculateTestKey(event.getTestClass(), event.getTestMethod());
    }

    private String calculateTestKey(String testClass, String testMethod) {
        return testClass + "#" + testMethod;
    }
}
