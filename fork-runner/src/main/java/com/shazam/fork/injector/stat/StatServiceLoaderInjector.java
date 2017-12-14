package com.shazam.fork.injector.stat;

import com.shazam.fork.stat.StatServiceLoader;

public class StatServiceLoaderInjector {
    private static final StatServiceLoader INSTANCE = new StatServiceLoader();

    public static StatServiceLoader statServiceLoader() {
        return INSTANCE;
    }
}
