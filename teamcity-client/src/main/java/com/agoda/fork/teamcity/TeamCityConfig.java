package com.agoda.fork.teamcity;

public class TeamCityConfig {
    private final String url;
    private final String configuration;
    private final boolean debug;
    private final String user;
    private final String password;

    public TeamCityConfig(String url, String configuration, boolean debug, String user, String password) {
        this.url = url;
        this.configuration = configuration;
        this.debug = debug;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getConfiguration() {
        return configuration;
    }

    public boolean isDebug() {
        return debug;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
