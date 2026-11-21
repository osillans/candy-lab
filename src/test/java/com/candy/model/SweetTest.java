package com.candy.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.candy.support.SweetFixtures;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SweetTest {

    private static final Ingredients PLAIN =
            new Ingredients(8, 62000, 9000, Optional.empty(), 12);
    private static final Ingredients WITH_KIND = new Ingredients(2, 38000,
            1500, Optional.of(ChocolateKind.DARK), 35);
    private static final NutritionValue VALUE =
            new NutritionValue(0.0, 0.1, 97.5);

    private static Sweet create(String id, String name, int energy,
            String production) {
        return new Sweet(id, name, energy, CandyType.CARAMEL, PLAIN, VALUE,
                production);
    }

    @Test
    void keepsAllValues() {
        Sweet sweet = SweetFixtures.caramel();

        assertEquals("C001", sweet.id());
        assertEquals("Sunny Caramel", sweet.name());
        assertEquals(390, sweet.energy());
        assertEquals(CandyType.CARAMEL, sweet.type());
        assertEquals(PLAIN, sweet.ingredients());
        assertEquals(VALUE, sweet.value());
        assertEquals("Kyiv Confectionery", sweet.production());
    }

    @Test
    void acceptsChocolateSweetWithKind() {
        Sweet sweet = SweetFixtures.chocolate();

        assertTrue(sweet.type().isChocolate());
        assertEquals(Optional.of(ChocolateKind.DARK),
                sweet.ingredients().chocolateKind());
    }

    @Test
    void rejectsChocolateSweetWithoutKind() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> new Sweet("C001", "Truffle", 500,
                        CandyType.CHOCOLATE_PLAIN, PLAIN, VALUE, "Factory"));

        assertTrue(error.getMessage().contains("chocolate kind"));
    }

    @Test
    void rejectsNonChocolateSweetWithKind() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sweet("C001", "Toffee", 500, CandyType.TOFFEE,
                        WITH_KIND, VALUE, "Factory"));
    }

    @Test
    void acceptsIdOfFourCharacters() {
        assertEquals("C999", create("C999", "Candy", 100, "Factory").id());
    }

    @Test
    void rejectsShortId() {
        assertThrows(IllegalArgumentException.class,
                () -> create("C01", "Candy", 100, "Factory"));
    }

    @Test
    void rejectsLongId() {
        assertThrows(IllegalArgumentException.class,
                () -> create("C0001", "Candy", 100, "Factory"));
    }

    @Test
    void rejectsMissingId() {
        assertThrows(IllegalArgumentException.class,
                () -> create(null, "Candy", 100, "Factory"));
    }

    @Test
    void acceptsNameLengthBoundaries() {
        assertEquals("Ab", create("C001", "Ab", 100, "Factory").name());
        String longest = "x".repeat(40);
        assertEquals(longest,
                create("C001", longest, 100, "Factory").name());
    }

    @Test
    void rejectsTooShortName() {
        assertThrows(IllegalArgumentException.class,
                () -> create("C001", "A", 100, "Factory"));
    }

    @Test
    void rejectsTooLongName() {
        assertThrows(IllegalArgumentException.class,
                () -> create("C001", "x".repeat(41), 100, "Factory"));
    }

    @Test
    void acceptsEnergyBoundaries() {
        assertEquals(1, create("C001", "Candy", 1, "Factory").energy());
        assertEquals(900, create("C001", "Candy", 900, "Factory").energy());
    }

    @Test
    void rejectsEnergyBelowMinimum() {
        assertThrows(IllegalArgumentException.class,
                () -> create("C001", "Candy", 0, "Factory"));
    }

    @Test
    void rejectsEnergyAboveMaximum() {
        assertThrows(IllegalArgumentException.class,
                () -> create("C001", "Candy", 901, "Factory"));
    }

    @Test
    void rejectsMissingType() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sweet("C001", "Candy", 100, null, PLAIN, VALUE,
                        "Factory"));
    }

    @Test
    void rejectsMissingIngredients() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sweet("C001", "Candy", 100, CandyType.CARAMEL,
                        null, VALUE, "Factory"));
    }

    @Test
    void rejectsMissingValue() {
        assertThrows(IllegalArgumentException.class,
                () -> new Sweet("C001", "Candy", 100, CandyType.CARAMEL,
                        PLAIN, null, "Factory"));
    }

    @Test
    void acceptsProductionLengthBoundaries() {
        assertEquals("AB", create("C001", "Candy", 100, "AB").production());
        String longest = "y".repeat(60);
        assertEquals(longest,
                create("C001", "Candy", 100, longest).production());
    }

    @Test
    void rejectsTooShortProduction() {
        assertThrows(IllegalArgumentException.class,
                () -> create("C001", "Candy", 100, "A"));
    }

    @Test
    void rejectsTooLongProduction() {
        assertThrows(IllegalArgumentException.class,
                () -> create("C001", "Candy", 100, "y".repeat(61)));
    }

    @Test
    void summaryDescribesTheSweet() {
        assertEquals(
                "C001 Sunny Caramel (Caramel, 390 kcal, Kyiv Confectionery)",
                SweetFixtures.caramel().summary());
    }

    @Test
    void equalSweetsAreEqual() {
        assertEquals(SweetFixtures.caramel(), SweetFixtures.caramel());
        assertEquals(SweetFixtures.caramel().hashCode(),
                SweetFixtures.caramel().hashCode());
    }

    @Test
    void differentSweetsAreNotEqual() {
        assertNotEquals(SweetFixtures.caramel(), SweetFixtures.chocolate());
    }
}