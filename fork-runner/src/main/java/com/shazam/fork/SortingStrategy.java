package com.shazam.fork;

import com.shazam.fork.sorting.TeamCitySortingStrategy;
import groovy.lang.Closure;

public class SortingStrategy {
    public Boolean defaultStrategy;
    public TeamCitySortingStrategy teamcity;

    public void teamcity(Closure<?> manualClosure) {
        teamcity = new TeamCitySortingStrategy();
        manualClosure.setDelegate(teamcity);
        manualClosure.call();
    }
}
