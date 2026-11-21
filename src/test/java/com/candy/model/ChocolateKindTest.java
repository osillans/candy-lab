package com.candy.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class ChocolateKindTest {

    @Test
    void hasThreeKinds() {
        assertEquals(3, ChocolateKind.values().length);
    }

    @Test
    void xmlValuesAreDeclaredInSchema() throws IOException {
        String schema = Files.readString(Path.of("data", "candy.xsd"));

        for (ChocolateKind kind : ChocolateKind.values()) {
            assertTrue(schema.contains(
                    "<xs:enumeration value=\"" + kind.xmlValue() + "\"/>"));
        }
    }

    @Test
    void everyKindIsFoundByItsXmlValue() {
        for (ChocolateKind kind : ChocolateKind.values()) {
            assertSame(kind, XmlEnum.fromXml(ChocolateKind.class,
                    kind.xmlValue()));
        }
    }

    @Test
    void xmlValuesMatchTheSchemaSpelling() {
        assertEquals("Dark", ChocolateKind.DARK.xmlValue());
        assertEquals("Milk", ChocolateKind.MILK.xmlValue());
        assertEquals("White", ChocolateKind.WHITE.xmlValue());
    }
}