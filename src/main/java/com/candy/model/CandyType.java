package com.candy.model;

public enum CandyType implements XmlEnum {
    CARAMEL("Caramel", false),
    TOFFEE("Toffee", false),
    CHOCOLATE_FILLED("ChocolateFilled", true),
    CHOCOLATE_PLAIN("ChocolatePlain", true);

    private final String xmlValue;
    private final boolean chocolate;

    CandyType(String xmlValue, boolean chocolate) {
        this.xmlValue = xmlValue;
        this.chocolate = chocolate;
    }

    @Override
    public String xmlValue() {
        return xmlValue;
    }

    public boolean isChocolate() {
        return chocolate;
    }
}