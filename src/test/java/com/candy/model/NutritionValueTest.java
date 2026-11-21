package com.candy.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class NutritionValueTest {

    @Test
    void keepsAllValues() {
        NutritionValue value = new NutritionValue(2.5, 10.5, 82.0);

        assertEquals(2.5, value.proteins(), 0.0001);
        assertEquals(10.5, value.fats(), 0.0001);
        assertEquals(82.0, value.carbohydrates(), 0.0001);
    }

    @Test
    void acceptsZeroValues() {
        NutritionValue value = new NutritionValue(0.0, 0.0, 0.0);

        assertEquals(0.0, value.carbohydrates(), 0.0001);
    }

    @Test
    void acceptsSingleComponentOfOneHundredGrams() {
        assertEquals(100.0,
                new NutritionValue(100.0, 0.0, 0.0).proteins(), 0.0001);
        assertEquals(100.0,
                new NutritionValue(0.0, 100.0, 0.0).fats(), 0.0001);
        assertEquals(100.0,
                new NutritionValue(0.0, 0.0, 100.0).carbohydrates(), 0.0001);
    }

    @Test
    void acceptsTotalOfExactlyOneHundredGrams() {
        NutritionValue value = new NutritionValue(50.0, 25.0, 25.0);

        assertEquals(100.0, value.proteins() + value.fats()
                + value.carbohydrates(), 0.0001);
    }

    @Test
    void rejectsNegativeProteins() {
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionValue(-0.1, 0.0, 0.0));
    }

    @Test
    void rejectsNegativeFats() {
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionValue(0.0, -0.1, 0.0));
    }

    @Test
    void rejectsNegativeCarbohydrates() {
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionValue(0.0, 0.0, -0.1));
    }

    @Test
    void rejectsProteinsAboveOneHundred() {
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionValue(100.1, 0.0, 0.0));
    }

    @Test
    void rejectsFatsAboveOneHundred() {
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionValue(0.0, 100.1, 0.0));
    }

    @Test
    void rejectsCarbohydratesAboveOneHundred() {
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionValue(0.0, 0.0, 100.1));
    }

    @Test
    void rejectsNotANumber() {
        assertThrows(IllegalArgumentException.class,
                () -> new NutritionValue(Double.NaN, 0.0, 0.0));
    }

    @Test
    void rejectsTotalAboveOneHundredGrams() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new NutritionValue(50.0, 30.0, 20.1));

        assertTrue(error.getMessage().contains("together"));
    }

    @Test
    void equalValuesAreEqual() {
        assertEquals(new NutritionValue(1.0, 2.0, 3.0),
                new NutritionValue(1.0, 2.0, 3.0));
    }
}