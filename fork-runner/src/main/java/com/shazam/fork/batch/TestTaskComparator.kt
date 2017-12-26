package com.shazam.fork.batch

import com.agoda.fork.stat.TestMetric
import com.shazam.fork.batch.tasks.TestTask
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TestTaskComparator : Comparator<TestTask> {

    private fun getDefaultComparator(): java.util.Comparator<TestMetric> {
        return Comparator.comparingDouble<TestMetric> { value ->
            Math.sqrt(value.variance) * 2 + value.expectedValue
        }.reversed()
    }

    private fun compareMetrics(o1: TestMetric, o2: TestMetric): Int {
        return if (o1.variance == 0.0 || o1.expectedValue == 0.0) {
            -1
        } else if (o2.variance == 0.0 || o2.expectedValue == 0.0) {
            1
        } else {
            getDefaultComparator().compare(o1, o2)
        }
    }

    override fun compare(o1: TestTask, o2: TestTask): Int {
        val testMetric1 = extractTestMetric(o1)
        val testMetric2 = extractTestMetric(o2)
        return compareMetrics(testMetric1, testMetric2)
    }

    private fun extractTestMetric(task: TestTask) = when (task) {
        is TestTask.SingleTestTask -> task.event.testMetric
        is TestTask.MultiTestTask -> task.list.fold(TestMetric.empty(), { acc, case ->
            TestMetric.create(acc.expectedValue + case.testMetric.expectedValue,
                    acc.variance + case.testMetric.variance)
        })
    }
}