package com.shazam.fork.summary.executiontimeline;

import com.google.gson.Gson;
import com.shazam.fork.io.HtmlGenerator;
import com.shazam.fork.summary.Summary;
import com.shazam.fork.summary.SummaryPrinter;

import java.io.File;

import static com.shazam.fork.io.Files.copyResource;

public class HtmlStatsSummaryPrinter implements SummaryPrinter {
    private static final String HTML_OUTPUT = "html";
    private static final String STATIC = "static";
    private static final String INDEX_FILENAME = "stat.html";
    private static final String[] STATIC_ASSETS = {
            "chart.css",
            "chart.js"
    };
    private final File htmlOutput;
    private final File staticOutput;
    private final HtmlGenerator htmlGenerator;
    private final StatSummarySerializer serializer;
    private final Gson gson;

    public HtmlStatsSummaryPrinter(File rootOutput, HtmlGenerator htmlGenerator,
                                   StatSummarySerializer serializer,
                                   Gson gson) {
        this.htmlGenerator = htmlGenerator;
        htmlOutput = new File(rootOutput, HTML_OUTPUT);
        staticOutput = new File(htmlOutput, STATIC);
        this.serializer = serializer;
        this.gson = gson;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void print(Summary summary) {
        htmlOutput.mkdirs();
        copyAssets();
        StatHtmlSummary statHtmlSummary = toStatHtmlSummary(summary);
        htmlGenerator.generateHtml("stat/index.html", htmlOutput, INDEX_FILENAME, statHtmlSummary);
    }

    private StatHtmlSummary toStatHtmlSummary(Summary summary) {
        StatHtmlSummary statHtmlSummary = new StatHtmlSummary();
        statHtmlSummary.dataset = gson.toJson(serializer.parse(summary));
        return statHtmlSummary;
    }

    private void copyAssets() {
        for (String asset : STATIC_ASSETS) {
            copyResource("/static/", asset, staticOutput);
        }
    }
}
