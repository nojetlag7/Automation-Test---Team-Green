package org.teamgreen.mentorconnectautomation.ui;

import com.codeborne.selenide.WebDriverRunner;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import org.teamgreen.mentorconnectautomation.base.BaseUiTest;
import org.teamgreen.mentorconnectautomation.config.TestConfig;
import org.teamgreen.mentorconnectautomation.pages.LoginPage;
import org.testng.annotations.Test;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("MentorConnect UI")
@Feature("Authentication")
public class LoginUiTest extends BaseUiTest {

    @Test
    @Description("UI-01 Valid login opens the dashboard")
    public void validLoginOpensDashboard() {
        LoginPage login = new LoginPage().open();
        login.signIn(TestConfig.mentorEmail(), TestConfig.mentorPassword());
        login.shouldReachDashboard();
        webdriver().shouldHave(urlContaining("/dashboard"));
        assertThat(WebDriverRunner.url()).contains("/dashboard");
    }

    @Test
    @Description("UI-02 Invalid password stays on the login page")
    public void invalidPasswordStaysOnLogin() {
        LoginPage login = new LoginPage().open();
        login.signIn(TestConfig.mentorEmail(), "not-the-password");
        login.shouldStayOnLoginWithError("Invalid email or password");
        assertThat(WebDriverRunner.url()).contains("/login");
    }
}
