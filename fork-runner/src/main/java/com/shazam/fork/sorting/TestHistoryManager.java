package com.shazam.fork.sorting;

import com.agoda.fork.sorting.TestHistory;
import com.android.ddmlib.testrunner.TestIdentifier;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TestHistoryManager {
    private final ConcurrentHashMap<TestIdentifier, TestHistory> map = new ConcurrentHashMap<>();

    public void addTestHistories(List<TestHistory> testHistoryList) {
        for (TestHistory history : testHistoryList) {
            addTestHistory(history);
        }
    }

    public void addTestHistory(TestHistory testHistory) {
        TestIdentifier testIdentifier = new TestIdentifier(testHistory.getTestClass(), testHistory.getTestMethod());
        map.put(testIdentifier, testHistory);
    }

    public TestHistory getTestHistory(TestIdentifier testIdentifier) {
        return map.get(testIdentifier);
    }
}
