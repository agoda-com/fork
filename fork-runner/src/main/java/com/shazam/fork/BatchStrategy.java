package com.shazam.fork;

import com.shazam.fork.batch.ChunkStrategy;
import groovy.lang.Closure;

public class BatchStrategy {
    public Boolean defaultStrategy;
    public ChunkStrategy chunkStrategy;
    public ExpectedTimeStrategy expectedTimeBasedStrategy;
    public VarianceStrategy varianceBasedStrategy;

    public void chunked(Closure<?> manualClosure) {
        chunkStrategy = new ChunkStrategy();
        manualClosure.setDelegate(chunkStrategy);
        manualClosure.call();
    }

    public void expectedTime(Closure<?> manualClosure) {
        expectedTimeBasedStrategy = new ExpectedTimeStrategy();
        manualClosure.setDelegate(expectedTimeBasedStrategy);
        manualClosure.call();
    }

    public void variance(Closure<?> manualClosure) {
        varianceBasedStrategy = new VarianceStrategy();
        manualClosure.setDelegate(varianceBasedStrategy);
        manualClosure.call();
    }
}
