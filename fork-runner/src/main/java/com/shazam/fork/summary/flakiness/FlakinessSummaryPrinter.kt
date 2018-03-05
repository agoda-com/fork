package com.shazam.fork.summary.flakiness

import com.google.gson.GsonBuilder
import com.shazam.fork.stat.TestExecutionReporter
import com.shazam.fork.store.TestCaseStore
import com.shazam.fork.summary.ResultStatus
import com.shazam.fork.summary.Summary
import com.shazam.fork.summary.SummaryPrinter
import com.shazam.fork.summary.TestResult
import com.shazam.fork.system.io.FileManager
import java.io.FileWriter

class FlakinessSummaryPrinter(private val fileManager: FileManager,
                              private val testCaseStore: TestCaseStore,
                              private val reporter: TestExecutionReporter) : SummaryPrinter {

    private data class FlakinessReport(val testName: String,
                                       val deviceSerial: String,
                                       val ignored: Boolean,
                                       val success: Boolean,
                                       val failReason: String,
                                       val timestamp: Long)

    private fun extractTimestamp(pool: String,
                                 test: TestResult): Long {
        val device = test.device
        val tests = reporter.getTests(pool, device)
        return tests.minBy { Math.abs((test.timeTaken * 1000) - (it.endTime - it.startTime)) }?.let { it.startTime + it.endTime } ?: System.currentTimeMillis()

    }

    override fun print(summary: Summary) {
        summary.poolSummaries.flatMap {
            val poolname = it.poolName
            it.testResults.map { test ->
                val testName = "${test.testClass}#${test.testMethod}"
                FlakinessReport(testName = testName,
                        deviceSerial = test.device.safeSerial,
                        ignored = testCaseStore.get(testName)?.isIgnored ?: false,
                        success = test.resultStatus == ResultStatus.PASS,
                        failReason = test.trace,
                        timestamp = extractTimestamp(poolname, test))
            }.plus(it.ignoredTests.map {
                FlakinessReport(testName = "${it.testClass}.${it.testMethod}",
                        deviceSerial = "",
                        ignored = true,
                        success = false,
                        failReason = "",
                        timestamp = extractTimestamp(poolname, it))
            })
        }.let { items ->
            val file = fileManager.createSummaryFile("flakiness")
            FileWriter(file).use {
                GsonBuilder().setPrettyPrinting()
                        .create()
                        .toJson(items, it)
            }
        }
    }
}
