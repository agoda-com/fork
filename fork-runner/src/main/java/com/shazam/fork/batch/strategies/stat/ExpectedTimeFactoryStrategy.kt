package com.shazam.fork.batch.strategies.stat

class ExpectedTimeFactoryStrategy(level: Int) : StatBasedFactoryStrategy(level, { it.testMetric.expectedValue })
