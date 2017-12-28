package com.shazam.fork.batch.strategies

import com.google.common.collect.Lists
import com.shazam.fork.batch.BatchFactoryStrategy
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent

class SplitFactoryStrategy(private val count: Int) : BatchFactoryStrategy {
    override fun batches(poolSize: Int, input: Collection<TestCaseEvent>): List<TestTask> {
        return Lists.partition(input.toList(),count).map { TestTask.MultiTestTask(it) }
    }
}
