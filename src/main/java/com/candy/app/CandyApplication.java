package com.candy.app;

import com.candy.exception.CandyException;
import com.candy.model.Sweet;
import com.candy.parser.CandyParser;
import com.candy.parser.DomCandyParser;
import com.candy.parser.SaxCandyParser;
import com.candy.parser.StaxCandyParser;
import com.candy.sort.SweetOrder;
import com.candy.xml.XmlValidator;
import com.candy.xml.XslTransformer;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

public final class CandyApplication {

    private static final Logger LOGGER =
            Logger.getLogger(CandyApplication.class.getName());

    private final Path xmlFile;
    private final Path outputFile;
    private final XmlValidator validator;
    private final List<CandyParser> parsers;
    private final XslTransformer transformer;

    public CandyApplication(Path xmlFile, Path outputFile,
            XmlValidator validator, List<CandyParser> parsers,
            XslTransformer transformer) {
        if (parsers.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one parser is required");
        }
        this.xmlFile = xmlFile;
        this.outputFile = outputFile;
        this.validator = validator;
        this.parsers = List.copyOf(parsers);
        this.transformer = transformer;
    }

    public static CandyApplication create(AppFiles files) {
        return new CandyApplication(files.xml(), files.output(),
                new XmlValidator(files.xsd()),
                List.of(new DomCandyParser(), new SaxCandyParser(),
                        new StaxCandyParser()),
                new XslTransformer(files.xsl()));
    }

    public List<Sweet> run() {
        LOGGER.info("Application started");
        List<String> errors = validator.validate(xmlFile);
        if (!errors.isEmpty()) {
            throw new CandyException("XML file " + xmlFile + " is not valid: "
                    + errors.size() + " error(s)");
        }
        List<Sweet> sweets = parseWithAllParsers();
        for (SweetOrder order : SweetOrder.values()) {
            logSorted(sweets, order);
        }
        transformer.transform(xmlFile, outputFile);
        LOGGER.info("Application finished");
        return sweets;
    }

    private List<Sweet> parseWithAllParsers() {
        CandyParser first = parsers.get(0);
        List<Sweet> sweets = first.parse(xmlFile);
        for (CandyParser other : parsers.subList(1, parsers.size())) {
            if (!sweets.equals(other.parse(xmlFile))) {
                throw new CandyException(other.getName()
                        + " parser result differs from " + first.getName()
                        + " parser result");
            }
        }
        return sweets;
    }

    private static void logSorted(List<Sweet> sweets, SweetOrder order) {
        LOGGER.info("Sweets sorted " + order);
        sweets.stream().sorted(order)
                .forEach(sweet -> LOGGER.info(sweet.summary()));
    }
}