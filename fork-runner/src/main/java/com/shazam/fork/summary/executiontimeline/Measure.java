package com.shazam.fork.summary.executiontimeline;

import com.google.gson.annotations.SerializedName;

import java.util.List;


public class Measure {
    @SerializedName("measure")
    private final String measure;
    @SerializedName("data")
    private final List<Data> data;

    public Measure(String measure, List<Data> data) {
        this.measure = measure;
        this.data = data;
    }

    public String getMeasure() {
        return measure;
    }

    public List<Data> getData() {
        return data;
    }
}


