package com.candy.model;

import static com.candy.support.UtilityClassAssertions.assertUtilityClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ValidationTest {

    @Test
    void isUtilityClass() throws Exception {
        assertUtilityClass(Validation.class);
    }

    @Test
    void acceptsTextOnBothLengthBoundaries() {
        Validation.checkText("ab", "name", 2, 4);
        Validation.checkText("abcd", "name", 2, 4);
    }

    @Test
    void rejectsNullText() {
        assertThrows(IllegalArgumentException.class,
                () -> Validation.checkText(null, "name", 2, 4));
    }

    @Test
    void rejectsTooShortText() {
        assertThrows(IllegalArgumentException.class,
                () -> Validation.checkText("a", "name", 2, 4));
    }

    @Test
    void rejectsTooLongText() {
        assertThrows(IllegalArgumentException.class,
                () -> Validation.checkText("abcde", "name", 2, 4));
    }

    @Test
    void textErrorNamesTheFieldAndTheLimits() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.checkText("a", "name", 2, 4));

        assertEquals("name must have 2..4 characters", error.getMessage());
    }

    @Test
    void acceptsIntegersOnBothBoundaries() {
        Validation.checkRange(1, "energy", 1, 900);
        Validation.checkRange(900, "energy", 1, 900);
    }

    @Test
    void acceptsDecimalsOnBothBoundaries() {
        Validation.checkRange(0.0, "fats", 0.0, 100.0);
        Validation.checkRange(100.0, "fats", 0.0, 100.0);
    }

    @Test
    void rejectsValueBelowMinimum() {
        assertThrows(IllegalArgumentException.class,
                () -> Validation.checkRange(0, "energy", 1, 900));
    }

    @Test
    void rejectsValueAboveMaximum() {
        assertThrows(IllegalArgumentException.class,
                () -> Validation.checkRange(901, "energy", 1, 900));
    }

    @Test
    void rejectsNotANumber() {
        assertThrows(IllegalArgumentException.class, () -> Validation
                .checkRange(Double.NaN, "fats", 0.0, 100.0));
    }

    @Test
    void rangeErrorNamesTheFieldAndTheValue() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.checkRange(901, "energy", 1, 900));

        assertTrue(error.getMessage().contains("energy"));
        assertTrue(error.getMessage().contains("901"));
    }

    @Test
    void acceptsPresentValue() {
        Validation.checkPresent("value", "field");
    }

    @Test
    void rejectsMissingValue() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> Validation.checkPresent(null, "field"));

        assertEquals("field must not be null", error.getMessage());
    }
}