/*
 * Copyright 2014 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.shazam.fork.runner.listeners

import com.android.ddmlib.testrunner.TestIdentifier
import com.android.ddmlib.testrunner.XmlTestRunListener
import com.google.common.collect.ImmutableMap
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.Device
import com.shazam.fork.model.Pool
import com.shazam.fork.model.TestCaseEvent
import com.shazam.fork.model.TestCaseEventFactory
import com.shazam.fork.runner.ProgressReporter
import com.shazam.fork.system.io.FileManager
import com.shazam.fork.system.io.FileType

import java.io.File

import com.shazam.fork.summary.TestResult.SUMMARY_KEY_TOTAL_FAILURE_COUNT

class ForkXmlTestRunListener(private val fileManager: FileManager,
                             private val pool: Pool,
                             private val device: Device,
                             private val testCase: TestTask,
                             private val progressReporter: ProgressReporter,
                             private val factory: TestCaseEventFactory) : XmlTestRunListener() {
    private var test: TestIdentifier? = null

    override fun getResultFile(reportDir: File): File {
        return when (testCase) {
            is TestTask.SingleTestTask -> fileManager.createFile(FileType.TEST, pool, device, testCase.event)
            is TestTask.MultiTestTask -> fileManager.createFile(FileType.TEST, pool, device, testCase.list.hashCode().toString())
        }
    }

    override fun testStarted(test: TestIdentifier) {
        this.test = test
        super.testStarted(test)
    }

    override fun getPropertiesAttributes(): Map<String, String> {
        val mapBuilder = ImmutableMap.builder<String, String>()
                .putAll(super.getPropertiesAttributes())
        if (test != null) {
            val testFailuresCount = progressReporter.getTestFailuresCount(pool, factory.newTestCase(test!!))
            if (testFailuresCount > 0) {
                mapBuilder.put(SUMMARY_KEY_TOTAL_FAILURE_COUNT, Integer.toString(testFailuresCount))
            }
        }
        //        if (testCase != null) {
        //            mapBuilder.putAll(testCase.getProperties());
        //        }
        return mapBuilder.build()
    }
}
