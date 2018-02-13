package com.shazam.fork.summary.html

import com.google.gson.Gson
import com.shazam.fork.injector.ConfigurationInjector.configuredOutput
import com.shazam.fork.summary.Summary
import com.shazam.fork.summary.SummaryPrinter
import java.io.File
import java.util.*


class HtmlSummaryPrinterV2(val gson: Gson,
                           val rootOutput: File = configuredOutput()) : SummaryPrinter {
    override fun print(summary: Summary) {
        writeHtmlReport(gson, summary, rootOutput, Date())
    }
}