package com.candy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class SaxCandyParserTest extends ParserTestBase {

    @Override
    CandyParser parser() {
        return new SaxCandyParser();
    }

    @Test
    void isNamedSax() {
        assertEquals("SAX", parser().getName());
    }
}