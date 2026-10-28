package org.teamgreen.mentorconnectautomation.pages;

import com.codeborne.selenide.SelenideElement;

import java.io.File;
import java.time.Duration;
import java.time.LocalDate;

import static com.codeborne.selenide.ClickOptions.usingJavaScript;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.Wait;

public class EventFormDialog {
    public EventFormDialog shouldBeOpen() {
        dialog().shouldBe(visible);
        return this;
    }

    public EventFormDialog setTitle(String title) {
        titleField().setValue(title);
        return this;
    }

    public EventFormDialog clearTitle() {
        titleField().clear();
        return this;
    }

    public SelenideElement titleField() {
        return dialog().$("#title");
    }

    public SelenideElement titleError() {
        return dialog().$("#title").parent().$("span.text-red-500");
    }

    public EventFormDialog setDescription(String description) {
        descriptionField().setValue(description);
        return this;
    }

    public SelenideElement descriptionField() {
        return dialog().$("#description");
    }

    public EventFormDialog selectType(String label) {
        dialog().$("#type").click();
        $$("[role='option']").findBy(exactText(label)).click();
        dialog().$("#type").shouldHave(text(label));
        return this;
    }

    public EventFormDialog selectVisibility(String label) {
        visibilityTrigger().click();
        $$("[role='option']").findBy(text(label)).click();
        visibilityTrigger().shouldHave(text(label));
        return this;
    }

    public SelenideElement visibilityTrigger() {
        return dialog().$("#visibility");
    }

    public boolean pastDayIsDisabled() {
        openPreviousMonth();
        SelenideElement day = dayInCurrentMonth("15");
        return day.is(disabled) || "true".equals(day.getAttribute("aria-disabled"));
    }

    public EventFormDialog pickPastStartDate() {
        if ($$("button[data-day]").isEmpty()) {
            openPreviousMonth();
        }
        dayInCurrentMonth("15").click(usingJavaScript());
        applyDate();
        return this;
    }

    public EventFormDialog pickFutureStartDate() {
        pickDay(startDateButton(), "Select start date", LocalDate.now().plusDays(1));
        return this;
    }

    public EventFormDialog pickFutureEndDate() {
        pickDay(endDateButton(), "Select end date", LocalDate.now().plusDays(2));
        return this;
    }

    private void pickDay(SelenideElement trigger, String placeholder, LocalDate date) {
        trigger.click();
        $$("button[data-day]").first().shouldBe(visible);
        if (date.getMonthValue() != LocalDate.now().getMonthValue()) {
            $("button[aria-label='Go to the Next Month']").click(usingJavaScript());
        }
        dayInCurrentMonth(String.valueOf(date.getDayOfMonth())).click(usingJavaScript());
        applyDate();
        trigger.shouldNotHave(text(placeholder));
    }

    public SelenideElement dateError() {
        return dialog().$("label[for='date']").parent().$("p.text-red-500");
    }

    public EventFormDialog setMaxParticipants(String value) {
        dialog().$("#maxParticipants").setValue(value);
        return this;
    }

    public SelenideElement maxParticipantsField() {
        return dialog().$("#maxParticipants");
    }

    public String maxParticipantsValidationMessage() {
        return maxParticipantsField().getAttribute("validationMessage");
    }

    public EventFormDialog setLocation(String location) {
        dialog().$("#location").setValue(location);
        return this;
    }

    public EventFormDialog setOnline(boolean online) {
        if (isOnline() != online) {
            dialog().$("#isOnline").click();
        }
        return this;
    }

    public boolean isOnline() {
        return "true".equals(dialog().$("#isOnline").getAttribute("aria-checked"));
    }

    public SelenideElement meetingLink() {
        return dialog().$("#meetingLink");
    }

    public SelenideElement location() {
        return dialog().$("#location");
    }

    public EventFormDialog upload(File file) {
        dialog().$("input[type='file']").uploadFile(file);
        return this;
    }

    public SelenideElement uploadedName(String fileName) {
        return dialog().$$("p").findBy(exactText(fileName));
    }

    public void submit() {
        dialog().$$("button").findBy(exactText("Create Event")).click();
    }

    public void cancel() {
        dialog().$$("button").findBy(exactText("Cancel")).click();
        dialog().shouldNotBe(visible, Duration.ofSeconds(10));
    }

    public void shouldShowToast(String message) {
        $("[data-sonner-toast]").shouldHave(text(message), Duration.ofSeconds(20));
    }

    private SelenideElement dialog() {
        return $x("//div[@role='dialog'][.//*[contains(normalize-space(.), 'Create New Event')]]");
    }

    private void openPreviousMonth() {
        openStartPicker();
        String before = dayInCurrentMonth("15").getAttribute("data-day");
        $("button[aria-label='Go to the Previous Month']").click(usingJavaScript());
        Wait().until(driver -> {
            String now = dayInCurrentMonth("15").getAttribute("data-day");
            return now != null && !now.equals(before);
        });
    }

    private void openStartPicker() {
        startDateButton().click();
        $$("button[data-day]").first().shouldBe(visible);
    }

    private SelenideElement startDateButton() {
        return dialog().$("label[for='date']").parent().$("button");
    }

    private SelenideElement endDateButton() {
        return dialog().$("label[for='endDate']").parent().$("button");
    }

    private void applyDate() {
        $$("button").findBy(exactText("Apply")).click(usingJavaScript());
    }

    private SelenideElement dayInCurrentMonth(String dayNumber) {
        return $$("button[data-day]").asFixedIterable().stream()
                .filter(day -> dayNumber.equals(day.getText().trim()))
                .filter(day -> {
                    String cellClass = day.parent().getAttribute("class");
                    return cellClass == null || !cellClass.contains("outside");
                })
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No in-month day " + dayNumber));
    }
}
