package com.shazam.fork.execution

import com.shazam.fork.BatchStrategy
import com.shazam.fork.CustomExecutionStrategy
import com.shazam.fork.batch.BatchFactoryStrategy
import com.shazam.fork.batch.BatchTestQueue
import com.shazam.fork.batch.strategies.DefaultFactoryStrategy
import com.shazam.fork.batch.strategies.SplitFactoryStrategy
import com.shazam.fork.batch.strategies.stat.ExpectedTimeFactoryStrategy
import com.shazam.fork.batch.strategies.stat.VarianceFactoryStrategy
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent

class TestTaskQueueProvider(private val batchStrategy: BatchStrategy,
                            private val executionStrategy: CustomExecutionStrategy) {

    fun create(maxDevicesPerPool: Int, input: Collection<TestCaseEvent>): BatchTestQueue {
        val list = preprocessTestTasks(input, executionStrategy)
        val extractedStrategy = extractBatchStrategy(batchStrategy)
        val supportBatches = list.groupBy { it.permissionsToRevoke.isEmpty() }
        val tasks = extractedStrategy.batches(maxDevicesPerPool, supportBatches[true] ?: emptyList())
        val singleTestTasks = supportBatches[false]?.map { TestTask.SingleTestTask(it) } ?: emptyList()
        val queue = BatchTestQueue(tasks.size + singleTestTasks.size)
        queue.addAll(tasks)
        queue.addAll(singleTestTasks)
        return queue
    }

    private fun preprocessTestTasks(testCases: Collection<TestCaseEvent>, executionStrategy: CustomExecutionStrategy): Collection<TestCaseEvent> {
        return if (executionStrategy.flakinessStrategy != null) {
            val count = executionStrategy.flakinessStrategy.count
            testCases.flatMap { task ->
                (1..count).map {
                    task
                }
            }
        } else {
            testCases
        }
    }

    private fun extractBatchStrategy(batchStrategy: BatchStrategy): BatchFactoryStrategy {
        return when {
            batchStrategy.chunkStrategy != null -> SplitFactoryStrategy(batchStrategy.chunkStrategy.batchSize)
            batchStrategy.expectedTimeBasedStrategy != null -> ExpectedTimeFactoryStrategy(batchStrategy.expectedTimeBasedStrategy.percentile)
            batchStrategy.varianceBasedStrategy != null -> VarianceFactoryStrategy(batchStrategy.varianceBasedStrategy.percentile)
            else -> DefaultFactoryStrategy()
        }
    }
}