package com.shazam.fork.model;

import com.android.ddmlib.testrunner.TestIdentifier;
import com.shazam.fork.stat.TestStatsLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestCaseEventFactory {

    private final TestStatsLoader statsLoader;

    public TestCaseEventFactory(TestStatsLoader statsLoader) {
        this.statsLoader = statsLoader;
    }

    public TestCaseEvent newTestCase(@Nonnull TestIdentifier testIdentifier) {
        return new TestCaseEvent(testIdentifier.getTestName(),
                testIdentifier.getClassName(),
                false,
                Collections.emptyList(),
                Collections.emptyMap(),
                statsLoader.findMetric(testIdentifier.getClassName(), testIdentifier.getClassName()));
    }

    public TestCaseEvent newTestCase(@Nullable String testMethod, @Nonnull String testClass, boolean isIgnored, List<String> permissionsToRevoke, Map<String, String> properties) {
        return new TestCaseEvent(testMethod, testClass, isIgnored, permissionsToRevoke, properties, statsLoader.findMetric(testClass, testMethod));
    }
}