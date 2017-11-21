package com.shazam.fork;

import com.agoda.fork.sorting.StatLoader;
import com.agoda.fork.teamcity.TeamCityStatLoaderProvider;
import com.shazam.fork.model.TestCaseEvent;
import com.shazam.fork.sorting.BalancerQueue;
import com.shazam.fork.sorting.TeamCitySortingStrategy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class QueueProvider {

    private final Configuration configuration;

    public QueueProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    public Queue<TestCaseEvent> createQueue(Collection<TestCaseEvent> testCaseEvents) {
        SortingStrategy sortingStrategy = configuration.getSortingStrategy();

        if (sortingStrategy.teamcity != null) {
            TeamCitySortingStrategy teamcity = sortingStrategy.teamcity;

            TeamCityProvider teamCityStatLoaderProvider = new TeamCityProvider(teamcity);
            StatLoader statLoader = teamCityStatLoaderProvider.getStatLoader();
            String configuration = teamcity.configuration;

            return new BalancerQueue(testCaseEvents, statLoader.loadHistory(configuration));
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
