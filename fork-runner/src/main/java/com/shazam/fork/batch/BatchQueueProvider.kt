package com.shazam.fork.batch

import com.shazam.fork.model.TestCaseEvent

class BatchQueueProvider {
    fun provide(list: Collection<TestCaseEvent>): BatchTestQueue {
        val tasks = TestBatchFactory().tasks(list)
        val queue = BatchTestQueue()
        queue.addAll(tasks)
        return queue
    }
}