package com.shazam.forktest;

import org.junit.Ignore;
import org.junit.Test;
import com.shazam.annotations.CustomTestAnnotation1;

import static org.junit.Assert.assertEquals;

@CustomTestAnnotation1
public class CustomTestAnnotation1ClassTest {

    @Test
    public void methodOfAnCustomTestAnnotation1TestClass() {
        assertEquals(4, 2 + 2);
    }
}

