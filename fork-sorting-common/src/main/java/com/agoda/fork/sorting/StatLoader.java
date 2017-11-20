package com.agoda.fork.sorting;

import javax.annotation.Nonnull;
import java.util.List;

public interface StatLoader {
    List<TestHistory> loadHistory(@Nonnull String configuration);
}
