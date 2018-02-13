package com.shazam.fork.summary.html

import com.google.gson.annotations.SerializedName
import com.shazam.fork.summary.PoolSummary
import com.shazam.fork.summary.ResultStatus

data class HtmlPoolSummary(
        @SerializedName("id") val id: String,
        @SerializedName("tests") val tests: List<HtmlShortTest>,
        @SerializedName("passed_count") val passedCount: Int,
        @SerializedName("failed_count") val failedCount: Int,
        @SerializedName("duration_millis") val durationMillis: Long,
        @SerializedName("devices") val devices: List<HtmlDevice>)

fun PoolSummary.toHtmlPoolSummary() = HtmlPoolSummary(
        id = poolName,
        tests = testResults.map { it.toHtmlShortSuite() },
        passedCount = testResults.count { it.resultStatus == ResultStatus.PASS },
        failedCount = testResults.count { it.resultStatus != ResultStatus.PASS },
        durationMillis = testResults.sumByDouble { it.timeTaken.toDouble() * 1000 }.toLong(),
        devices = testResults.map { it.device }.distinct().map { it.toHtmlDevice() }
)