# Test inventory

Results are copied from Surefire, not edited.

Everyday run: `mvnw test` on 2026-10-28 11:18–11:19 CET. Surefire: **Tests run: 18, Failures: 0, Errors: 0, Skipped: 0**. Elapsed 75.63 s. BUILD SUCCESS. The `registration` group was excluded. Report: `target/surefire-reports`.

Registration run: `mvnw test -Dgroups=registration` on 2026-10-28 11:19–11:20 CET. Surefire: **Tests run: 2, Failures: 0, Errors: 0, Skipped: 0**. Elapsed 16.23 s. BUILD SUCCESS. Report: `target/surefire-reports-registration`.

The File column is relative to `src/test/java/org/teamgreen/mentorconnectautomation/`.

| Id | File | Method | Kind | Expected | Result |
| --- | --- | --- | --- | --- | --- |
| UI-01 | `ui/LoginUiTest.java` | `validLoginOpensDashboard` | Positive, state, regression | Valid mentor login opens a URL containing `/dashboard` and the heading `Welcome back, Automation`. The existing Events page then shows the heading `Events` | PASS (7.8 s) |
| UI-02 | `ui/LoginUiTest.java` | `invalidPasswordStaysOnLogin` | Negative | Wrong password stays on `/login` and shows `Invalid email or password` | PASS (5.1 s) |
| UI-03 | `ui/EventUiTest.java` | `titleBoundary` | Data-driven, boundary, text validation | CSV rows: `ab` shows the min-3 message, `abc` has no title error, 101 characters shows the max-100 message | PASS, all 3 rows (5.0 s, 6.5 s, 10.8 s) |
| UI-04 | `ui/EventUiTest.java` | `createPublicWorkshop` | Positive, dropdown, date, number, textarea, state | Type, clear, and retype a unique title. The description field keeps both lines. Type is set to Webinar. Visibility is changed to Private, then to Public. Toast `Event created successfully`. The events list shows the title, and the event page shows both description lines, Webinar, and Public | PASS (45.0 s) |
| UI-05 | `ui/EventUiTest.java` | `zeroParticipantsIsRejected` | Number boundary | `0` is rejected by the field constraint. The browser validation message contains `greater than or equal to 1` | PASS (6.0 s) |
| UI-06 | `ui/EventUiTest.java` | `pastDateIsRejected` | Date validation | A past day is disabled, or submitting it shows `Event date cannot be more than 5 minutes in the past` | PASS (13.4 s) |
| UI-07 | `ui/EventUiTest.java` | `pngUploadShowsFileName` | Image and file acceptance | Uploading `sample.png` shows that file name in the dialog | PASS (7.7 s) |
| UI-08 | `ui/EventUiTest.java` | `unsupportedFileIsRejected` | File rejection | `rejected.exe` shows the toast `Failed to upload files. Please try again.` (`notes.txt` is `text/plain`, which the upload route allows) | PASS (4.8 s) |
| UI-09 | `ui/EventUiTest.java` | `cancelDoesNotCreateEvent` | Dialog | Cancel closes the dialog and the title is not in the list | PASS (8.8 s) |
| UI-10 | `ui/EventUiTest.java` | `onlineSwitchRevealsMeetingLink` | Dynamic UI | Turning the online switch on shows the meeting link and hides location. Turning it off reverses that | PASS (8.1 s) |
| API-01 | `api/EventsApiTest.java` | `eventsRequireAuthentication` | Auth, status, headers, error body | `GET /api/events` with no cookie returns 401, JSON, and a non-blank `error` | PASS (6.3 s) |
| API-02 | `api/RegistrationApiTest.java` | `registerMentee` | Positive body. Group `registration` | `POST /api/auth/register` with a unique mentee returns 201, the same email, and `user.status` `ACTIVE` | PASS (12.2 s) |
| API-03 | `api/RegistrationApiTest.java` | `registerRejectsShortPassword` | Negative validation. Group `registration` | A 7-character password returns 400 and a message containing `at least 8 characters` | PASS (12.2 s) |
| API-04 | `api/EventsApiTest.java` | `createEventReturnsSavedEvent` | Schema and create | `POST /api/events` returns 201 JSON. `id`, `title`, `description`, `type`, and `date` are strings, and `date` is ISO-8601. `isOnline` is boolean false. `maxParticipants` is the number 10. `visibility` is `PUBLIC`. `creator.id` is a string | PASS (9.3 s) |
| API-05 | `api/EventsApiTest.java` | `readEventById` | Read | `GET /api/events/{id}` returns 200 and the same title | PASS (2.5 s) |
| API-06 | `api/EventsApiTest.java` | `updateEventTitle` | Update | `PUT /api/events/{id}` returns 200 and the new title | PASS (2.7 s) |
| API-07 | `api/EventsApiTest.java` | `deleteEventThenGetIsNotFound` | Delete and error handling | Delete returns 200 with a message containing `deleted`. The following GET returns 404 and `not found` | PASS (9.1 s) |
| API-08 | `api/EventsApiTest.java` | `titleLengthBoundaries` | Boundary | Title `ab` returns 400 and the min-3 message. Title of 101 `a` characters returns 400 and the max-100 message | PASS (1.7 s) |
