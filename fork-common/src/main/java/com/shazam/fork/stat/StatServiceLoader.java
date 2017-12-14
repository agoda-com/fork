package com.shazam.fork.stat;

import com.agoda.fork.stat.StatLoader;
import com.agoda.fork.stat.StatLoaderProvider;
import com.agoda.fork.stat.TestHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class StatServiceLoader {

    private static final Logger logger = LoggerFactory.getLogger(StatServiceLoader.class);

    private final String path;

    public StatServiceLoader(String path) {
        this.path = path;
    }

    public List<TestHistory> load() {
        logger.info("StatServiceLoader.load");
        return StreamSupport.stream(ServiceLoader.load(StatLoaderProvider.class).spliterator(), true)
                .flatMap(loader -> {
                    List<TestHistory> histories = new ArrayList<>();
                    try {
                        histories.addAll(loader.create(path).loadHistory());
                        logger.info("histories.size = " + histories.size());
                    } catch (FileNotFoundException e) {
                        logger.error("can't load history", e);
                    }
                    return histories.stream();
                })
                .collect(Collectors.toList());
    }
}
