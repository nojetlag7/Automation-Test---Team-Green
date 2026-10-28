package org.teamgreen.mentorconnectautomation.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class EventsApi {
    private static final Logger LOG = LoggerFactory.getLogger(EventsApi.class);
    private final RequestSpecification spec;

    public EventsApi(RequestSpecification spec) {
        this.spec = spec;
    }

    public Response listUpcoming() {
        return given().spec(spec).queryParam("upcoming", true).queryParam("limit", 100).when().get("/api/events");
    }

    public Response create(Map<String, Object> body) {
        return given().spec(spec).body(body).when().post("/api/events");
    }

    public Response get(String id) {
        return given().spec(spec).when().get("/api/events/{id}", id);
    }

    public Response updateTitle(String id, String title) {
        return given().spec(spec).body(Map.of("title", title)).when().put("/api/events/{id}", id);
    }

    public Response delete(String id) {
        return given().spec(spec).when().delete("/api/events/{id}", id);
    }

    public void deleteByTitleQuietly(String title) {
        try {
            Response list = listUpcoming();
            if (list.statusCode() != 200) {
                LOG.warn("Cleanup could not list events (status {})", list.statusCode());
                return;
            }
            List<Map<String, Object>> events = list.jsonPath().getList("events");
            if (events == null) {
                return;
            }
            for (Map<String, Object> event : events) {
                if (title.equals(event.get("title"))) {
                    Response deleted = delete(String.valueOf(event.get("id")));
                    if (deleted.statusCode() != 200) {
                        LOG.warn("Cleanup delete for {} returned {}", title, deleted.statusCode());
                    }
                }
            }
        } catch (RuntimeException e) {
            LOG.warn("Cleanup for {} failed: {}", title, e.getMessage());
        }
    }
}
