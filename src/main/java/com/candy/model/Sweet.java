package com.candy.model;

public record Sweet(String id, String name, int energy, CandyType type,
        Ingredients ingredients, NutritionValue value, String production) {

    public Sweet {
        Validation.checkText(id, "id", 4, 4);
        Validation.checkText(name, "name", 2, 40);
        Validation.checkRange(energy, "energy", 1, 900);
        Validation.checkPresent(type, "type");
        Validation.checkPresent(ingredients, "ingredients");
        Validation.checkPresent(value, "value");
        Validation.checkText(production, "production", 2, 60);
        if (type.isChocolate() != ingredients.chocolateKind().isPresent()) {
            throw new IllegalArgumentException(
                    "chocolate kind is required for chocolate sweets only");
        }
    }

    public String summary() {
        return String.format("%s %s (%s, %d kcal, %s)", id, name,
                type.xmlValue(), energy, production);
    }
}