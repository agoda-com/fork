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
package com.shazam.fork.runner

import com.android.ddmlib.*
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.Device
import com.shazam.fork.model.Pool
import com.shazam.fork.system.adb.Installer
import com.shazam.fork.system.io.RemoteFileManager.*
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.*
import java.util.concurrent.CountDownLatch

class DeviceTestRunner(private val installer: Installer,
                       private val pool: Pool,
                       private val device: Device,
                       private val queueOfTestsInPool: Queue<TestTask>,
                       private val deviceCountDownLatch: CountDownLatch,
                       private val progressReporter: ProgressReporter,
                       private val testRunFactory: TestRunFactory) : Runnable {

    companion object {
        private val logger = LoggerFactory.getLogger(DeviceTestRunner::class.java)
    }

    private var lastEvent = 0

    override fun run() {
        val deviceInterface = device.deviceInterface
        try {
            DdmPreferences.setTimeOut(30000)
            installer.prepareInstallation(deviceInterface)
            // For when previous run crashed/disconnected and left files behind
            removeRemoteDirectory(deviceInterface)
            createRemoteDirectory(deviceInterface)
            createCoverageDirectory(deviceInterface)
            clearLogcat(deviceInterface)

            while (queueOfTestsInPool.isNotEmpty()) {
                queueOfTestsInPool.poll()?.run {
                    if (hashCode() == lastEvent) {
                        val next = queueOfTestsInPool.poll()
                        if (next != null) {
                            queueOfTestsInPool.add(this)
                            runTest(next)
                        } else {
                            runTest(this)
                        }
                    } else {
                        runTest(this)
                    }
                }
            }

        } finally {
            logger.info("Device {} from pool {} finished", device.serial, pool.name)
            deviceCountDownLatch.countDown()
        }
    }

    private fun runTest(testTask: TestTask) {
        lastEvent = testTask.hashCode()
        testRunFactory.createTestRun(testTask,
                device,
                pool,
                progressReporter,
                queueOfTestsInPool).execute()
    }

    private fun clearLogcat(device: IDevice) {
        try {
            device.executeShellCommand("logcat -c", NullOutputReceiver())
        } catch (e: TimeoutException) {
            logger.warn("Could not clear logcat on device: ${device.serialNumber}", e)
        } catch (e: AdbCommandRejectedException) {
            logger.warn("Could not clear logcat on device: ${device.serialNumber}", e)
        } catch (e: ShellCommandUnresponsiveException) {
            logger.warn("Could not clear logcat on device: ${device.serialNumber}", e)
        } catch (e: IOException) {
            logger.warn("Could not clear logcat on device: ${device.serialNumber}", e)
        }
    }
}
