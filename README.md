# MentorConnect UI and API automation

Team Green. Selenium UI tests and REST API tests against the hosted MentorConnect app at `https://mentor-connect-css.vercel.app`.

Selenide is the Selenium binding. Tests use Chrome WebDriver. Locators and waits live in the page objects.

The plain test command does not open a browser window. Chrome runs headless unless you pass `-Dselenide.headless=false`. A window you open yourself is not the one the tests drive. Selenide starts its own Chrome.

## What you need

- JDK 17 or newer. The build targets Java 17 (`maven.compiler.release` is 17). Check with `java -version`. Maven uses `JAVA_HOME`, not whichever `java` happens to be first on `PATH`. If `JAVA_HOME` points at an older JDK, the build fails.
- Google Chrome installed. The tests start it. You do not log in by hand first.
- A network path to `https://mentor-connect-css.vercel.app`.

Maven itself does not need a separate install. Use the wrapper: `mvnw.cmd` on Windows, `./mvnw` on macOS or Linux.

On Windows PowerShell, from this directory, a JDK 17 that is not the default looks like this:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.12.7-hotspot"
$env:Path = "$env:JAVA_HOME\bin;" + $env:Path
java -version
```

`java -version` must report 17 or newer before you continue. Use the path of the JDK that is actually installed. JDK 24 also works, because the project compiles with `--release 17`.

If an older run was compiled with Java 22, Surefire can fail with `class file version 66.0` while this JDK only understands up to 61.0. Delete the old output and let Maven compile again:

```powershell
Remove-Item -Recurse -Force target
```

## Credentials

Copy the example file and fill in the mentor account. Everyday tests log in as that existing mentor. They do not register new mentors and they do not spend invitation codes.

```powershell
Copy-Item src\test\resources\config.properties.example src\test\resources\config.properties
```

`src/test/resources/config.properties`:

```properties
baseUrl=https://mentor-connect-css.vercel.app
mentorEmail=the-mentor-email
mentorPassword=the-mentor-password
```

The real file is gitignored. Do not commit it, and do not paste the password into Allure screenshots or the terminal history you share.

## Commands

Run these from `mentorconnect-automation`. On PowerShell, keep the `-D` flags in quotes so the shell does not split them.

Everyday suite, no visible windows. This is the run that reports 18 tests. UI-03 counts as three because it runs once per CSV row. API-02 and API-03 are excluded here.

```powershell
.\mvnw.cmd test
```

A good run ends with `Tests run: 18, Failures: 0, Errors: 0, Skipped: 0` and `BUILD SUCCESS`. The first run downloads dependencies and is slower. About 80 seconds is normal after that.

Same suite, with Chrome on screen. Up to three windows open at once, because tests run in parallel. Expect the same 18 tests.

```powershell
.\mvnw.cmd test "-Dselenide.headless=false"
```

One class, or one method, still headless unless you add the headless flag. This example is the three title rows only, so Surefire reports 3 tests, not 18.

```powershell
.\mvnw.cmd test "-Dselenide.headless=false" "-Dtest=EventUiTest#titleBoundary"
```

`-Dtest` filters the run. It does not remove the other tests from the project. A filtered run also replaces `target/surefire-reports`. Run the everyday command again when you want the 18-test report back.

Registration only. API-02 and API-03. The app allows 3 registration calls per hour from one client and counts a call before it validates the body. Do not loop this command. Run the everyday suite first. This group writes its own report folders, so it does not replace the 18-test Surefire or Allure output.

```powershell
.\mvnw.cmd test "-Dgroups=registration"
```

A good registration run ends with `Tests run: 2, Failures: 0`.

## Reports

Everyday Surefire report, opened in a browser:

`target/surefire-reports/index.html`

Everyday Allure report. This command builds the report from `target/allure-results` and opens it.

```powershell
.\mvnw.cmd allure:serve
```

Registration Surefire text: `target/surefire-reports-registration`.

Registration Allure report:

```powershell
.\mvnw.cmd allure:serve "-Dallure.results.directory=target/allure-results-registration"
```

Each API test attaches `HTTP request` and `HTTP response` in Allure. Cookie, Set-Cookie, Authorization, and password values are stored as `[redacted]`.

## What the console noise means

`Unable to find CDP implementation matching 155` is a warning, not a failure. Selenium 4.26 does not ship a DevTools client for the installed Chrome. Clicks and assertions use WebDriver, so the suite still passes. The line repeats because each test starts its own Chrome.

`No webdriver is bound to current thread` and `Dead threads watchdog... nothing to close` are Selenide starting and closing those browsers. `TestNG-test-Surefire test-1`, `test-2`, and `test-3` are the three parallel workers.

`No sources to compile` is expected. This module has no `src/main` code. Everything under test is the hosted site.

## How the suite runs

Surefire runs TestNG methods in parallel with 3 threads. `perCoreThreadCount` is false, so the thread count stays 3 on a machine with more cores. Each test signs in itself and deletes events it created. A failed delete is logged and does not replace the assertion result.

The browser is 1400 by 900 so the desktop event dialog is used. Below 768 pixels the same form becomes a stepped mobile dialog. Page load strategy is `eager`, because the hosted page does not reach a complete load while third-party scripts are still running.

Event create, update, and delete are limited to 10 mutations per minute per client. If those calls start returning HTTP 429, set `threadCount` in `pom.xml` to 2 and run again.

## Layout

```
src/test/java/org/teamgreen/mentorconnectautomation/
  config/     TestConfig, reads config.properties
  base/       browser setup and REST Assured request specs
  pages/      login, events list, event dialog
  api/        session sign-in, events client, Allure redaction
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

`docs/TEST_INVENTORY.md` lists every test id, what it asserts, and the last Surefire result. `docs/FAILURE_INVESTIGATION.md` is the UI-04 end-date failure: cause, fix, and rerun.

## Why this stack

The module is Java. Selenide, TestNG, REST Assured, and Allure are the usual Selenium set on the JVM. Java 17 is the release this build targets, so a supervisor with JDK 17 can compile and run it from this README.
