package com.shazam.fork.summary.flakiness

import com.google.gson.Gson
import com.shazam.fork.summary.Summary
import com.shazam.fork.summary.SummaryPrinter
import com.shazam.fork.system.io.FileManager
import java.io.FileWriter

class AggregatedFlakinessPerDeviceSummaryPrinter(private val fileManager: FileManager,
                                                 private val gson: Gson) : SummaryPrinter {

    data class TestSuccessPerDeviceRate(val serial: String, val successRate: Float)

    override fun print(summary: Summary) {
        val grouped = summary.poolSummaries.flatMap {
            it.testResults
        }.groupBy({ it.device }, { it })

        val aggregated = grouped.map {
            TestSuccessPerDeviceRate(it.key.safeSerial, successRate(it.value))
        }

        FileWriter(fileManager.createSummaryFile("aggregated_flakiness_per_device")).use {
            gson.toJson(aggregated, it)
            it.flush()
        }
    }
}