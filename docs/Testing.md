---
layout: page
title: Testing guide
---

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Running tests

You can run tests in two ways.

* **Method 1: Using IntelliJ JUnit test runner**
  * To run all tests, right-click on the `src/test/java` folder and choose `Run 'All Tests'`
  * To run a subset of tests, you can right-click on a test package,
    test class, or a test and choose `Run 'ABC'`
* **Method 2: Using Gradle**
  * Open a console and run the command `gradlew clean test` (Mac/Linux: `./gradlew clean test`)

<div markdown="span" class="alert alert-secondary">:link: **Link**: Read [this Gradle Tutorial from the se-edu/guides](https://se-education.org/guides/tutorials/gradle.html) to learn more about using Gradle.
</div>

### Command recall tests

`CommandHistoryTest` tests the history helper without starting JavaFX. `CommandBoxTest` creates real
JavaFX controls and dispatches key events to check recall, drafts, submission, and error handling.
`JavaFxExtension` shares one JavaFX runtime across UI test classes, runs control interactions on its
application thread with bounded waits, and shuts down the toolkit after the test suite.
The tests do not open a visible application window.

To run both test classes on Windows:

```powershell
.\gradlew.bat test --tests seedu.address.ui.CommandHistoryTest --tests seedu.address.ui.CommandBoxTest
```

On macOS or Linux with a graphical display, use `./gradlew` instead of `.\gradlew.bat`.
JavaFX still requires a display even when no test window is shown. On Linux without a display,
install Xvfb using the distribution's package manager and run:

```sh
xvfb-run --auto-servernum ./gradlew clean test
```

The Linux CI job already runs `check coverage` under Xvfb; Windows and macOS use the normal Gradle
command. The same tests run on all three platforms, with no additional Java test dependencies.

Dispatched events do not test physical keyboard input, window focus, or visual appearance. Also follow
the [command recall manual tests](DeveloperGuide.md#recalling-previous-commands) in the running app.

### Favourite contact tests

The favourite feature is covered at these levels:

* `PersonTest` and `EditCommandTest`: default status, copies, equality, and preservation when editing fields
  and toggling tags.
* `FavouriteCommandsTest` and `ListFavouriteCommandTest`: displayed indices, repeated commands,
  preservation of details and filters, stored result order, and removal from the favourite view.
* `FavouriteCommandParsersTest`, `ListCommandParserTest` and `AddressBookParserTest`: supported syntax,
  whitespace, case sensitivity, missing or invalid indices, and extra arguments.
* `JsonAdaptedPersonTest`, `JsonAddressBookStorageTest` and `LogicManagerTest`: actual JSON round trips,
  files without favourite status, notes and tag order, and command execution across a reload.
* `FavouriteArchiveIntegrationTest`: archived favourites hidden until restored, favourite changes rejected
  for archived contacts, preserved notes and emergency details, and files missing either status flag.
* `PersonCardTest`: real JavaFX cards and list cells, badge visibility, command-driven updates,
  preservation of other displayed details, and cell reuse after a contact is removed.

Run the full checks on Windows:

```powershell
.\gradlew.bat check coverage
```

To run the card and command-box UI tests together:

```powershell
.\gradlew.bat test --tests seedu.address.ui.PersonCardTest --tests seedu.address.ui.CommandBoxTest
```

Both UI test classes use `JavaFxExtension`. The display and Linux Xvfb requirements above apply.
Automated checks do not replace checking the badge's appearance in the running app; follow the
[favourite contact manual checks](DeveloperGuide.md#managing-favourite-contacts).

--------------------------------------------------------------------------------------------------------------------

## Types of tests

This project has three types of tests:

1. *Unit tests* target the lowest-level methods and classes.<br>
   For example: `seedu.address.commons.StringUtilTest`
1. *Integration tests* check how multiple code units work together; the individual units are assumed to work.<br>
   For example: `seedu.address.storage.StorageManagerTest`
1. *Hybrid tests* combine unit and integration testing. These tests check both the individual units and how they work together.<br>
   For example: `seedu.address.logic.LogicManagerTest`
