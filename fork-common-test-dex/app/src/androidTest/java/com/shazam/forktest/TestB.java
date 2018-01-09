package com.shazam.forktest;

import com.shazam.annotations.Inheritance;

@Inheritance
public class TestB extends TestA {
    public TestB() {
        super(2, 2, 4);
    }
}

