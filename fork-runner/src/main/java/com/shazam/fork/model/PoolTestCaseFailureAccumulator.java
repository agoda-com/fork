package com.shazam.fork.model;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.collect.SetMultimap;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class that keeps track of the number of times each testCase is executed for device.
 */
public class PoolTestCaseFailureAccumulator implements PoolTestCaseAccumulator {

    private SetMultimap<Pool, TestCaseEventCounter> map = Multimaps.synchronizedSetMultimap(HashMultimap.<Pool, TestCaseEventCounter>create());

    @Override
    public void record(Pool pool, TestCaseEvent testCaseEvent) {
        if (!map.containsKey(pool)) {
            map.put(pool, createNew(testCaseEvent));
        }

        if (map.get(pool).stream().noneMatch(isSameTestCase(testCaseEvent))) {
            map.get(pool).add(
                    createNew(testCaseEvent)
                            .withIncreasedCount());
        } else {
            map.get(pool).stream().filter(isSameTestCase(testCaseEvent)).findFirst().get()
                    .increaseCount();
        }
    }

    @Override
    public int getCount(Pool pool, TestCaseEvent testCaseEvent) {
        if (map.containsKey(pool)) {
            return map.get(pool).stream().filter(isSameTestCase(testCaseEvent)).findFirst().orElse(TestCaseEventCounter.EMPTY)
                    .getCount();
        } else {
            return 0;
        }
    }

    @Override
    public int getCount(TestCaseEvent testCaseEvent) {
        int result = 0;
        List<TestCaseEventCounter> counters = map.values().stream().filter(isSameTestCase(testCaseEvent)).collect(Collectors.toList());
        for (TestCaseEventCounter counter : counters) {
            result += counter.getCount();
        }
        return result;
    }

    private static TestCaseEventCounter createNew(final TestCaseEvent testCaseEvent) {
        return new TestCaseEventCounter(testCaseEvent, 0);
    }

    private static Predicate<TestCaseEventCounter> isSameTestCase(final TestCaseEvent testCaseEvent) {
        return input -> input.getTestCaseEvent() != null
                && testCaseEvent.equals(input.getTestCaseEvent());
    }
}
