package com.candy.model;

final class Validation {

    private Validation() {
    }

    static void checkText(String value, String field, int minLength,
            int maxLength) {
        if (value == null || value.length() < minLength
                || value.length() > maxLength) {
            throw new IllegalArgumentException(field + " must have "
                    + minLength + ".." + maxLength + " characters");
        }
    }

    static <T extends Comparable<T>> void checkRange(T value, String field,
            T min, T max) {
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            throw new IllegalArgumentException(field + " must be between "
                    + min + " and " + max + " but was " + value);
        }
    }

    static void checkPresent(Object value, String field) {
        if (value == null) {
            throw new IllegalArgumentException(field + " must not be null");
        }
    }
}