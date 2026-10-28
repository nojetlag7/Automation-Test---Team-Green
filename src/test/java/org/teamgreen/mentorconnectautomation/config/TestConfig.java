package org.teamgreen.mentorconnectautomation.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class TestConfig {
    private static final Properties PROPS = new Properties();

    static {
        try (InputStream in = TestConfig.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (in == null) {
                throw new IllegalStateException(
                        "Missing src/test/resources/config.properties. Copy config.properties.example and fill in the mentor account.");
            }
            PROPS.load(in);
        } catch (IOException e) {
            throw new IllegalStateException("Could not read config.properties", e);
        }
    }

    private TestConfig() {
    }

    public static String baseUrl() {
        return required("baseUrl");
    }

    public static String mentorEmail() {
        return required("mentorEmail");
    }

    public static String mentorPassword() {
        return required("mentorPassword");
    }

    private static String required(String key) {
        String override = System.getProperty(key);
        String value = override == null || override.isBlank() ? PROPS.getProperty(key) : override;
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Set " + key + " in config.properties");
        }
        return value.trim();
    }
}
