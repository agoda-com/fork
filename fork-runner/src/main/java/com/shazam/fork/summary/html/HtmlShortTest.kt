package com.shazam.fork.summary.html

import com.google.gson.annotations.SerializedName
import com.shazam.fork.summary.ResultStatus
import com.shazam.fork.summary.TestResult

data class HtmlShortTest(
        @SerializedName("id") val id: String,
        @SerializedName("package_name") val packageName: String,
        @SerializedName("class_name") val className: String,
        @SerializedName("name") val name: String,
        @SerializedName("duration_millis") val durationMillis: Long,
        @SerializedName("status") val status: Status,
        @SerializedName("deviceId") val deviceId: String
)

fun TestResult.toHtmlShortSuite() = HtmlShortTest(
        id = "$testClass$testMethod",
        packageName = testClass.substringBeforeLast("."),
        className = testClass.substringAfterLast("."),
        name = testMethod,
        durationMillis = (timeTaken * 1000).toLong(),
        status = when {
            isIgnored -> Status.Ignored
            resultStatus == ResultStatus.PASS -> Status.Passed
            else -> Status.Failed
        },
        deviceId = this.device.safeSerial)