package com.candy.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class XmlEnumTest {

    enum Color implements XmlEnum {
        RED("red"),
        GREEN("green");

        private final String xmlValue;

        Color(String xmlValue) {
            this.xmlValue = xmlValue;
        }

        @Override
        public String xmlValue() {
            return xmlValue;
        }
    }

    @Test
    void findsConstantByXmlValue() {
        assertSame(Color.RED, XmlEnum.fromXml(Color.class, "red"));
        assertSame(Color.GREEN, XmlEnum.fromXml(Color.class, "green"));
    }

    @Test
    void rejectsUnknownValue() {
        IllegalArgumentException error = assertThrows(
                IllegalArgumentException.class,
                () -> XmlEnum.fromXml(Color.class, "blue"));

        assertEquals("Unknown Color value: blue", error.getMessage());
    }

    @Test
    void matchesCaseSensitively() {
        assertThrows(IllegalArgumentException.class,
                () -> XmlEnum.fromXml(Color.class, "RED"));
    }

    @Test
    void rejectsNullValue() {
        assertThrows(IllegalArgumentException.class,
                () -> XmlEnum.fromXml(Color.class, null));
    }
}