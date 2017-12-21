package com.shazam.fork.batch

import com.google.common.collect.Lists
import com.shazam.fork.batch.tasks.TestTask
import com.shazam.fork.model.TestCaseEvent
import java.util.*

class TestBatchFactory {
    fun tasks(list: Collection<TestCaseEvent>): List<TestTask> {
        val average = list.map { it.testMetric.variance }.average()
        val grouped = list.groupBy { it.testMetric.variance < average }
        val short = grouped[true].orEmpty()
        val resultArray = ArrayList<TestTask>()
        val tempArray = ArrayList<TestCaseEvent>()
        short.forEach {
            if (tempArray.sumByDouble { it.testMetric.variance } < average*2) {
                tempArray.add(it)
            } else {
                resultArray.add(TestTask.MultiTestTask(Lists.newArrayList(tempArray)))
                tempArray.clear()
            }
        }
        if(tempArray.isNotEmpty()){
            resultArray.add(TestTask.MultiTestTask(Lists.newArrayList(tempArray)))
        }
        val single = grouped[false].orEmpty().map { TestTask.SingleTestTask(it) }

        return resultArray + single
    }
}