package com.shazam.fork.summary.html

import com.google.gson.annotations.SerializedName

data class HtmlTestLogDetails(@SerializedName("pool_id") val poolId: String,
                              @SerializedName("test_id") val testId: String,
                              @SerializedName("display_name") val displayName: String,
                              @SerializedName("device_id") val deviceId: String,
                              @SerializedName("log_path") val logPath: String)


fun toHtmlTestLogDetails(poolId: String,
                         fullTest: HtmlFullTest) = HtmlTestLogDetails(
        poolId = poolId,
        testId = fullTest.id,
        displayName = fullTest.name,
        deviceId = fullTest.deviceId,
        logPath = "../../../../../logcat_json/$poolId/${fullTest.deviceId}/${fullTest.packageName}.${fullTest.className}%23${fullTest.name}.json"
)

