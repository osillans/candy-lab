package com.candy.xml;

import java.util.logging.Logger;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.TransformerException;

final class LoggingErrorListener implements ErrorListener {

    private static final Logger LOGGER =
            Logger.getLogger(LoggingErrorListener.class.getName());

    @Override
    public void warning(TransformerException exception) {
        LOGGER.warning(exception.getMessageAndLocation());
    }

    @Override
    public void error(TransformerException exception) {
        LOGGER.severe(exception.getMessageAndLocation());
    }

    @Override
    public void fatalError(TransformerException exception) {
        LOGGER.severe(exception.getMessageAndLocation());
    }
}