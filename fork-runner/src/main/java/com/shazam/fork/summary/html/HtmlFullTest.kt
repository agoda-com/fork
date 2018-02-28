package com.shazam.fork.summary.html

import com.google.gson.annotations.SerializedName
import com.shazam.fork.model.Diagnostics
import com.shazam.fork.summary.ResultStatus
import com.shazam.fork.summary.TestResult

data class HtmlFullTest(
        @SerializedName("pool_id") val poolId: String,
        @SerializedName("package_name") val packageName: String,
        @SerializedName("class_name") val className: String,
        @SerializedName("name") val name: String,
        @SerializedName("id") val id: String = "$packageName$className$name",
        @SerializedName("duration_millis") val durationMillis: Long,
        @SerializedName("status") val status: Status,
        @SerializedName("stacktrace") val stacktrace: String?,
        @SerializedName("deviceId") val deviceId: String,
        @SerializedName("diagnostic_video") val diagnosticVideo: Boolean,
        @SerializedName("diagnostic_screenshots") val diagnosticScreenshots: Boolean,
        @SerializedName("screenshot") val screenshot: String,
        @SerializedName("video") val video: String,
        @SerializedName("log_file") val logFile : String
)

fun TestResult.toHtmlFullTest(poolId: String) = HtmlFullTest(
        poolId = poolId,
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
        deviceId = this.device.safeSerial,
        diagnosticVideo = device.supportedDiagnostics == Diagnostics.VIDEO,
        diagnosticScreenshots = device.supportedDiagnostics == Diagnostics.SCREENSHOTS,
        stacktrace = this.trace,
        screenshot = when (device.supportedDiagnostics == Diagnostics.SCREENSHOTS && resultStatus != ResultStatus.PASS) {
            true -> "../../../../animation/$poolId/${device.safeSerial}/$testClass%23$testMethod.gif"
            false -> ""
        },
        video = when (device.supportedDiagnostics == Diagnostics.VIDEO && resultStatus != ResultStatus.PASS) {
            true -> "../../../../screenrecord/$poolId/${device.safeSerial}/$testClass%23$testMethod.mp4"
            false -> ""
        },
        logFile = "../../../../logcat/$poolId/${device.safeSerial}/$testClass%23$testMethod.log")
