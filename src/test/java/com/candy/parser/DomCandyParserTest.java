package com.candy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class DomCandyParserTest extends ParserTestBase {

    @Override
    CandyParser parser() {
        return new DomCandyParser();
    }

    @Test
    void isNamedDom() {
        assertEquals("DOM", parser().getName());
    }
}