package com.shazam.fork.injector.preprocessor;

import com.shazam.fork.preprocessor.TestsPreprocessor;

import static com.shazam.fork.injector.ConfigurationInjector.configuration;

public class TestsPreprocessorInjector {
    public static TestsPreprocessor testsPreprocessor(){
        return new TestsPreprocessor(configuration().getCustomExecutionStrategy());
    }
}
