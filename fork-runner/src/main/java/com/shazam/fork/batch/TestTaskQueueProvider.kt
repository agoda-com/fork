package com.shazam.fork.batch

import com.shazam.fork.BatchStrategy
import com.shazam.fork.batch.strategies.*
import com.shazam.fork.batch.strategies.stat.ExpectedTimeFactoryStrategy
import com.shazam.fork.batch.strategies.stat.VarianceFactoryStrategy
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent
import org.slf4j.LoggerFactory

class TestTaskQueueProvider(private val batchStrategy: BatchStrategy) {

    private val logger = LoggerFactory.getLogger(TestTaskQueueProvider::class.java)

    fun create(maxDevicesPerPool: Int, list: Collection<TestCaseEvent>): BatchTestQueue {
        val extractedStrategy = extractStrategy(batchStrategy)

        val tasks = extractedStrategy.batches(maxDevicesPerPool, list)
        val testsInBatches = tasks.map {
            when (it) {
                is TestTask.SingleTestTask -> 1
                is TestTask.MultiTestTask -> it.list.size
            }
        }.reduce { acc, n -> acc + n }
        logger.error("tests = ${list.size}, batches = ${tasks.size}, tests in batches = $testsInBatches")
        val queue = BatchTestQueue()
        queue.addAll(tasks)
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