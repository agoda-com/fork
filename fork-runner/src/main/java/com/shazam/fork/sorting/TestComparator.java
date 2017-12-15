package com.shazam.fork.sorting;

import com.shazam.fork.model.TestCaseEvent;

import java.util.Comparator;

class TestComparator implements Comparator<TestCaseEvent> {
    private static Comparator<TestCaseEvent> getDefaultComparator() {
        return Comparator.<TestCaseEvent>comparingDouble(value ->
                Math.sqrt(value.getTestMetric().getVariance()) * 2 + value.getTestMetric().getExpectedValue()
        ).reversed();
    }

    @Override
    public int compare(TestCaseEvent o1, TestCaseEvent o2) {
        if (o1.getTestMetric().getVariance() == 0 || o1.getTestMetric().getExpectedValue() == 0) {
            return -1;
        } else if (o2.getTestMetric().getVariance() == 0 || o2.getTestMetric().getExpectedValue() == 0) {
            return 1;
        } else {
            return getDefaultComparator().compare(o1, o2);
        }
    }
}
