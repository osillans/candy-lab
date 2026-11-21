package com.candy.support;

import com.candy.app.AppFiles;
import java.nio.file.Path;

public final class AppFilesFixtures {

    private AppFilesFixtures() {
    }

    public static AppFiles projectDataWithOutputIn(Path directory) {
        return new AppFiles(
                Path.of("data", "candy.xml"),
                Path.of("data", "candy.xsd"),
                Path.of("data", "candy-grouped.xsl"),
                directory.resolve("out").resolve("grouped.xml"),
                directory.resolve("logs").resolve("test.log"));
    }
}