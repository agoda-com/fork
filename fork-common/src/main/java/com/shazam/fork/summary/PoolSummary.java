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

import java.util.ArrayList;
import java.util.Collection;

public class PoolSummary {
	private final String poolName;
	private final Collection<TestResult> testResults;
	private final ArrayList<TestResult> ignoredTests;
	private final ArrayList<TestResult> failedTests;
	private final ArrayList<FlakyTest> flakyTest;

	public String getPoolName() {
		return poolName;
	}

	public Collection<TestResult> getTestResults() {
		return testResults;
	}

	public ArrayList<TestResult> getIgnoredTests() {
		return ignoredTests;
	}

	public ArrayList<TestResult> getFailedTests() {
		return failedTests;
	}

	public ArrayList<FlakyTest> getFlakyTest() {
		return flakyTest;
	}

	public static class Builder {
		private String poolName;
		private final Collection<TestResult> testResults = new ArrayList<>();
		private final ArrayList<TestResult> ignoredTests = new ArrayList<>();
		private ArrayList<TestResult> failedTests =  new ArrayList<>();
		public ArrayList<FlakyTest> flakyTests = new ArrayList<>();

		public static Builder aPoolSummary() {
			return new Builder();
		}

		public Builder withPoolName(String poolName) {
			this.poolName = poolName;
			return this;
		}

		public Builder addTestResults(Collection<TestResult> testResults) {
			this.testResults.addAll(testResults);
			return this;
		}

		public Builder addFlakyTest(FlakyTest s) {
			this.flakyTests.add(s);
			return this;
		}

		public Builder addFailedTests(TestResult failedTests) {
			this.failedTests.add(failedTests);
			return this;
		}

		public Builder addIgnoredTest(TestResult s) {
			this.ignoredTests.add(s);
			return this;
		}

		public PoolSummary build() {
			return new PoolSummary(this);
		}
	}

	private PoolSummary(Builder builder) {
		testResults = builder.testResults;
		poolName = builder.poolName;
		failedTests = builder.failedTests;
		flakyTest = builder.flakyTests;
		ignoredTests = builder.ignoredTests;
	}
}
