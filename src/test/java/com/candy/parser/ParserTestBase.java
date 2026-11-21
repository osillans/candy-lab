package com.candy.parser;

import static com.candy.support.XmlSamples.CARAMEL;
import static com.candy.support.XmlSamples.CHOCOLATE;
import static com.candy.support.XmlSamples.document;
import static com.candy.support.XmlSamples.replace;
import static com.candy.support.XmlSamples.without;
import static com.candy.support.XmlSamples.write;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.candy.exception.CandyException;
import com.candy.model.Sweet;
import com.candy.support.SweetFixtures;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

abstract class ParserTestBase {

    private static final String COMPACT = """
            <Sweet id="C001"><Production>Kyiv Confectionery</Production>\
            <Value><Carbohydrates unit="g">97.5</Carbohydrates>\
            <Fats unit="g">0.1</Fats><Proteins unit="g">0.0</Proteins>\
            </Value><Ingredients><Vanillin unit="mg">12</Vanillin>\
            <Fructose unit="mg">9000</Fructose>\
            <Sugar unit="mg">62000</Sugar><Water unit="ml">8</Water>\
            </Ingredients><Type>Caramel</Type>\
            <Energy unit="kcal">390</Energy><Name>Sunny Caramel</Name>\
            </Sweet>""";

    private static final List<String> REQUIRED_TAGS = List.of("Name",
            "Energy", "Type", "Water", "Sugar", "Fructose", "Vanillin",
            "Proteins", "Fats", "Carbohydrates", "Production");

    private static final List<List<String>> NUMBERS = List.of(
            List.of("Energy", "390"), List.of("Water", "8"),
            List.of("Sugar", "62000"), List.of("Fructose", "9000"),
            List.of("Vanillin", "12"), List.of("Proteins", "0.0"),
            List.of("Fats", "0.1"), List.of("Carbohydrates", "97.5"));

    @TempDir
    Path tempDir;

    abstract CandyParser parser();

    private List<Sweet> parseText(String xml) throws IOException {
        return parser().parse(write(tempDir, "candy.xml", xml));
    }

    private CandyException failureOf(String xml) throws IOException {
        return failureOf(write(tempDir, "candy.xml", xml));
    }

    private CandyException failureOf(Path file) {
        return assertThrows(CandyException.class,
                () -> parser().parse(file));
    }

    @Test
    void parsesSampleFile() {
        List<Sweet> sweets = parser().parse(Path.of("data", "candy.xml"));

        assertEquals(8, sweets.size());
        assertEquals(SweetFixtures.caramel(), sweets.get(0));
        assertEquals(SweetFixtures.chocolate(), sweets.get(2));
        assertEquals("C008", sweets.get(7).id());
    }

    @Test
    void parsesCaramelSweet() throws IOException {
        assertEquals(List.of(SweetFixtures.caramel()),
                parseText(document(CARAMEL)));
    }

    @Test
    void parsesChocolateSweetWithKind() throws IOException {
        assertEquals(List.of(SweetFixtures.chocolate()),
                parseText(document(CHOCOLATE)));
    }

    @Test
    void keepsDocumentOrder() throws IOException {
        List<Sweet> sweets = parseText(document(CHOCOLATE, CARAMEL));

        assertEquals("C003", sweets.get(0).id());
        assertEquals("C001", sweets.get(1).id());
    }

    @Test
    void parsesCompactDocumentWithShuffledElements() throws IOException {
        assertEquals(List.of(SweetFixtures.caramel()),
                parseText("<Candy>" + COMPACT + "</Candy>"));
    }

    @Test
    void parsesEmptyCandy() throws IOException {
        assertTrue(parseText("<Candy/>").isEmpty());
    }

    @Test
    void ignoresUnknownElements() throws IOException {
        String xml = replace(CARAMEL, "</Sweet>",
                "<Extra>unused</Extra></Sweet>");

        assertEquals(List.of(SweetFixtures.caramel()),
                parseText(document(xml)));
    }

    @Test
    void readsCdataText() throws IOException {
        String xml = replace(CARAMEL, "Sunny Caramel",
                "<![CDATA[Sunny Caramel]]>");

        assertEquals("Sunny Caramel",
                parseText(document(xml)).get(0).name());
    }

    @Test
    void joinsTextBrokenByEntities() throws IOException {
        String xml = replace(CARAMEL, "Sunny Caramel", "Salt &amp; Pepper");

        assertEquals("Salt & Pepper",
                parseText(document(xml)).get(0).name());
    }

    @Test
    void rejectsEveryMissingRequiredElement() throws IOException {
        for (String tag : REQUIRED_TAGS) {
            CandyException error = failureOf(document(without(CARAMEL, tag)));

            assertTrue(error.getMessage().contains("<" + tag + ">"));
        }
    }

    @Test
    void rejectsNonNumericValues() throws IOException {
        for (List<String> number : NUMBERS) {
            String tag = number.get(0);
            String xml = replace(CARAMEL, ">" + number.get(1) + "</" + tag,
                    ">abc</" + tag);

            CandyException error = failureOf(document(xml));

            assertTrue(error.getMessage().contains("<" + tag + ">"));
        }
    }

    @Test
    void rejectsUnknownType() throws IOException {
        String xml = replace(CARAMEL, ">Caramel<", ">Marshmallow<");

        assertTrue(failureOf(document(xml)).getMessage()
                .contains("Marshmallow"));
    }

    @Test
    void rejectsUnknownChocolateKind() throws IOException {
        String xml = replace(CHOCOLATE, ">Dark<", ">Ruby<");

        assertTrue(failureOf(document(xml)).getMessage().contains("Ruby"));
    }

    @Test
    void rejectsEnergyBelowMinimum() throws IOException {
        String xml = replace(CARAMEL, ">390<", ">0<");

        assertTrue(failureOf(document(xml)).getMessage()
                .contains("energy"));
    }

    @Test
    void rejectsEnergyAboveMaximum() throws IOException {
        String xml = replace(CARAMEL, ">390<", ">901<");

        assertTrue(failureOf(document(xml)).getMessage()
                .contains("energy"));
    }

    @Test
    void rejectsChocolateWithoutKind() throws IOException {
        String xml = without(CHOCOLATE, "ChocolateType");

        assertTrue(failureOf(document(xml)).getMessage()
                .contains("chocolate kind"));
    }

    @Test
    void rejectsKindOnNonChocolateSweet() throws IOException {
        String xml = replace(CARAMEL, "</Ingredients>",
                "<ChocolateType>Milk</ChocolateType></Ingredients>");

        assertTrue(failureOf(document(xml)).getMessage()
                .contains("chocolate kind"));
    }

    @Test
    void rejectsMissingId() throws IOException {
        String xml = replace(CARAMEL, " id=\"C001\"", "");

        assertTrue(failureOf(document(xml)).getMessage().contains("id"));
    }

    @Test
    void rejectsMalformedXml() throws IOException {
        CandyException error =
                failureOf("<Candy><Sweet id=\"C001\"></Candy>");

        assertTrue(error.getMessage().contains("Cannot parse"));
    }

    @Test
    void rejectsEmptyFile() throws IOException {
        assertTrue(failureOf("").getMessage().contains("Cannot parse"));
    }

    @Test
    void rejectsMissingFile() {
        CandyException error = failureOf(tempDir.resolve("none.xml"));

        assertInstanceOf(IOException.class, error.getCause());
    }

    @Test
    void errorMessageNamesParserAndFile() throws IOException {
        CandyException error = failureOf("<Candy>");

        assertTrue(error.getMessage()
                .contains(parser().getName() + " parser"));
        assertTrue(error.getMessage().contains("candy.xml"));
    }
}