package com.shazam.fork.summary.flakiness

import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.shazam.fork.stat.TestExecution
import com.shazam.fork.stat.TestExecutionReporter
import com.shazam.fork.summary.Summary
import com.shazam.fork.summary.SummaryPrinter
import com.shazam.fork.system.io.FileManager
import java.io.FileWriter

class FlakinessSummaryPrinter(private val fileManager: FileManager,
                              private val reporter: TestExecutionReporter) : SummaryPrinter {

    private data class FlakinessReport(@SerializedName("package") val testPackage: String,
                                       @SerializedName("class") val testClass: String,
                                       @SerializedName("method") val testMethod: String,
                                       val deviceSerial: String,
                                       val ignored: Boolean,
                                       val success: Boolean,
                                       val timestamp: Long)

    override fun print(summary: Summary) {
        summary.poolSummaries.flatMap {
            val poolName = it.poolName
            it.testResults.map { it.device }.distinctBy { it.safeSerial }.flatMap { device ->
                reporter.getTests(poolName, device).map { report ->
                    val test = report.test
                    val testPackage = test.className.substringBeforeLast('.')
                    val testClass = test.className.substringAfterLast('.')

                    FlakinessReport(
                            testPackage = testPackage,
                            testClass = testClass,
                            testMethod = test.testName,
                            deviceSerial = device.safeSerial,
                            ignored = report.status == TestExecution.Status.IGNORED,
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
