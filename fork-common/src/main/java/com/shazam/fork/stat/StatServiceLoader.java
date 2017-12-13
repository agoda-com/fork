package com.shazam.fork.stat;

import com.agoda.fork.sorting.StatLoader;
import com.agoda.fork.sorting.TestHistory;

import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StatServiceLoader {
    public List<TestHistory> load() {
        return StreamSupport.stream(ServiceLoader.load(StatLoader.class).spliterator(), true)
                .flatMap(a -> a.loadHistory().stream())
                .collect(Collectors.toList());
    }
}
