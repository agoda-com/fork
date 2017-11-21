package com.shazam.fork.injector.stat;

import com.shazam.fork.stat.TestHistoryManager;

public class TestHistoryManagerInjector {
    private static final TestHistoryManager instance = new TestHistoryManager();

    public static TestHistoryManager testHistoryManager() {
        return instance;
    }
}
