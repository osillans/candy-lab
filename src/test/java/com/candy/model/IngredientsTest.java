package com.candy.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import org.junit.jupiter.api.Test;

class IngredientsTest {

    private static Ingredients create(int water, int sugar, int fructose,
            int vanillin) {
        return new Ingredients(water, sugar, fructose, Optional.empty(),
                vanillin);
    }

    @Test
    void keepsAllValues() {
        Ingredients ingredients = new Ingredients(8, 62000, 9000,
                Optional.of(ChocolateKind.MILK), 12);

        assertEquals(8, ingredients.waterMl());
        assertEquals(62000, ingredients.sugarMg());
        assertEquals(9000, ingredients.fructoseMg());
        assertEquals(Optional.of(ChocolateKind.MILK),
                ingredients.chocolateKind());
        assertEquals(12, ingredients.vanillinMg());
    }

    @Test
    void acceptsLowerBoundaries() {
        Ingredients ingredients = create(0, 0, 0, 0);

        assertEquals(0, ingredients.waterMl());
        assertTrue(ingredients.chocolateKind().isEmpty());
    }

    @Test
    void acceptsUpperBoundaries() {
        Ingredients ingredients = create(100, 100000, 100000, 100000);

        assertEquals(100, ingredients.waterMl());
        assertEquals(100000, ingredients.vanillinMg());
    }

    @Test
    void rejectsNegativeWater() {
        assertThrows(IllegalArgumentException.class,
                () -> create(-1, 0, 0, 0));
    }

    @Test
    void rejectsTooMuchWater() {
        assertThrows(IllegalArgumentException.class,
                () -> create(101, 0, 0, 0));
    }

    @Test
    void rejectsNegativeSugar() {
        assertThrows(IllegalArgumentException.class,
                () -> create(0, -1, 0, 0));
    }

    @Test
    void rejectsTooMuchSugar() {
        assertThrows(IllegalArgumentException.class,
                () -> create(0, 100001, 0, 0));
    }

    @Test
    void rejectsNegativeFructose() {
        assertThrows(IllegalArgumentException.class,
                () -> create(0, 0, -1, 0));
    }

    @Test
    void rejectsTooMuchFructose() {
        assertThrows(IllegalArgumentException.class,
                () -> create(0, 0, 100001, 0));
    }

    @Test
    void rejectsNegativeVanillin() {
        assertThrows(IllegalArgumentException.class,
                () -> create(0, 0, 0, -1));
    }

    @Test
    void rejectsTooMuchVanillin() {
        assertThrows(IllegalArgumentException.class,
                () -> create(0, 0, 0, 100001));
    }

    @Test
    void rejectsMissingChocolateKindHolder() {
        assertThrows(IllegalArgumentException.class,
                () -> new Ingredients(0, 0, 0, null, 0));
    }

    @Test
    void equalIngredientsAreEqual() {
        assertEquals(create(1, 2, 3, 4), create(1, 2, 3, 4));
        assertEquals(create(1, 2, 3, 4).hashCode(),
                create(1, 2, 3, 4).hashCode());
    }
}