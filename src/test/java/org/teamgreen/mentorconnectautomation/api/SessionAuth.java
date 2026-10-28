package org.teamgreen.mentorconnectautomation.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.teamgreen.mentorconnectautomation.config.TestConfig;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.given;

public final class SessionAuth {
    private SessionAuth() {
    }

    public static Map<String, String> signIn(String email, String password) {
        String baseUrl = TestConfig.baseUrl();
        Response csrf = given()
                .redirects().follow(false)
                .accept(ContentType.JSON)
                .when()
                .get(baseUrl + "/api/auth/csrf")
                .then()
                .statusCode(200)
                .extract()
                .response();

        String csrfToken = csrf.jsonPath().getString("csrfToken");
        Map<String, String> cookies = new HashMap<>(csrf.getCookies());

        Response login = given()
                .redirects().follow(false)
                .cookies(cookies)
                .contentType(ContentType.URLENC)
                .formParam("csrfToken", csrfToken)
                .formParam("email", email)
                .formParam("password", password)
                .formParam("callbackUrl", baseUrl + "/dashboard")
                .formParam("json", "true")
                .when()
                .post(baseUrl + "/api/auth/callback/credentials");

        login.getDetailedCookies().forEach(cookie -> cookies.put(cookie.getName(), cookie.getValue()));

        boolean hasSession = cookies.keySet().stream().anyMatch(name -> name.contains("session-token"));
        if (!hasSession) {
            throw new IllegalStateException(
                    "Sign-in did not set a session cookie. Status " + login.statusCode() + " body " + login.asString());
        }
        return cookies;
    }

    public static Map<String, String> signInMentor() {
        return signIn(TestConfig.mentorEmail(), TestConfig.mentorPassword());
    }
}
