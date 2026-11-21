package com.candy.support;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class CapturingHandler extends Handler {

    private final List<String> messages = new ArrayList<>();
    private final Logger logger;

    private CapturingHandler(Logger logger) {
        this.logger = logger;
    }

    public static CapturingHandler attachTo(Logger logger) {
        CapturingHandler handler = new CapturingHandler(logger);
        logger.addHandler(handler);
        return handler;
    }

    public void detach() {
        logger.removeHandler(this);
    }

    public List<String> messages() {
        return messages;
    }

    public boolean contains(Level level, String fragment) {
        return messages.stream().anyMatch(
                message -> message.startsWith(level.getName() + ": ")
                        && message.contains(fragment));
    }

    @Override
    public void publish(LogRecord record) {
        messages.add(record.getLevel().getName() + ": "
                + record.getMessage());
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() {
    }
}