package com.shazam.fork.sorting;

import com.shazam.fork.model.TestCaseEvent;

import java.util.concurrent.PriorityBlockingQueue;

class SortedTestQueue extends PriorityBlockingQueue<TestCaseEvent> {
    private static final TestComparator comparator = new TestComparator();
    private static final int DEFAULT_INITIAL_CAPACITY = 100;

    SortedTestQueue() {
        super(DEFAULT_INITIAL_CAPACITY, comparator);
    }
}
