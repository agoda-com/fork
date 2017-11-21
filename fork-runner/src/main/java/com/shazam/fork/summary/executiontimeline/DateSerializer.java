package com.shazam.fork.summary.executiontimeline;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateSerializer extends TypeAdapter<Date> {

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    public void write(JsonWriter out, Date value) throws IOException {
        out.value(format.format(value));
    }

    @Override
    public Date read(JsonReader in) throws IOException {
        throw new UnsupportedOperationException();
    }
}
