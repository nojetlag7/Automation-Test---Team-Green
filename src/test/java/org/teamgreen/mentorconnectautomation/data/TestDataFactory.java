package org.teamgreen.mentorconnectautomation.data;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class TestDataFactory {
    private TestDataFactory() {
    }

    public static String eventTitle() {
        return "sqa-" + UUID.randomUUID().toString().substring(0, 8);
    }

    public static String menteeEmail() {
        return "sqa.green." + UUID.randomUUID() + "@example.com";
    }

    public static Map<String, Object> publicEvent(String title) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("title", title);
        body.put("description", "Scheduled by the Team Green suite.");
        body.put("type", "WORKSHOP");
        body.put("date", Instant.now().plus(7, ChronoUnit.DAYS).truncatedTo(ChronoUnit.SECONDS).toString());
        body.put("isOnline", false);
        body.put("visibility", "PUBLIC");
        body.put("location", "QA Lab");
        body.put("maxParticipants", 10);
        return body;
    }

    public static Map<String, Object> menteeRegistration(String email, String password) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("email", email);
        body.put("password", password);
        body.put("firstName", "Sqa");
        body.put("lastName", "Mentee");
        body.put("role", "MENTEE");
        return body;
    }
}
