package com.shazam.fork.runner.listeners

import com.android.ddmlib.testrunner.TestIdentifier
import com.android.ddmlib.testrunner.TestResult
import com.android.ddmlib.testrunner.TestRunResult
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.Device
import com.shazam.fork.model.Pool
import com.shazam.fork.model.TestCaseEvent
import com.shazam.fork.model.TestCaseEventFactory
import com.shazam.fork.runner.ProgressReporter
import com.shazam.fork.system.io.FileManager
import com.shazam.fork.system.io.FileType
import org.ietf.jgss.GSSException.FAILURE
import org.slf4j.LoggerFactory
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.log

class RetryListener(private val pool: Pool, private val device: Device,
                    private val queueOfTestsInPool: Queue<TestTask>,
                    private val progressReporter: ProgressReporter,
                    private val testTask: TestTask,
                    private val fileManager: FileManager,
                    private val testCaseEventFactory: TestCaseEventFactory) : NoOpITestRunListener() {

    private val logger = LoggerFactory.getLogger(RetryListener::class.java)


    private val testResults = TestRunResult()

    override fun testStarted(test: TestIdentifier?) {
        testResults.testStarted(test)
    }

    override fun testFailed(test: TestIdentifier?, trace: String?) {
        testResults.testFailed(test, trace)
    }

    override fun testAssumptionFailure(test: TestIdentifier?, trace: String?) {
        testResults.testAssumptionFailure(test, trace)
    }

    override fun testIgnored(test: TestIdentifier?) {
        testResults.testIgnored(test)
    }

    override fun testEnded(test: TestIdentifier?, testMetrics: MutableMap<String, String>?) {
        testResults.testEnded(test, testMetrics)
    }

    override fun testRunFailed(errorMessage: String) {
        testResults.testRunFailed(errorMessage)
        logger.error("testRunFailed")
    }

    override fun testRunEnded(elapsedTime: Long, runMetrics: Map<String, String>) {
        super.testRunEnded(elapsedTime, runMetrics)
        logger.error("testRunStopped")
        restartFailedTests()
    }

    private fun restartFailedTests() {
        val failed = listOf(TestResult.TestStatus.FAILURE, TestResult.TestStatus.INCOMPLETE, TestResult.TestStatus.ASSUMPTION_FAILURE)
        if (testResults.testResults.any { failed.contains(it.value.status) }) {
            when (testTask) {
                is TestTask.SingleTestTask -> {
                    restartFailedTest(testTask.event)
                }
                is TestTask.MultiTestTask -> {
                    testResults.testResults.filterValues {
                        failed.contains(it.status)
                    }.keys.map {
                        logger.error("$it event is failed")
                        testCaseEventFactory.newTestCase(it)
                    }.forEach {
                        restartFailedTest(it)
                    }
                }
            }
        }
    }

    private fun restartFailedTest(event: TestCaseEvent) {
        val test = TestIdentifier(event.testClass, event.testMethod)
        logger.error("restartFailedTest $test")

        progressReporter.recordFailedTestCase(pool, event)

        if (progressReporter.requestRetry(pool, event)) {
            queueOfTestsInPool.add(TestTask.SingleTestTask(event))
            logger.info("Test " + test.toString() + " enqueued again into pool:" + pool.name)
            removeFailureTraceFiles(test)
        } else {
            logger.info("Test " + test.toString() + " failed on device " + device.safeSerial + " but retry is not allowed.")
        }
    }


    private fun removeFailureTraceFiles(test: TestIdentifier) {
        val file = fileManager.getFile(FileType.TEST, pool.name, device.safeSerial, test)
        logger.error("removeFailureTraceFiles ${file.absolutePath} exists = ${file.exists()}")
        val deleted = file.delete()
        if (!deleted) {
            logger.warn("Failed to remove file  " + file.absoluteFile + " for a failed but enqueued again test")
        }
    }
}
