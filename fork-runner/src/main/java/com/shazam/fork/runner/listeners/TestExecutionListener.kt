package com.shazam.fork.runner.listeners

import com.android.ddmlib.testrunner.TestIdentifier
import com.android.ddmlib.testrunner.TestResult
import com.android.ddmlib.testrunner.TestRunResult
import com.shazam.fork.model.Device
import com.shazam.fork.model.Pool
import com.shazam.fork.stat.TestExecution
import com.shazam.fork.stat.TestExecutionReporter
import com.shazam.fork.stat.toTestExecutionStatus

class TestExecutionListener(private val pool: Pool,
                            private val device: Device,
                            private val executionReporter: TestExecutionReporter) : NoOpITestRunListener() {

    private val runResult = TestRunResult()

    override fun testRunStarted(runName: String, testCount: Int) {
        runResult.testRunStarted(runName, testCount)
    }

    override fun testStarted(test: TestIdentifier) {
        runResult.testStarted(test)
    }

    override fun testFailed(test: TestIdentifier, trace: String) {
        runResult.testFailed(test, trace)
    }

    override fun testAssumptionFailure(test: TestIdentifier, trace: String) {
        runResult.testAssumptionFailure(test, trace)
    }

    override fun testIgnored(test: TestIdentifier) {
        runResult.testIgnored(test)
    }

    override fun testEnded(test: TestIdentifier, testMetrics: MutableMap<String, String>) {
        runResult.testEnded(test, testMetrics)
    }

    override fun testRunFailed(errorMessage: String) {
        runResult.testRunFailed(errorMessage)
    }

    override fun testRunStopped(elapsedTime: Long) {
        runResult.testRunStopped(elapsedTime)
    }

    override fun testRunEnded(elapsedTime: Long, runMetrics: MutableMap<String, String>) {
        runResult.testRunEnded(elapsedTime, runMetrics)
        report()
    }

    private fun report() {
        runResult.testResults.forEach {
            reportStatus(it.key, it.value)
        }
    }

    private fun reportStatus(test: TestIdentifier, value: TestResult) {
        val endedAfter = value.endTime - value.startTime
        val status = value.status.toTestExecutionStatus()
        val execution = TestExecution(test, value.startTime, endedAfter, status)
        executionReporter.add(pool, device, execution)
    }
}
