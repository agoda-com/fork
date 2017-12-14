package com.agoda.fork.teamcity.entities;

import java.util.List;

public class Builds {
    private final List<Build> build;

    public Builds(List<Build> build) {
        this.build = build;
    }

    public List<Build> getBuild() {
        return build;
    }
}