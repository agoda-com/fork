package com.shazam.fork.summary.executiontimeline;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Measure {
    @SerializedName("measure")
    private final String measure;

    @SerializedName("stats")
    private final ExecutionStats executionStats;

    @SerializedName("data")
    private final List<Data> data;

    public Measure(String measure, ExecutionStats executionStats, List<Data> data) {
        this.measure = measure;
        this.executionStats = executionStats;
        this.data = data;
    }

    public String getMeasure() {
        return measure;
    }

    public ExecutionStats getExecutionStats() {
        return executionStats;
    }

    public List<Data> getData() {
        return data;
    }
}
