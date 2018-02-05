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
package com.shazam.fork.runner;

import com.shazam.fork.batch.tasks.TestTask;
import com.shazam.fork.model.Pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Queue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class PoolTestRunner implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(PoolTestRunner.class);
    public static final String DROPPED_BY = "DroppedBy-";

    private static final long LOOKUP_TIMEOUT = TimeUnit.MINUTES.toMillis(10);
    private static final long UPDATE_TIMEOUT = TimeUnit.SECONDS.toMillis(20);
    private static final long MAX_ATTEMPTS = LOOKUP_TIMEOUT / UPDATE_TIMEOUT;

    private final Pool pool;
    private final Queue<TestTask> testCases;
    private final CountDownLatch poolCountDownLatch;
    private final DeviceTestRunnerFactory deviceTestRunnerFactory;
    private final ProgressReporter progressReporter;
    private final AtomicInteger runningCounter = new AtomicInteger(0);

    public PoolTestRunner(DeviceTestRunnerFactory deviceTestRunnerFactory, Pool pool,
                          Queue<TestTask> testCases,
                          CountDownLatch poolCountDownLatch,
                          ProgressReporter progressReporter) {
        this.pool = pool;
        this.testCases = testCases;
        this.poolCountDownLatch = poolCountDownLatch;
        this.deviceTestRunnerFactory = deviceTestRunnerFactory;
        this.progressReporter = progressReporter;
    }

    public void run() {
        String poolName = pool.getName();
        final DeviceExecutor concurrentDeviceExecutor = new DeviceExecutor("DeviceExecutor-%d");

        pool.setAppendCallback((device) -> {
            String serial = device.getSerial();
            if (!concurrentDeviceExecutor.isTaskInProgress(serial)) {
                logger.info("got new device {} - start thread", serial);
                Runnable deviceTestRunner = deviceTestRunnerFactory.createDeviceTestRunner(pool, testCases,
                        runningCounter,
                        device, progressReporter);
                concurrentDeviceExecutor.execute(device.getSerial(), deviceTestRunner);
            } else {
                logger.info("device {} already runned", serial);
            }

        });


        try {
            logger.info("Pool {} started", poolName);

            int waitCounter = 0;
            int lastSize = 0;
            while (!testCases.isEmpty() || concurrentDeviceExecutor.tasksCount() > 0) {
                logger.info("Pool {} has {} tests in queue and {} running tests", poolName, testCases.size(), concurrentDeviceExecutor.tasksCount());
                pool.update();
                if (testCases.size() == lastSize) {
                    if (waitCounter++ > MAX_ATTEMPTS) {
                        logger.info("Pool {} has no result during last {} seconds", poolName, LOOKUP_TIMEOUT / 1000);
                        throw new RuntimeException("Timeout for test running: pool "
                                                   + poolName + " has no result during last " +
                                                   LOOKUP_TIMEOUT / 1000 + " seconds");
                    }
                } else {
                    waitCounter = 0;
                    lastSize = testCases.size();
                }

                Thread.sleep(UPDATE_TIMEOUT);
            }

        } catch (InterruptedException e) {
            logger.warn("Pool {} was interrupted while running", poolName);

        } finally {
            concurrentDeviceExecutor.shutdown();
            logger.info("Pool {} finished", poolName);
            poolCountDownLatch.countDown();
            logger.info("Pools remaining: {}", poolCountDownLatch.getCount());
        }
    }
}
