package com.shazam.fork.sorting;

import com.shazam.fork.model.TestCaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;

public class SortedTestQueue extends LinkedList<TestCaseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(SortedTestQueue.class);

    private static final TestComparator comparator = new TestComparator();

    @Override
    public boolean add(TestCaseEvent event) {
        boolean res = super.add(event);
        super.sort(comparator);
        return res;
    }

    @Override
    public boolean addAll(Collection<? extends TestCaseEvent> c) {
        boolean res = super.addAll(c);
        super.sort(comparator);
        return res;
    }

    @Override
    public TestCaseEvent poll() {
        TestCaseEvent event = super.poll();
        logger.error(event.toString());
        return event;
    }
}
