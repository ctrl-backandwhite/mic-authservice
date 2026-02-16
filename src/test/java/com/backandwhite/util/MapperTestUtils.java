package com.backandwhite.util;

import java.lang.reflect.Field;

public final class MapperTestUtils {

    private MapperTestUtils() {
        // Utility class.
    }

    public static void setField(Object target, String fieldName, Object value) {
        try {
            Field field = target.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to set field '" + fieldName + "'", e);
        }
    }
}
