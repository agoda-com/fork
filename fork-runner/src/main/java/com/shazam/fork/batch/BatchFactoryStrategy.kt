package com.shazam.fork.batch

import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent

interface BatchFactoryStrategy {
    fun batches(poolSize : Int, input: Collection<TestCaseEvent>): List<TestTask>
}