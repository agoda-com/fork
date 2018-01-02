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

import org.apache.commons.lang3.BooleanUtils;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static com.shazam.fork.summary.ResultStatus.PASS;
import static java.util.stream.Collectors.toList;

public class OutcomeAggregator {

    public boolean aggregate(Summary summary) {
        if (summary == null || summary.getPoolSummaries().isEmpty()) {
            return false;
        }

        List<PoolSummary> poolSummaries = summary.getPoolSummaries();
        Collection<Boolean> poolOutcomes = poolSummaries.stream().map(toPoolOutcome()).collect(toList());
        return and(poolOutcomes);
    }

    static Function<? super PoolSummary, Boolean> toPoolOutcome() {
        return (Function<PoolSummary, Boolean>) input -> {
            final Collection<TestResult> testResults = input.getTestResults();
            final Collection<Boolean> testOutcomes = testResults.stream().map(toTestOutcome()).collect(toList());
            return !testOutcomes.isEmpty() && and(testOutcomes);
        };
    }

    private static Function<TestResult, Boolean> toTestOutcome() {
        return input -> PASS.equals(input.getResultStatus());
    }

    private static Boolean and(final Collection<Boolean> booleans) {
        return BooleanUtils.and(booleans.toArray(new Boolean[booleans.size()]));
    }

}
