package com.shazam.fork.summary.flakiness

import com.google.gson.GsonBuilder
import com.shazam.fork.stat.TestExecution
import com.shazam.fork.stat.TestExecutionReporter
import com.shazam.fork.store.TestCaseStore
import com.shazam.fork.summary.Summary
import com.shazam.fork.summary.SummaryPrinter
import com.shazam.fork.system.io.FileManager
import java.io.FileWriter

class FlakinessSummaryPrinter(private val fileManager: FileManager,
                              private val testCaseStore: TestCaseStore,
                              private val reporter: TestExecutionReporter) : SummaryPrinter {

    private data class FlakinessReport(val testName: String,
                                       val deviceSerial: String,
                                       val ignored: Boolean,
                                       val success: Boolean,
                                       val timestamp: Long)

    override fun print(summary: Summary) {
        summary.poolSummaries.flatMap {
            val poolname = it.poolName
            it.testResults.map { it.device }.distinctBy { it.safeSerial }.flatMap { device ->
                reporter.getTests(poolname, device).map { report ->
                    val test = report.test
                    val testName = "${test.className}#${test.testName}"
                    FlakinessReport(testName = testName,
                            deviceSerial = device.safeSerial,
                            ignored = testCaseStore.get(testName)?.isIgnored ?: false,
                            success = report.status == TestExecution.Status.PASSED,
                            timestamp = report.endTime)
                }
            }
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
