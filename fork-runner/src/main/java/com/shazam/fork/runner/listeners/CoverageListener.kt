package com.shazam.fork.runner.listeners

import com.android.ddmlib.testrunner.TestIdentifier
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.*
import com.shazam.fork.system.io.FileManager
import com.shazam.fork.system.io.RemoteFileManager

import org.slf4j.LoggerFactory

import com.shazam.fork.system.io.FileType.COVERAGE

class CoverageListener(private val device: Device,
                       private val fileManager: FileManager,
                       private val pool: Pool,
                       private val testCase: TestTask) : NoOpITestRunListener() {
    private val logger = LoggerFactory.getLogger(CoverageListener::class.java)

    override fun testEnded(test: TestIdentifier, testMetrics: Map<String, String>) {
        if (testCase is TestTask.MultiTestTask) {
            saveCoverageFile("/sdcard/fork/coverage.ec", test)
        }
    }

    override fun testRunEnded(elapsedTime: Long, runMetrics: Map<String, String>) {
        when (testCase) {
            is TestTask.SingleTestTask -> {
                saveCoverageFile(testCase.event)
            }
        }
    }

    private fun saveCoverageFile(testCase: TestCaseEvent) {
        val testIdentifier = TestIdentifier(testCase.testClass, testCase.testMethod)

        val remoteFile = RemoteFileManager.getCoverageFileName(testIdentifier)
        val file = fileManager.createFile(COVERAGE, pool, device, testIdentifier)
        try {
            device.deviceInterface.pullFile(remoteFile, file.absolutePath)
        } catch (e: Exception) {
            logger.error("Something went wrong while pulling coverage file", e)
        }
    }

    private fun saveCoverageFile(path: String, test: TestIdentifier) {
        val file = fileManager.createFile(COVERAGE, pool, device, test)
        try {
            device.deviceInterface.pullFile(path, file.absolutePath)
        } catch (e: Exception) {
            logger.error("Something went wrong while pulling coverage file", e)
        }
    }
}
