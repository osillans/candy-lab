package com.candy.app;

import java.nio.file.Path;

public record AppFiles(Path xml, Path xsd, Path xsl, Path output, Path log) {

    public static AppFiles inDirectory(Path root) {
        return new AppFiles(
                root.resolve("data").resolve("candy.xml"),
                root.resolve("data").resolve("candy.xsd"),
                root.resolve("data").resolve("candy-grouped.xsl"),
                root.resolve("output").resolve("candy-grouped.xml"),
                root.resolve("logs").resolve("candy.log"));
    }

    public static AppFiles defaults() {
        return inDirectory(Path.of(""));
    }
}