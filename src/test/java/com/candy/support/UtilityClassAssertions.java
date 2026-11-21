package com.candy.support;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public final class UtilityClassAssertions {

    private UtilityClassAssertions() {
    }

    public static void assertUtilityClass(Class<?> type) throws Exception {
        assertTrue(Modifier.isFinal(type.getModifiers()));
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        assertEquals(1, constructors.length);
        Constructor<?> constructor = constructors[0];
        assertTrue(Modifier.isPrivate(constructor.getModifiers()));
        constructor.setAccessible(true);
        constructor.newInstance();
    }
}