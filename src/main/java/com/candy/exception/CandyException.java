package com.candy.exception;

public class CandyException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CandyException(String message) {
        super(message);
    }

    public CandyException(String message, Throwable cause) {
        super(message, cause);
    }
}