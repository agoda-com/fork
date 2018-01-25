package com.shazam.fork.summary.flakiness

import com.shazam.fork.summary.ResultStatus
import com.shazam.fork.summary.TestResult

internal fun successRate(list: List<TestResult>): Float {
    val passed = list.count { it.resultStatus == ResultStatus.PASS }
    return (passed / list.size.toFloat()) * 100
}