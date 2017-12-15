package com.shazam.fork.batch.tasks

import com.shazam.fork.model.TestCaseEvent

sealed class TestTask {
    data class SingleTestTask(val event: TestCaseEvent) : TestTask()

    data class MultiTestTask(val list: List<TestCaseEvent>) : TestTask()
}