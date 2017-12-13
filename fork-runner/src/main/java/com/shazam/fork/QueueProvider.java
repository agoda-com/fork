package com.shazam.fork;

import com.agoda.fork.stat.StatLoader;
import com.agoda.fork.stat.TestHistory;
import com.agoda.fork.teamcity.TeamCityStatLoaderProvider;
import com.shazam.fork.model.TestCaseEvent;
import com.shazam.fork.sorting.BalancerQueue;
import com.shazam.fork.sorting.TeamCitySortingStrategy;
import com.shazam.fork.stat.TestHistoryManager;
import com.shazam.fork.stat.TestStatsLoader;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueueProvider {

    private final Configuration configuration;
    private final TestHistoryManager testHistoryManager;
    private final TestStatsLoader loader;

    public QueueProvider(Configuration configuration,
                         TestHistoryManager testHistoryManager,
                         TestStatsLoader loader) {
        this.configuration = configuration;
        this.testHistoryManager = testHistoryManager;
        this.loader = loader;
    }

    public Queue<TestCaseEvent> createQueue(Collection<TestCaseEvent> testCaseEvents) {
        SortingStrategy sortingStrategy = configuration.getSortingStrategy();

        if (sortingStrategy.teamcity != null) {
            TeamCitySortingStrategy teamcity = sortingStrategy.teamcity;
            throw new RuntimeException();
//            loader.load();
//            List<TestHistory> tests = loader.
//            testHistoryManager.addTestHistories(tests);

//            return new BalancerQueue(testCaseEvents, tests);
        }

        return new LinkedList<>();
    }
}
