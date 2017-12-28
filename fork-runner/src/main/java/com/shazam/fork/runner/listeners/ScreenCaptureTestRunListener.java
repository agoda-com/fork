/*
 * Copyright 2015 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.shazam.fork.runner.listeners;

import com.android.ddmlib.IDevice;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.shazam.fork.model.Device;
import com.shazam.fork.model.Pool;
import com.shazam.fork.system.io.FileManager;

import java.util.Map;

class ScreenCaptureTestRunListener extends NoOpITestRunListener {
    private final FileManager fileManager;
    private final IDevice deviceInterface;
    private final Pool pool;
    private final Device device;

    private ScreenCapturer screenCapturer;
    private boolean hasFailed;

    public ScreenCaptureTestRunListener(FileManager fileManager, Pool pool, Device device) {
        this.fileManager = fileManager;
        this.deviceInterface = device.getDeviceInterface();
        this.pool = pool;
        this.device = device;
    }

    @Override
    public void testStarted(TestIdentifier test) {
        hasFailed = false;
        screenCapturer = new ScreenCapturer(deviceInterface, fileManager, pool, device, test);
        new Thread(screenCapturer, "ScreenCapturer").start();
    }

    @Override
    public void testFailed(TestIdentifier test, String trace) {
        hasFailed = true;
    }

    @Override
    public void testAssumptionFailure(TestIdentifier test, String trace) {
        screenCapturer.stopCapturing(hasFailed);
    }

    @Override
    public void testIgnored(TestIdentifier test) {
        screenCapturer.stopCapturing(hasFailed);
    }

    @Override
    public void testEnded(TestIdentifier test, Map<String, String> testMetrics) {
        screenCapturer.stopCapturing(hasFailed);
    }
}
