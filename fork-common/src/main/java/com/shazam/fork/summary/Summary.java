/*
 * Copyright 2014 Shazam Entertainment Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License.
 *
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.shazam.fork.summary;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;


public class Summary {
    private final List<PoolSummary> poolSummaries;
    private final String title;
    private final String subtitle;
    private final ArrayList<IgnoredTest> ignoredTests;
    private final ArrayList<FailedTest> failedTests;

    @Nonnull
    public List<PoolSummary> getPoolSummaries() {
        return poolSummaries;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    @Nonnull
    public ArrayList<IgnoredTest> getIgnoredTests() {
        return ignoredTests;
    }

    public ArrayList<FailedTest> getFailedTests() {
        return failedTests;
    }

    public static class Builder {
        private final List<PoolSummary> poolSummaries = new ArrayList<>();
        private final ArrayList<IgnoredTest> ignoredTests = new ArrayList<>();
        private String title = "Report Title";
        private String subtitle = "Report Subtitle";
        private ArrayList<FailedTest> failedTests =  new ArrayList<>();

        public static Builder aSummary() {
            return new Builder();
        }

        public Builder addPoolSummary(PoolSummary poolSummary) {
            poolSummaries.add(poolSummary);
            return this;
        }

        public Builder withTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder withSubtitle(String subtitle) {
            this.subtitle = subtitle;
            return this;
        }

        public Builder addIgnoredTest(IgnoredTest s) {
            this.ignoredTests.add(s);
            return this;
        }

        public Builder addFailedTests(FailedTest failedTests) {
            this.failedTests.add(failedTests);
            return this;
        }

        public Summary build() {
            return new Summary(this);
        }
    }

    private Summary(Builder builder) {
        poolSummaries = builder.poolSummaries;
        title = builder.title;
        subtitle = builder.subtitle;
        ignoredTests = builder.ignoredTests;
        failedTests = builder.failedTests;
    }
}
