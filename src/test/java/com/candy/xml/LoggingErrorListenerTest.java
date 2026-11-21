package com.candy.xml;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.candy.support.CapturingHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.transform.TransformerException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LoggingErrorListenerTest {

    private final Logger logger =
            Logger.getLogger(LoggingErrorListener.class.getName());

    private CapturingHandler handler;
    private LoggingErrorListener listener;

    @BeforeEach
    void setUp() {
        handler = CapturingHandler.attachTo(logger);
        listener = new LoggingErrorListener();
    }

    @AfterEach
    void tearDown() {
        handler.detach();
    }

    @Test
    void logsWarningsAsWarnings() {
        listener.warning(new TransformerException("careful"));

        assertEquals(1, handler.messages().size());
        assertTrue(handler.contains(Level.WARNING, "careful"));
    }

    @Test
    void logsErrorsAsSevere() {
        listener.error(new TransformerException("broken"));

        assertEquals(1, handler.messages().size());
        assertTrue(handler.contains(Level.SEVERE, "broken"));
    }

    @Test
    void logsFatalErrorsAsSevere() {
        listener.fatalError(new TransformerException("fatal"));

        assertEquals(1, handler.messages().size());
        assertTrue(handler.contains(Level.SEVERE, "fatal"));
    }
}