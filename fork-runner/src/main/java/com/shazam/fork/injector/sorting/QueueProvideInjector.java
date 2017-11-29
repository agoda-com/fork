package com.shazam.fork.injector.sorting;

import com.shazam.fork.QueueProvider;

import static com.shazam.fork.injector.ConfigurationInjector.configuration;
import static com.shazam.fork.injector.stat.TestHistoryManagerInjector.testHistoryManager;

public class QueueProvideInjector {
    public static QueueProvider queueProvider() {
        return new QueueProvider(configuration(),testHistoryManager());
    }
}
