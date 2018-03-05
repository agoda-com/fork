package com.shazam.fork.stat

import com.android.ddmlib.testrunner.TestIdentifier
import com.android.ddmlib.testrunner.TestResult

data class TestExecution(val test: TestIdentifier, val startTime: Long, val endTime: Long, val status: Status) {

    enum class Status {
        FAILED,
        PASSED,
        IGNORED
    }
}

fun TestResult.TestStatus.toTestExecutionStatus() = when (this) {
    TestResult.TestStatus.IGNORED -> TestExecution.Status.IGNORED
    TestResult.TestStatus.PASSED -> TestExecution.Status.PASSED
    else -> TestExecution.Status.FAILED
}