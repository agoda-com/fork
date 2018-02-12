package com.shazam.fork.execution

import com.shazam.fork.BatchStrategy
import com.shazam.fork.batch.BatchFactoryStrategy
import com.shazam.fork.batch.BatchTestQueue
import com.shazam.fork.batch.strategies.DefaultFactoryStrategy
import com.shazam.fork.batch.strategies.SplitFactoryStrategy
import com.shazam.fork.batch.strategies.stat.ExpectedTimeFactoryStrategy
import com.shazam.fork.batch.strategies.stat.VarianceFactoryStrategy
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent
import org.slf4j.LoggerFactory

class TestTaskQueueProvider(private val batchStrategy: BatchStrategy) {

    val logger = LoggerFactory.getLogger(TestTaskQueueProvider::class.java)

    fun create(list: Collection<TestCaseEvent>): BatchTestQueue {
        val extractedStrategy = extractBatchStrategy(batchStrategy)
        val supportBatches = list.groupBy { it.permissionsToRevoke.isEmpty() }
        val tasks = extractedStrategy.batches(supportBatches[true] ?: emptyList())
        val singleTestTasks = supportBatches[false]?.map { TestTask.SingleTestTask(it) } ?: emptyList()
        val queue = BatchTestQueue(tasks.size + singleTestTasks.size)
        queue.addAll(tasks)
        queue.addAll(singleTestTasks)
        return queue
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