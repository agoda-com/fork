package com.shazam.fork.batch

import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent

class TestBatchFactory {
    fun tasks(list: List<TestCaseEvent>): List<TestTask> {
        val average = list.map { it.testMetric.expectedValue }.average()
        val grouped = list.groupBy { it.testMetric.expectedValue < average }
        val single = grouped[false].orEmpty().map { TestTask.SingleTestTask(it) }
        val multi = listOf(TestTask.MultiTestTask(grouped[true].orEmpty()))
        return multi + single
    }
}