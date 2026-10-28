package org.teamgreen.mentorconnectautomation.base;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.teamgreen.mentorconnectautomation.config.TestConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

public abstract class BaseUiTest {
    private static boolean listenerRegistered;

    @BeforeClass
    public void configureBrowser() {
        Configuration.browser = "chrome";
        Configuration.headless = Boolean.parseBoolean(System.getProperty("selenide.headless", "true"));
        Configuration.browserSize = "1400x900";
        Configuration.timeout = 10_000;
        Configuration.pageLoadTimeout = 60_000;
        Configuration.pageLoadStrategy = "eager";
        Configuration.baseUrl = TestConfig.baseUrl();
        if (!listenerRegistered) {
            SelenideLogger.addListener("allure", new AllureSelenide().screenshots(true).savePageSource(false));
            listenerRegistered = true;
        }
    }

    @BeforeMethod
    public void startFromHome() {
        Selenide.open("/");
    }

    @AfterMethod
    public void closeBrowser() {
        Selenide.closeWebDriver();
    }
}
