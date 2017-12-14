package com.shazam.fork.injector.stat;

import com.shazam.fork.summary.SummaryPrinter;
import com.shazam.fork.summary.executiontimeline.HtmlStatsSummaryPrinter;
import com.shazam.fork.summary.executiontimeline.JsonStatsSummarySerializer;
import com.shazam.fork.summary.executiontimeline.StatSummarySerializer;

import static com.shazam.fork.injector.ConfigurationInjector.configuredOutput;
import static com.shazam.fork.injector.GsonInjector.gson;
import static com.shazam.fork.injector.stat.TestExecutionReporterInjector.testExecutionReporter;
import static com.shazam.fork.injector.stat.TestStatLoaderInjector.testStatsLoader;
import static com.shazam.fork.injector.summary.HtmlGeneratorInjector.htmlGenerator;
import static com.shazam.fork.injector.system.FileManagerInjector.fileManager;

public class ExecutionTimeLineSummaryPrinterInjector {
    public static SummaryPrinter jsonSummaryStatsSerializer() {
        return new JsonStatsSummarySerializer(fileManager(), gson(), statSummarySerializer());
    }

    public static SummaryPrinter htmlStatsSummaryPrinter() {
        return new HtmlStatsSummaryPrinter(configuredOutput(), htmlGenerator(), statSummarySerializer(), gson());
    }

    public static StatSummarySerializer statSummarySerializer() {
        return new StatSummarySerializer(testExecutionReporter(), testStatsLoader());
    }
}
