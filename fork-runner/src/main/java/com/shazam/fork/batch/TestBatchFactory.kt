package com.shazam.fork.batch

import com.agoda.fork.stat.TestMetric
import com.google.common.collect.Lists
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent
import java.util.*
import kotlin.collections.ArrayList

class TestBatchFactory {

    fun tasks(list: Collection<TestCaseEvent>): List<TestTask> {
        val average = list.map { it.testMetric.variance }.average()
        val grouped = list.groupBy { it.testMetric.variance < average }
        val short = grouped[true].orEmpty().sortedByDescending { it.testMetric.variance }

        val single = grouped[false].orEmpty().map { TestTask.SingleTestTask(it) }

        return short.map { TestTask.SingleTestTask(it) } + single
    }
}