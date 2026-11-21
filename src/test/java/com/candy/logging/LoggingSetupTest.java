package com.candy.logging;

import static com.candy.support.UtilityClassAssertions.assertUtilityClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.candy.support.LoggingState;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class LoggingSetupTest {

    @TempDir
    Path tempDir;

    private final Logger root = Logger.getLogger(LoggingSetup.ROOT_LOGGER);

    @AfterEach
    void tearDown() {
        LoggingState.reset();
    }

    private String readLog(Path file) throws IOException {
        for (Handler handler : root.getHandlers()) {
            handler.flush();
        }
        return Files.readString(file);
    }

    @Test
    void isUtilityClass() throws Exception {
        assertUtilityClass(LoggingSetup.class);
    }

    @Test
    void addsConsoleAndFileHandlers() throws IOException {
        LoggingSetup.configure(tempDir.resolve("candy.log"));

        Handler[] handlers = root.getHandlers();
        assertEquals(2, handlers.length);
        assertInstanceOf(ConsoleHandler.class, handlers[0]);
        assertInstanceOf(FileHandler.class, handlers[1]);
    }

    @Test
    void consoleShowsInfoAndFileShowsFine() throws IOException {
        LoggingSetup.configure(tempDir.resolve("candy.log"));

        assertEquals(Level.INFO, root.getHandlers()[0].getLevel());
        assertEquals(Level.FINE, root.getHandlers()[1].getLevel());
        assertEquals(Level.FINE, root.getLevel());
    }

    @Test
    void usesTheProjectFormatter() throws IOException {
        LoggingSetup.configure(tempDir.resolve("candy.log"));

        for (Handler handler : root.getHandlers()) {
            assertInstanceOf(LogFormatter.class, handler.getFormatter());
        }
    }

    @Test
    void doesNotDuplicateOutputInParentHandlers() throws IOException {
        LoggingSetup.configure(tempDir.resolve("candy.log"));

        assertFalse(root.getUseParentHandlers());
    }

    @Test
    void writesInfoAndFineMessagesToTheFile() throws IOException {
        Path file = tempDir.resolve("candy.log");
        LoggingSetup.configure(file);

        Logger logger = Logger.getLogger("com.candy.sample");
        logger.info("info-message");
        logger.fine("fine-message");
        String content = readLog(file);

        assertTrue(content.contains("INFO    com.candy.sample - "
                + "info-message"));
        assertTrue(content.contains("FINE    com.candy.sample - "
                + "fine-message"));
    }

    @Test
    void doesNotWriteFinerMessagesToTheFile() throws IOException {
        Path file = tempDir.resolve("candy.log");
        LoggingSetup.configure(file);

        Logger.getLogger("com.candy.sample").finer("finer-message");

        assertFalse(readLog(file).contains("finer-message"));
    }

    @Test
    void createsMissingDirectories() throws IOException {
        Path file = tempDir.resolve("logs").resolve("deep").resolve("a.log");

        LoggingSetup.configure(file);

        assertTrue(Files.exists(file));
    }

    @Test
    void reconfiguringReplacesTheHandlers() throws IOException {
        LoggingSetup.configure(tempDir.resolve("first.log"));
        Handler first = root.getHandlers()[0];

        LoggingSetup.configure(tempDir.resolve("second.log"));

        assertEquals(2, root.getHandlers().length);
        assertFalse(root.getHandlers()[0] == first);
    }

    @Test
    void failedConfigurationKeepsTheCurrentHandlers() throws IOException {
        LoggingSetup.configure(tempDir.resolve("candy.log"));
        Handler console = root.getHandlers()[0];
        Path blocker = Files.writeString(tempDir.resolve("blocker"), "file");

        assertThrows(IOException.class, () -> LoggingSetup
                .configure(blocker.resolve("candy.log")));

        assertEquals(2, root.getHandlers().length);
        assertSame(console, root.getHandlers()[0]);
    }
}