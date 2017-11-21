package com.shazam.fork.stat;

import com.agoda.fork.sorting.TestHistory;
import com.android.ddmlib.testrunner.TestIdentifier;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class TestHistoryManager {
    private final ConcurrentHashMap<TestIdentifier, TestHistory> map = new ConcurrentHashMap<>();

    public void addTestHistories(Collection<TestHistory> testHistoryList) {
        for (TestHistory history : testHistoryList) {
            addTestHistory(history);
        }
    }

    public void addTestHistory(TestHistory testHistory) {
        TestIdentifier testIdentifier = new TestIdentifier(testHistory.getTestClass(), testHistory.getTestMethod());
        map.put(testIdentifier, testHistory);
    }

    public Optional<TestHistory> getTestHistory(TestIdentifier testIdentifier) {
        return Optional.ofNullable(map.get(testIdentifier));
    }
}
