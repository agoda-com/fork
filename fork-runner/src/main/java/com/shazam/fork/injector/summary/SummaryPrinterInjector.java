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
package com.shazam.fork.injector.summary;

import com.shazam.fork.summary.CompositeSummaryPrinter;
import com.shazam.fork.summary.JsonSummarySerializer;
import com.shazam.fork.summary.LogSummaryPrinter;
import com.shazam.fork.summary.SummaryPrinter;
import com.shazam.fork.summary.flakiness.FlakinessSummaryPrinter;
import com.shazam.fork.summary.html.HtmlSummaryPrinter;

import static com.shazam.fork.injector.ConfigurationInjector.configuredOutput;
import static com.shazam.fork.injector.GsonInjector.gson;
import static com.shazam.fork.injector.stat.ExecutionTimeLineSummaryPrinterInjector.htmlStatsSummaryPrinter;
import static com.shazam.fork.injector.stat.ExecutionTimeLineSummaryPrinterInjector.jsonSummaryStatsSerializer;
import static com.shazam.fork.injector.stat.TestExecutionReporterInjector.testExecutionReporter;

import static com.shazam.fork.injector.system.FileManagerInjector.fileManager;

public class SummaryPrinterInjector {

    private SummaryPrinterInjector() {
    }

    public static SummaryPrinter summaryPrinter() {
        return new CompositeSummaryPrinter(consoleSummaryPrinter(),
                htmlSummaryPrinter(),
                jsonSummarySerializer(),
                jsonSummaryStatsSerializer(),
                htmlStatsSummaryPrinter(),
                flakinessSummaryPrinter());
    }

    private static SummaryPrinter htmlSummaryPrinter(){
        return new HtmlSummaryPrinter(gson(),configuredOutput());
    }

    private static SummaryPrinter consoleSummaryPrinter() {
        return new LogSummaryPrinter();
    }

    private static SummaryPrinter jsonSummarySerializer() {
        return new JsonSummarySerializer(fileManager(), gson());
    }

    private static SummaryPrinter flakinessSummaryPrinter() {
        return new FlakinessSummaryPrinter(fileManager(), testExecutionReporter());
    }
}
