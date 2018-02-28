package com.shazam.fork.injector.stat

import com.shazam.fork.injector.GsonInjector.gson
import com.shazam.fork.injector.system.FileManagerInjector.fileManager
import com.shazam.fork.stat.TestExecutionReporter

object TestExecutionReporterInjector {
    @JvmStatic
    fun testExecutionReporter(): TestExecutionReporter {
        return TestExecutionReporter(fileManager(), gson())
    }
}
