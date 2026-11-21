package com.candy.logging;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.ZoneOffset;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import org.junit.jupiter.api.Test;

class LogFormatterTest {

    private static final String NEW_LINE = System.lineSeparator();

    private static LogRecord record(Level level, String message) {
        LogRecord record = new LogRecord(level, message);
        record.setLoggerName("com.candy.Demo");
        record.setInstant(Instant.parse("2026-11-21T10:15:30Z"));
        return record;
    }

    @Test
    void formatsOneLinePerRecord() {
        String line = new LogFormatter(ZoneOffset.UTC)
                .format(record(Level.INFO, "Hello"));

        assertEquals("2026-11-21 10:15:30 INFO    com.candy.Demo - Hello"
                + NEW_LINE, line);
    }

    @Test
    void alignsLevelNames() {
        LogFormatter formatter = new LogFormatter(ZoneOffset.UTC);

        assertTrue(formatter.format(record(Level.SEVERE, "x"))
                .contains(" SEVERE  com.candy.Demo"));
        assertTrue(formatter.format(record(Level.WARNING, "x"))
                .contains(" WARNING com.candy.Demo"));
    }

    @Test
    void appendsTheException() {
        LogRecord record = record(Level.SEVERE, "Failed");
        record.setThrown(new IllegalStateException("boom"));

        String line = new LogFormatter(ZoneOffset.UTC).format(record);

        assertTrue(line.endsWith(" - Failed (java.lang.IllegalStateException"
                + ": boom)" + NEW_LINE));
    }

    @Test
    void substitutesMessageParameters() {
        LogRecord record = record(Level.INFO, "Value {0}");
        record.setParameters(new Object[] {"x"});

        String line = new LogFormatter(ZoneOffset.UTC).format(record);

        assertTrue(line.endsWith(" - Value x" + NEW_LINE));
    }

    @Test
    void usesTheGivenTimeZone() {
        String line = new LogFormatter(ZoneOffset.ofHours(2))
                .format(record(Level.INFO, "Hello"));

        assertTrue(line.startsWith("2026-11-21 12:15:30 "));
    }

    @Test
    void usesTheSystemZoneByDefault() {
        String line = new LogFormatter().format(record(Level.INFO, "Hello"));

        assertTrue(line.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2} "
                + "INFO {4}com\\.candy\\.Demo - Hello\\R"));
    }
}