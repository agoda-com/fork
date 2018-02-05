package com.shazam.fork.store

import com.google.gson.Gson
import com.shazam.fork.model.TestCaseEvent
import com.shazam.fork.system.io.FileManager

class TestCaseStore(private val fileManager: FileManager,
                    private val gson: Gson) {
    val map = mutableMapOf<String, TestCaseEvent>()

    fun putAll(input: Collection<TestCaseEvent>) {
        map.putAll(input.associateBy { createKey(it) })
    }

    fun getAll(): Collection<TestCaseEvent> {
        return map.values
    }

    fun get(className: String, methodName: String) = map[createKey(className, methodName)]

    fun get(testName: String) = map[testName]

    fun createKey(event: TestCaseEvent) = createKey(event.testClass, event.testMethod)

    fun createKey(className: String, methodName: String) = "$className#$methodName"
}