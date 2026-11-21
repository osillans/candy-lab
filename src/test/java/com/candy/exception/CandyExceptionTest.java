package com.candy.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class CandyExceptionTest {

    @Test
    void keepsMessage() {
        CandyException exception = new CandyException("broken");

        assertEquals("broken", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void keepsMessageAndCause() {
        IllegalStateException cause = new IllegalStateException("root");

        CandyException exception = new CandyException("broken", cause);

        assertEquals("broken", exception.getMessage());
        assertSame(cause, exception.getCause());
    }

    @Test
    void isUncheckedException() {
        assertInstanceOf(RuntimeException.class, new CandyException("x"));
    }
}