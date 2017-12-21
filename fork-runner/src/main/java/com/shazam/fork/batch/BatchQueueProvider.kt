package com.shazam.fork.batch

import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent
import org.slf4j.LoggerFactory

class BatchQueueProvider {

    val logger = LoggerFactory.getLogger(BatchQueueProvider::class.java)

    fun provide(list: Collection<TestCaseEvent>): BatchTestQueue {
        val tasks = TestBatchFactory().tasks(list)
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
}