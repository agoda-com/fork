package com.agoda.fork.stat;

import java.io.FileNotFoundException;

public interface StatLoaderProvider {
    StatLoader create(String path) throws FileNotFoundException;
}
