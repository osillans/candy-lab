package com.candy.parser;

import static com.candy.support.UtilityClassAssertions.assertUtilityClass;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class XmlTagsTest {

    @Test
    void isUtilityClass() throws Exception {
        assertUtilityClass(XmlTags.class);
    }

    @Test
    void everyTagIsDeclaredInSchema() throws Exception {
        String schema = Files.readString(Path.of("data", "candy.xsd"));
        List<Field> fields = Arrays.stream(XmlTags.class.getDeclaredFields())
                .filter(field -> !field.isSynthetic()).toList();

        assertEquals(14, fields.size());
        for (Field field : fields) {
            field.setAccessible(true);
            String tag = (String) field.get(null);
            assertTrue(schema.contains("name=\"" + tag + "\""));
        }
    }
}