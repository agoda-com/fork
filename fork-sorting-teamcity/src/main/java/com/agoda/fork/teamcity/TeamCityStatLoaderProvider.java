package com.agoda.fork.teamcity;

import com.agoda.fork.sorting.StatLoader;

public class TeamCityStatLoaderProvider {
    public static StatLoader createStatLoader(String url, String user, String password, boolean debug) {
        TeamCityConfig config = new TeamCityConfig(url, debug, new AuthConfig(user, password));
        return createStatLoader(config);
    }

    public static StatLoader createStatLoader(TeamCityConfig config) {
        TeamCityClient client = new TeamCityClient(config);
        return new TeamCityStatLoader(client.teamCityService());
    }
}
