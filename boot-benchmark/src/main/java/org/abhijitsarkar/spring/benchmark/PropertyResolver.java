package org.abhijitsarkar.spring.benchmark;

import java.util.Locale;
import java.util.Objects;

public class PropertyResolver {
    private PropertyResolver() {
    }

    public static String resolveOrElseThrow(String key) {
        String value = resolve(key);

        Objects.requireNonNull(value, String.format("Property %s is required", key));

        return value;
    }

    public static String resolve(String key) {
        String value = System.getProperties().getProperty(key);
        if (Objects.isNull(value)) {
            value = System.getenv(mainClassEnv(key, ""));
        }
        if (Objects.isNull(value)) {
            value = System.getenv(mainClassEnv(key, "_"));
        }

        return value;
    }

    private static String mainClassEnv(String key, String hyphenReplacement) {
        return key
                .replace('.', '_')
                .replaceAll("-", hyphenReplacement)
                .toUpperCase(Locale.ENGLISH);
    }
}
