package org.teamgreen.mentorconnectautomation.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class EventTitleData {
    private EventTitleData() {
    }

    public static Object[][] rows() {
        List<Object[]> rows = new ArrayList<>();
        try (InputStream in = EventTitleData.class.getClassLoader().getResourceAsStream("testdata/event-titles.csv")) {
            if (in == null) {
                throw new IllegalStateException("Missing testdata/event-titles.csv");
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                String line = reader.readLine();
                while ((line = reader.readLine()) != null) {
                    if (line.isBlank()) {
                        continue;
                    }
                    int split = line.lastIndexOf(',');
                    String title = line.substring(0, split);
                    String expected = line.substring(split + 1);
                    if ("MAX_PLUS_ONE".equals(title)) {
                        title = "a".repeat(101);
                    }
                    rows.add(new Object[]{title, expected});
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Could not read event title data", e);
        }
        return rows.toArray(Object[][]::new);
    }
}
