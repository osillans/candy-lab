package com.candy.parser;

import com.candy.exception.CandyException;
import com.candy.model.Sweet;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public abstract class AbstractCandyParser implements CandyParser {

    private static final Logger LOGGER =
            Logger.getLogger(AbstractCandyParser.class.getName());

    private final String name;

    protected AbstractCandyParser(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public final List<Sweet> parse(Path xmlFile) {
        LOGGER.info("Parsing " + xmlFile + " with " + name + " parser");
        try (InputStream input = Files.newInputStream(xmlFile)) {
            List<Sweet> sweets = parseStream(input);
            sweets.forEach(sweet ->
                    LOGGER.fine(name + " parsed " + sweet.summary()));
            LOGGER.info(name + " parser read " + sweets.size() + " sweets");
            return sweets;
        } catch (IOException | SAXException | ParserConfigurationException
                | XMLStreamException e) {
            throw new CandyException("Cannot parse " + xmlFile + " with "
                    + name + " parser: " + e.getMessage(), e);
        }
    }

    protected abstract List<Sweet> parseStream(InputStream input)
            throws IOException, SAXException, ParserConfigurationException,
            XMLStreamException;
}