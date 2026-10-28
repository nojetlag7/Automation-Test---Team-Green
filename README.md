# MentorConnect UI and API automation

Team Green. Selenium UI tests and REST API tests against the hosted MentorConnect app at `https://mentor-connect-css.vercel.app`.

Selenide is the Selenium binding. Tests use the same Chrome WebDriver; locators and waits live in the page objects.

## What you need

- A JDK that can compile `--release 22` (JDK 22 or newer). This project was run with JDK 24. Maven uses `JAVA_HOME`, so point it at that JDK. The wrapper is included, so a separate Maven install is optional.
- Google Chrome.

## Credentials

Copy `src/test/resources/config.properties.example` to `src/test/resources/config.properties` and set `mentorEmail` and `mentorPassword`. The real file is gitignored. Do not commit it, and do not paste the password into Allure screenshots.

Everyday tests log in as one mentor that already exists. They do not register new mentors and they do not spend invitation codes.

## Commands

From this directory:

```
.\mvnw.cmd test
```

Runs the UI tests and the events API tests. The `registration` group is excluded.

```
.\mvnw.cmd test -Dgroups=registration
```

Runs only API-02 and API-03. The app counts registration attempts before it validates the body, and the limit is 3 requests per hour from one client. Do not loop this command.

Run the everyday suite first, then the registration group. Registration writes to its own folders, so it does not replace the 18-test Surefire report or the Allure results.

```
.\mvnw.cmd allure:serve
```

Opens the everyday Allure report from `target/allure-results`. Surefire text for that run is `target/surefire-reports`.

```
.\mvnw.cmd allure:serve -Dallure.results.directory=target/allure-results-registration
```

Opens the registration report. Its Surefire text is `target/surefire-reports-registration`. API calls are attached on each test as `HTTP request` and `HTTP response`. Cookie, Set-Cookie, Authorization, and password fields are replaced with `[redacted]`.

Headless Chrome is the default. To watch the browser:

```
.\mvnw.cmd test -Dselenide.headless=false
```

## How the suite runs

Surefire runs TestNG methods in parallel with 3 threads. `perCoreThreadCount` is false, so the thread count stays 3. Each test signs in itself and deletes events it created. A failed delete is logged and does not replace the assertion result.

The browser is 1400 by 900 so the desktop event dialog is used. Below 768 pixels the same form becomes a stepped mobile dialog. Page load strategy is `eager`, because the hosted page does not reach a complete load while third-party scripts are still running.

If event create, update, or delete starts returning HTTP 429, lower `threadCount` in `pom.xml` to 2. That limit is 10 mutations per minute per client.

## Layout

```
src/test/java/org/teamgreen/mentorconnectautomation/
  config/     config.properties
  base/       browser setup and Rest Assured request specs
  pages/      login, events list, event dialog
  api/        session cookie sign-in and the events client
  data/       unique titles and the title CSV
  ui/         UI-01 to UI-10
  api/        API-01, API-04 to API-08, plus the registration group
src/test/resources/
  config.properties.example
  testdata/event-titles.csv
  files/sample.png
  files/rejected.exe
docs/TEST_INVENTORY.md
docs/FAILURE_INVESTIGATION.md
```

## Why this stack

The repository was already Java. Selenide, TestNG, Rest Assured, and Allure are the usual Selenium JVM set, so a clean clone plus this README is enough to run the suite.
