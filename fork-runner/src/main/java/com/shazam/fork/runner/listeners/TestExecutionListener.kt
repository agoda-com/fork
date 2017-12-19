package com.shazam.fork.runner.listeners

import com.android.ddmlib.testrunner.TestIdentifier
import com.shazam.fork.model.Device
import com.shazam.fork.stat.TestExecution
import com.shazam.fork.stat.TestExecutionReporter

import java.lang.System.currentTimeMillis

class TestExecutionListener internal constructor(private val device: Device,
                                                 private val executionReporter: TestExecutionReporter) : NoOpITestRunListener() {

    private var startTime: Long = 0

    private var failed: Boolean = false

    override fun testStarted(test: TestIdentifier) {
        startTime = currentTimeMillis()
        failed = false
    }

    override fun testFailed(test: TestIdentifier, trace: String) {
        failed = true
    }

    override fun testEnded(test: TestIdentifier, testMetrics: Map<String, String>) {
        val endedAfter = currentTimeMillis() - startTime
        val status = when (failed) {
            true -> TestExecution.Status.FAILED
            false -> TestExecution.Status.ENDED
        }
        val execution = TestExecution(test, startTime, endedAfter, status)
        executionReporter.add(device, execution)
    }
}
