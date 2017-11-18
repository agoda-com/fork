package com.shazam.forktest;

import org.junit.Ignore;
import org.junit.Test;
import com.shazam.annotations.CustomTestAnnotation2;

import static org.junit.Assert.assertEquals;

@CustomTestAnnotation2
public class CustomTestAnnotation2ClassTest {

    @Test
    public void methodOfAnCustomTestAnnotation2TestClass() {
        assertEquals(4, 2 + 2);
    }
}

