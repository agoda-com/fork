package com.shazam.fork.batch.strategies

import com.shazam.fork.batch.BatchFactoryStrategy
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent

class SplitFactoryStrategy(private val count: Int) : BatchFactoryStrategy {
    override fun batches(poolSize: Int, input: Collection<TestCaseEvent>): List<TestTask> {
        return input.chunked(count).map { TestTask.MultiTestTask(it) }
    }
}
