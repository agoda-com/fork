package com.shazam.chimprunner.injector.stat;

import com.shazam.fork.stat.StatServiceLoader;

public class StatServiceLoaderInjector {

    private static StatServiceLoader INSTANCE = null;

    public static synchronized StatServiceLoader statServiceLoader() {
        if (INSTANCE == null) {
            INSTANCE = new StatServiceLoader("");
        }
        return INSTANCE;
    }
}
