package com.shazam.chimprunner.injector.model;

import com.shazam.fork.model.TestCaseEventFactory;

import static com.shazam.chimprunner.injector.stat.TestStatLoaderInjector.testStatsLoader;

public class TestCaseEventFactoryInjector {
    public static TestCaseEventFactory testCaseEventFactory(){
        return new TestCaseEventFactory(testStatsLoader());
    }
}
