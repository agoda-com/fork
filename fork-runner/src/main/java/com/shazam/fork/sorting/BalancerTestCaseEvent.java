package com.shazam.fork.sorting;

import com.agoda.fork.stat.TestMetric;
import com.shazam.fork.model.TestCaseEvent;

class BalancerTestCaseEvent {
    private final TestCaseEvent testCaseEvent;
    private final TestMetric metric;

    BalancerTestCaseEvent(TestCaseEvent testCaseEvent, TestMetric testMetric) {
        this.testCaseEvent = testCaseEvent;
        this.metric = testMetric;
    }

    TestCaseEvent getTestCaseEvent() {
        return testCaseEvent;
    }

    TestMetric getMetric() {
        return metric;
    }
}
