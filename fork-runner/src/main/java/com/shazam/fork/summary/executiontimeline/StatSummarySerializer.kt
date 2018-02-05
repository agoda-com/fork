package com.shazam.fork.summary.executiontimeline

import com.agoda.fork.stat.TestMetric
import com.android.ddmlib.testrunner.TestIdentifier
import com.shazam.fork.model.Device
import com.shazam.fork.stat.TestExecution
import com.shazam.fork.stat.TestExecutionReporter
import com.shazam.fork.stat.TestStatsLoader
import com.shazam.fork.summary.PoolSummary
import com.shazam.fork.summary.Summary

class StatSummarySerializer(private val reporter: TestExecutionReporter,
                            private val testStatsLoader: TestStatsLoader) {

    private fun prepareTestName(fullTestName: String): String {
        return fullTestName.substring(fullTestName.lastIndexOf('.') + 1)
    }

    private fun parseData(pool: String, device: Device): List<Data> {
        val executions = reporter.getTests(pool,device)
        return executions.map { this.convertToData(it) }
    }

    private fun convertToData(execution: TestExecution): Data {
        val preparedTestName = prepareTestName(execution.test.toString())
        val testMetric = getTestMetric(execution)
        return createData(execution, execution.status, preparedTestName, testMetric)
    }

    private fun createData(execution: TestExecution, status: TestExecution.Status, preparedTestName: String, testMetric: TestMetric): Data {
        return Data(preparedTestName, status,
                execution.startTime,
                execution.startTime + execution.endTime,
                testMetric.expectedValue, testMetric.variance)
    }

    private fun getTestMetric(execution: TestExecution): TestMetric {
        return testStatsLoader.findMetric(execution.test)
    }

    private fun calculateExecutionStats(data: List<Data>): ExecutionStats {
        return ExecutionStats(calculateIdle(data), calculateAverageExecutionTime(data))
    }

    private fun calculateAverageExecutionTime(data: List<Data>): Long {
        return data.map { this.calculateDuration(it) }.average().toLong()
    }

    private fun calculateDuration(a: Data): Long {
        return a.endDate - a.startDate
    }

    private fun calculateIdle(data: List<Data>): Long {
        return data.stream().collect(SlidingCollector(2, 1)).map {
            if (it.size == 2) {
                it[1].startDate - it[0].endDate
            } else {
                0
            }
        }.sum()
    }

    private fun parsePoolSummary(poolSummary: PoolSummary): List<Measure> {
        return poolSummary.testResults
                .map { it.device }
                .distinct()
                .map { this.createMeasure(poolSummary.poolName, it) }
    }

    private fun createMeasure(pool: String, device: Device): Measure {
        val data = parseData(pool, device)
        return Measure(device.serial, calculateExecutionStats(data), data)
    }

    private fun parseList(poolSummaries: List<PoolSummary>): List<Measure> {
        return poolSummaries.flatMap { this.parsePoolSummary(it) }
    }

    private fun extractIdentifiers(summary: PoolSummary): List<TestIdentifier> {
        return summary.testResults
                .map { result -> TestIdentifier(result.testClass, result.testMethod) }
    }

    private fun passedTestCount(summary: Summary): Int {
        return summary.poolSummaries
                .flatMap { this.extractIdentifiers(it) }
                .distinct()
                .count()
    }

    private fun aggregateExecutionStats(list: List<Measure>): ExecutionStats {
        val summaryIdle = list
                .map { it.executionStats.idleTimeMillis }
                .sum()
        val avgTestExecutionTime = list
                .map { it.executionStats.averageTestExecutionTimeMillis }
                .average()
                .toLong()
        return ExecutionStats(summaryIdle, avgTestExecutionTime)
    }

    fun parse(summary: Summary): ExecutionResult {
        val failedTests = summary.failedTests.size
        val ignoredTests = summary.ignoredTests.size
        val passedTestCount = passedTestCount(summary)
        val measures = parseList(summary.poolSummaries)
        return ExecutionResult(passedTestCount, failedTests, aggregateExecutionStats(measures), measures)
    }
}
