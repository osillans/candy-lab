package com.candy.parser;

import static com.candy.support.XmlSamples.write;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.candy.exception.CandyException;
import com.candy.model.Sweet;
import com.candy.support.CapturingHandler;
import com.candy.support.SweetFixtures;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.xml.sax.SAXException;

class AbstractCandyParserTest {

    @FunctionalInterface
    interface Body {
        List<Sweet> run(InputStream input) throws IOException, SAXException,
                ParserConfigurationException, XMLStreamException;
    }

    private static final class StubParser extends AbstractCandyParser {

        private final Body body;

        StubParser(Body body) {
            super("stub");
            this.body = body;
        }

        @Override
        protected List<Sweet> parseStream(InputStream input)
                throws IOException, SAXException,
                ParserConfigurationException, XMLStreamException {
            return body.run(input);
        }
    }

    private final Logger logger =
            Logger.getLogger(AbstractCandyParser.class.getName());

    @TempDir
    Path tempDir;

    private CapturingHandler handler;
    private Path file;

    @BeforeEach
    void setUp() throws IOException {
        handler = CapturingHandler.attachTo(logger);
        file = write(tempDir, "any.xml", "<Candy/>");
    }

    @AfterEach
    void tearDown() {
        handler.detach();
        logger.setLevel(null);
    }

    @Test
    void exposesItsName() {
        assertEquals("stub", new StubParser(input -> List.of()).getName());
    }

    @Test
    void returnsWhatParseStreamReturns() {
        List<Sweet> expected = List.of(SweetFixtures.caramel());

        List<Sweet> actual = new StubParser(input -> expected).parse(file);

        assertSame(expected, actual);
    }

    @Test
    void passesFileContentToParseStream() throws IOException {
        byte[][] read = new byte[1][];

        new StubParser(input -> {
            read[0] = input.readAllBytes();
            return List.of();
        }).parse(file);

        assertEquals("<Candy/>", new String(read[0]));
    }

    @Test
    void closesTheInputStream() {
        InputStream[] captured = new InputStream[1];

        new StubParser(input -> {
            captured[0] = input;
            return List.of();
        }).parse(file);

        assertThrows(IOException.class, () -> captured[0].read());
    }

    @Test
    void logsStartAndResult() {
        new StubParser(input -> List.of(SweetFixtures.caramel()))
                .parse(file);

        assertTrue(handler.contains(Level.INFO, "Parsing " + file
                + " with stub parser"));
        assertTrue(handler.contains(Level.INFO,
                "stub parser read 1 sweets"));
    }

    @Test
    void logsEverySweetAtFineLevel() {
        logger.setLevel(Level.FINE);

        new StubParser(input -> List.of(SweetFixtures.caramel()))
                .parse(file);

        assertTrue(handler.contains(Level.FINE,
                "stub parsed C001 Sunny Caramel"));
    }

    @Test
    void wrapsInputOutputFailure() {
        CandyException error = assertThrows(CandyException.class,
                () -> new StubParser(input -> {
                    throw new IOException("disk");
                }).parse(file));

        assertTrue(error.getMessage().contains("Cannot parse " + file));
        assertTrue(error.getMessage().contains("stub parser"));
        assertTrue(error.getMessage().contains("disk"));
        assertInstanceOf(IOException.class, error.getCause());
    }

    @Test
    void wrapsSaxFailure() {
        CandyException error = assertThrows(CandyException.class,
                () -> new StubParser(input -> {
                    throw new SAXException("sax");
                }).parse(file));

        assertInstanceOf(SAXException.class, error.getCause());
    }

    @Test
    void wrapsParserConfigurationFailure() {
        CandyException error = assertThrows(CandyException.class,
                () -> new StubParser(input -> {
                    throw new ParserConfigurationException("config");
                }).parse(file));

        assertInstanceOf(ParserConfigurationException.class,
                error.getCause());
    }

    @Test
    void wrapsStreamFailure() {
        CandyException error = assertThrows(CandyException.class,
                () -> new StubParser(input -> {
                    throw new XMLStreamException("stax");
                }).parse(file));

        assertInstanceOf(XMLStreamException.class, error.getCause());
    }

    @Test
    void doesNotWrapCandyExceptions() {
        CandyException original = new CandyException("model");

        CandyException thrown = assertThrows(CandyException.class,
                () -> new StubParser(input -> {
                    throw original;
                }).parse(file));

        assertSame(original, thrown);
    }

    @Test
    void reportsMissingFile() {
        Path missing = tempDir.resolve("missing.xml");

        CandyException error = assertThrows(CandyException.class,
                () -> new StubParser(input -> List.of()).parse(missing));

        assertInstanceOf(IOException.class, error.getCause());
    }
}