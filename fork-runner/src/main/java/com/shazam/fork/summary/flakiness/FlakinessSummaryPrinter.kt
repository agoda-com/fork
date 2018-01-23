package com.shazam.fork.summary.flakiness

import com.google.gson.Gson
import com.shazam.fork.summary.ResultStatus
import com.shazam.fork.summary.Summary
import com.shazam.fork.summary.SummaryPrinter
import com.shazam.fork.system.io.FileManager
import java.io.FileWriter

class FlakinessSummaryPrinter(private val fileManager: FileManager,
                              private val gson: Gson) : SummaryPrinter {

    private data class FlakinessReport(val testName: String,
                                       val deviceSerial: String,
                                       val ignored: Boolean,
                                       val success: Boolean,
                                       val failReason: String)

    override fun print(summary: Summary) {
        summary.poolSummaries.flatMap {
            it.testResults.map {
                FlakinessReport(testName = "${it.testClass}#${it.testMethod}",
                        deviceSerial = it.device.safeSerial,
                        ignored = false,
                        success = it.resultStatus == ResultStatus.PASS,
                        failReason = it.trace)
            }
        }.let {
            val file = fileManager.createSummaryFile()
            gson.toJson(it, FileWriter(file))
        }
    }
}
