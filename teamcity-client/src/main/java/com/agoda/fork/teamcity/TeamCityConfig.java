package com.agoda.fork.teamcity;

public class TeamCityConfig {
    private final String url;
    private final boolean debug;
    private final AuthConfig authConfig;

    public TeamCityConfig(String url, boolean debug, AuthConfig authConfig) {
        this.url = url;
        this.debug = debug;
        this.authConfig = authConfig;
    }

    public String getUrl() {
        return url;
    }

    public boolean isDebug() {
        return debug;
    }

    public AuthConfig getAuthConfig() {
        return authConfig;
    }
}
