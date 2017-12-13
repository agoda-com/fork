package com.agoda.fork.sorting;

public class TestMetric {

    public static TestMetric empty() {
        return new TestMetric(0.0, 0.0);
    }

    public static TestMetric create(double expectedValue, double variance) {
        return new TestMetric(expectedValue, variance);
    }

    private final double expectedValue;
    private final double variance;

    public TestMetric(double expectedValue, double variance) {
        this.expectedValue = expectedValue;
        this.variance = variance;
    }

    public double getExpectedValue() {
        return expectedValue;
    }

    public double getVariance() {
        return variance;
    }

    @Override
    public String toString() {
        return "TestMetric{" +
                "expectedValue=" + expectedValue +
                ", variance=" + variance +
                '}';
    }
}
