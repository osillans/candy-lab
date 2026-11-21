package com.candy.xml;

import static com.candy.support.XmlSamples.CARAMEL;
import static com.candy.support.XmlSamples.CHOCOLATE;
import static com.candy.support.XmlSamples.document;
import static com.candy.support.XmlSamples.replace;
import static com.candy.support.XmlSamples.without;
import static com.candy.support.XmlSamples.write;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.candy.support.CapturingHandler;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class XmlValidatorTest {

    private static final Path SCHEMA = Path.of("data", "candy.xsd");

    private final Logger logger =
            Logger.getLogger(XmlValidator.class.getName());

    @TempDir
    Path tempDir;

    private CapturingHandler handler;
    private XmlValidator validator;

    @BeforeEach
    void setUp() {
        handler = CapturingHandler.attachTo(logger);
        validator = new XmlValidator(SCHEMA);
    }

    @AfterEach
    void tearDown() {
        handler.detach();
    }

    private List<String> errorsOf(String xml) throws IOException {
        return validator.validate(write(tempDir, "candy.xml", xml));
    }

    private void assertValid(String xml) throws IOException {
        assertEquals(List.of(), errorsOf(xml));
    }

    private void assertInvalid(String xml) throws IOException {
        List<String> errors = errorsOf(xml);

        assertFalse(errors.isEmpty());
        assertTrue(errors.get(0).startsWith("line "));
    }

    @Test
    void acceptsSampleFile() {
        assertEquals(List.of(), validator.validate(
                Path.of("data", "candy.xml")));
    }

    @Test
    void acceptsCaramelAndChocolateSweets() throws IOException {
        assertValid(document(CARAMEL, CHOCOLATE));
    }

    @Test
    void acceptsEnergyBoundaries() throws IOException {
        assertValid(document(replace(CARAMEL, ">390<", ">1<")));
        assertValid(document(replace(CARAMEL, ">390<", ">900<")));
    }

    @Test
    void rejectsEnergyOutsideBoundaries() throws IOException {
        assertInvalid(document(replace(CARAMEL, ">390<", ">0<")));
        assertInvalid(document(replace(CARAMEL, ">390<", ">901<")));
    }

    @Test
    void acceptsWaterBoundaries() throws IOException {
        assertValid(document(replace(CARAMEL, ">8<", ">0<")));
        assertValid(document(replace(CARAMEL, ">8<", ">100<")));
    }

    @Test
    void rejectsWaterOutsideBoundaries() throws IOException {
        assertInvalid(document(replace(CARAMEL, ">8<", ">-1<")));
        assertInvalid(document(replace(CARAMEL, ">8<", ">101<")));
    }

    @Test
    void acceptsMilligramBoundaries() throws IOException {
        assertValid(document(replace(CARAMEL, ">62000<", ">0<")));
        assertValid(document(replace(CARAMEL, ">62000<", ">100000<")));
    }

    @Test
    void rejectsMilligramsOutsideBoundaries() throws IOException {
        assertInvalid(document(replace(CARAMEL, ">62000<", ">-1<")));
        assertInvalid(document(replace(CARAMEL, ">62000<", ">100001<")));
    }

    @Test
    void acceptsGramBoundaries() throws IOException {
        assertValid(document(replace(CARAMEL, ">0.0<", ">100<")));
        assertValid(document(replace(CARAMEL, ">97.5<", ">0<")));
    }

    @Test
    void rejectsGramsOutsideBoundaries() throws IOException {
        assertInvalid(document(replace(CARAMEL, ">97.5<", ">100.1<")));
        assertInvalid(document(replace(CARAMEL, ">97.5<", ">-0.1<")));
    }

    @Test
    void rejectsTooManyFractionDigits() throws IOException {
        assertInvalid(document(replace(CARAMEL, ">97.5<", ">97.55<")));
    }

    @Test
    void rejectsNonNumericEnergy() throws IOException {
        assertInvalid(document(replace(CARAMEL, ">390<", ">abc<")));
    }

    @Test
    void acceptsNameLengthBoundaries() throws IOException {
        assertValid(document(replace(CARAMEL, "Sunny Caramel", "Ab")));
        assertValid(document(replace(CARAMEL, "Sunny Caramel",
                "x".repeat(40))));
    }

    @Test
    void rejectsNameLengthOutsideBoundaries() throws IOException {
        assertInvalid(document(replace(CARAMEL, "Sunny Caramel", "A")));
        assertInvalid(document(replace(CARAMEL, "Sunny Caramel",
                "x".repeat(41))));
    }

    @Test
    void acceptsProductionLengthBoundaries() throws IOException {
        assertValid(document(replace(CARAMEL, "Kyiv Confectionery", "AB")));
        assertValid(document(replace(CARAMEL, "Kyiv Confectionery",
                "y".repeat(60))));
    }

    @Test
    void rejectsProductionLengthOutsideBoundaries() throws IOException {
        assertInvalid(document(replace(CARAMEL, "Kyiv Confectionery",
                "A")));
        assertInvalid(document(replace(CARAMEL, "Kyiv Confectionery",
                "y".repeat(61))));
    }

    @Test
    void rejectsValueOutsideTypeEnumeration() throws IOException {
        assertInvalid(document(replace(CARAMEL, ">Caramel<", ">Gum<")));
    }

    @Test
    void rejectsValueOutsideChocolateEnumeration() throws IOException {
        assertInvalid(document(replace(CHOCOLATE, ">Dark<", ">Ruby<")));
    }

    @Test
    void acceptsIdBoundaries() throws IOException {
        assertValid(document(replace(CARAMEL, "\"C001\"", "\"C000\"")));
        assertValid(document(replace(CARAMEL, "\"C001\"", "\"C999\"")));
    }

    @Test
    void rejectsIdThatDoesNotMatchThePattern() throws IOException {
        for (String id : List.of("c001", "C01", "X001", "C0001", "1001")) {
            assertInvalid(document(replace(CARAMEL, "\"C001\"",
                    "\"" + id + "\"")));
        }
    }

    @Test
    void rejectsDuplicateIds() throws IOException {
        assertInvalid(document(CARAMEL, CARAMEL));
    }

    @Test
    void rejectsMissingIdAttribute() throws IOException {
        assertInvalid(document(replace(CARAMEL, " id=\"C001\"", "")));
    }

    @Test
    void rejectsMissingUnitAttribute() throws IOException {
        assertInvalid(document(replace(CARAMEL, "<Energy unit=\"kcal\">",
                "<Energy>")));
    }

    @Test
    void rejectsWrongUnitValue() throws IOException {
        assertInvalid(document(replace(CARAMEL, "unit=\"kcal\"",
                "unit=\"kJ\"")));
    }

    @Test
    void rejectsMissingRequiredElement() throws IOException {
        assertInvalid(document(without(CARAMEL, "Name")));
    }

    @Test
    void rejectsUnexpectedElement() throws IOException {
        assertInvalid(document(replace(CARAMEL, "</Sweet>",
                "<Extra/></Sweet>")));
    }

    @Test
    void rejectsElementsInWrongOrder() throws IOException {
        String xml = replace(without(CARAMEL, "Production"), "<Name>",
                "<Production>Factory</Production><Name>");

        assertInvalid(document(xml));
    }

    @Test
    void rejectsCandyWithoutSweets() throws IOException {
        assertInvalid("<Candy/>");
    }

    @Test
    void rejectsUnknownRootElement() throws IOException {
        assertInvalid("<Sweets/>");
    }

    @Test
    void leavesChocolateConsistencyToJavaCode() throws IOException {
        assertValid(document(without(CHOCOLATE, "ChocolateType")));
        assertValid(document(replace(CARAMEL, "<Vanillin",
                "<ChocolateType>Milk</ChocolateType><Vanillin")));
    }

    @Test
    void reportsMalformedXml() throws IOException {
        List<String> errors = errorsOf("<Candy><Sweet></Candy>");

        assertTrue(errors.get(errors.size() - 1)
                .startsWith("Cannot validate"));
    }

    @Test
    void reportsMissingXmlFile() {
        List<String> errors =
                validator.validate(tempDir.resolve("missing.xml"));

        assertFalse(errors.isEmpty());
    }

    @Test
    void reportsMissingSchemaFile() {
        XmlValidator broken = new XmlValidator(tempDir.resolve("none.xsd"));

        List<String> errors = broken.validate(Path.of("data", "candy.xml"));

        assertEquals(1, errors.size());
        assertTrue(errors.get(0).startsWith("Cannot validate"));
    }

    @Test
    void logsSuccess() {
        validator.validate(Path.of("data", "candy.xml"));

        assertTrue(handler.contains(Level.INFO, "Validating"));
        assertTrue(handler.contains(Level.INFO, "is valid"));
    }

    @Test
    void logsEveryErrorAsSevere() throws IOException {
        List<String> errors = errorsOf(document(replace(CARAMEL, ">390<",
                ">0<")));

        for (String error : errors) {
            assertTrue(handler.contains(Level.SEVERE, error));
        }
        assertFalse(handler.contains(Level.INFO, "is valid"));
    }
}