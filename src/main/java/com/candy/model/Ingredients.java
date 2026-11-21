package com.candy.model;

import java.util.Optional;

public record Ingredients(int waterMl, int sugarMg, int fructoseMg,
        Optional<ChocolateKind> chocolateKind, int vanillinMg) {

    private static final int MAX_WATER_ML = 100;
    private static final int MAX_MILLIGRAMS = 100_000;

    public Ingredients {
        Validation.checkRange(waterMl, "waterMl", 0, MAX_WATER_ML);
        Validation.checkRange(sugarMg, "sugarMg", 0, MAX_MILLIGRAMS);
        Validation.checkRange(fructoseMg, "fructoseMg", 0, MAX_MILLIGRAMS);
        Validation.checkPresent(chocolateKind, "chocolateKind");
        Validation.checkRange(vanillinMg, "vanillinMg", 0, MAX_MILLIGRAMS);
    }
}