package com.shazam.fork.injector.sorting;

import com.shazam.fork.sorting.QueueProvider;

import static com.shazam.fork.injector.ConfigurationInjector.configuration;

public class QueueProviderInjector {
    public static QueueProvider queueProvider(){
        return new QueueProvider(configuration());
    }
}