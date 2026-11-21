package com.candy.app;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class AppFilesTest {

    @Test
    void resolvesFilesInsideTheGivenDirectory() {
        AppFiles files = AppFiles.inDirectory(Path.of("base"));

        assertEquals(Path.of("base", "data", "candy.xml"), files.xml());
        assertEquals(Path.of("base", "data", "candy.xsd"), files.xsd());
        assertEquals(Path.of("base", "data", "candy-grouped.xsl"),
                files.xsl());
        assertEquals(Path.of("base", "output", "candy-grouped.xml"),
                files.output());
        assertEquals(Path.of("base", "logs", "candy.log"), files.log());
    }

    @Test
    void defaultsAreRelativeToTheWorkingDirectory() {
        AppFiles files = AppFiles.defaults();

        assertEquals(Path.of("data", "candy.xml"), files.xml());
        assertEquals(Path.of("output", "candy-grouped.xml"),
                files.output());
    }

    @Test
    void defaultInputFilesExist() {
        AppFiles files = AppFiles.defaults();

        assertTrue(Files.exists(files.xml()));
        assertTrue(Files.exists(files.xsd()));
        assertTrue(Files.exists(files.xsl()));
    }

    @Test
    void equalFilesAreEqual() {
        assertEquals(AppFiles.inDirectory(Path.of("a")),
                AppFiles.inDirectory(Path.of("a")));
    }
}