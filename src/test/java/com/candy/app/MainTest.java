package com.candy.app;

import static com.candy.support.UtilityClassAssertions.assertUtilityClass;
import static com.candy.support.XmlSamples.CARAMEL;
import static com.candy.support.XmlSamples.document;
import static com.candy.support.XmlSamples.write;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.candy.support.AppFilesFixtures;
import com.candy.support.LoggingState;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class MainTest {

    @TempDir
    Path tempDir;

    @AfterEach
    void tearDown() {
        LoggingState.reset();
    }

    private String logAfterRun() throws IOException {
        LoggingState.reset();
        return Files.readString(tempDir.resolve("logs").resolve("test.log"));
    }

    @Test
    void isUtilityClass() throws Exception {
        assertUtilityClass(Main.class);
    }

    @Test
    void runWritesTheGroupedFileAndTheLog() throws IOException {
        AppFiles files = AppFilesFixtures.projectDataWithOutputIn(tempDir);

        Main.run(files);

        assertTrue(Files.exists(files.output()));
        String log = logAfterRun();
        assertTrue(log.contains("INFO    com.candy.app.CandyApplication - "
                + "Application finished"));
        assertFalse(log.contains("SEVERE"));
    }

    @Test
    void runLogsFailureInsteadOfThrowing() throws IOException {
        Path invalid = write(tempDir, "invalid.xml",
                document(CARAMEL.replace(">390<", ">0<")));
        AppFiles good = AppFilesFixtures.projectDataWithOutputIn(tempDir);
        AppFiles files = new AppFiles(invalid, good.xsd(), good.xsl(),
                good.output(), good.log());

        Main.run(files);

        String log = logAfterRun();
        assertTrue(log.contains("SEVERE  com.candy.app.Main - "
                + "Application failed"));
        assertTrue(log.contains("is not valid"));
        assertFalse(Files.exists(files.output()));
    }

    @Test
    void mainRunsWithTheDefaultFiles() throws IOException {
        Main.main(new String[0]);

        assertTrue(Files.exists(AppFiles.defaults().output()));
        assertTrue(Files.exists(AppFiles.defaults().log()));
        assertEquals(8, Files.readAllLines(AppFiles.defaults().output())
                .stream().filter(line -> line.contains("<Sweet ")).count());
    }
}