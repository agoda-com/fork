package com.shazam.fork.suite;

import com.google.common.base.Strings;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class AnnotationParser {
    @Nonnull
    public static Function<String, List<String>> parseAnnotation() {
        return annotations -> {
            if (Strings.isNullOrEmpty(annotations)) return emptyList();
            return Arrays.stream(annotations.split(","))
                    .map(c -> "L" + c.replace('.', '/').trim() + ';')
                    .collect(toList());
        };
    }
}
