
package com.shazam.fork.suite;

import com.shazam.fork.io.DexFileExtractor;
import com.shazam.fork.model.TestCaseEvent;
import com.shazam.fork.stat.TestStatsLoader;
import org.hamcrest.Matcher;
import org.jf.dexlib.DexFile;
import org.junit.Before;
import org.junit.Test;

import javax.annotation.Nonnull;
import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shazam.fork.io.FakeDexFileExtractor.fakeDexFileExtractor;
import static com.shazam.fork.io.Files.convertFileToDexFile;
import static com.shazam.fork.model.TestCaseEvent.newTestCase;
import static com.shazam.fork.suite.FakeTestClassMatcher.fakeTestClassMatcher;
import static com.shazam.fork.suite.TestSuiteLoaderTestUtils.sameTestEventAs;
import static com.shazam.shazamcrest.MatcherAssert.assertThat;
import static com.shazam.shazamcrest.matcher.Matchers.sameBeanAs;
import static java.util.Arrays.asList;
import static java.util.Collections.*;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;

public class TestSuiteLoaderExcludeTest {
    private static final File ANY_INSTRUMENTATION_APK_FILE = null;

    private final DexFileExtractor fakeDexFileExtractor = fakeDexFileExtractor().thatReturns(testDexFile());
    private final TestClassMatcher fakeTestClassMatcher = fakeTestClassMatcher().thatAlwaysMatches();
    private TestSuiteLoader testSuiteLoader;

    private DexFile testDexFile() {
        URL testDexResourceUrl = this.getClass().getResource("/tests.dex");
        String testDexFile = testDexResourceUrl.getFile();
        File file = new File(testDexFile);
        return convertFileToDexFile().apply(file);
    }

    @Before
    public void setUp() throws Exception {
        testSuiteLoader = new TestSuiteLoader(ANY_INSTRUMENTATION_APK_FILE, fakeDexFileExtractor,
                fakeTestClassMatcher, "", "com.shazam.annotations.CustomTestAnnotation1",new TestStatsLoader());
    }

    @Test
    public void testExcludeAnnotation() throws Exception {
        Collection<TestCaseEvent> events = testSuiteLoader.loadTestSuite();
        assertThat(events, not(hasItem(sameTestEventAs("methodOfAnCustomTestAnnotation1TestClass", "com.shazam.forktest.CustomTestAnnotation1ClassTest", false))));
        assertThat(events, hasItem(sameTestEventAs("methodOfAnCustomTestAnnotation2TestClass", "com.shazam.forktest.CustomTestAnnotation2ClassTest", false)));
    }
}
