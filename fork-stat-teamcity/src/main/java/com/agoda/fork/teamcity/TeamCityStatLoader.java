package com.agoda.fork.teamcity;

import com.agoda.fork.stat.*;
import com.agoda.fork.teamcity.entities.Build;
import com.agoda.fork.teamcity.entities.Test;
import com.agoda.fork.teamcity.entities.TestResult;
import io.reactivex.Single;
import io.reactivex.Observable;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class TeamCityStatLoader implements StatLoader {

    private final TeamCityService teamCityService;
    private final TeamCityConfig teamCityConfig;

    public TeamCityStatLoader(TeamCityService teamCityService, TeamCityConfig teamCityConfig) {
        this.teamCityService = teamCityService;
        this.teamCityConfig = teamCityConfig;
    }

    @Override
    public List<TestHistory> loadHistory() {
        return testHistories(teamCityConfig.getConfiguration());
    }

    private TestHistory createTestHistory(Test test) {
        String testClassName = test.getName().substring(test.getName().lastIndexOf(':') + 2, test.getName().lastIndexOf('.'));
        String testMethodName = test.getName().substring(test.getName().lastIndexOf('.') + 1);
        return new TestHistory(testClassName, testMethodName, new ArrayList<>(), TestMetric.empty());
    }

    private TestExecution parseTestExecution(Test test) {
        return new TestExecution(test.getDuration());
    }

    private TestHistory fillExpectedAndVariance(TestHistory testHistory) {
        List<Integer> ints =
                testHistory.getExecutions().stream().map(TestExecution::getDuration).collect(toList());
        double expectedValue = MathUtils.expectedValue(ints);
        double variance = MathUtils.variance(ints, expectedValue);
        return new TestHistory(testHistory.getTestClass(),
                testHistory.getTestMethod(), testHistory.getExecutions(), TestMetric.create(expectedValue, variance));
    }

    private List<TestHistory> testHistories(String configuration) {
        List<TestResult> results = requestTestResults(configuration);
        Map<String, TestHistory> historyMap = new HashMap<>();
        results.forEach(testResult -> parseResult(testResult, historyMap));

        return historyMap.values()
                .parallelStream()
                .map(this::fillExpectedAndVariance)
                .collect(toList());
    }

    private void parseResult(TestResult testResult, Map<String, TestHistory> historyMap) {
        List<Test> tests = testResult.getTestOccurrence();
        if (tests != null) {
            tests.forEach(test -> {
                historyMap.computeIfAbsent(test.getName(), s -> createTestHistory(test));
                historyMap.computeIfPresent(test.getName(), (s, testHistory) -> {
                    testHistory.getExecutions().add(parseTestExecution(test));
                    return testHistory;
                });
            });
        }
    }


    private List<TestResult> requestTestResults(String configuration) {
        return teamCityService.builds(configuration).retry(3)
                .flatMapObservable(builds1 -> Observable.fromIterable(builds1.getBuild()))
                .flatMapSingle(this::requestTestResult)
                .toList()
                .blockingGet();
    }

    private Single<TestResult> requestTestResult(Build build) {
        return teamCityService.tests("build:(id:" + build.getId() + "),status:SUCCESS,count:10000")
                .retry(3)
                .map(testResult -> new TestResult(
                        testResult.getCount(),
                        testResult.getHref(),
                        testResult.getTestOccurrence())
                );

    }
}
