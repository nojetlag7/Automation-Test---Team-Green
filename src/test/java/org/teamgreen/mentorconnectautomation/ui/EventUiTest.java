package org.teamgreen.mentorconnectautomation.ui;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.teamgreen.mentorconnectautomation.api.EventsApi;
import org.teamgreen.mentorconnectautomation.api.SessionAuth;
import org.teamgreen.mentorconnectautomation.base.BaseUiTest;
import org.teamgreen.mentorconnectautomation.config.TestConfig;
import org.teamgreen.mentorconnectautomation.data.EventTitleData;
import org.teamgreen.mentorconnectautomation.data.TestDataFactory;
import org.teamgreen.mentorconnectautomation.pages.EventFormDialog;
import com.codeborne.selenide.Selenide;
import org.teamgreen.mentorconnectautomation.pages.EventsPage;
import org.teamgreen.mentorconnectautomation.pages.LoginPage;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.time.Duration;

import static com.codeborne.selenide.Condition.empty;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Condition.visible;
import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("MentorConnect UI")
@Feature("Events")
public class EventUiTest extends BaseUiTest {

    @DataProvider(name = "eventTitles")
    public static Object[][] eventTitles() {
        return EventTitleData.rows();
    }

    @Test(dataProvider = "eventTitles")
    @Description("UI-03 Data-driven event title boundaries")
    public void titleBoundary(String title, String expected) {
        EventFormDialog dialog = openDialog();
        dialog.setTitle(title).submit();
        if ("accepted".equals(expected)) {
            dialog.titleError().shouldBe(empty);
        } else {
            dialog.titleError().shouldHave(text(expected));
        }
    }

    @Test
    @Description("UI-04 Create a public workshop and see it in the list")
    public void createPublicWorkshop() {
        String title = TestDataFactory.eventTitle();
        try {
            EventFormDialog dialog = openDialog();
            dialog.setTitle("temp").clearTitle().setTitle(title);
            dialog.titleField().shouldHave(value(title));
            dialog.setDescription("Line one\nLine two of the workshop description.");
            dialog.selectType("Webinar");
            dialog.visibilityTrigger().shouldHave(text("Public"));
            dialog.pickFutureStartDate().pickFutureEndDate();
            dialog.setLocation("Room 4").setMaxParticipants("10");
            dialog.submit();
            dialog.shouldShowToast("Event created successfully");

            EventsPage events = new EventsPage().open();
            Selenide.Wait().withTimeout(Duration.ofSeconds(40)).pollingEvery(Duration.ofSeconds(5)).until(driver -> {
                events.search(title);
                if (events.eventTitle(title).exists()) {
                    return true;
                }
                Selenide.refresh();
                return false;
            });
            events.eventTitle(title).shouldBe(visible);
        } finally {
            cleanup(title);
        }
    }

    @Test
    @Description("UI-05 Max participants of 0 is rejected")
    public void zeroParticipantsIsRejected() {
        EventFormDialog dialog = openDialog();
        dialog.setTitle(TestDataFactory.eventTitle()).setMaxParticipants("0").submit();
        assertThat(dialog.maxParticipantsValidationMessage()).contains("greater than or equal to 1");
    }

    @Test
    @Description("UI-06 A past start date is rejected or disabled")
    public void pastDateIsRejected() {
        EventFormDialog dialog = openDialog();
        dialog.setTitle(TestDataFactory.eventTitle());
        if (dialog.pastDayIsDisabled()) {
            return;
        }
        dialog.pickPastStartDate().submit();
        dialog.dateError().shouldHave(text("Event date cannot be more than 5 minutes in the past"));
    }

    @Test
    @Description("UI-07 A PNG upload shows the file name")
    public void pngUploadShowsFileName() {
        EventFormDialog dialog = openDialog();
        dialog.upload(resource("files/sample.png"));
        dialog.uploadedName("sample.png").shouldBe(visible, Duration.ofSeconds(20));
    }

    @Test
    @Description("UI-08 An unsupported file is rejected")
    public void unsupportedFileIsRejected() {
        EventFormDialog dialog = openDialog();
        dialog.upload(resource("files/rejected.exe"));
        dialog.shouldShowToast("Failed to upload files. Please try again.");
    }

    @Test
    @Description("UI-09 Cancel closes the dialog and does not create the event")
    public void cancelDoesNotCreateEvent() {
        String title = TestDataFactory.eventTitle();
        EventFormDialog dialog = openDialog();
        dialog.setTitle(title).cancel();
        EventsPage events = new EventsPage();
        events.search(title);
        events.eventTitle(title).shouldNotBe(visible, Duration.ofSeconds(10));
    }

    @Test
    @Description("UI-10 The online switch swaps meeting link and location")
    public void onlineSwitchRevealsMeetingLink() {
        EventFormDialog dialog = openDialog();
        dialog.location().shouldBe(visible);
        dialog.setOnline(true);
        dialog.meetingLink().shouldBe(visible);
        dialog.location().shouldNotBe(visible);
        dialog.setOnline(false);
        dialog.location().shouldBe(visible);
        dialog.meetingLink().shouldNotBe(visible);
    }

    private EventFormDialog openDialog() {
        new LoginPage().open().signIn(TestConfig.mentorEmail(), TestConfig.mentorPassword());
        new LoginPage().shouldReachDashboard();
        return new EventsPage().open().scheduleEvent();
    }

    private void cleanup(String title) {
        try {
            RequestSpecification spec = given()
                    .baseUri(TestConfig.baseUrl())
                    .accept(ContentType.JSON)
                    .contentType(ContentType.JSON)
                    .cookies(SessionAuth.signInMentor());
            new EventsApi(spec).deleteByTitleQuietly(title);
        } catch (RuntimeException ignored) {
            // A failed cleanup must not hide the assertion that already ran.
        }
    }

    private static File resource(String path) {
        var url = EventUiTest.class.getClassLoader().getResource(path);
        if (url == null) {
            throw new IllegalStateException("Missing test resource " + path);
        }
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new IllegalStateException(path, e);
        }
    }
}
