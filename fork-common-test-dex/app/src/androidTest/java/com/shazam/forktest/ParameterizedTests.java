package com.shazam.forktest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.shazam.annotations.CustomParameterizedAnnotation;

import java.util.Arrays;
import java.util.Collection;
import static org.junit.Assert.assertEquals;

@CustomParameterizedAnnotation
@RunWith(Parameterized.class)
public class ParameterizedTests {

    String test;
    int number;
    String result;

    public ParameterizedTests(String test, int number, String result) {
        this.test = test;
        this.number = number;
        this.result = result;
    }

    @Test
    public void sumTestMethod() {
        assertEquals(test + number, result);
    }

    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][]{
                {"test", 1, "test1"}, {"test", 2, "test2"}, {"test", 3, "test3"}
        });
    }
}
