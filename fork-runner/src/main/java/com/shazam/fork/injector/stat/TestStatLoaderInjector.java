package com.shazam.fork.injector.stat;

import com.shazam.fork.stat.TestStatsLoader;

import static com.shazam.fork.injector.stat.StatServiceLoaderInjector.statServiceLoader;

public class TestStatLoaderInjector {
    private static final TestStatsLoader INSTANCE = new TestStatsLoader(statServiceLoader());

    public static TestStatsLoader testStatsLoader() {
        return INSTANCE;
    }
}
