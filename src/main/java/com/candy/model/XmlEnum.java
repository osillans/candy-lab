package com.candy.model;

public interface XmlEnum {

    String xmlValue();

    static <E extends Enum<E> & XmlEnum> E fromXml(Class<E> type,
            String text) {
        for (E constant : type.getEnumConstants()) {
            if (constant.xmlValue().equals(text)) {
                return constant;
            }
        }
        throw new IllegalArgumentException(
                "Unknown " + type.getSimpleName() + " value: " + text);
    }
}