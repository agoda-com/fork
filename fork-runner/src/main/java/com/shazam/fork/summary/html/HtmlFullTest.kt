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
        @SerializedName("logcat_path") val logcatPath: String,
        @SerializedName("deviceId") val deviceId: String,
        @SerializedName("diagnostic_video") val diagnosticVideo: Boolean,
        @SerializedName("diagnostic_screenshots") val diagnosticScreenshots: Boolean,
        @SerializedName("screenshot") val screenshot: String,
        @SerializedName("video") val video: String
)

fun TestResult.toHtmlFullTest(poolId: String) = HtmlFullTest(
        poolId = poolId,
        id = "$testClass$testMethod",
        packageName = testClass.substringBeforeLast("."),
        className = testClass.substringAfterLast("."),
        name = testMethod,
        durationMillis = (timeTaken * 1000).toLong(),
        status = when (this.resultStatus) {
            ResultStatus.FAIL -> Status.Failed
            ResultStatus.PASS -> Status.Passed
            ResultStatus.ERROR -> Status.Failed
        },
        deviceId = this.device.safeSerial,
        diagnosticVideo = device.supportedDiagnostics == Diagnostics.VIDEO,
        diagnosticScreenshots = device.supportedDiagnostics == Diagnostics.SCREENSHOTS,
        stacktrace = this.trace,
        screenshot = when (device.supportedDiagnostics == Diagnostics.SCREENSHOTS) {
            true -> ""
            false -> ""
        },
        video = when (device.supportedDiagnostics == Diagnostics.VIDEO) {
            true -> ""
            false -> ""
        },
        logcatPath = ""
)

//{{#diagnosticVideo}}
//<video class="diagnostic {{status}}" width="35%" height="35%" controls
//src="../../../screenrecord/{{plainPoolName}}/{{deviceSafeSerial}}/{{plainClassName}}%23{{plainMethodName}}.mp4"
//type="video/mp4">
//Is video supported in this browser?
//</video>
//{{/diagnosticVideo}}
//{{#diagnosticScreenshots}}
//<img class="diagnostic {{status}}" width="35%" height="35%"
//src="../../../animation/{{plainPoolName}}/{{deviceSafeSerial}}/{{plainClassName}}%23{{plainMethodName}}.gif"/>
//{{/diagnosticScreenshots}}
//<pre class="test {{status}}">{{#trace}}
