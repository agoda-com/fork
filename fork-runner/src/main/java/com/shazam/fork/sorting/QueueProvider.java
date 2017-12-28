package com.shazam.fork.sorting;

import com.shazam.fork.Configuration;
import com.shazam.fork.model.TestCaseEvent;

import java.util.LinkedList;
import java.util.Queue;

public class QueueProvider {

    private final Configuration configuration;

    public QueueProvider(Configuration configuration) {
        this.configuration = configuration;
    }

    public Queue<TestCaseEvent> create() {
        if (configuration.getSortingStrategy().statistics != null) {
            return new SortedTestQueue();
        }
        return new LinkedList<>();
    }
}
