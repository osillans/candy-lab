package com.candy.support;

import java.util.logging.Handler;
import java.util.logging.Logger;

public final class LoggingState {

    private static final String ROOT_LOGGER = "com.candy";

    private LoggingState() {
    }

    public static void reset() {
        Logger logger = Logger.getLogger(ROOT_LOGGER);
        for (Handler handler : logger.getHandlers()) {
            logger.removeHandler(handler);
            handler.close();
        }
        logger.setUseParentHandlers(true);
        logger.setLevel(null);
    }
}