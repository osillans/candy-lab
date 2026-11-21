package com.candy.model;

public enum ChocolateKind implements XmlEnum {
    DARK("Dark"),
    MILK("Milk"),
    WHITE("White");

    private final String xmlValue;

    ChocolateKind(String xmlValue) {
        this.xmlValue = xmlValue;
    }

    @Override
    public String xmlValue() {
        return xmlValue;
    }
}