package com.shazam.forktest;

import android.Manifest;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TestA {

    private final int a;
    private final int b;
    private final int res;

    public TestA(int a, int b, int res) {
        this.a = a;
        this.b = b;
        this.res = res;
    }

    @Test
    public void methodTest() {
        assertEquals(res, a + b);
    }
}

