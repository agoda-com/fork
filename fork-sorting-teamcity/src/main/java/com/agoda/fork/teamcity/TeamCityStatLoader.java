package com.agoda.fork.teamcity;

import com.agoda.fork.sorting.*;
import com.agoda.fork.teamcity.entities.Build;
import com.agoda.fork.teamcity.entities.Test;
import com.agoda.fork.teamcity.entities.TestResult;
import io.reactivex.Single;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

import java.util.*;

import static java.util.stream.Collectors.toList;

public class TeamCityStatLoader implements StatLoader {

    private final TeamCityService teamCityService;

    public TeamCityStatLoader(TeamCityService teamCityService) {
        this.teamCityService = teamCityService;
    }

    @Override
    public List<TestHistory> loadHistory() {
        throw new UnsupportedOperationException();
//        String configuration = "";
//        return testHistories(configuration);
    }

    private TestHistory createTestHistory(Test test) {
        String testClassName = test.getName().substring(test.getName().lastIndexOf(':') + 2, test.getName().lastIndexOf('.'));
        String testMethodName = test.getName().substring(test.getName().lastIndexOf('.') + 1);
        return new TestHistory(testClassName, testMethodName, new ArrayList<>(), TestMetric.empty());
    }

    private TestExecution parseTestExecution(TestResult testResult, Test test) {
        int retryCount = testResult.getRetries().size();
        final int duration;
        if (retryCount > 0) {
            duration = test.getDuration() / retryCount;
        } else {
            duration = test.getDuration();
        }
        return new TestExecution(duration, retryCount);
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
                    testHistory.getExecutions().add(parseTestExecution(testResult, test));
                    return testHistory;
                });
            });
        }
    }


    private List<TestResult> requestTestResults(String configuration) {
        return teamCityService.builds(configuration).retry(3)
                .flatMapObservable(builds1 -> Observable.fromIterable(builds1.getBuild()))
                .flatMapSingle(build -> requestTestResult(build, configuration))
                .toList()
                .blockingGet();
    }

    private String extractFormattedTestName(String string) {
        String[] strs = string.split("/");
        return strs[strs.length - 1];
    }

    private List<String> parseRetries(String responseBody) {
        return Arrays.stream(responseBody.split("[\\r\\n]+"))
                .filter(s -> !s.trim().isEmpty())
                .map(this::extractFormattedTestName)
                .collect(toList());
    }

    private Single<TestResult> requestTestResult(Build build, String configuration) {
        Single<String> artifact = teamCityService.artifactContent(configuration, build.getId(), "screenshotEspressoTesting/RetryTest.txt")
                .retry(3)
                .map(ResponseBody::string)
                .onErrorReturnItem("");
        Single<TestResult> tests = teamCityService.tests("build:(id:" + build.getId() + "),status:SUCCESS,count:10000")
                .retry(3);

        return Single.zip(artifact, tests,
                (response, testResult) -> new TestResult(testResult.getCount(), testResult.getHref(),
                        testResult.getTestOccurrence(), parseRetries(response)));
    }
}
