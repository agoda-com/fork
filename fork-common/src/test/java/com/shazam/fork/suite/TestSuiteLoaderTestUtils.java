package com.shazam.fork.suite;

import com.shazam.fork.model.TestCaseEvent;
import com.shazam.fork.model.TestCaseEventFactory;
import com.shazam.fork.stat.StatServiceLoader;
import com.shazam.fork.stat.TestStatsLoader;
import org.hamcrest.Matcher;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;

import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

public class TestSuiteLoaderTestUtils {

    private static final TestCaseEventFactory factory = new TestCaseEventFactory(new TestStatsLoader(new StatServiceLoader("")));

    @Nonnull
    static Matcher<TestCaseEvent> sameTestEventAs(String testMethod, String testClass, Map<String, String> properties) {
        return sameBeanAs(factory.newTestCase(testMethod, testClass, false, emptyList(), properties));
    }

    @Nonnull
    static Matcher<TestCaseEvent> sameTestEventAs(String testMethod, String testClass, boolean isIgnored) {
        return sameBeanAs(factory.newTestCase(testMethod, testClass, isIgnored, emptyList(), emptyMap()));
    }

    @Nonnull
    static Matcher<TestCaseEvent> sameTestEventAs(String testMethod, String testClass, boolean isIgnored, List<String> permissions) {
        return sameBeanAs(factory.newTestCase(testMethod, testClass, isIgnored, permissions, emptyMap()));
    }
}
