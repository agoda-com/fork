package com.shazam.fork.injector.sorting;

import com.shazam.fork.QueueProvider;

import static com.shazam.fork.injector.ConfigurationInjector.configuration;

public class QueueProvideInjector {
    public static QueueProvider queueProvider() {
        return new QueueProvider(configuration());
    }
}
