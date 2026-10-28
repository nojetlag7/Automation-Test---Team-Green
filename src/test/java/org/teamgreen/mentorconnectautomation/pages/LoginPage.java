package org.teamgreen.mentorconnectautomation.pages;

import com.codeborne.selenide.Selenide;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class LoginPage {
    public LoginPage open() {
        Selenide.open("/login");
        $("h1").shouldHave(text("Sign In"));
        $("#email").shouldBe(visible);
        return this;
    }

    public void signIn(String email, String password) {
        enter(email, password);
        clickSignIn();
        boolean reachedDashboard = $("h1").is(text("Welcome back"), Duration.ofSeconds(8));
        boolean showedError = $(".text-red-600, .text-red-400").is(visible);
        boolean stillSubmitting = $("button[type='submit']").is(exactText("Signing In..."));
        if (!reachedDashboard && !showedError && !stillSubmitting) {
            enter(email, password);
            clickSignIn();
        }
    }

    public void shouldReachDashboard() {
        $("h1").shouldHave(text("Welcome back, Automation"), Duration.ofSeconds(20));
    }

    private void enter(String email, String password) {
        $("#email").setValue(email).shouldHave(value(email));
        $("#password").setValue(password);
    }

    private void clickSignIn() {
        $("button[type='submit']").shouldHave(exactText("Sign In")).click();
    }

    public void shouldStayOnLoginWithError(String message) {
        $("h1").shouldHave(text("Sign In"));
        $(".text-red-600, .text-red-400").shouldBe(visible).shouldHave(text(message));
    }
}
