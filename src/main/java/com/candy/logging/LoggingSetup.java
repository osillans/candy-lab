package com.candy.logging;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LoggingSetup {

    static final String ROOT_LOGGER = "com.candy";

    private LoggingSetup() {
    }

    public static void configure(Path logFile) throws IOException {
        Files.createDirectories(logFile.toAbsolutePath().getParent());
        ConsoleHandler console = new ConsoleHandler();
        console.setLevel(Level.INFO);
        console.setFormatter(new LogFormatter());
        FileHandler file = new FileHandler(logFile.toString(), false);
        file.setLevel(Level.FINE);
        file.setFormatter(new LogFormatter());

        Logger logger = Logger.getLogger(ROOT_LOGGER);
        for (Handler old : logger.getHandlers()) {
            logger.removeHandler(old);
            old.close();
        }
        logger.setUseParentHandlers(false);
        logger.setLevel(Level.FINE);
        logger.addHandler(console);
        logger.addHandler(file);
    }
}