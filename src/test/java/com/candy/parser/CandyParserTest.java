package com.candy.parser;

import static com.candy.support.XmlSamples.CARAMEL;
import static com.candy.support.XmlSamples.CHOCOLATE;
import static com.candy.support.XmlSamples.document;
import static com.candy.support.XmlSamples.write;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.candy.exception.CandyException;
import com.candy.model.Sweet;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class CandyParserTest {

    private static final List<CandyParser> PARSERS = List.of(
            new DomCandyParser(), new SaxCandyParser(),
            new StaxCandyParser());

    @TempDir
    Path tempDir;

    @Test
    void allParsersReadTheSampleFileIdentically() {
        Path sample = Path.of("data", "candy.xml");
        List<Sweet> expected = PARSERS.get(0).parse(sample);

        assertEquals(8, expected.size());
        for (CandyParser parser : PARSERS) {
            assertEquals(expected, parser.parse(sample));
        }
    }

    @Test
    void allParsersReadTwoSweetsIdentically() throws IOException {
        Path file = write(tempDir, "two.xml", document(CHOCOLATE, CARAMEL));
        List<Sweet> expected = PARSERS.get(0).parse(file);

        assertEquals(2, expected.size());
        for (CandyParser parser : PARSERS) {
            assertEquals(expected, parser.parse(file));
        }
    }

    @Test
    void allParsersRejectTheSameBrokenDocument() throws IOException {
        Path file = write(tempDir, "broken.xml", "<Candy><Sweet>");

        for (CandyParser parser : PARSERS) {
            assertThrows(CandyException.class, () -> parser.parse(file));
        }
    }

    @Test
    void allParsersRejectTheSameInvalidSweet() throws IOException {
        Path file = write(tempDir, "invalid.xml",
                document(CARAMEL.replace(">390<", ">0<")));

        for (CandyParser parser : PARSERS) {
            assertThrows(CandyException.class, () -> parser.parse(file));
        }
    }

    @Test
    void parserNamesAreDistinct() {
        Set<String> names = new HashSet<>();
        for (CandyParser parser : PARSERS) {
            names.add(parser.getName());
        }

        assertEquals(Set.of("DOM", "SAX", "StAX"), names);
    }
}