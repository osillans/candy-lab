package com.candy.logging;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {

    private final DateTimeFormatter timeFormat;

    public LogFormatter() {
        this(ZoneId.systemDefault());
    }

    public LogFormatter(ZoneId zone) {
        this.timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                .withZone(zone);
    }

    @Override
    public String format(LogRecord record) {
        StringBuilder line = new StringBuilder()
                .append(timeFormat.format(record.getInstant()))
                .append(' ')
                .append(String.format("%-7s", record.getLevel().getName()))
                .append(' ')
                .append(record.getLoggerName())
                .append(" - ")
                .append(formatMessage(record));
        if (record.getThrown() != null) {
            line.append(" (").append(record.getThrown()).append(')');
        }
        return line.append(System.lineSeparator()).toString();
    }
}