package com.shazam.fork;

import com.shazam.fork.sorting.Stats;
import groovy.lang.Closure;

public class SortingStrategy {
    public Boolean defaultStrategy;
    public Stats statistics;


    public void stats(Closure<?> manualClosure) {
        statistics = new Stats();
        manualClosure.setDelegate(statistics);
        manualClosure.call();
    }
}
