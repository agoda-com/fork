package com.shazam.fork.batch

import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent

interface BatchFactoryStrategy {
    fun batches(input: Collection<TestCaseEvent>): List<TestTask>
}