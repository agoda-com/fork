package com.shazam.fork.batch

import com.shazam.fork.BatchStrategy
import com.shazam.fork.batch.strategies.*
import com.shazam.fork.batch.strategies.stat.ExpectedTimeFactoryStrategy
import com.shazam.fork.batch.strategies.stat.VarianceFactoryStrategy
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent
import org.slf4j.LoggerFactory

class TestTaskQueueProvider(private val batchStrategy: BatchStrategy) {

    fun create(maxDevicesPerPool: Int, list: Collection<TestCaseEvent>): BatchTestQueue {
        val extractedStrategy = extractStrategy(batchStrategy)
        val supportBatches = list.groupBy { it.permissionsToRevoke.isEmpty() }
        val tasks = extractedStrategy.batches(maxDevicesPerPool, supportBatches[true] ?: emptyList())
        val queue = BatchTestQueue(tasks.size)
        queue.addAll(tasks)
        queue.addAll(supportBatches[false]?.map { TestTask.SingleTestTask(it) } ?: emptyList())
        return queue
    }

    private fun extractStrategy(batchStrategy: BatchStrategy): BatchFactoryStrategy {
        return when {
            batchStrategy.chunkStrategy != null -> SplitFactoryStrategy(batchStrategy.chunkStrategy.batchSize)
            batchStrategy.expectedTimeBasedStrategy != null -> ExpectedTimeFactoryStrategy(batchStrategy.expectedTimeBasedStrategy.percentile)
            batchStrategy.varianceBasedStrategy != null -> VarianceFactoryStrategy(batchStrategy.varianceBasedStrategy.percentile)
            else -> DefaultFactoryStrategy()
        }
    }
}