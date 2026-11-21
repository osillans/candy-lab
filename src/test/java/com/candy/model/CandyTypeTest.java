package com.candy.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class CandyTypeTest {

    @Test
    void hasFourTypes() {
        assertEquals(4, CandyType.values().length);
    }

    @Test
    void xmlValuesAreDeclaredInSchema() throws IOException {
        String schema = Files.readString(Path.of("data", "candy.xsd"));

        for (CandyType type : CandyType.values()) {
            assertTrue(schema.contains(
                    "<xs:enumeration value=\"" + type.xmlValue() + "\"/>"));
        }
    }

    @Test
    void everyTypeIsFoundByItsXmlValue() {
        for (CandyType type : CandyType.values()) {
            assertSame(type, XmlEnum.fromXml(CandyType.class,
                    type.xmlValue()));
        }
    }

    @Test
    void onlyChocolateTypesAreChocolate() {
        assertFalse(CandyType.CARAMEL.isChocolate());
        assertFalse(CandyType.TOFFEE.isChocolate());
        assertTrue(CandyType.CHOCOLATE_FILLED.isChocolate());
        assertTrue(CandyType.CHOCOLATE_PLAIN.isChocolate());
    }

    @Test
    void xmlValuesMatchTheSchemaSpelling() {
        assertEquals("Caramel", CandyType.CARAMEL.xmlValue());
        assertEquals("Toffee", CandyType.TOFFEE.xmlValue());
        assertEquals("ChocolateFilled",
                CandyType.CHOCOLATE_FILLED.xmlValue());
        assertEquals("ChocolatePlain", CandyType.CHOCOLATE_PLAIN.xmlValue());
    }
}