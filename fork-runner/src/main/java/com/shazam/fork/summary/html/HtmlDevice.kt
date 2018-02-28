package com.shazam.fork.summary.html

import com.google.gson.annotations.SerializedName
import com.shazam.fork.model.Device

data class HtmlDevice(@SerializedName("apiLevel") val apiLevel: String,
                      @SerializedName("isTable") val isTablet: Boolean,
                      @SerializedName("serial") val serial: String,
                      @SerializedName("modelName") val modelName: String)

fun Device.toHtmlDevice() = HtmlDevice(
        apiLevel = apiLevel,
        isTablet = isTablet,
        serial = safeSerial,
        modelName = modelName
)