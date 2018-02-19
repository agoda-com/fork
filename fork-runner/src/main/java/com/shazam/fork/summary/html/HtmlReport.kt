package com.shazam.fork.summary.html

import com.google.gson.Gson
import com.shazam.fork.summary.Summary
import org.apache.commons.lang3.StringEscapeUtils
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * Following file tree structure will be created:
 * - index.json
 * - suites/suiteId.json
 * - suites/deviceId/testId.json
 */

fun writeHtmlReport(gson: Gson, summary: Summary, rootOutput: File) {
    val outputDir = File(rootOutput, "/html")
    rootOutput.mkdirs()
    outputDir.mkdirs()

    val htmlIndexJson = gson.toJson(summary.toHtmlIndex())

    val formattedDate = SimpleDateFormat("HH:mm:ss z, MMM d yyyy").apply { timeZone = TimeZone.getTimeZone("UTC") }.format(Date())

    val appJs = File(outputDir, "app.min.js")
    inputStreamFromResources("html-report/app.min.js").copyTo(appJs.outputStream())

    val appCss = File(outputDir, "app.min.css")
    inputStreamFromResources("html-report/app.min.css").copyTo(appCss.outputStream())

    // index.html is a page that can render all kinds of inner pages: Index, Suite, Test.
    val indexHtml = inputStreamFromResources("html-report/index.html").reader().readText()

    val indexHtmlFile = File(outputDir, "index.html")

    fun File.relativePathToHtmlDir(): String = outputDir.relativePathTo(this.parentFile).let { relativePath ->
        when (relativePath) {
            "" -> relativePath
            else -> "$relativePath/"
        }
    }

    indexHtmlFile.writeText(indexHtml
            .replace("\${relative_path}", indexHtmlFile.relativePathToHtmlDir())
            .replace("\${data_json}", "window.mainData = $htmlIndexJson")
            .replace("\${log}", "")
            .replace("\${date}", formattedDate)
    )

    val poolsDir = File(outputDir, "pools").apply { mkdirs() }

    summary.poolSummaries.forEach { pool ->
        val poolJson = gson.toJson(pool.toHtmlPoolSummary())
        val poolHtmlFile = File(poolsDir, "${pool.poolName}.html")

        poolHtmlFile.writeText(indexHtml
                .replace("\${relative_path}", poolHtmlFile.relativePathToHtmlDir())
                .replace("\${data_json}", "window.pool = $poolJson")
                .replace("\${log}", "")
                .replace("\${date}", formattedDate)
        )

        pool.testResults.map { it to File(File(poolsDir, pool.poolName), it.device.safeSerial).apply { mkdirs() } }
                .map { (test, testDir) -> Triple(test, test.toHtmlFullTest(poolId = pool.poolName), testDir) }
                .forEach { (test, htmlTest, testDir) ->
                    val testJson = gson.toJson(htmlTest)
                    val testHtmlFile = File(testDir, "${htmlTest.id}.html")

                    testHtmlFile.writeText(indexHtml
                            .replace("\${relative_path}", testHtmlFile.relativePathToHtmlDir())
                            .replace("\${data_json}", "window.test = $testJson")
                            .replace("\${log}", generateLogcatHtml(test.trace))
                            .replace("\${date}", formattedDate)
                    )
                }
    }
}

/*
 * Fixed version of `toRelativeString()` from Kotlin stdlib that forces use of absolute file paths.
 * See https://youtrack.jetbrains.com/issue/KT-14056
*/

fun File.relativePathTo(base: File): String = absoluteFile.toRelativeString(base.absoluteFile)

fun inputStreamFromResources(path: String): InputStream = HtmlPoolSummary::class.java.classLoader.getResourceAsStream(path)

fun generateLogcatHtml(logcatOutput: String): String = when (logcatOutput.isNotEmpty()) {
    false -> ""
    true -> logcatOutput
            .lines()
            .map { line -> """<div class="log__${cssClassForLogcatLine(line)}">${StringEscapeUtils.escapeXml11(line)}</div>""" }
            .fold(StringBuilder("""<div class="content"><div class="card log">""")) { stringBuilder, line ->
                stringBuilder.appendln(line)
            }.appendln("""</div></div>""").toString()
}

fun cssClassForLogcatLine(logcatLine: String): String {
    // Logcat line example: `06-07 16:55:14.490  2100  2100 I MicroDetectionWorker: #onError(false)`
    // First letter is Logcat level.
    return when (logcatLine.firstOrNull { it.isLetter() }) {
        'V' -> "verbose"
        'D' -> "debug"
        'I' -> "info"
        'W' -> "warning"
        'E' -> "error"
        'A' -> "assert"
        else -> "default"
    }
}
