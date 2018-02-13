package com.shazam.fork.summary.html

import com.google.gson.annotations.SerializedName
import com.shazam.fork.summary.PoolSummary
import com.shazam.fork.summary.ResultStatus
import com.shazam.fork.summary.Summary
import kotlin.math.roundToInt
import kotlin.math.roundToLong

data class HtmlIndex(
        @SerializedName("title") val title: String,
        @SerializedName("total_failed") val totalFailed: Int,
        @SerializedName("total_ignored") val totalIgnored: Int,
        @SerializedName("total_passed") val totalPassed: Int,
        @SerializedName("total_duration_millis") val totalDuration: Long,
        @SerializedName("average_duration_millis") val averageDuration: Long,
        @SerializedName("max_duration_millis") val maxDuration: Long,
        @SerializedName("min_duration_millis") val minDuration: Long,
        @SerializedName("pools") val pools: List<HtmlPoolSummary>)

fun Summary.toHtmlIndex() = HtmlIndex(
        title = title,
        totalFailed = failedTests.size,
        totalIgnored = ignoredTests.size,
        totalPassed = poolSummaries.map { it.testResults.filter { it.resultStatus == ResultStatus.PASS } }.count(),
        totalDuration = totalDuration(poolSummaries),
        averageDuration = averageDuration(poolSummaries),
        maxDuration = maxDuration(poolSummaries).toLong(),
        minDuration = minDuration(poolSummaries).toLong(),
        pools = poolSummaries.map { it.toHtmlPoolSummary() }
)

fun totalDuration(poolSummaries: List<PoolSummary>): Long {
    return poolSummaries.flatMap { it.testResults }.sumByDouble { it.timeTaken.toDouble() * 1000 }.toLong()
}

fun averageDuration(poolSummaries: List<PoolSummary>) = durationPerPool(poolSummaries).average().roundToLong()

fun minDuration(poolSummaries: List<PoolSummary>) = durationPerPool(poolSummaries).min() ?: 0

private fun durationPerPool(poolSummaries: List<PoolSummary>) =
        poolSummaries.map { it.testResults }
                .map { it.sumBy { (it.timeTaken * 1000).roundToInt() } }

fun maxDuration(poolSummaries: List<PoolSummary>) = durationPerPool(poolSummaries).max() ?: 0
