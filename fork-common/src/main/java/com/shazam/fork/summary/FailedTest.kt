package com.shazam.fork.summary

data class FailedTest(val testResult: TestResult,
                      val retries: Int)