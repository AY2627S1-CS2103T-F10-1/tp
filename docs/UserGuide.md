---
layout: page
title: User Guide
---

AddressBook Level 3 (AB3) is a **desktop application for managing contacts, optimized for use through a Command Line Interface (CLI)** while retaining the benefits of a Graphical User Interface (GUI). If you type quickly, AB3 can help you manage contacts faster than traditional GUI applications.

* Table of Contents
{:toc}

--------------------------------------------------------------------------------------------------------------------

## Quick start

1. Ensure that Java `25` or later is installed on your computer.<br>
   **Mac users:** Ensure you have the precise JDK version prescribed [here](https://se-education.org/guides/tutorials/javaInstallationMac.html).

1. Download the latest `.jar` file from [here](https://github.com/se-edu/addressbook-level3/releases).

1. Copy the file to the folder you want to use as the _home folder_ for your AddressBook.

1. Open a terminal, `cd` to the folder containing the JAR file, and run `java -jar addressbook.jar`.<br>
   A GUI similar to the one below should appear in a few seconds. Note how the app contains some sample data.<br>
   ![Ui](images/Ui.png)

1. Type a command in the command box and press Enter to execute it. For example, type **`help`** and press Enter to open the help window.<br>
   Some example commands you can try:

   * `list` : Lists all contacts.

   * `list emerg` : Lists contacts tagged as emergency.

   * `add John Doe /phone 98765432` : Adds a contact named `John Doe` to the Address Book.

   * `delete 3` : Deletes the 3rd contact shown in the current list.

   * `clear` : Deletes all contacts.

   * `exit` : Exits the app.

1. Use the Up (`↑`) and Down (`↓`) arrow keys in the command box to
   [recall previous commands](#recalling-previous-commands).

1. Refer to the [Features](#features) section below for details of each command.

--------------------------------------------------------------------------------------------------------------------

## Features

<div markdown="block" class="alert alert-info">

**:information_source: Notes about the command format:**<br>

* Words in `UPPER_CASE` are the parameters to be supplied by the user.<br>
  For example, in `add n/NAME`, replace `NAME` with a value such as `John Doe`.

* Items in square brackets are optional.<br>
  For example, `n/NAME [t/TAG]` can be used as `n/John Doe t/friend` or as `n/John Doe`.

* Items followed by `…`​ can appear zero or more times.<br>
  For example, `[t/TAG]…​` may be omitted, or written as `t/friend` or `t/friend t/family`.

* Parameters can be in any order.<br>
  For example, if the command specifies `n/NAME p/PHONE_NUMBER`, `p/PHONE_NUMBER n/NAME` is also acceptable.

* Extraneous parameters for `help`, `exit`, and `clear` are ignored. The `list` command rejects extra parameters.<br>
  For example, `help 123` is interpreted as `help`.

* If you are using a PDF version of this document, be careful when copying and pasting commands that span multiple lines as space characters surrounding line-breaks may be omitted when copied over to the application.
</div>

### Viewing help: `help`

Shows a message explaining how to access the help page.

![help message](images/helpMessage.png)

Format: `help`


### Recalling previous commands

Reuse or correct previously submitted commands without typing them again.

Click the command box, then use these keys without holding Shift, Ctrl, Alt or Command:

Key | Action
----|-------
**↑ (Up)** | Recall the most recent command, then progressively older commands.
**↓ (Down)** | Browse newer commands. Moving past the newest entry restores the unfinished input typed before browsing.
**Enter** | Submit the text displayed in the command box.

* Recalling a command only fills the command box and moves the caret to the end.
  The contact list and result message do not change until you press Enter.
* The latest 100 non-blank submissions are remembered, including failed commands and repeated submissions.
  Empty or whitespace-only input is ignored. When the history is full, the oldest entry is discarded.
* Pressing ↑ at the oldest entry keeps showing that command. Pressing ↓ after restoring the unfinished
  input leaves that input unchanged. With no history, either arrow leaves the text unchanged.
* You can edit recalled commands. Navigating history discards unsubmitted changes to recalled text;
  it does not overwrite the original command or the unfinished input saved before browsing.
* Submitting a non-blank command adds a new history entry and discards the saved unfinished input.
  The next ↑ recalls that submission, even if it failed.
* History clears when you close the app; it is not saved with your contacts.
* This feature recalls text and does not undo or redo previous actions. Resubmitted commands use the
  current contact data and displayed indices: `delete 2` deletes whoever is currently shown as contact #2.

Example:

1. Submit `list`, then `find /tag Resident`.
1. Type `find /name Al` without pressing Enter.
1. Press ↑ to recall `find /tag Resident`, then ↑ again to recall `list`.
1. Press ↓ to return to `find /tag Resident`, then ↓ again to restore `find /name Al`.
1. Edit the text if needed and press Enter to execute it.

### Adding a contact: `add`

Adds a contact with a required name and phone number, and optional room, tags and note. Tags show a contact's category at a glance (e.g., `Resident`, `Resident Fellow`, `Staff`).

Format: `add NAME /phone PHONE [/room ROOM] [/tag TAG]…​ [/note NOTE]`

* Name is required and can contain up to 100 characters. Existing name character rules apply.
* Phone is required and can contain up to 20 characters: digits, spaces, `+`, hyphens and parentheses.
* Room is optional for any contact, regardless of tags. It can contain up to 20 characters:
  letters, digits, spaces and hyphens.
* Note is optional free text, such as an allergy or how the contact prefers to be reached. It can contain
  up to 200 characters of any kind except `/`, which marks the start of a field.
* Enter the name first. `/phone`, `/room`, `/tag` and `/note` can then be given in any order.
  `/phone`, `/room` and `/note` cannot be repeated.
* Repeat `/tag` once per tag, up to five tags. Spaces inside a tag are allowed, so `/tag Resident Fellow` is one tag.
* Tags may contain letters, digits, spaces and hyphens (`-`), up to 50 characters. They are not case-sensitive,
  and are displayed with the first letter of each word capitalized.
* Duplicate tags, including tags differing only in case or extra spaces (e.g., `/tag RA /tag ra`), are rejected.
* Leading and trailing spaces are trimmed, and extra whitespace is collapsed into one space.
* Supplying `/room`, `/tag` or `/note` without a value is rejected.
* A contact is a duplicate when its name matches an existing contact (case-sensitive).
  The add command trims and collapses whitespace before comparing names. Phone number, room and tags do not
  affect duplicate detection.
* Contacts with the same name cannot be added, even when their phone numbers differ.
* On success, the contact appears in the list with its supplied room, tags and note, and
  `Contact NAME added successfully.` is shown. On failure, no contact is added.

Examples:

* `add Sarah Tan /phone 9123 4567`
* `add Daniel Lim /phone +65 8123 4567 /room B-204`
* `add Sarah Tan /phone 91234567 /room B-204 /tag Resident /tag Floor 3`
* `add Wei Ming /phone 9876 5432 /room C-310 /note Allergic to peanuts, carries an EpiPen`

### Listing all contacts: `list`

Shows all stored contacts in numbered order, including their available details.
This restores the full list after a search and does not change any stored contact.

Format: `list`

* Any text after `list` is rejected. To show only contacts with a given tag, use [`find /tag`](#locating-persons-by-name-tag-or-phone-find).

* The keyword is case-insensitive: `list`, `LIST`, and `LiSt` work the same way.
* The command keyword is case-sensitive: use lowercase `list`. `LIST` and `LiSt` are rejected as unknown commands.
* Leading and trailing spaces are ignored.
* Use `list emerg` for the emergency shortcut described below. To search by any tag, use
  [`find /tag`](#locating-persons-by-name-or-tag-find).
* Unsupported arguments, such as `list abc` or the retired `list /filter Emergency`, are rejected with
  `Invalid command format. Usage: list`. The displayed list stays unchanged when the command is rejected.
* When there are no stored contacts, the list is empty and the result message is `No contacts found.`.
* Otherwise, the result message is `Listed all persons.`.

### Listing emergency contacts: `list emerg`

Shows only contacts carrying the complete tag `emergency`, in numbered order with their available details.
It searches all stored contacts, including those hidden by a previous search.
The command does not add, edit, delete, or deduplicate contacts.

Format: `list emerg`

* Both command keywords are case-sensitive: use lowercase `list emerg`.
  `LIST emerg`, `List emerg`, and `list EMERG` are rejected.
* Emergency tag matching is case-insensitive: tags such as `emergency`, `Emergency`, and `EMERGENCY` all match.
* Leading and trailing spaces, and extra spaces between the keywords, are ignored.
* A contact with several tags appears once if any tag is `emergency`.
  Tags such as `emerg` or `emergencyservice` do not match.
* Extra arguments, such as `list emerg friend`, are rejected with `Invalid command format. Usage: list emerg`.
  The displayed list stays unchanged when the command is rejected.
* When no emergency contacts exist, the list is empty and the message is `No emergency contacts found.`.
* Otherwise, the result message is `Listed all emergency contacts.`.
* `find /tag Emergency` displays the same contacts through the general tag search.
* Run `list` to return to all contacts. `list` alone is a valid command to list everyone.

Example using the existing tag syntax:

1. `add Hall Security /phone 98765432 /tag Emergency`
1. `list emerg` shows Hall Security and any other emergency contacts.
1. `list` restores all contacts.

### Editing a person: `edit`

Changes one or more details of an existing contact.

Format: `edit INDEX [/name NAME] [/phone PHONE] [/room ROOM] [/tag TAG]…​ [/note NOTE]`

* `INDEX` is the number shown beside the contact in the current list, and must be a positive integer.
* At least one field must be given, in any order. Fields you leave out stay as they are.
* Each field follows the same rules as in `add`, so `/name`, `/phone`, `/room` and `/note` cannot be repeated.
* `/tag` replaces all of the contact's existing tags with the tags given. Repeat `/tag` once per tag.
* An optional field given with nothing after it is removed: `/room` on its own removes the room,
  `/note` on its own removes the note, and `/tag` on its own removes all tags. `/name` and `/phone` are required, so they cannot be removed.

Examples:
* `edit 2 /phone 9123 4567` changes the phone number of the 2nd contact.
* `edit 1 /room B-204 /tag Resident /tag Floor 3` sets the room of the 1st contact and replaces their tags.
* `edit 3 /room` removes the room of the 3rd contact.
* `edit 1 /note Prefers WhatsApp over calls` sets or replaces the note of the 1st contact.
* `edit 1 /note` removes the note of the 1st contact.


### Locating persons by name, tag or phone: `find`

Finds persons whose full name is exactly the given name, or who have the given tag or phone number.

Format: `find /name NAME`, `find /tag TAG` or `find /phone NUMBER`

* Exactly one of `/name`, `/tag` and `/phone` must be given, and nothing may be typed before it.
* Name search is case-sensitive; for example, `find /name alex yeoh` does not match `Alex Yeoh`.
* Tag search ignores case and extra spaces; for example, `find /tag Friends` matches the tag `friends`.
* Spaces before and after `NAME`, `TAG` or `NUMBER` are ignored.
* If no person matches, the message `No contacts found` is shown and the list is empty. Otherwise, the number of persons listed is shown.
* A prefix must be followed by a space, so `find /tagfriends` is not recognised as a tag search.
* If the prefix is missing or not followed by a space (such as `/tagfriends`), multiple search prefixes are given, or `NAME`, `TAG` or `NUMBER` is empty, an invalid command format message is shown.
* If `/name`, `/tag` or `/phone` is repeated, a message listing the repeated prefix is shown.

Finding by name (`/name`):
* Everything after `/name` is treated as one full name. It is not split into separate keywords.
* The whole name must match; for example, `find /name Alex` does not match `Alex Yeoh`.
* Spaces inside the name are matched as typed.

Finding by phone (`/phone`):
* Searches all contacts for an exact phone number, including contacts hidden by an earlier search.
* Extra spaces are ignored, as when storing phone numbers. Other formatting must match:
  `+65 9123 4567` does not match `+6591234567`, and `9123` does not match `91234567`.
* All contacts sharing the number are listed. Any non-empty value is accepted; an unknown value matches nobody.
* Example: `find /phone 91234567` lists contacts whose phone number is `91234567`.

Finding by tag (`/tag`):
* Only one tag can be searched at a time, so `find /tag friends /tag family` is rejected.
* Any non-empty `TAG` is accepted. A value that is not a valid tag, or that no contact has, simply matches nobody.
* A person is listed if any one of their tags is exactly `TAG`; for example, `find /tag friends` matches a person tagged `friends` and `owesMoney`, but not one tagged `friends2`.

Examples:
* `find /name Alex Yeoh` returns `Alex Yeoh`
* `find /name Alex` returns no persons, because `Alex` is not the full name
* `find /tag friends` returns all persons who have the tag `friends`

### Deleting a person: `delete`

Deletes the specified person from the address book.

Format: `delete INDEX` or `delete /name NAME`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​
* Name deletion searches **all contacts**, even when the displayed list is filtered.
* `/name NAME` must be the contact's full name, matched case-sensitively, the same way as [`find /name`](#locating-persons-by-name-tag-or-phone-find).
* If `/name` is missing or repeated, `NAME` is empty or there is text before `/name`, an invalid command format message is shown.
* Exactly one match is deleted immediately. With no matches, nothing is deleted.
* If more than one contact matches, nothing is deleted. The matching contacts are displayed with their indexes; continue with `delete INDEX`.
* The search results remain displayed; deleting the only match leaves an empty list. Use `list` to show all contacts.
* Purely numeric input is treated as an index, so `delete 1` can never be a name search.


Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find /name Betsy Crower` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Clearing all entries: `clear`

Clears all entries from the address book.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
A contact with more than 5 distinct tags also makes the data file invalid.<br>
Furthermore, certain edits can cause the AddressBook to behave in unexpected ways (e.g., if a value entered is outside of the acceptable range). Therefore, edit the data file only if you are confident that you can update it correctly.
</div>

### Archiving data files `[coming in v2.0]`

_Details coming soon ..._

--------------------------------------------------------------------------------------------------------------------

## FAQ

**Q**: How do I transfer my data to another computer?<br>
**A**: Install the app on the other computer and overwrite the data file it creates with the data file from your previous AddressBook home folder.

--------------------------------------------------------------------------------------------------------------------

## Known issues

1. **When using multiple screens**, if you move the application to a secondary screen, and later switch to using only the primary screen, the GUI will open off-screen. The remedy is to delete the `preferences.json` file created by the application before running the application again.
2. **If you minimize the Help Window** and then run the `help` command (or use the `Help` menu, or the keyboard shortcut `F1`) again, the original Help Window will remain minimized, and no new Help Window will appear. The remedy is to manually restore the minimized Help Window.

--------------------------------------------------------------------------------------------------------------------

## Command summary

Action | Format, Examples
--------|------------------
**Add** | `add NAME /phone PHONE [/room ROOM] [/tag TAG]…​ [/note NOTE]` <br> e.g., `add Daniel Lim /phone 8123 4567 /room B-204 /tag Resident Fellow /tag Floor 3 /note Prefers WhatsApp`
**Clear** | `clear`
**Delete** | `delete INDEX` or `delete /name NAME`<br> e.g., `delete 3`, `delete /name Alice Tan`
**Edit** | `edit INDEX [/name NAME] [/phone PHONE] [/room ROOM] [/tag TAG]…​ [/note NOTE]`<br> e.g., `edit 2 /phone 9123 4567`, `edit 1 /tag`, `edit 1 /note`
**Find** | `find /name NAME`, `find /tag TAG` or `find /phone NUMBER`<br> e.g., `find /name James Jake`, `find /tag friends`
**List** | `list`
**List emergency contacts** | `list emerg`
**Help** | `help`
**Recall commands** | ↑ for older commands; ↓ for newer commands or the saved unfinished input. Press Enter to execute. The command box must be focused.
