package com.candy.xml;

import com.candy.exception.CandyException;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Logger;
import javax.xml.transform.ErrorListener;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public final class XslTransformer {

    private static final Logger LOGGER =
            Logger.getLogger(XslTransformer.class.getName());

    private final Path stylesheet;

    public XslTransformer(Path stylesheet) {
        this.stylesheet = stylesheet;
    }

    public void transform(Path xmlFile, Path outputFile) {
        LOGGER.info("Transforming " + xmlFile + " with " + stylesheet);
        try {
            Files.createDirectories(outputFile.toAbsolutePath().getParent());
            ErrorListener listener = new LoggingErrorListener();
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setErrorListener(listener);
            Transformer transformer = factory.newTransformer(
                    new StreamSource(stylesheet.toFile()));
            transformer.setErrorListener(listener);
            try (OutputStream output = Files.newOutputStream(outputFile)) {
                transformer.transform(new StreamSource(xmlFile.toFile()),
                        new StreamResult(output));
            }
            LOGGER.info("Result written to " + outputFile);
        } catch (IOException | TransformerException e) {
            throw new CandyException("XSL transformation failed: "
                    + e.getMessage(), e);
        }
    }
}