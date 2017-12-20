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

import com.android.ddmlib.testrunner.ITestRunListener;
import com.google.gson.Gson;
import com.shazam.fork.Configuration;
import com.shazam.fork.batch.tasks.TestTask;
import com.shazam.fork.model.Device;
import com.shazam.fork.model.Pool;
import com.shazam.fork.model.TestCaseEventFactory;
import com.shazam.fork.runner.ProgressReporter;
import com.shazam.fork.stat.TestExecutionReporter;
import com.shazam.fork.system.io.FileManager;

import java.io.File;
import java.util.List;
import java.util.Queue;

import static com.shazam.fork.model.Diagnostics.SCREENSHOTS;
import static com.shazam.fork.model.Diagnostics.VIDEO;
import static java.util.Arrays.asList;

public class TestRunListenersFactory {

    private final Configuration configuration;
    private final FileManager fileManager;
    private final Gson gson;
    private final TestExecutionReporter testExecutionReporter;

    public TestRunListenersFactory(Configuration configuration,
                                   FileManager fileManager,
                                   Gson gson,
                                   TestExecutionReporter testExecutionReporter) {
        this.configuration = configuration;
        this.fileManager = fileManager;
        this.gson = gson;
        this.testExecutionReporter = testExecutionReporter;
    }

    public List<ITestRunListener> createTestListeners(TestTask testCase,
                                                      Device device,
                                                      Pool pool,
                                                      ProgressReporter progressReporter,
                                                      Queue<TestTask> testCaseEventQueue,
                                                      TestCaseEventFactory factory) {
        return asList(
                new ProgressTestRunListener(pool, progressReporter),
                getForkXmlTestRunListener(fileManager, configuration.getOutput(), pool, device, testCase, progressReporter, factory),
                new ConsoleLoggingTestRunListener(configuration.getTestPackage(), device.getSerial(),
                        device.getModelName(), progressReporter),
                new LogCatTestRunListener(gson, fileManager, pool, device),
                new SlowWarningTestRunListener(),
                new TestExecutionListener(device, testExecutionReporter),
                getScreenTraceTestRunListener(fileManager, pool, device),
                new RetryListener(pool, device, testCaseEventQueue, progressReporter, testCase, fileManager, factory),
                getCoverageTestRunListener(configuration, device, fileManager, pool, testCase));
    }


    private SingleForkXmlTestRunListener getForkXmlTestRunListener(FileManager fileManager,
                                                                   File output,
                                                                   Pool pool,
                                                                   Device device,
                                                                   TestTask testCase,
                                                                   ProgressReporter progressReporter,
                                                                   TestCaseEventFactory factory) {
        SingleForkXmlTestRunListener xmlTestRunListener = new SingleForkXmlTestRunListener(fileManager,
                pool,
                device,
                testCase,
                progressReporter,
                factory,
                output);
        return xmlTestRunListener;
    }

    private ITestRunListener getCoverageTestRunListener(Configuration configuration,
                                                        Device device,
                                                        FileManager fileManager,
                                                        Pool pool,
                                                        TestTask testCase) {
        if (configuration.isCoverageEnabled()) {
            return new CoverageListener(device, fileManager, pool, testCase);
        }
        return new NoOpITestRunListener();
    }

    private ITestRunListener getScreenTraceTestRunListener(FileManager fileManager, Pool pool, Device device) {
        if (VIDEO.equals(device.getSupportedDiagnostics())) {
            return new ScreenRecorderTestRunListener(fileManager, pool, device);
        }

        if (SCREENSHOTS.equals(device.getSupportedDiagnostics()) && configuration.canFallbackToScreenshots()) {
            return new ScreenCaptureTestRunListener(fileManager, pool, device);
        }

        return new NoOpITestRunListener();
    }
}
