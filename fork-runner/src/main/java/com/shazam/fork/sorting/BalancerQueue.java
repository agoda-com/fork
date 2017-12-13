package com.shazam.fork.sorting;

import com.agoda.fork.stat.TestHistory;
import com.agoda.fork.stat.TestMetric;
import com.shazam.fork.model.TestCaseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class BalancerQueue implements Queue<TestCaseEvent> {

    private static final Logger logger = LoggerFactory.getLogger(BalancerQueue.class);

    private LinkedList<BalancerTestCaseEvent> queue = new LinkedList<>();
    private final Map<String, TestMetric> testMetrics;

    private static final TestComparator comparator = new TestComparator();

    public BalancerQueue(Collection<TestCaseEvent> testCases, List<TestHistory> testHistories) {
        testMetrics = testHistories
                .stream()
                .collect(toMap(this::calculateTestKey, TestHistory::getTestMetric));
        this.addAll(testCases);
    }

    private String calculateTestKey(TestHistory history) {
        return calculateTestKey(history.getTestClass(), history.getTestMethod());
    }

    private String calculateTestKey(TestCaseEvent event) {
        return calculateTestKey(event.getTestClass(), event.getTestMethod());
    }

    private String calculateTestKey(String testClass, String testMethod) {
        return testClass + "#" + testMethod;
    }

    private BalancerTestCaseEvent convert(TestCaseEvent event) {
        TestMetric metric = testMetrics.getOrDefault(calculateTestKey(event), TestMetric.empty());
        return new BalancerTestCaseEvent(event, metric);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<TestCaseEvent> iterator() {
        return queue.stream().map(BalancerTestCaseEvent::getTestCaseEvent).iterator();
    }

    @Override
    public Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean add(TestCaseEvent testCaseEvent) {
        return queue.add(convert(testCaseEvent));
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends TestCaseEvent> c) {
        boolean res = queue.addAll(c.stream().map(this::convert).collect(Collectors.toList()));
        queue.sort(comparator);
        return res;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean offer(TestCaseEvent testCaseEvent) {
        return queue.offer(convert(testCaseEvent));
    }

    @Override
    public TestCaseEvent remove() {
        return Optional.ofNullable(queue.remove())
                .map(BalancerTestCaseEvent::getTestCaseEvent)
                .orElse(null);
    }

    @Override
    public TestCaseEvent poll() {
        return Optional.ofNullable(queue.poll())
                .map(a -> {
                    logger.info("Test name = " + calculateTestKey(a.getTestCaseEvent()) + " " + a.getMetric().toString());
                    return a.getTestCaseEvent();
                })
                .orElse(null);
    }

    @Override
    public TestCaseEvent element() {
        return Optional.ofNullable(queue.element())
                .map(BalancerTestCaseEvent::getTestCaseEvent)
                .orElse(null);
    }

    @Override
    public TestCaseEvent peek() {
        return Optional.ofNullable(queue.peek())
                .map(BalancerTestCaseEvent::getTestCaseEvent)
                .orElse(null);
    }
}
