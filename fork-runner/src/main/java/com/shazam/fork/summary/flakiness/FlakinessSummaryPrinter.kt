package com.shazam.fork.summary.flakiness

import com.google.gson.GsonBuilder
import com.shazam.fork.store.TestCaseStore
import com.shazam.fork.summary.ResultStatus
import com.shazam.fork.summary.Summary
import com.shazam.fork.summary.SummaryPrinter
import com.shazam.fork.system.io.FileManager
import java.io.FileWriter

class FlakinessSummaryPrinter(private val fileManager: FileManager,
                              private val testCaseStore: TestCaseStore) : SummaryPrinter {

    private data class FlakinessReport(val testName: String,
                                       val deviceSerial: String,
                                       val ignored: Boolean,
                                       val success: Boolean,
                                       val failReason: String)

    override fun print(summary: Summary) {
        summary.poolSummaries.flatMap {
            it.testResults.map {
                val testName = "${it.testClass}#${it.testMethod}"
                FlakinessReport(testName = testName,
                        deviceSerial = it.device.safeSerial,
                        ignored = testCaseStore.get(testName)?.isIgnored ?: false,
                        success = it.resultStatus == ResultStatus.PASS,
                        failReason = it.trace)
            }.plus(it.ignoredTests.map {
                FlakinessReport(testName = "${it.testClass}.${it.testMethod}",
                        deviceSerial = "",
                        ignored = true,
                        success = false,
                        failReason = "")
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
