package com.shazam.fork.sorting;

import com.shazam.fork.model.Device;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TestExecutionReporter {

    private final ConcurrentHashMap<Device, List<TestExecution>> tests = new ConcurrentHashMap<>();
    private final AtomicBoolean finish = new AtomicBoolean(false);

    public void stop() {
        finish.compareAndSet(false, true);
    }

    public void add(Device device, TestExecution time) {
        if (!finish.get()) {
            tests.computeIfPresent(device, (testIdentifier, testExecutionTimes) -> {
                testExecutionTimes.add(time);
                return testExecutionTimes;
            });
            tests.computeIfAbsent(device, testIdentifier -> new ArrayList<>());
        }
    }

    public List<TestExecution> getTests(Device device) {
        return tests.get(device);
    }
}
