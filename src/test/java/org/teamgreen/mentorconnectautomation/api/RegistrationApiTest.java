package org.teamgreen.mentorconnectautomation.api;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.response.Response;
import org.teamgreen.mentorconnectautomation.base.BaseApiTest;
import org.teamgreen.mentorconnectautomation.data.TestDataFactory;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("MentorConnect API")
@Feature("Registration")
public class RegistrationApiTest extends BaseApiTest {

    @Test(groups = "registration")
    @Description("API-02 A new mentee is created as an active user")
    public void registerMentee() {
        String email = TestDataFactory.menteeEmail();
        Response response = anonymous()
                .body(TestDataFactory.menteeRegistration(email, "Testing123!"))
                .when()
                .post("/api/auth/register");

        assertThat(response.statusCode()).isEqualTo(201);
        assertThat(response.jsonPath().getString("user.email")).isEqualTo(email);
        assertThat(response.jsonPath().getString("user.status")).isEqualTo("ACTIVE");
    }

    @Test(groups = "registration")
    @Description("API-03 A short password is rejected")
    public void registerRejectsShortPassword() {
        Response response = anonymous()
                .body(TestDataFactory.menteeRegistration(TestDataFactory.menteeEmail(), "Abcdefg"))
                .when()
                .post("/api/auth/register");

        assertThat(response.statusCode()).isEqualTo(400);
        assertThat(response.jsonPath().getString("error")).contains("at least 8 characters");
    }
}
