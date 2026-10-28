package org.teamgreen.mentorconnectautomation.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;

import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class EventsPage {
    public EventsPage open() {
        Selenide.open("/dashboard/events");
        $("h1").shouldHave(exactText("Events"));
        return this;
    }

    public EventFormDialog scheduleEvent() {
        $$("button").findBy(exactText("Schedule Event")).shouldBe(visible).click();
        return new EventFormDialog().shouldBeOpen();
    }

    public EventsPage search(String title) {
        $("input[placeholder^='Search events']").setValue(title);
        return this;
    }

    public SelenideElement eventTitle(String title) {
        return $$("h3").findBy(exactText(title));
    }

    public EventsPage openEvent(String title) {
        eventTitle(title).click();
        $("h1").shouldHave(exactText(title), Duration.ofSeconds(15));
        return this;
    }

    public EventsPage shouldShowSavedEvent(String description, String typeLabel, String visibilityLabel) {
        $$("p").findBy(text(description)).shouldBe(visible);
        $$("[data-slot='badge']").findBy(exactText(typeLabel)).shouldBe(visible);
        $$("[data-slot='badge']").findBy(exactText(visibilityLabel)).shouldBe(visible);
        return this;
    }
}
