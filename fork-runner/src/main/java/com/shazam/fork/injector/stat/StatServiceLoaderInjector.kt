package com.shazam.fork.injector.stat

import com.shazam.fork.injector.ConfigurationInjector.configuration
import com.shazam.fork.stat.StatServiceLoader

object StatServiceLoaderInjector {

    private val INSTANCE by lazy {
        StatServiceLoader(configuration().sortingStrategy?.statistics?.path ?: "")
    }

    @JvmStatic
    fun statServiceLoader(): StatServiceLoader {
        return INSTANCE
    }
}
