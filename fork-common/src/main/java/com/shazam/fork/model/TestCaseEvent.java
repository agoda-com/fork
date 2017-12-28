package com.shazam.fork.model;

import com.agoda.fork.stat.TestMetric;
import com.google.common.base.Objects;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.SIMPLE_STYLE;

public class TestCaseEvent {

    @Nullable
    private final String testMethod;
    @Nonnull
    private final String testClass;
    private final boolean isIgnored;
    private final List<String> permissionsToRevoke;
    private final Map<String, String> properties;
    private final TestMetric testMetric;

    TestCaseEvent(@Nullable String testMethod, @Nonnull String testClass, boolean isIgnored, List<String> permissionsToRevoke, Map<String, String> properties, TestMetric testMetric) {
        this.testMethod = testMethod;
        this.testClass = testClass;
        this.isIgnored = isIgnored;
        this.permissionsToRevoke = permissionsToRevoke;
        this.properties = properties;
        this.testMetric = testMetric;
    }

    @Nullable
    public String getTestMethod() {
        return testMethod;
    }

    @Nonnull
    public String getTestClass() {
        return testClass;
    }

    public boolean isIgnored() {
        return isIgnored;
    }

    public List<String> getPermissionsToRevoke() {
        return permissionsToRevoke;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public TestMetric getTestMetric() {
        return testMetric;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.testMethod, this.testClass);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TestCaseEvent other = (TestCaseEvent) obj;
        return Objects.equal(this.testMethod, other.testMethod)
                && Objects.equal(this.testClass, other.testClass);
    }

    @Override
    public String toString() {
        return reflectionToString(this, SIMPLE_STYLE);
    }
}
