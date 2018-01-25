package com.shazam.fork.injector.store;

import com.shazam.fork.store.TestCaseStore;

public class TestCaseStoreInjector {
    private static final TestCaseStore INSTANCE = new TestCaseStore();

    public static TestCaseStore testCaseStore() {
        return INSTANCE;
    }
}
