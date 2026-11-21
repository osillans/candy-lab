package com.candy.model;

public record NutritionValue(double proteins, double fats,
        double carbohydrates) {

    private static final double MAX_GRAMS = 100.0;

    public NutritionValue {
        Validation.checkRange(proteins, "proteins", 0.0, MAX_GRAMS);
        Validation.checkRange(fats, "fats", 0.0, MAX_GRAMS);
        Validation.checkRange(carbohydrates, "carbohydrates", 0.0, MAX_GRAMS);
        if (proteins + fats + carbohydrates > MAX_GRAMS) {
            throw new IllegalArgumentException(
                    "proteins, fats and carbohydrates must not exceed "
                            + MAX_GRAMS + " g together");
        }
    }
}