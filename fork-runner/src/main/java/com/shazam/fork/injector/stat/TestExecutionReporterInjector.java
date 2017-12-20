package com.shazam.fork.injector.stat;

import com.shazam.fork.stat.TestExecutionReporter;

public class TestExecutionReporterInjector {
    private static final TestExecutionReporter instance = new TestExecutionReporter();

    public static TestExecutionReporter testExecutionReporter(){
        return instance;
    }
}
