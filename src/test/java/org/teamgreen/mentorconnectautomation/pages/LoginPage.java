package org.teamgreen.mentorconnectautomation.pages;

import com.codeborne.selenide.Selenide;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    public LoginPage open() {
        Selenide.open("/login");
        $("h1").shouldHave(text("Sign In"));
        return this;
    }

    public void signIn(String email, String password) {
        $("#email").setValue(email);
        $("#password").setValue(password);
        $("button[type='submit']").click();
    }

    public void shouldReachDashboard() {
        $("h1").shouldHave(text("Welcome back, Automation"));
    }

    public void shouldStayOnLoginWithError(String message) {
        $("h1").shouldHave(text("Sign In"));
        $(".text-red-600, .text-red-400").shouldBe(visible).shouldHave(text(message));
    }
}
