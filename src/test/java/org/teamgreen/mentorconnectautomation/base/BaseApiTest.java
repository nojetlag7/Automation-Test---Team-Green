package org.teamgreen.mentorconnectautomation.base;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.teamgreen.mentorconnectautomation.api.RedactingAllureFilter;
import org.teamgreen.mentorconnectautomation.config.TestConfig;

import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class BaseApiTest {
    protected RequestSpecification anonymous() {
        return given()
                .baseUri(TestConfig.baseUrl())
                .accept(ContentType.JSON)
                .contentType(ContentType.JSON)
                .filter(new RedactingAllureFilter());
    }

    protected RequestSpecification withSession(Map<String, String> cookies) {
        return anonymous().cookies(cookies);
    }
}
