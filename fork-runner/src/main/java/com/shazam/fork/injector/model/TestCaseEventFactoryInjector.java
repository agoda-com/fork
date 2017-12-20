package com.shazam.fork.injector.model;

import com.shazam.fork.model.TestCaseEventFactory;

import static com.shazam.fork.injector.stat.TestStatLoaderInjector.testStatsLoader;

public class TestCaseEventFactoryInjector {
    public static TestCaseEventFactory testCaseEventFactory(){
        return new TestCaseEventFactory(testStatsLoader());
    }
}
