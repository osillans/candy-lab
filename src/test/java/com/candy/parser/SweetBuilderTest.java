package com.candy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.candy.exception.CandyException;
import com.candy.support.SweetFixtures;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SweetBuilderTest {

    private static final Map<String, String> CARAMEL = new LinkedHashMap<>();

    static {
        CARAMEL.put("Name", "Sunny Caramel");
        CARAMEL.put("Energy", "390");
        CARAMEL.put("Type", "Caramel");
        CARAMEL.put("Water", "8");
        CARAMEL.put("Sugar", "62000");
        CARAMEL.put("Fructose", "9000");
        CARAMEL.put("Vanillin", "12");
        CARAMEL.put("Proteins", "0.0");
        CARAMEL.put("Fats", "0.1");
        CARAMEL.put("Carbohydrates", "97.5");
        CARAMEL.put("Production", "Kyiv Confectionery");
    }

    private static SweetBuilder caramelBuilderWith(String id,
            Map<String, String> changes) {
        SweetBuilder builder = new SweetBuilder(id);
        Map<String, String> values = new LinkedHashMap<>(CARAMEL);
        values.putAll(changes);
        values.forEach(builder::set);
        return builder;
    }

    private static SweetBuilder caramelBuilder() {
        return caramelBuilderWith("C001", Map.of());
    }

    @Test
    void buildsCaramelSweet() {
        assertEquals(SweetFixtures.caramel(), caramelBuilder().build());
    }

    @Test
    void buildsChocolateSweet() {
        SweetBuilder builder = new SweetBuilder("C003");
        builder.set("Name", "Dark Truffle");
        builder.set("Energy", "550");
        builder.set("Type", "ChocolateFilled");
        builder.set("Water", "2");
        builder.set("Sugar", "38000");
        builder.set("Fructose", "1500");
        builder.set("ChocolateType", "Dark");
        builder.set("Vanillin", "35");
        builder.set("Proteins", "5.5");
        builder.set("Fats", "35.0");
        builder.set("Carbohydrates", "52.0");
        builder.set("Production", "Lviv Sweets");

        assertEquals(SweetFixtures.chocolate(), builder.build());
    }

    @Test
    void trimsSurroundingWhitespace() {
        SweetBuilder builder = new SweetBuilder("C001");
        CARAMEL.forEach((tag, text) -> builder.set(tag,
                "\n    " + text + "  \n"));

        assertEquals(SweetFixtures.caramel(), builder.build());
    }

    @Test
    void ignoresContainerAndUnknownTags() {
        SweetBuilder builder = caramelBuilder();
        builder.set("Ingredients", "\n   \n");
        builder.set("Value", "text");
        builder.set("Extra", "anything");

        assertEquals(SweetFixtures.caramel(), builder.build());
    }

    @Test
    void reportsEveryMissingRequiredTag() {
        for (String tag : CARAMEL.keySet()) {
            SweetBuilder builder = new SweetBuilder("C001");
            CARAMEL.forEach((key, text) -> {
                if (!key.equals(tag)) {
                    builder.set(key, text);
                }
            });

            CandyException error = assertThrows(CandyException.class,
                    builder::build);

            assertEquals("Missing <" + tag + "> in sweet C001",
                    error.getMessage());
        }
    }

    @Test
    void rejectsNonNumericValues() {
        List<String> numeric = List.of("Energy", "Water", "Sugar",
                "Fructose", "Vanillin", "Proteins", "Fats", "Carbohydrates");

        for (String tag : numeric) {
            SweetBuilder builder = new SweetBuilder("C001");

            CandyException error = assertThrows(CandyException.class,
                    () -> builder.set(tag, "abc"));

            assertTrue(error.getMessage()
                    .startsWith("Invalid value 'abc' in <" + tag + ">"));
            assertInstanceOf(NumberFormatException.class, error.getCause());
        }
    }

    @Test
    void rejectsDecimalWhereIntegerIsExpected() {
        SweetBuilder builder = new SweetBuilder("C001");

        assertThrows(CandyException.class,
                () -> builder.set("Energy", "390.5"));
    }

    @Test
    void rejectsEmptyNumber() {
        SweetBuilder builder = new SweetBuilder("C001");

        assertThrows(CandyException.class, () -> builder.set("Energy", " "));
    }

    @Test
    void rejectsUnknownCandyType() {
        SweetBuilder builder = new SweetBuilder("C001");

        CandyException error = assertThrows(CandyException.class,
                () -> builder.set("Type", "Marshmallow"));

        assertTrue(error.getMessage().contains("Unknown CandyType value"));
    }

    @Test
    void rejectsUnknownChocolateKind() {
        SweetBuilder builder = new SweetBuilder("C001");

        CandyException error = assertThrows(CandyException.class,
                () -> builder.set("ChocolateType", "Ruby"));

        assertTrue(error.getMessage().contains("Unknown ChocolateKind"));
    }

    @Test
    void wrapsSweetValidationErrors() {
        SweetBuilder builder = caramelBuilderWith("C001",
                Map.of("Energy", "0"));

        CandyException error = assertThrows(CandyException.class,
                builder::build);

        assertTrue(error.getMessage().startsWith(
                "Invalid sweet C001: energy must be between 1 and 900"));
        assertInstanceOf(IllegalArgumentException.class, error.getCause());
    }

    @Test
    void wrapsIngredientValidationErrors() {
        SweetBuilder builder = caramelBuilderWith("C001",
                Map.of("Water", "101"));

        CandyException error = assertThrows(CandyException.class,
                builder::build);

        assertTrue(error.getMessage().contains("waterMl"));
    }

    @Test
    void wrapsNutritionValidationErrors() {
        SweetBuilder builder = caramelBuilderWith("C001",
                Map.of("Proteins", "60.0"));

        CandyException error = assertThrows(CandyException.class,
                builder::build);

        assertTrue(error.getMessage().contains("together"));
    }

    @Test
    void rejectsMissingSweetId() {
        SweetBuilder builder = caramelBuilderWith(null, Map.of());

        CandyException error = assertThrows(CandyException.class,
                builder::build);

        assertTrue(error.getMessage().startsWith("Invalid sweet null: id"));
    }
}