# Test inventory

Results are copied from Surefire, not edited.

Everyday run: `mvnw test` on 2026-10-28 00:55–00:56 CET. Surefire: **Tests run: 18, Failures: 0, Errors: 0, Skipped: 0**. Elapsed 73.66 s. BUILD SUCCESS. The `registration` group was excluded.

Registration run: `mvnw test -Dgroups=registration` on 2026-10-28 00:58 CET. Surefire: **Tests run: 2, Failures: 0, Errors: 0, Skipped: 0**. Elapsed 10.27 s. BUILD SUCCESS.

| Id | Method | Kind | Expected | Result |
| --- | --- | --- | --- | --- |
| UI-01 | `LoginUiTest.validLoginOpensDashboard` | Positive, state, regression | Valid mentor login opens a URL containing `/dashboard` and the heading `Welcome back, Automation` | PASS (2.7 s) |
| UI-02 | `LoginUiTest.invalidPasswordStaysOnLogin` | Negative | Wrong password stays on `/login` and shows `Invalid email or password` | PASS (3.1 s) |
| UI-03 | `EventUiTest.titleBoundary` | Data-driven, boundary, text validation | CSV rows: `ab` shows the min-3 message, `abc` has no title error, 101 characters shows the max-100 message | PASS, all 3 rows (5.6 s, 5.5 s, 5.7 s) |
| UI-04 | `EventUiTest.createPublicWorkshop` | Positive, dropdown, date, number, textarea, state | Type, clear, and retype a unique title. Set description, Webinar, Public, a future start and end, location, and max participants 10. Toast `Event created successfully`, then the events list shows the title | PASS (41.4 s) |
| UI-05 | `EventUiTest.zeroParticipantsIsRejected` | Number boundary | `0` is rejected by the field constraint. The browser validation message contains `greater than or equal to 1` | PASS (5.6 s) |
| UI-06 | `EventUiTest.pastDateIsRejected` | Date validation | A past day is disabled, or submitting it shows `Event date cannot be more than 5 minutes in the past` | PASS (14.3 s) |
| UI-07 | `EventUiTest.pngUploadShowsFileName` | Image and file acceptance | Uploading `sample.png` shows that file name in the dialog | PASS (7.4 s) |
| UI-08 | `EventUiTest.unsupportedFileIsRejected` | File rejection | `rejected.exe` shows the toast `Failed to upload files. Please try again.` (`notes.txt` is `text/plain`, which the upload route allows) | PASS (4.9 s) |
| UI-09 | `EventUiTest.cancelDoesNotCreateEvent` | Dialog | Cancel closes the dialog and the title is not in the list | PASS (8.2 s) |
| UI-10 | `EventUiTest.onlineSwitchRevealsMeetingLink` | Dynamic UI | Turning the online switch on shows the meeting link and hides location. Turning it off reverses that | PASS (6.7 s) |
| API-01 | `EventsApiTest.eventsRequireAuthentication` | Auth, status, headers, error body | `GET /api/events` with no cookie returns 401, JSON, and a non-blank `error` | PASS (9.8 s) |
| API-02 | `RegistrationApiTest.registerMentee` | Positive body. Group `registration` | `POST /api/auth/register` with a unique mentee returns 201, the same email, and `user.status` `ACTIVE` | PASS (7.7 s) |
| API-03 | `RegistrationApiTest.registerRejectsShortPassword` | Negative validation. Group `registration` | A 7-character password returns 400 and a message containing `at least 8 characters` | PASS (7.7 s) |
| API-04 | `EventsApiTest.createEventReturnsSavedEvent` | Schema and create | `POST /api/events` returns 201 with `id`, the title, type `WORKSHOP`, and `date` | PASS (13.4 s) |
| API-05 | `EventsApiTest.readEventById` | Read | `GET /api/events/{id}` returns 200 and the same title | PASS (3.1 s) |
| API-06 | `EventsApiTest.updateEventTitle` | Update | `PUT /api/events/{id}` returns 200 and the new title | PASS (2.2 s) |
| API-07 | `EventsApiTest.deleteEventThenGetIsNotFound` | Delete and error handling | Delete returns 200 with a message containing `deleted`. The following GET returns 404 and `not found` | PASS (13.3 s) |
| API-08 | `EventsApiTest.titleLengthBoundaries` | Boundary | Title `ab` returns 400 and the min-3 message. Title of 101 `a` characters returns 400 and the max-100 message | PASS (1.3 s) |
