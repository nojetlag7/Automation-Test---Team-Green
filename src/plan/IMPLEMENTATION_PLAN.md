# MentorConnect automation plan

Save this document to `mentorconnect-automation/src/plan/IMPLEMENTATION_PLAN.md`. The JetBrains starter in `MainPage.java` and `MainPageTest.java` is replaced when implementation starts. Target is the hosted app at `https://mentor-connect-css.vercel.app`. Blob upload on that deployment is already confirmed.

## Decisions

- Stay on the stack already in `pom.xml`: Java 22, Maven, Selenium via Selenide 7.6, TestNG, AssertJ, Allure. Add Rest Assured for the API layer. Selenide is the Selenium binding: same WebDriver, with locators and waits in the page objects. The demo should say that plainly.
- One stable mentor account, created once with the mentor invitation code, stored in a gitignored `src/test/resources/config.properties`. Everyday tests log in as that user. The 1,000-use codes are not spent by the suite.
- Browser size `1400x900`. `event-form-dialog.tsx` switches to a stepped mobile form on a narrow viewport. The suite must hit the desktop dialog, where title, description, type, visibility, dates, location, max participants, and attachments are on one surface.
- Create events as Public. Group visibility requires a `groupId`. Public avoids depending on a group existing.
- No `data-testid` in the app today. Use existing ids (`email`, `password`, `title`, `description`, `type`, `visibility`, `maxParticipants`). The date trigger is the button whose text is `Select start date & time`. The file input is the hidden `input[type=file]` inside the dialog. Add a `data-testid` only if one of those two stays ambiguous after the first run.

## Why this app fits

The event dialog plus login covers every mandatory control with real behavior: text (`#title`, `#email`), textarea (`#description`), number (`#maxParticipants`, min 1), dropdown (Radix select `#type` and `#visibility`), date (`DateTimePicker`; API rejects a past date in `schemas.ts`), file and image (`FileUpload` to `POST /api/upload`), dialog (create dialog and delete `AlertDialog`), and dynamic UI (the online switch swaps meeting link and location). Login and event rules supply positive, negative, boundary, and validation cases.

## Layout

```
mentorconnect-automation/
  src/plan/IMPLEMENTATION_PLAN.md
  src/test/java/org/teamgreen/mentorconnectautomation/
    config/TestConfig.java
    base/BaseUiTest.java
    base/BaseApiTest.java
    pages/LoginPage.java
    pages/EventsPage.java
    pages/EventFormDialog.java
    api/SessionAuth.java
    api/EventsApi.java
    data/EventTitleData.java
  src/test/resources/
    config.properties.example
    testdata/event-titles.csv
    files/sample.png
    files/notes.txt
    testng.xml
  README.md
  docs/TEST_INVENTORY.md
  docs/FAILURE_INVESTIGATION.md
```

`TestConfig` reads `baseUrl`, `mentorEmail`, and `mentorPassword`. `config.properties.example` is committed with empty values. The real file is gitignored. Allure `@Epic`, `@Feature`, and `@Description` carry the test id on every method.

`BaseUiTest` sets the browser size, registers the Allure Selenide listener, and opens the base URL. Page objects are created in `@BeforeMethod`. Selenide's driver is thread-local, so a page object field is safe under parallel methods. Waits are Selenide conditions (`shouldBe`, `shouldHave`) with a 10 second timeout. No `Thread.sleep`.

## UI tests (10)

Each test logs in itself, uses a title like `sqa-` plus a short unique id, and deletes any event it created.

- **UI-01** Valid login opens `/dashboard`. Positive, state, and the regression check that sign-in still works. Assert the URL and a dashboard heading.
- **UI-02** Wrong password stays on `/login` and shows `Invalid email or password`. Negative. The copy comes from `login-form.tsx`.
- **UI-03** Data-driven event title, TestNG `@DataProvider` fed by `event-titles.csv`. Rows: `ab` expects the min-3 message, a 3-character title is accepted, 101 characters expects the max-100 message. Same method, three Allure iterations. This is the required data-driven test. Also covers boundary and text validation. UI-04 still types, clears, and retypes the title so enter/edit/clear is explicit.
- **UI-04** Create a public workshop with description, type, visibility, a future date, location, and max participants `10`. Assert the selected type label, then assert the events list shows the title. Positive, dropdown, date, number, textarea, state.
- **UI-05** Max participants `0` shows the positive-number error. Number boundary.
- **UI-06** Past date. If the calendar day is disabled, assert it is disabled. If it can be selected, submit and assert the future-date error. Date validation.
- **UI-07** Upload `sample.png`. Assert the file name appears in the dialog. Image and file acceptance. The input is hidden; Selenide `uploadFile` targets `input[type=file]` directly.
- **UI-08** Upload `notes.txt` (not in the allow-list). Assert the rejection toast. File rejection.
- **UI-09** Open Schedule Event, fill a title, press Cancel. Assert the dialog closes and the title is absent from the list. Dialog.
- **UI-10** Turn on "This is an online event" and assert the meeting-link field appears. Turn it off and assert location is back. Dynamic UI.

## API tests (8)

Rest Assured uses a `RequestSpecification` built inside each test. No static cookie jar and no mutated global `RestAssured` state, so methods can run in parallel.

`SessionAuth` signs in the way the browser does: `GET /api/auth/csrf`, then `POST /api/auth/callback/credentials` with the csrf token, email, password, and `json=true`. It keeps the session cookie the response sets (on this HTTPS host that is the secure NextAuth cookie). The first run confirms the cookie name; it is not hardcoded before that.

Events are `POST /api/events`, `GET /api/events/{id}`, `PUT /api/events/{id}`, `DELETE /api/events/{id}`. Mutations sit behind `API_MUTATION` (10 per minute per client). The suite stays inside that.

- **API-01** `GET /api/events` with no cookie. Assert 401, `Content-Type` JSON, and an `error` field. Auth, status, headers, error body.
- **API-02** `POST /api/auth/register` with a unique mentee email and a valid body. Assert 201 and `status` `ACTIVE` (auto-approve is on). Positive body check. TestNG group `registration`.
- **API-03** Register with a 7-character password. Assert 400 and the 8-character message. Negative validation. Same group.
- **API-04** Create event. Assert 201 and the presence and types of `id`, `title`, `type`, and `date`. Schema plus create.
- **API-05** Create, then `GET` by id. Assert 200 and the same title. Read.
- **API-06** Create, then `PUT` a new title. Assert 200 and the new title. Update.
- **API-07** Create, `DELETE`, then `GET` the id. Assert the delete status and a following 404. Delete and error handling.
- **API-08** `POST` a title of `ab`. Assert 400 and the min-length message. Boundary. A second assertion in the same test sends 101 characters and expects the max-length message.

API-02 and API-03 are the only registration calls. `register/route.ts` applies a limit of 3 requests per hour before it validates, so a failing call spends a slot too. They live in a TestNG group excluded from the everyday `mvn test` run, and they are included for the graded run and the demo. Mentee registration does not use the mentor or admin invitation codes.

File upload stays in UI-07 and UI-08, which hit `POST /api/upload` through the dialog. An API upload pair is optional after the eight above are green.

## Parallel and independence

`testng.xml` uses `parallel="methods"` and `thread-count="3"`. Surefire gets the same settings. Remove `testFailureIgnore` so a failure fails the build.

- Every test creates the user session and the records it needs.
- Event titles and registration emails include a unique id.
- Cleanup deletes events in a `finally` block. A failed delete is logged and does not hide the assertion result.
- The `registration` group is outside the parallel everyday suite so a 429 is not mistaken for a product bug.
- If event mutations return 429, drop the thread count to 2 and record that in the README. That is an environment limit, and the assignment allows documenting it.

Evidence for the demo is the Allure timeline (overlapping bars) plus the Surefire thread names.

## Failure investigation

Do not plant a failing assertion or edit an expected result to force green. The first real red run (likely the date popover or a Radix select rendered in a portal) is written up in `docs/FAILURE_INVESTIGATION.md` with: what failed, how it was detected, the cause (app defect, data, sync, locator, environment, or assertion), how it was traced, what changed, and how the rerun proved the fix. Screenshots come from the Allure Selenide listener. Secrets stay out of screenshots and attachments.

## Reporting and the write-up

- `mvn test` runs UI and API except `registration`.
- `mvn test -Dgroups=registration` runs the two register cases.
- `mvn allure:serve` opens the report.
- README covers Java 22, Chrome, Maven, the config file, those commands, parallel, and where the report lands.
- `docs/TEST_INVENTORY.md` lists every id, type, expected result, and pass/fail after a real run. Results are copied from Allure, not edited.
- Language justification for the slides: the repo was already Java, and Selenide, TestNG, Rest Assured, and Allure are the usual Selenium JVM set, which an instructor can run from a clean clone with the README.

## Suggested split for the contribution slide

Five areas, so each person can explain a module: framework and config; login and data-driven UI; event dialog page object; API client and API tests; parallel, Allure, inventory, and the failure write-up. Names can be filled in when the team divides the work.
