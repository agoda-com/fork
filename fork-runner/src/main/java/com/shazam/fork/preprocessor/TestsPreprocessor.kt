package com.shazam.fork.preprocessor

import com.shazam.fork.CustomExecutionStrategy
import com.shazam.fork.model.TestCaseEvent

class TestsPreprocessor(private val executionStrategy: CustomExecutionStrategy) {
    fun process(input: Collection<TestCaseEvent>): Collection<TestCaseEvent> {
        return if (executionStrategy.flakinessStrategy != null) {
            val count = executionStrategy.flakinessStrategy.count

            input.flatMap { test ->
                (1..count).map { test }
            }
        } else {
            input
        }
    }
}