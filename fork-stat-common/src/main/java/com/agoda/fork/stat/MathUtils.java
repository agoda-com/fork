package com.agoda.fork.stat;

import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MathUtils {
    public static double expectedValue(List<Integer> list) {
        return prepareData(list).entrySet()
                .stream()
                .map(e -> e.getKey() * e.getValue())
                .reduce((acc, v) -> acc + v)
                .orElse(0.0);
    }

    public static double variance(List<Integer> list, double expectedValue) {
        return prepareData(list).entrySet()
                .stream()
                .map(e -> Math.pow(e.getKey() - expectedValue, 2.0) * e.getValue())
                .reduce((acc, value) -> acc + value)
                .orElse(0.0);
    }

    private static Map<Integer, Double> prepareData(List<Integer> list) {
        double defP = 1.0 / list.size();

        return list.stream()
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                .entrySet()
                .stream()
                .map(entry -> {
                    double p = entry.getValue() * defP;
                    return new AbstractMap.SimpleEntry<>(entry.getKey(), p);
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }
}