# Failure investigation

UI-04, creating a public event from the desktop dialog, failed on the hosted app before any expected result was changed. The later green run is the same assertion: the success toast, then the new title in the events list.

## What failed

`EventUiTest.createPublicWorkshop` filled title, description, type Webinar, visibility Public, a future start date, location, and max participants, then clicked Create Event. The success toast never appeared. The dialog stayed open. There was no red field message under the inputs.

Screenshot: `build/reports/tests/1793144666921.0.png`. The form is still on screen. Start date is set (15 November). End date still says `Select end date & time`.

## How it was detected

Selenide waited for the toast `Event created successfully` and timed out. The Allure Selenide listener saved the screenshot. The page source showed the dialog still mounted and no `text-red-500` validation under the fields.

## Cause

Application defect on the hosted build.

The create handler sends `endDate: null` when the optional end date is empty. `createEventSchema` declared `endDate` as `z.string().datetime().optional()`. Zod `optional()` accepts `undefined` and a missing key. It rejects `null`. `POST /api/events` therefore returns 400 even though the field is optional in the form.

`useCreateEvent` catches that error and returns `null` (`lib/hooks/use-events.ts`). The events page then toasts `Failed to create event. Please try again.` and leaves the dialog open. That toast expires during the wait, so the screenshot looks like a silent failure.

## How it was traced

1. The screenshot showed a complete valid form and an empty end date, with no inline error.
2. The page handler builds the JSON body and, on the hosted bundle, sets `endDate` to `null` when the picker is empty.
3. `createEventSchema` in `lib/validations/schemas.ts` used `.optional()` only, so `null` fails validation before the event is saved.
4. `useCreateEvent` turns the rejected request into `null`, and the page treats that as a failed create.

## What changed

Local app fix, not deployed yet:

- `createEventSchema.endDate` is `.optional().nullable()`, so an explicit `null` is valid.
- The events page sends `endDate` only when the user picked one (`undefined` is omitted from JSON).

The hosted site still has the old client until that change is deployed. UI-04 does not depend on the deploy: it also picks an end date (the day after tomorrow), so the body contains a real ISO `endDate` instead of `null`. The assertion is unchanged.

Two earlier red runs were test issues, not this defect:

- The date popover. The test waited for the visible text `Select Date & Time`. That header is not displayed while the day buttons paint over the form. The page object now waits for `button[data-day]` and clicks the day and Apply with JavaScript.
- Max participants `0`. The test looked for the React message `Must be a positive number`. The input has `min="1"`, so Chrome blocks submit first. The message is `Value must be greater than or equal to 1.` UI-05 reads that `validationMessage`.

After the create toast started passing, the list search still showed `No events found matching your search` (`build/reports/tests/1793145112858.0.png`). The upcoming list is the first page, ordered by date, and `GET /api/events` is cached `private, max-age=30`. A next-month date could fall off that page, and a single refetch could still be the cached list. UI-04 now schedules the event for tomorrow and refreshes until the title appears, up to 40 seconds.

## Rerun

`mvnw -Dtest=EventUiTest#createPublicWorkshop test` passed after the end date and the list refresh.

Everyday suite, 2026-10-28 00:56 CET: Tests run 18, Failures 0, Errors 0, Skipped 0. BUILD SUCCESS.

Registration group, 2026-10-28 00:58 CET: Tests run 2, Failures 0, Errors 0, Skipped 0. BUILD SUCCESS.
