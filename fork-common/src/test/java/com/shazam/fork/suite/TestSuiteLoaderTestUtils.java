package com.shazam.fork.suite;

import com.agoda.fork.stat.TestMetric;
import com.shazam.fork.model.TestCaseEvent;
import org.hamcrest.Matcher;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static com.shazam.fork.model.TestCaseEvent.newTestCase;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class TestSuiteLoaderTestUtils {
    @Nonnull
    static Matcher<TestCaseEvent> sameTestEventAs(String testMethod, String testClass, Map<String, String> properties) {
        return sameBeanAs(newTestCase(testMethod, testClass, false, emptyList(), properties, TestMetric.empty()));
    }

    @Nonnull
    static Matcher<TestCaseEvent> sameTestEventAs(String testMethod, String testClass, boolean isIgnored) {
        return sameBeanAs(newTestCase(testMethod, testClass, isIgnored, emptyList(), emptyMap(), TestMetric.empty()));
    }

    @Nonnull
    static Matcher<TestCaseEvent> sameTestEventAs(String testMethod, String testClass, boolean isIgnored, List<String> permissions) {
        return sameBeanAs(newTestCase(testMethod, testClass, isIgnored, permissions, emptyMap(), TestMetric.empty()));
    }
}
