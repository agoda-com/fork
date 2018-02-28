package com.shazam.fork.runner;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DeviceExecutor {

    private final DeviceInternalExecuter executer;
    private final SynchronousQueue<Runnable> queue;
    private final ThreadFactory namedThreadFactory;

    private final Object stick = new Object();

    private final BiMap<String, Runnable> tasks = HashBiMap.create();
    private final BiMap<String, Thread> threads = HashBiMap.create();

    public DeviceExecutor(String nameFormat) {
        namedThreadFactory = new ThreadFactoryBuilder().setNameFormat(nameFormat).build();
        queue = new SynchronousQueue<>();

        executer = new DeviceInternalExecuter(0, 2147483647, 60L, TimeUnit.SECONDS, queue, namedThreadFactory);
    }

    public void execute(String key, Runnable task) {
        synchronized (stick) {
            if (tasks.containsKey(key)) {
                throw new IllegalStateException("Already have task for " + key);
            }
            tasks.put(key, task);
            executer.execute(task);
        }
    }

    public boolean isTaskInProgress(String key) {
        synchronized (stick) {
            return tasks.containsKey(key) && (!threads.containsKey(key) || threads.get(key).isAlive());
        }
    }

    public void shutdown() {
        executer.shutdown();
    }

    public int tasksCount() {
        synchronized (stick) {
            return tasks.size();
        }
    }

    private class DeviceInternalExecuter extends ThreadPoolExecutor {
        public DeviceInternalExecuter(int i, int i1, long l, TimeUnit timeUnit, BlockingQueue<Runnable> blockingQueue, ThreadFactory threadFactory) {
            super(i, i1, l, timeUnit, blockingQueue, threadFactory);
        }

        @Override protected void beforeExecute(Thread thread, Runnable runnable) {
            super.beforeExecute(thread, runnable);
            threads.put(tasks.inverse().get(runnable), thread);
        }

        @Override protected void afterExecute(Runnable runnable, Throwable throwable) {
            super.afterExecute(runnable, throwable);
            String key = tasks.inverse().get(runnable);
            tasks.remove(key);
            threads.remove(key);
        }
    }
}