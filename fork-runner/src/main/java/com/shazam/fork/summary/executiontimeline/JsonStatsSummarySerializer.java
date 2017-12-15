package com.shazam.fork.summary.executiontimeline;

import com.google.gson.Gson;
import com.shazam.fork.summary.Summary;
import com.shazam.fork.summary.SummaryPrinter;
import com.shazam.fork.system.io.FileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static org.apache.commons.io.IOUtils.closeQuietly;

public class JsonStatsSummarySerializer implements SummaryPrinter {

    private static final Logger logger = LoggerFactory.getLogger(JsonStatsSummarySerializer.class);
    private final FileManager fileManager;
    private final Gson gson;
    private final StatSummarySerializer serializer;

    public JsonStatsSummarySerializer(FileManager fileManager, Gson gson, StatSummarySerializer serializer) {
        this.fileManager = fileManager;
        this.gson = gson;
        this.serializer = serializer;
    }

    @Override
    public void print(Summary summary) {
        ExecutionResult result = serializer.parse(summary);
        FileWriter writer = null;
        try {
            File summaryFile = fileManager.createSummaryFile();
            writer = new FileWriter(summaryFile);
            gson.toJson(result, writer);
            writer.flush();
        } catch (IOException e) {
            logger.error("Could not serialize the summary.", e);
        } finally {
            closeQuietly(writer);
        }
    }
}
