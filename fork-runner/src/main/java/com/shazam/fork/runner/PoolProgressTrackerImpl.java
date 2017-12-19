/*
 * Copyright 2015 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */

package com.shazam.fork.runner;

import java.util.concurrent.atomic.AtomicInteger;

public class PoolProgressTrackerImpl implements PoolProgressTracker {

    private final int totalTests;
    private AtomicInteger failedTests;
    private AtomicInteger completedTests;

    public PoolProgressTrackerImpl(int totalTests) {
        this.totalTests = totalTests;
        this.failedTests = new AtomicInteger(0);
        this.completedTests = new AtomicInteger(0);

    }

    @Override
    public void completedTest() {
        completedTests.incrementAndGet();
    }

    @Override
    public void failedTest() {
        failedTests.incrementAndGet();
    }

    @Override
    public void trackTestEnqueuedAgain() {
        completedTests.decrementAndGet();
        failedTests.decrementAndGet();
    }

    @Override
    public float getProgress() {
        return (float) completedTests.get() / (float) totalTests;
    }

    @Override
    public int getNumberOfFailedTests() {
        return failedTests.get();
    }
}
