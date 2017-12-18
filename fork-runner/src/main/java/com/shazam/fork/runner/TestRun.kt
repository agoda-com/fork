/*
 * Copyright 2016 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.shazam.fork.runner

import com.android.ddmlib.AdbCommandRejectedException
import com.android.ddmlib.ShellCommandUnresponsiveException
import com.android.ddmlib.TimeoutException
import com.android.ddmlib.testrunner.ITestRunListener
import com.android.ddmlib.testrunner.RemoteAndroidTestRunner
import com.android.ddmlib.testrunner.TestIdentifier
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.system.PermissionGrantingManager
import com.shazam.fork.system.io.RemoteFileManager

import org.slf4j.LoggerFactory

import java.io.IOException

import java.lang.String.format
import java.util.*

internal class TestRun(private val poolName: String,
                       private val testRunParameters: TestRunParameters,
                       private val testRunListeners: List<ITestRunListener>,
                       private val permissionGrantingManager: PermissionGrantingManager) {

    val logger = LoggerFactory.getLogger(TestRun::class.java)

    fun execute() {
        val applicationPackage = testRunParameters.applicationPackage
        val device = testRunParameters.deviceInterface

        val runner = RemoteAndroidTestRunner(
                testRunParameters.testPackage,
                testRunParameters.testRunner,
                device)

        val test = testRunParameters.test

        runner.setRunName(poolName)
        val testSize = testRunParameters.testSize
        if (testSize != null) {
            logger.error("TestSize = $testSize")
            runner.setTestSize(testSize)
        }

        runner.setMaxtimeToOutputResponse(testRunParameters.testOutputTimeout)

        runner.apply {
            when (test) {
                is TestTask.SingleTestTask -> executeSingle(test, runner)
                is TestTask.MultiTestTask -> executeMultiple(test, runner)
            }
        }


        try {
            runner.run(testRunListeners)
        } catch (e: ShellCommandUnresponsiveException) {
            logger.warn("Test got stuck. You can increase the timeout in settings if it's too strict")
//            logger.warn("Test: $testClassName got stuck. You can increase the timeout in settings if it's too strict")
        } catch (e: TimeoutException) {
            logger.warn("Test got stuck. You can increase the timeout in settings if it's too strict")
//            logger.warn("Test: $testClassName got stuck. You can increase the timeout in settings if it's too strict")
        } catch (e: AdbCommandRejectedException) {
//            throw RuntimeException(format("Error while running test %s %s", test.getTestClass(), test.getTestMethod()), e)
            throw RuntimeException("Error while running test")
        } catch (e: IOException) {
            throw RuntimeException("Error while running test")
//            throw RuntimeException(format("Error while running test %s %s", test.getTestClass(), test.getTestMethod()), e)
        } finally {
//            permissionGrantingManager.restorePermissions(applicationPackage, device, permissionsToRevoke)
        }

    }

    private fun executeMultiple(test: TestTask.MultiTestTask, runner: RemoteAndroidTestRunner) {
        val classes = test.list.map {
            "${it.testClass}#${it.testMethod}"
        }.toTypedArray()

        if (testRunParameters.isCoverageEnabled) {
            runner.setCoverage(true)
            runner.addInstrumentationArg("coverageFile", "/sdcard/fork/coverage.ec")
        }

        logger.error("executeMultiple ${Arrays.toString(classes)}")
        runner.setClassNames(classes)
    }


    private fun executeSingle(test: TestTask.SingleTestTask, runner: RemoteAndroidTestRunner) {
        logger.error("executeSingle ${test.event.testClass}#${test.event.testMethod}")
        val testClassName = test.event.testClass
        val testMethodName = test.event.testMethod

        runner.setMethodName(testClassName, testMethodName)

        if (testRunParameters.isCoverageEnabled) {
            runner.setCoverage(true)
            runner.addInstrumentationArg("coverageFile", RemoteFileManager.getCoverageFileName(TestIdentifier(testClassName, testMethodName)))
        }

        val permissionsToRevoke = test.event.permissionsToRevoke
        val applicationPackage = testRunParameters.applicationPackage
        val device = testRunParameters.deviceInterface
        permissionGrantingManager.revokePermissions(applicationPackage, device, permissionsToRevoke)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(TestRun::class.java)
    }
}
