package com.shazam.fork.injector.stat;

import com.shazam.fork.stat.StatServiceLoader;

import static com.shazam.fork.injector.ConfigurationInjector.configuration;

public class StatServiceLoaderInjector {

    private static StatServiceLoader INSTANCE = null;

    public static synchronized StatServiceLoader statServiceLoader() {
        if (INSTANCE == null) {
            INSTANCE = new StatServiceLoader(configuration().getSortingStrategy().statistics.path);
        }
        return INSTANCE;
    }
}
