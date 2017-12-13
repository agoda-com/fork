package com.shazam.fork.stat;

import com.agoda.fork.stat.TestHistory;
import com.agoda.fork.stat.TestMetric;
import com.shazam.fork.model.TestCaseEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class TestStatsLoader {

    StatServiceLoader loader;

    private final List<TestHistory> testHistories = new ArrayList<>();
    private final Map<String, TestMetric> testMetris = new HashMap<>();

    public void load() {
        testHistories.addAll(loader.load());
        testMetris.putAll(testHistories
                .stream()
                .collect(toMap(this::calculateTestKey, TestHistory::getTestMetric)));
    }

    public TestMetric findMetric(String className, String methodName) {
        return testMetris.getOrDefault(calculateTestKey(className, methodName), TestMetric.empty());
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
