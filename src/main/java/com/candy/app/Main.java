package com.candy.app;

import com.candy.exception.CandyException;
import com.candy.logging.LoggingSetup;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main {

    private static final Logger LOGGER =
            Logger.getLogger(Main.class.getName());

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        run(AppFiles.defaults());
    }

    static void run(AppFiles files) throws IOException {
        LoggingSetup.configure(files.log());
        try {
            CandyApplication.create(files).run();
        } catch (CandyException e) {
            LOGGER.log(Level.SEVERE, "Application failed", e);
        }
    }
}