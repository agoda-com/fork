package com.shazam.fork;

import com.agoda.fork.sorting.StatLoader;
import com.agoda.fork.sorting.TestHistory;
import com.agoda.fork.teamcity.TeamCityStatLoaderProvider;
import com.shazam.fork.model.TestCaseEvent;
import com.shazam.fork.sorting.BalancerQueue;
import com.shazam.fork.sorting.TeamCitySortingStrategy;
import com.shazam.fork.stat.TestHistoryManager;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class QueueProvider {

    private final Configuration configuration;
    private final TestHistoryManager testHistoryManager;

    public QueueProvider(Configuration configuration,
                         TestHistoryManager testHistoryManager) {
        this.configuration = configuration;
        this.testHistoryManager = testHistoryManager;
    }

    public Queue<TestCaseEvent> createQueue(Collection<TestCaseEvent> testCaseEvents) {
        SortingStrategy sortingStrategy = configuration.getSortingStrategy();

        if (sortingStrategy.teamcity != null) {
            TeamCitySortingStrategy teamcity = sortingStrategy.teamcity;

            TeamCityProvider teamCityStatLoaderProvider = new TeamCityProvider(teamcity);
            StatLoader statLoader = teamCityStatLoaderProvider.getStatLoader();
            List<TestHistory> tests = statLoader.loadHistory();
            testHistoryManager.addTestHistories(tests);

            return new BalancerQueue(testCaseEvents, tests);
        }

        return new LinkedList<>();
    }

    //TODO: extract
    private class TeamCityProvider {
        private TeamCitySortingStrategy teamcity;

        public TeamCityProvider(TeamCitySortingStrategy teamcity) {
            this.teamcity = teamcity;
        }

        public StatLoader getStatLoader() {
            String url = teamcity.url;
            String user = teamcity.user;
            String password = teamcity.password;
            boolean debug = teamcity.debug;
            return TeamCityStatLoaderProvider.createStatLoader(url, user, password, debug);
        }
    }
}
