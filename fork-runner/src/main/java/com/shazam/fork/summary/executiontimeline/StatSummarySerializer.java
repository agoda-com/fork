package com.shazam.fork.summary.executiontimeline;

import com.agoda.fork.stat.TestHistory;
import com.agoda.fork.stat.TestMetric;
import com.android.ddmlib.testrunner.TestIdentifier;
import com.shazam.fork.model.Device;
import com.shazam.fork.stat.TestExecution;
import com.shazam.fork.stat.TestExecutionReporter;
import com.shazam.fork.stat.TestHistoryManager;
import com.shazam.fork.stat.TestStatsLoader;
import com.shazam.fork.summary.PoolSummary;
import com.shazam.fork.summary.Summary;
import com.shazam.fork.summary.TestResult;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class StatSummarySerializer {

    private final TestExecutionReporter reporter;
    private final TestStatsLoader testStatsLoader;

    public StatSummarySerializer(TestExecutionReporter reporter,
                                 TestStatsLoader testStatsLoader) {
        this.reporter = reporter;
        this.testStatsLoader = testStatsLoader;
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
                execution.getStartTime(),
                execution.getStartTime() + execution.getEndTime(),
                testMetric.getExpectedValue(), testMetric.getVariance());
    }

    private TestMetric getTestMetric(TestExecution execution) {
        return testStatsLoader.findMetric(execution.getTest());
    }

    private ExecutionStats calculateExecutionStats(List<Data> data) {

        return new ExecutionStats(calculateIdle(data), calculateAverageExecutionTime(data));
    }

    private long calculateAverageExecutionTime(List<Data> data) {
        return (long) data.stream()
                .mapToLong(this::calculateDuration)
                .average()
                .orElse(0.0);
    }

    private long calculateDuration(Data a) {
        return a.getEndDate() - a.getStartDate();
    }

    private Long calculateIdle(List<Data> data) {
        if (data.size() == 0) return 0L;
        long time = 0;
        for (int i = 1; i < data.size(); i++) {
            time += data.get(i).getStartDate() - data.get(i - 1).getEndDate();
        }
        return time;
    }

    private Measure createMeasure(Device device) {
        List<Data> data = parseData(device).collect(toList());
        return new Measure(device.getSerial(), calculateExecutionStats(data), data);
    }

    private Stream<Measure> parsePoolSummary(PoolSummary poolSummary) {
        return poolSummary.getTestResults()
                .stream()
                .map(TestResult::getDevice)
                .distinct()
                .map(this::createMeasure);
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

    private ExecutionStats aggregateExecutionStats(List<Measure> list) {
        long summaryIdle = 0;
        long avgTestExecutionTime = 0;
        for (Measure measure : list) {
            summaryIdle += measure.getExecutionStats().getIdleTimeMillis();
            avgTestExecutionTime += measure.getExecutionStats().getAverageTestExecutionTimeMillis();
        }
        int size = list.size();
        //Temp workaround for Parameterized tests
        if (size != 0) {
            avgTestExecutionTime = avgTestExecutionTime / size;
        }
        return new ExecutionStats(summaryIdle, avgTestExecutionTime);
    }

    public ExecutionResult parse(Summary summary) {
        int failedTests = summary.getFailedTests().size();
        int ignoredTests = summary.getIgnoredTests().size();
        int passedTestCount = passedTestCount(summary) - ignoredTests - failedTests;
        List<Measure> measures = parseList(summary.getPoolSummaries());
        return new ExecutionResult(passedTestCount, failedTests, aggregateExecutionStats(measures), measures);
    }
}
