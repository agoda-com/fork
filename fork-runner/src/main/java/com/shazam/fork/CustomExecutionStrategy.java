package com.shazam.fork;

import com.shazam.fork.flakiness.FlakinessExecutionStrategy;
import groovy.lang.Closure;

public class CustomExecutionStrategy {
    public Boolean defaultStrategy;
    public FlakinessExecutionStrategy flakinessStrategy;


    public void flakinessStrategy(Closure<?> manualClosure) {
        flakinessStrategy = new FlakinessExecutionStrategy();
        manualClosure.setDelegate(flakinessStrategy);
        manualClosure.call();
    }
}
