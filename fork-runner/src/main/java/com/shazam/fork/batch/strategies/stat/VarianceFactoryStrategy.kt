package com.shazam.fork.batch.strategies.stat

class VarianceFactoryStrategy(level: Int) : StatBasedFactoryStrategy(level,{ it.testMetric.variance })
