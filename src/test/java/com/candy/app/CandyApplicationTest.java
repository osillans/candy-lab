package com.candy.app;

import static com.candy.support.XmlSamples.CARAMEL;
import static com.candy.support.XmlSamples.document;
import static com.candy.support.XmlSamples.write;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.candy.exception.CandyException;
import com.candy.model.Sweet;
import com.candy.parser.AbstractCandyParser;
import com.candy.parser.CandyParser;
import com.candy.support.AppFilesFixtures;
import com.candy.support.CapturingHandler;
import com.candy.support.SweetFixtures;
import com.candy.xml.XmlValidator;
import com.candy.xml.XslTransformer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CandyApplicationTest {

    private static final Path XML = Path.of("data", "candy.xml");
    private static final Path XSD = Path.of("data", "candy.xsd");
    private static final Path XSL = Path.of("data", "candy-grouped.xsl");

    private final Logger appLogger =
            Logger.getLogger(CandyApplication.class.getName());
    private final Logger parserLogger =
            Logger.getLogger(AbstractCandyParser.class.getName());

    @Mock
    CandyParser first;

    @Mock
    CandyParser second;

    @TempDir
    Path tempDir;

    private CapturingHandler appLog;
    private CapturingHandler parserLog;
    private Path output;

    @BeforeEach
    void setUp() {
        appLog = CapturingHandler.attachTo(appLogger);
        parserLog = CapturingHandler.attachTo(parserLogger);
        output = tempDir.resolve("out").resolve("grouped.xml");
    }

    @AfterEach
    void tearDown() {
        appLog.detach();
        parserLog.detach();
    }

    private CandyApplication applicationWith(Path xml,
            CandyParser... parsers) {
        return new CandyApplication(xml, output, new XmlValidator(XSD),
                List.of(parsers), new XslTransformer(XSL));
    }

    private String firstMessageAfter(String header) {
        List<String> messages = appLog.messages();
        return messages.get(messages.indexOf("INFO: " + header) + 1);
    }

    @Test
    void runsTheWholePipelineOnTheSampleData() {
        AppFiles files = AppFilesFixtures.projectDataWithOutputIn(tempDir);

        List<Sweet> sweets = CandyApplication.create(files).run();

        assertEquals(8, sweets.size());
        assertTrue(Files.exists(files.output()));
    }

    @Test
    void usesDomSaxAndStaxParsers() {
        CandyApplication.create(AppFilesFixtures
                .projectDataWithOutputIn(tempDir)).run();

        assertTrue(parserLog.contains(Level.INFO, "DOM parser read 8"));
        assertTrue(parserLog.contains(Level.INFO, "SAX parser read 8"));
        assertTrue(parserLog.contains(Level.INFO, "StAX parser read 8"));
    }

    @Test
    void logsStartAndFinish() {
        CandyApplication.create(AppFilesFixtures
                .projectDataWithOutputIn(tempDir)).run();

        assertTrue(appLog.contains(Level.INFO, "Application started"));
        assertTrue(appLog.contains(Level.INFO, "Application finished"));
    }

    @Test
    void logsSweetsInEveryOrder() {
        CandyApplication.create(AppFilesFixtures
                .projectDataWithOutputIn(tempDir)).run();

        assertTrue(firstMessageAfter("Sweets sorted BY_NAME")
                .contains("C008 Cream Toffee"));
        assertTrue(firstMessageAfter("Sweets sorted BY_ENERGY")
                .contains("C007 Lemon Drop"));
        assertTrue(firstMessageAfter("Sweets sorted BY_PRODUCTION")
                .contains("C007 Lemon Drop"));
    }

    @Test
    void returnsTheSweetsInDocumentOrder() {
        List<Sweet> sweets = CandyApplication.create(AppFilesFixtures
                .projectDataWithOutputIn(tempDir)).run();

        assertEquals("C001", sweets.get(0).id());
        assertEquals("C008", sweets.get(7).id());
    }

    @Test
    void stopsOnInvalidXmlBeforeParsing() throws IOException {
        Path invalid = write(tempDir, "invalid.xml",
                document(CARAMEL.replace(">390<", ">0<")));

        CandyException error = assertThrows(CandyException.class,
                () -> applicationWith(invalid, first).run());

        assertTrue(error.getMessage().contains("is not valid"));
        verifyNoInteractions(first);
        assertFalse(Files.exists(output));
    }

    @Test
    void singleParserIsEnough() {
        List<Sweet> parsed = List.of(SweetFixtures.caramel());
        when(first.parse(XML)).thenReturn(parsed);

        List<Sweet> sweets = applicationWith(XML, first).run();

        assertSame(parsed, sweets);
        verify(first).parse(XML);
        assertTrue(Files.exists(output));
    }

    @Test
    void acceptsParsersThatAgree() {
        List<Sweet> parsed = List.of(SweetFixtures.caramel());
        when(first.parse(XML)).thenReturn(parsed);
        when(second.parse(XML)).thenReturn(List.of(SweetFixtures.caramel()));

        List<Sweet> sweets = applicationWith(XML, first, second).run();

        assertEquals(parsed, sweets);
        verify(first).parse(XML);
        verify(second).parse(XML);
    }

    @Test
    void rejectsParsersThatDisagree() {
        when(first.parse(XML)).thenReturn(List.of(SweetFixtures.caramel()));
        when(second.parse(XML)).thenReturn(
                List.of(SweetFixtures.chocolate()));
        when(first.getName()).thenReturn("first");
        when(second.getName()).thenReturn("second");

        CandyException error = assertThrows(CandyException.class,
                () -> applicationWith(XML, first, second).run());

        assertEquals("second parser result differs from first parser result",
                error.getMessage());
        assertFalse(Files.exists(output));
    }

    @Test
    void rejectsEmptyParserList() {
        assertThrows(IllegalArgumentException.class,
                () -> applicationWith(XML));
    }

    @Test
    void reportsFailedTransformation() {
        when(first.parse(XML)).thenReturn(List.of(SweetFixtures.caramel()));
        CandyApplication application = new CandyApplication(XML, output,
                new XmlValidator(XSD), List.of(first),
                new XslTransformer(tempDir.resolve("none.xsl")));

        CandyException error = assertThrows(CandyException.class,
                application::run);

        assertTrue(error.getMessage()
                .startsWith("XSL transformation failed"));
    }
}