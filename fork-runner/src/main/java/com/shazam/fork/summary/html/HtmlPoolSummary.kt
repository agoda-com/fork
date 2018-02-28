package com.shazam.fork.summary.html

import com.google.gson.annotations.SerializedName
import com.shazam.fork.summary.PoolSummary
import com.shazam.fork.summary.ResultStatus

data class HtmlPoolSummary(
        @SerializedName("id") val id: String,
        @SerializedName("tests") val tests: List<HtmlShortTest>,
        @SerializedName("passed_count") val passedCount: Int,
        @SerializedName("failed_count") val failedCount: Int,
        @SerializedName("ignored_count") val ignoredCount: Int,
        @SerializedName("duration_millis") val durationMillis: Long,
        @SerializedName("devices") val devices: List<HtmlDevice>)

fun PoolSummary.toHtmlPoolSummary() = HtmlPoolSummary(
        id = poolName,
        tests = testResults.map { it.toHtmlShortSuite() },
        passedCount = testResults.filterNot {
            it.isIgnored
        }.count {
            it.resultStatus == ResultStatus.PASS
        },
        failedCount = testResults.filterNot {
            it.isIgnored
        }.count {
            it.resultStatus != ResultStatus.PASS
        },
        ignoredCount = testResults.count {
            it.isIgnored
        },
        durationMillis = testResults.sumByDouble {
            it.timeTaken.toDouble() * 1000
        }.toLong(),
        devices = testResults.map {
            it.device
        }.distinct().map {
            it.toHtmlDevice()
        }
)