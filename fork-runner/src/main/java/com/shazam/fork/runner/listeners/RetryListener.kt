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

class RetryListener(private val pool: Pool,
                    private val device: Device,
                    private val queueOfTestsInPool: Queue<TestTask>,
                    private val progressReporter: ProgressReporter,
                    private val testTask: TestTask,
                    private val fileManager: FileManager,
                    private val testCaseEventFactory: TestCaseEventFactory) : NoOpITestRunListener() {

    private val logger = LoggerFactory.getLogger(RetryListener::class.java)

    private val testResults = TestRunResult()

    override fun testStarted(test: TestIdentifier) {
        testResults.testStarted(test)
    }

    override fun testFailed(test: TestIdentifier, trace: String) {
        testResults.testFailed(test, trace)
    }

    override fun testAssumptionFailure(test: TestIdentifier, trace: String) {
        testResults.testAssumptionFailure(test, trace)
    }

    override fun testRunStarted(runName: String, testCount: Int) {
        testResults.testRunStarted(runName, testCount)
    }

    override fun testRunStopped(elapsedTime: Long) {
        testResults.testRunStopped(elapsedTime)
    }

    override fun testIgnored(test: TestIdentifier) {
        testResults.testIgnored(test)
    }

    override fun testEnded(test: TestIdentifier, testMetrics: MutableMap<String, String>) {
        testResults.testEnded(test, testMetrics)
    }

    override fun testRunFailed(errorMessage: String) {
        testResults.testRunFailed(errorMessage)
    }

    override fun testRunEnded(elapsedTime: Long, runMetrics: Map<String, String>) {
        restartFailedTests()
    }

    private fun restartFailedTests() {
        val allFinished = testResults.testResults.all { isSuccessful(it.value) }
        when (testTask) {
            is TestTask.SingleTestTask -> {
                if (!allFinished) {
                    restartFailedTest(testTask.event)
                }
            }
            is TestTask.MultiTestTask -> {
                if (!allFinished || testResults.testResults.size != testTask.list.size) {
                    restartMultiTestTask(testTask)
                }
            }
        }
    }

    private fun TestCaseEvent.identifier(): TestIdentifier {
        return TestIdentifier(testClass, testMethod)
    }

    private fun restartMultiTestTask(testTask: TestTask.MultiTestTask) {
        val tests = testTask.list
        val results = testResults.testResults

        val notExecuted = tests.filterNot { results.containsKey(it.identifier()) }

        val failed = results.filterValues { !isSuccessful(it) }

        failed.keys.map {
            testCaseEventFactory.newTestCase(it)
        }.forEach { restartFailedTest(it) }

        notExecuted.map {
            TestTask.SingleTestTask(it)
        }.forEach {
            queueOfTestsInPool.add(it)
        }
    }

    private fun isSuccessful(it: TestResult) =
            it.status == TestResult.TestStatus.PASSED || it.status == TestResult.TestStatus.IGNORED

    private fun restartFailedTest(event: TestCaseEvent) {
        val test = TestIdentifier(event.testClass, event.testMethod)
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
        val deleted = file.delete()
        if (!deleted) {
            logger.warn("Failed to remove file  " + file.absoluteFile + " for a failed but enqueued again test")
        }
    }
}
