package com.shazam.fork.injector.sorting;

import com.shazam.fork.execution.TestTaskQueueProvider;

import static com.shazam.fork.injector.ConfigurationInjector.configuration;

public class TestTaskQueueProviderInjector {
    public static TestTaskQueueProvider queueProvider(){
        return new TestTaskQueueProvider(configuration().getBatchStrategy(),configuration().getCustomExecutionStrategy());
    }
}
