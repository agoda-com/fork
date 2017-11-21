package com.shazam.fork.summary.executiontimeline;

import com.agoda.fork.sorting.TestHistory;
import com.agoda.fork.sorting.TestMetric;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.shazam.fork.model.Device;
import com.shazam.fork.sorting.TestExecution;
import com.shazam.fork.sorting.TestExecutionReporter;
import com.shazam.fork.sorting.TestHistoryManager;
import com.shazam.fork.summary.PoolSummary;
import com.shazam.fork.summary.Summary;
import com.shazam.fork.summary.TestResult;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class StatSummarySerializer {

    private final TestExecutionReporter reporter;
    private final TestHistoryManager testHistoryManager;

    public StatSummarySerializer(TestExecutionReporter reporter, TestHistoryManager testHistoryManager) {
        this.reporter = reporter;
        this.testHistoryManager = testHistoryManager;
    }


    private String prepareTestName(String fullTestName) {
        return fullTestName.substring(fullTestName.lastIndexOf('.') + 1);
    }

    private Stream<Data> parseData(Device device) {
        List<TestExecution> executions = reporter.getTests(device);
        return executions.stream().map(this::convertToData);
    }

    private Data convertToData(TestExecution execution) {
        int status = convertStatus(execution);
        String preparedTestName = prepareTestName(execution.getTest().toString());
        TestMetric testMetric = getTestMetric(execution);
        return createData(execution, status, preparedTestName, testMetric);
    }

    private int convertStatus(TestExecution execution) {
        return execution.getStatus() == TestExecution.Status.ENDED ? 1 : 0;
    }

    private Data createData(TestExecution execution, int status, String preparedTestName, TestMetric testMetric) {
        return new Data(preparedTestName, status,
                new Date(execution.getStartTime()),
                new Date(execution.getStartTime() + execution.getEndTime()),
                testMetric.getExpectedValue(), testMetric.getVariance());
    }

    private TestMetric getTestMetric(TestExecution execution) {
        return Optional
                .ofNullable(testHistoryManager.getTestHistory(execution.getTest()))
                .map(TestHistory::getTestMetric)
                .orElse(new TestMetric(0.0, 0.0));
    }

    private Stream<Measure> parsePoolSummary(PoolSummary poolSummary) {
        return poolSummary.getTestResults()
                .stream()
                .map(TestResult::getDevice)
                .distinct()
                .map(device -> new Measure(device.getSerial(), parseData(device).collect(toList())));
    }


    private List<Measure> parseList(List<PoolSummary> poolSummaries) {
        return poolSummaries.stream().flatMap(this::parsePoolSummary).collect(toList());
    }

    private Stream<TestIdentifier> extractIdentifiers(PoolSummary summary) {
        return summary.getTestResults()
                .stream()
                .map(result -> new TestIdentifier(result.getTestClass(), result.getTestMethod()));
    }

    private int passedTestCount(Summary summary) {
        return (int) summary.getPoolSummaries()
                .stream()
                .flatMap(this::extractIdentifiers)
                .distinct()
                .count();
    }

    public ExecutionResult parse(Summary summary) {
        int failedTests = summary.getFailedTests().size();
        int ignoredTests = summary.getIgnoredTests().size();
        int passedTestCount = passedTestCount(summary) - ignoredTests - failedTests;
        return new ExecutionResult(passedTestCount, failedTests, parseList(summary.getPoolSummaries()));
    }
}
