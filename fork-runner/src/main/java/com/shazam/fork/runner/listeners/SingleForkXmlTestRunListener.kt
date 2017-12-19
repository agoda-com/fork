package com.shazam.fork.runner.listeners

import com.android.SdkConstants
import com.android.ddmlib.Log
import com.android.ddmlib.testrunner.TestIdentifier
import com.android.ddmlib.testrunner.TestResult
import com.android.ddmlib.testrunner.TestRunResult
import com.google.common.collect.ImmutableMap
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.Device
import com.shazam.fork.model.Pool
import com.shazam.fork.model.TestCaseEventFactory
import com.shazam.fork.runner.ProgressReporter
import com.shazam.fork.summary.TestResult.SUMMARY_KEY_TOTAL_FAILURE_COUNT
import com.shazam.fork.system.io.FileManager
import com.shazam.fork.system.io.FileType
import org.kxml2.io.KXmlSerializer
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class SingleForkXmlTestRunListener(val fileManager: FileManager,
                                   val pool: Pool,
                                   val device: Device,
                                   val testCase: TestTask,
                                   val progressReporter: ProgressReporter,
                                   val factory: TestCaseEventFactory,
                                   val output: File) : NoOpITestRunListener() {

    private lateinit var runResult: TestRunResult
    private var startTime: Long = 0L

    override fun testStarted(test: TestIdentifier) {
        runResult = TestRunResult()
        runResult.testStarted(test)
        startTime = System.currentTimeMillis()
    }

    override fun testAssumptionFailure(test: TestIdentifier, trace: String) {
        runResult.testAssumptionFailure(test, trace)
        generateDocument(runResult, test, elapsedTime())
    }

    override fun testFailed(test: TestIdentifier, trace: String) {
        runResult.testFailed(test, trace)
        generateDocument(runResult, test, elapsedTime())
    }

    override fun testEnded(test: TestIdentifier, testMetrics: MutableMap<String, String>) {
        runResult.testEnded(test, testMetrics)
        generateDocument(runResult, test, elapsedTime())
    }

    private fun elapsedTime() = System.currentTimeMillis() - startTime

    override fun testIgnored(test: TestIdentifier?) {
        runResult.testIgnored(test)
    }

    fun getResultFile(test: TestIdentifier): File {
        return fileManager.createFile(FileType.TEST, pool, device, test)
    }

    private fun getAbsoluteReportPath(): String {
        return output.absolutePath
    }

    private fun generateDocument(runResult: TestRunResult, test: TestIdentifier, elapsedTime: Long) {
        val timestamp = getTimestamp()

        createOutputResultStream(test).use {
            val serializer = KXmlSerializer()
            serializer.setOutput(it, SdkConstants.UTF_8)
            serializer.startDocument(SdkConstants.UTF_8, null)
            serializer.setFeature(
                    "http://xmlpull.org/v1/doc/features.html#indent-output", true)
            printTestResults(serializer, timestamp, elapsedTime, runResult, test)
            serializer.endDocument()
            val msg = String.format("XML test result file generated at %s. %s",
                    getAbsoluteReportPath(), runResult.textSummary)
            Log.logAndDisplay(Log.LogLevel.INFO, LOG_TAG, msg)
        }
    }

    private fun getTimestamp(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss",
                Locale.getDefault())
        val gmt = TimeZone.getTimeZone("UTC")
        dateFormat.timeZone = gmt
        dateFormat.isLenient = true
        return dateFormat.format(Date())
    }

    private fun createOutputResultStream(test: TestIdentifier): OutputStream {
        val reportFile = getResultFile(test)
        return BufferedOutputStream(FileOutputStream(reportFile))
    }

    private fun getTestSuiteName(): String? {
        return pool.name
    }

    private fun printTestResults(serializer: KXmlSerializer,
                                 timestamp: String,
                                 elapsedTime: Long,
                                 runResult: TestRunResult, test: TestIdentifier) {
        serializer.startTag(ns, TESTSUITE)
        val name = getTestSuiteName()
        if (name != null) {
            serializer.attribute(ns, ATTR_NAME, name)
        }
        serializer.attribute(ns, ATTR_TESTS, Integer.toString(runResult.numTests))
        serializer.attribute(ns, ATTR_FAILURES, Integer.toString(
                runResult.numAllFailedTests))
        // legacy - there are no errors in JUnit4
        serializer.attribute(ns, ATTR_ERRORS, "0")
        serializer.attribute(ns, ATTR_SKIPPED, Integer.toString(runResult.getNumTestsInState(
                TestResult.TestStatus.IGNORED)))

        serializer.attribute(ns, ATTR_TIME, java.lang.Double.toString(elapsedTime.toDouble() / 1000f))
        serializer.attribute(ns, TIMESTAMP, timestamp)
        serializer.attribute(ns, HOSTNAME, hostName)

        serializer.startTag(ns, PROPERTIES)
        for ((key, value) in getPropertiesAttributes(test)) {
            serializer.startTag(ns, PROPERTY)
            serializer.attribute(ns, "name", key)
            serializer.attribute(ns, "value", value)
            serializer.endTag(ns, PROPERTY)
        }
        serializer.endTag(ns, PROPERTIES)

        val testResults = runResult.testResults
        for (testEntry in testResults.entries) {
            print(serializer, testEntry.key, testEntry.value)
        }

        serializer.endTag(ns, TESTSUITE)
    }

    fun getPropertiesAttributes(test: TestIdentifier): Map<String, String> {
        val mapBuilder = ImmutableMap.builder<String, String>()

        val testFailuresCount = progressReporter.getTestFailuresCount(pool, factory.newTestCase(test))
        if (testFailuresCount > 0) {
            mapBuilder.put(SUMMARY_KEY_TOTAL_FAILURE_COUNT, Integer.toString(testFailuresCount))
        }

        when (testCase) {
            is TestTask.SingleTestTask -> mapBuilder.putAll(testCase.event.properties)
        }
        return mapBuilder.build()
    }

    private fun getTestName(testId: TestIdentifier): String {
        return testId.testName
    }

    fun print(serializer: KXmlSerializer, testId: TestIdentifier, testResult: TestResult) {

        serializer.startTag(ns, TESTCASE)
        serializer.attribute(ns, ATTR_NAME, getTestName(testId))
        serializer.attribute(ns, ATTR_CLASSNAME, testId.className)
        val elapsedTimeMs = testResult.endTime - testResult.startTime
        serializer.attribute(ns, ATTR_TIME, java.lang.Double.toString(elapsedTimeMs.toDouble() / 1000f))

        when (testResult.status) {
            TestResult.TestStatus.FAILURE -> printFailedTest(serializer, FAILURE, testResult.stackTrace)
            TestResult.TestStatus.ASSUMPTION_FAILURE -> printFailedTest(serializer, SKIPPED_TAG, testResult.stackTrace)
            TestResult.TestStatus.IGNORED -> {
                serializer.startTag(ns, SKIPPED_TAG)
                serializer.endTag(ns, SKIPPED_TAG)
            }
        }

        serializer.endTag(ns, TESTCASE)
    }

    private fun printFailedTest(serializer: KXmlSerializer, tag: String, stack: String) {
        serializer.startTag(ns, tag)
        serializer.text(sanitize(stack))
        serializer.endTag(ns, tag)
    }

    private fun sanitize(text: String): String {
        return text.replace("\u0000", "<\\0>")
    }

    companion object {

        private val LOG_TAG = "XmlResultReporter"

        private val TESTSUITE = "testsuite"
        private val TESTCASE = "testcase"
        private val FAILURE = "failure"
        private val SKIPPED_TAG = "skipped"
        private val ATTR_NAME = "name"
        private val ATTR_TIME = "time"
        private val ATTR_ERRORS = "errors"
        private val ATTR_FAILURES = "failures"
        private val ATTR_SKIPPED = "skipped"
        private val ATTR_TESTS = "tests"
        private val PROPERTIES = "properties"
        private val PROPERTY = "property"
        private val ATTR_CLASSNAME = "classname"
        private val TIMESTAMP = "timestamp"
        private val HOSTNAME = "hostname"
        private var hostName = "localhost"
        private val ns: String? = null
    }
}
