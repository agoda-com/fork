package com.shazam.fork.batch

import com.shazam.fork.model.TestCaseEvent
import java.util.function.Predicate

class Batch(private val limit: Predicate<List<TestCaseEvent>>,
            val list: MutableList<TestCaseEvent> = ArrayList()) {
    fun add(event: TestCaseEvent): Boolean {
        return if (limit.test(list)) {
            list.add(event)
        } else {
            false
        }
    }
}