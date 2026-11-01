# Test inventory

Results are copied from Surefire, not edited.

Everyday run: `mvnw test` on 2026-10-28 17:33–17:35 CET. Surefire: **Tests run: 18, Failures: 0, Errors: 0, Skipped: 0**. Elapsed 110.4 s. BUILD SUCCESS. The `registration` group was excluded. Report: `target/surefire-reports`.

Registration run: `mvnw test -Dgroups=registration` on 2026-10-28 17:38 CET. Surefire: **Tests run: 2, Failures: 0, Errors: 0, Skipped: 0**. Elapsed 9.736 s. BUILD SUCCESS. Report: `target/surefire-reports-registration`.

The File column is relative to `src/test/java/org/teamgreen/mentorconnectautomation/`.

| Id | File | Method | Kind | Expected | Result |
| --- | --- | --- | --- | --- | --- |
| UI-01 | `ui/LoginUiTest.java` | `validLoginOpensDashboard` | Positive, state, regression | Valid mentor login opens a URL containing `/dashboard` and the heading `Welcome back, Automation`. The existing Events page then shows the heading `Events` | PASS (6.4 s) |
| UI-02 | `ui/LoginUiTest.java` | `invalidPasswordStaysOnLogin` | Negative | Wrong password stays on `/login` and shows `Invalid email or password` | PASS (12.7 s) |
| UI-03 | `ui/EventUiTest.java` | `titleBoundary` | Data-driven, boundary, text validation | CSV rows: `ab` shows the min-3 message, `abc` has no title error, 101 characters shows the max-100 message | PASS, all 3 rows (8.1 s, 10.8 s, 8.3 s) |
| UI-04 | `ui/EventUiTest.java` | `createPublicWorkshop` | Positive, dropdown, date, number, textarea, state | Type, clear, and retype a unique title. The description field keeps both lines. Type is set to Webinar. Visibility is changed to Private, then to Public. Toast `Event created successfully`. The events list shows the title, and the event page shows both description lines, Webinar, and Public | PASS (50.2 s) |
| UI-05 | `ui/EventUiTest.java` | `zeroParticipantsIsRejected` | Number boundary | `0` is rejected by the field constraint. The browser validation message contains `greater than or equal to 1` | PASS (8.4 s) |
| UI-06 | `ui/EventUiTest.java` | `pastDateIsRejected` | Date validation | A past day is disabled, or submitting it shows `Event date cannot be more than 5 minutes in the past` | PASS (11.4 s) |
| UI-07 | `ui/EventUiTest.java` | `pngUploadShowsFileName` | Image and file acceptance | Uploading `sample.png` shows that file name in the dialog | PASS (7.9 s) |
| UI-08 | `ui/EventUiTest.java` | `unsupportedFileIsRejected` | File rejection | `rejected.exe` shows the toast `Failed to upload files. Please try again.` (`notes.txt` is `text/plain`, which the upload route allows) | PASS (6.6 s) |
| UI-09 | `ui/EventUiTest.java` | `cancelDoesNotCreateEvent` | Dialog | Cancel closes the dialog and the title is not in the list | PASS (11.3 s) |
| UI-10 | `ui/EventUiTest.java` | `onlineSwitchRevealsMeetingLink` | Dynamic UI | Turning the online switch on shows the meeting link and hides location. Turning it off reverses that | PASS (7.7 s) |
| API-01 | `api/EventsApiTest.java` | `eventsRequireAuthentication` | Auth, status, headers, error body | `GET /api/events` with no cookie returns 401, JSON, and a non-blank `error` | PASS (5.6 s) |
| API-02 | `api/RegistrationApiTest.java` | `registerMentee` | Positive body. Group `registration` | `POST /api/auth/register` with a unique mentee returns 201, the same email, and `user.status` `ACTIVE` | PASS (6.7 s) |
| API-03 | `api/RegistrationApiTest.java` | `registerRejectsShortPassword` | Negative validation. Group `registration` | A 7-character password returns 400 and a message containing `at least 8 characters` | PASS (6.7 s) |
| API-04 | `api/EventsApiTest.java` | `createEventReturnsSavedEvent` | Schema and create | `POST /api/events` returns 201 JSON. `id`, `title`, `description`, `type`, and `date` are strings, and `date` is ISO-8601. `isOnline` is boolean false. `maxParticipants` is the number 10. `visibility` is `PUBLIC`. `creator.id` is a string | PASS (9.3 s) |
| API-05 | `api/EventsApiTest.java` | `readEventById` | Read | `GET /api/events/{id}` returns 200 and the same title | PASS (3.4 s) |
| API-06 | `api/EventsApiTest.java` | `updateEventTitle` | Update | `PUT /api/events/{id}` returns 200 and the new title | PASS (3.3 s) |
| API-07 | `api/EventsApiTest.java` | `deleteEventThenGetIsNotFound` | Delete and error handling | Delete returns 200 with a message containing `deleted`. The following GET returns 404 and `not found` | PASS (9.2 s) |
| API-08 | `api/EventsApiTest.java` | `titleLengthBoundaries` | Boundary | Title `ab` returns 400 and the min-3 message. Title of 101 `a` characters returns 400 and the max-100 message | PASS (1.7 s) |
