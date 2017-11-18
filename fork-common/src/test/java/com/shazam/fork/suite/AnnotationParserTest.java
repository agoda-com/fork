package com.shazam.fork.suite;

import org.junit.Test;

import java.util.List;

import static com.shazam.fork.suite.AnnotationParser.parseAnnotation;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class AnnotationParserTest {

    private static final String first = "com.test.Ignore";
    private static final String firstResult = "Lcom/test/Ignore;";
    private static final String second = "com.test.Test";
    private static final String secondResult = "Lcom/test/Test;";

    @Test
    public void testEmptyString() throws Exception {
        assertThat(parseAnnotation().apply("").size(), is(0));
    }

    @Test
    public void testOneAnnotation() throws Exception {
        List<String> annotations = parseAnnotation().apply(first);
        assertThat(annotations.size(), is(1));
        assertThat(annotations.get(0), equalTo(firstResult));
    }

    @Test
    public void testTwoAnnotations() throws Exception {
        String combined = first + "," + second;
        List<String> annotations = parseAnnotation().apply(combined);
        assertThat(annotations.size(), is(2));
        assertThat(annotations.get(0), equalTo(firstResult));
        assertThat(annotations.get(1), equalTo(secondResult));
    }
}
