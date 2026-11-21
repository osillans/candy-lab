package com.candy.parser;

import com.candy.exception.CandyException;
import com.candy.model.CandyType;
import com.candy.model.ChocolateKind;
import com.candy.model.Ingredients;
import com.candy.model.NutritionValue;
import com.candy.model.Sweet;
import com.candy.model.XmlEnum;
import java.util.Optional;
import java.util.function.Function;

final class SweetBuilder {

    private static final Function<String, Integer> INTEGER = Integer::valueOf;
    private static final Function<String, Double> DECIMAL = Double::valueOf;

    private final String id;
    private String name;
    private Integer energy;
    private CandyType type;
    private Integer water;
    private Integer sugar;
    private Integer fructose;
    private ChocolateKind chocolateKind;
    private Integer vanillin;
    private Double proteins;
    private Double fats;
    private Double carbohydrates;
    private String production;

    SweetBuilder(String id) {
        this.id = id;
    }

    void set(String tag, String text) {
        String value = text.trim();
        switch (tag) {
            case XmlTags.NAME -> name = value;
            case XmlTags.ENERGY -> energy = parse(tag, value, INTEGER);
            case XmlTags.TYPE -> type = parse(tag, value,
                    item -> XmlEnum.fromXml(CandyType.class, item));
            case XmlTags.WATER -> water = parse(tag, value, INTEGER);
            case XmlTags.SUGAR -> sugar = parse(tag, value, INTEGER);
            case XmlTags.FRUCTOSE -> fructose = parse(tag, value, INTEGER);
            case XmlTags.CHOCOLATE_TYPE -> chocolateKind = parse(tag, value,
                    item -> XmlEnum.fromXml(ChocolateKind.class, item));
            case XmlTags.VANILLIN -> vanillin = parse(tag, value, INTEGER);
            case XmlTags.PROTEINS -> proteins = parse(tag, value, DECIMAL);
            case XmlTags.FATS -> fats = parse(tag, value, DECIMAL);
            case XmlTags.CARBOHYDRATES ->
                    carbohydrates = parse(tag, value, DECIMAL);
            case XmlTags.PRODUCTION -> production = value;
            default -> {
            }
        }
    }

    Sweet build() {
        try {
            Ingredients ingredients = new Ingredients(
                    required(water, XmlTags.WATER),
                    required(sugar, XmlTags.SUGAR),
                    required(fructose, XmlTags.FRUCTOSE),
                    Optional.ofNullable(chocolateKind),
                    required(vanillin, XmlTags.VANILLIN));
            NutritionValue value = new NutritionValue(
                    required(proteins, XmlTags.PROTEINS),
                    required(fats, XmlTags.FATS),
                    required(carbohydrates, XmlTags.CARBOHYDRATES));
            return new Sweet(id, required(name, XmlTags.NAME),
                    required(energy, XmlTags.ENERGY),
                    required(type, XmlTags.TYPE), ingredients, value,
                    required(production, XmlTags.PRODUCTION));
        } catch (IllegalArgumentException e) {
            throw new CandyException(
                    "Invalid sweet " + id + ": " + e.getMessage(), e);
        }
    }

    private <T> T required(T value, String tag) {
        if (value == null) {
            throw new CandyException(
                    "Missing <" + tag + "> in sweet " + id);
        }
        return value;
    }

    private static <T> T parse(String tag, String text,
            Function<String, T> converter) {
        try {
            return converter.apply(text);
        } catch (IllegalArgumentException e) {
            throw new CandyException("Invalid value '" + text + "' in <"
                    + tag + ">: " + e.getMessage(), e);
        }
    }
}