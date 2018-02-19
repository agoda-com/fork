package com.shazam.fork.summary.flakiness

import com.google.gson.GsonBuilder
import com.shazam.fork.summary.Summary
import com.shazam.fork.summary.SummaryPrinter
import com.shazam.fork.system.io.FileManager
import java.io.FileWriter

class AggregatedFlakinessSummaryPrinter(val fileManager: FileManager) : SummaryPrinter {

    data class TestSuccessRate(val testName: String, val successRate: Float)

    override fun print(summary: Summary) {
        val grouped = summary.poolSummaries.flatMap {
            it.testResults
        }.groupBy({ "${it.testClass}#${it.testMethod}" }, { it })

        val aggregated = grouped.map {
            TestSuccessRate(it.key, successRate(it.value))
        }

        FileWriter(fileManager.createSummaryFile("aggregated_flakiness")).use {
            GsonBuilder().setPrettyPrinting()
                    .create()
                    .toJson(aggregated,it)
            it.flush()
        }
    }
}