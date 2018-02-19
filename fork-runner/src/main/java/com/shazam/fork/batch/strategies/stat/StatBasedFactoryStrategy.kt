package com.shazam.fork.batch.strategies.stat

import com.shazam.fork.batch.Batch
import com.shazam.fork.batch.BatchFactoryStrategy
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent
import java.util.*
import java.util.function.Predicate


abstract class StatBasedFactoryStrategy(private val level: Int,
                                        private val extract: (TestCaseEvent) -> (Double)) : BatchFactoryStrategy {

    companion object {
        private const val DEFAULT_BATCH_COUNT = 10
    }

    override fun batches(input: Collection<TestCaseEvent>): List<TestTask> {
        val expectedValues = input.sortedBy { extract(it) }
        val percentile = expectedValues[((expectedValues.size * level) / 100)].let { extract(it) }

        val grouped = input.groupBy { extract(it) < percentile }
        val short = LinkedList(grouped[true].orEmpty().sortedByDescending { extract(it) })
        val single = grouped[false].orEmpty().map { TestTask.SingleTestTask(it) }

        val list = ArrayList<Batch>(DEFAULT_BATCH_COUNT)
        val predicate: Predicate<List<TestCaseEvent>> = Predicate {
            percentile > it.sumByDouble { extract(it) }
        }
        list.fill(Batch(predicate))
        list.forEach {
            it.add(short.poll())
        }

        short.forEach { value ->
            if (list.none { it.add(value) }) {
                list.add(Batch(predicate).apply { add(value) })
            }
        }

        return list.map { TestTask.MultiTestTask(it.list) } + single
    }
}