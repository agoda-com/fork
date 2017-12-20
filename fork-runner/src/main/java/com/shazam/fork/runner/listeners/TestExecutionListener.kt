package com.shazam.fork.runner.listeners

import com.android.ddmlib.testrunner.TestIdentifier
import com.shazam.fork.model.Device
import com.shazam.fork.stat.TestExecution
import com.shazam.fork.stat.TestExecutionReporter
import org.slf4j.LoggerFactory

import java.lang.System.currentTimeMillis

class TestExecutionListener(private val device: Device,
                            private val executionReporter: TestExecutionReporter) : NoOpITestRunListener() {

    private val logger = LoggerFactory.getLogger(TestExecutionListener::class.java)

    private var startTime: Long = 0

    private var failed: Boolean = false

    override fun testStarted(test: TestIdentifier) {
        startTime = currentTimeMillis()
        failed = false
        logger.error("test $test started")
    }

    override fun testFailed(test: TestIdentifier, trace: String) {
        failed = true
        logger.error("test $test failed")
    }

    override fun testAssumptionFailure(test: TestIdentifier, trace: String?) {
        failed = true
        reportStatus(test)
        logger.error("test $test assumptionFailure")
    }

    override fun testEnded(test: TestIdentifier, testMetrics: Map<String, String>) {
        reportStatus(test)
        logger.error("test $test ended")
    }

    private fun reportStatus(test: TestIdentifier) {
        val endedAfter = currentTimeMillis() - startTime
        val status = when (failed) {
            true -> TestExecution.Status.FAILED
            false -> TestExecution.Status.ENDED
        }
        val execution = TestExecution(test, startTime, endedAfter, status)
        executionReporter.add(device, execution)
    }
}
