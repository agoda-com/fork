package com.agoda.fork.teamcity.entities;

public class Test {
    private final String id;
    private final String name;
    private final String status;
    private final int duration;
    private final String href;

    public Test(String id, String name, String status, int duration, String href) {
        this.id = id;
        this.name = name;
        this.status = status;
        this.duration = duration;
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public int getDuration() {
        return duration;
    }

    public String getHref() {
        return href;
    }
}
