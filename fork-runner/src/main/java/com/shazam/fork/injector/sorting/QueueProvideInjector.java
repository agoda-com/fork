package com.shazam.fork.injector.sorting;

import com.shazam.fork.QueueProvider;
import com.shazam.fork.stat.TestStatsLoader;

import static com.shazam.fork.injector.ConfigurationInjector.configuration;
import static com.shazam.fork.injector.stat.TestHistoryManagerInjector.testHistoryManager;

public class QueueProvideInjector {
    public static QueueProvider queueProvider() {
        return new QueueProvider(configuration(),testHistoryManager(),testStatLoader());
    }

    private static TestStatsLoader testStatLoader() {
        return new TestStatsLoader();
    }
}
