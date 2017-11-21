package com.shazam.fork.sorting;

import java.util.Comparator;

class TestComparator implements Comparator<BalancerTestCaseEvent>{
    private static Comparator<BalancerTestCaseEvent> getDefaultComparator() {
        return Comparator.<BalancerTestCaseEvent>comparingDouble(value ->
                Math.sqrt(value.getMetric().getVariance()) * 2 + value.getMetric().getExpectedValue()
        ).reversed();
    }

    @Override
    public int compare(BalancerTestCaseEvent o1, BalancerTestCaseEvent o2) {
        if (o1.getMetric().getVariance() == 0 || o1.getMetric().getExpectedValue() == 0) {
            return -1;
        } else if (o2.getMetric().getVariance() == 0 || o2.getMetric().getExpectedValue() == 0) {
            return 1;
        } else {
            return getDefaultComparator().compare(o1, o2);
        }
    }
}
