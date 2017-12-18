package com.shazam.fork.runner.listeners;

import com.android.ddmlib.testrunner.ITestRunListener;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.shazam.fork.model.Device;
import com.shazam.fork.stat.TestExecution;
import com.shazam.fork.stat.TestExecutionReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static java.lang.System.currentTimeMillis;

public class TestExecutionListener implements ITestRunListener {

    Logger logger = LoggerFactory.getLogger(TestExecutionListener.class);

    private long startTime = 0;

    private final Device device;
    private final TestExecutionReporter executionReporter;

    private boolean failed;

    TestExecutionListener(Device device,
                          TestExecutionReporter executionReporter) {
        this.device = device;
        this.executionReporter = executionReporter;
    }

    @Override
    public void testRunStarted(String runName, int testCount) {
        logger.error("testRunStarted = " + runName + " testCount = " + testCount);
    }

    @Override
    public void testStarted(TestIdentifier test) {
        startTime = currentTimeMillis();
        failed = false;
        logger.error("testStarted= " + test.toString());
    }

    @Override
    public void testFailed(TestIdentifier test, String trace) {
        failed = true;
    }

    @Override
    public void testAssumptionFailure(TestIdentifier test, String trace) {
    }

    @Override
    public void testIgnored(TestIdentifier test) {
    }

    @Override
    public void testEnded(TestIdentifier test, Map<String, String> testMetrics) {
        long endedAfter = currentTimeMillis() - startTime;
        TestExecution execution = new TestExecution(test,
                startTime,
                endedAfter,
                failed ? TestExecution.Status.FAILED : TestExecution.Status.ENDED);
        logger.error("testEnded = " + test.toString());
        executionReporter.add(device, execution);
    }

    @Override
    public void testRunFailed(String errorMessage) {
    }

    @Override
    public void testRunStopped(long elapsedTime) {
    }

    @Override
    public void testRunEnded(long elapsedTime, Map<String, String> runMetrics) {
        logger.error("testEnded");
    }
}
