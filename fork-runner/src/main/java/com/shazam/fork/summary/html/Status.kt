package com.shazam.fork.summary.html

import com.google.gson.annotations.SerializedName

enum class Status {
    @SerializedName("passed") Passed,
    @SerializedName("failed") Failed,
    @SerializedName("ignored") Ignored
}