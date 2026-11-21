package com.candy.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class StaxCandyParserTest extends ParserTestBase {

    @Override
    CandyParser parser() {
        return new StaxCandyParser();
    }

    @Test
    void isNamedStax() {
        assertEquals("StAX", parser().getName());
    }
}