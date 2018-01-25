package com.shazam.fork.summary.flakiness

import com.google.gson.Gson
import com.shazam.fork.summary.Summary
import com.shazam.fork.summary.SummaryPrinter
import com.shazam.fork.system.io.FileManager
import java.io.FileWriter

class AggregatedFlakinessSummaryPrinter(val fileManager: FileManager, val gson: Gson) : SummaryPrinter {

    data class TestSuccessRate(val testName: String, val successRate: Float)

    override fun print(summary: Summary) {
        val grouped = summary.poolSummaries.flatMap {
            it.testResults
        }.groupBy({ "${it.testClass}#${it.testMethod}" }, { it })

        val aggregated = grouped.map {
            TestSuccessRate(it.key, successRate(it.value))
        }

        FileWriter(fileManager.createSummaryFile("aggregated_flakiness")).use {
            gson.toJson(aggregated, it)
            it.flush()
        }
    }
}