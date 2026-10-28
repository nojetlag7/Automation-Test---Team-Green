package org.teamgreen.mentorconnectautomation.api;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.teamgreen.mentorconnectautomation.base.BaseApiTest;
import org.teamgreen.mentorconnectautomation.data.TestDataFactory;
import org.testng.annotations.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("MentorConnect API")
@Feature("Events")
public class EventsApiTest extends BaseApiTest {

    @Test
    @Description("API-01 Unauthenticated event list is rejected")
    public void eventsRequireAuthentication() {
        Response response = anonymous().when().get("/api/events");

        assertThat(response.statusCode()).isEqualTo(401);
        assertThat(response.contentType()).contains("application/json");
        assertThat(response.jsonPath().getString("error")).isNotBlank();
    }

    @Test
    @Description("API-04 Create event matches the event contract")
    public void createEventReturnsSavedEvent() {
        String title = TestDataFactory.eventTitle();
        EventsApi events = mentorEvents();
        try {
            Response response = events.create(TestDataFactory.publicEvent(title));

            assertThat(response.statusCode()).isEqualTo(201);
            assertThat(response.contentType()).contains("application/json");
            Object id = response.jsonPath().get("id");
            Object description = response.jsonPath().get("description");
            Object type = response.jsonPath().get("type");
            Object date = response.jsonPath().get("date");
            Object online = response.jsonPath().get("isOnline");
            Object visibility = response.jsonPath().get("visibility");
            Object location = response.jsonPath().get("location");
            Object maxParticipants = response.jsonPath().get("maxParticipants");
            Object creator = response.jsonPath().get("creator");
            Object creatorId = response.jsonPath().get("creator.id");
            assertThat(id).isInstanceOf(String.class);
            assertThat((String) id).isNotBlank();
            assertThat(response.jsonPath().getString("title")).isEqualTo(title);
            assertThat(description).isEqualTo("Scheduled by the Team Green suite.");
            assertThat(type).isEqualTo("WORKSHOP");
            assertThat(date).isInstanceOf(String.class);
            assertThat((String) date).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}(\\.\\d+)?Z");
            assertThat(online).isInstanceOf(Boolean.class).isEqualTo(false);
            assertThat(visibility).isEqualTo("PUBLIC");
            assertThat(location).isEqualTo("QA Lab");
            assertThat(maxParticipants).isInstanceOf(Number.class);
            assertThat(((Number) maxParticipants).intValue()).isEqualTo(10);
            assertThat(creator).isInstanceOf(Map.class);
            assertThat(creatorId).isInstanceOf(String.class);
        } finally {
            events.deleteByTitleQuietly(title);
        }
    }

    @Test
    @Description("API-05 Read event by id returns the same title")
    public void readEventById() {
        String title = TestDataFactory.eventTitle();
        EventsApi events = mentorEvents();
        String id = null;
        try {
            id = createdId(events, title);
            Response response = events.get(id);

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getString("title")).isEqualTo(title);
        } finally {
            if (id != null) {
                events.delete(id);
            }
        }
    }

    @Test
    @Description("API-06 Update event title")
    public void updateEventTitle() {
        String title = TestDataFactory.eventTitle();
        String updated = title + "-edit";
        EventsApi events = mentorEvents();
        String id = null;
        try {
            id = createdId(events, title);
            Response response = events.updateTitle(id, updated);

            assertThat(response.statusCode()).isEqualTo(200);
            assertThat(response.jsonPath().getString("title")).isEqualTo(updated);
        } finally {
            events.deleteByTitleQuietly(title);
            events.deleteByTitleQuietly(updated);
        }
    }

    @Test
    @Description("API-07 Delete event then the id is gone")
    public void deleteEventThenGetIsNotFound() {
        String title = TestDataFactory.eventTitle();
        EventsApi events = mentorEvents();
        String id = createdId(events, title);

        Response deleted = events.delete(id);
        Response missing = events.get(id);

        assertThat(deleted.statusCode()).isEqualTo(200);
        assertThat(deleted.jsonPath().getString("message")).contains("deleted");
        assertThat(missing.statusCode()).isEqualTo(404);
        assertThat(missing.jsonPath().getString("error")).contains("not found");
    }

    @Test
    @Description("API-08 Event title length boundaries")
    public void titleLengthBoundaries() {
        EventsApi events = mentorEvents();

        Response tooShort = events.create(TestDataFactory.publicEvent("ab"));
        assertThat(tooShort.statusCode()).isEqualTo(400);
        assertThat(tooShort.asString()).contains("Title must be at least 3 characters");

        Response tooLong = events.create(TestDataFactory.publicEvent("a".repeat(101)));
        assertThat(tooLong.statusCode()).isEqualTo(400);
        assertThat(tooLong.asString()).contains("Title must be less than 100 characters");
    }

    private EventsApi mentorEvents() {
        return new EventsApi(withSession(SessionAuth.signInMentor()));
    }

    private String createdId(EventsApi events, String title) {
        Response created = events.create(TestDataFactory.publicEvent(title));
        assertThat(created.statusCode()).isEqualTo(201);
        return created.jsonPath().getString("id");
    }
}
