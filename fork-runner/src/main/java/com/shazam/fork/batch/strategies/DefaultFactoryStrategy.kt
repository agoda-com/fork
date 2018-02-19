package com.shazam.fork.batch.strategies

import com.shazam.fork.batch.BatchFactoryStrategy
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent

class DefaultFactoryStrategy : BatchFactoryStrategy {
    override fun batches(input: Collection<TestCaseEvent>): List<TestTask> {
        return input.map { TestTask.SingleTestTask(it) }
    }
}
