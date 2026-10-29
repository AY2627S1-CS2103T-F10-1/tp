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

   * `list` : Lists all active contacts.

   * `list emerg` : Lists contacts tagged as emergency.

   * `fav 1` : Marks the 1st displayed contact as a favourite.

   * `list fav` : Lists favourite contacts.

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

* Extraneous parameters for `help`, `exit`, and `clear` are ignored. The `list`, `list emerg`, `list archived`, `list fav`,
  `fav INDEX` and `unfav INDEX` commands reject extra parameters.<br>
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
  up to 200 characters of any kind. `/` is allowed inside a word (e.g., `24/7`, `and/or`, `w/`, links), but a word
  cannot start with `/`, because that marks the start of a field. If a word in the note is a field name such as
  `/room` or `/tag`, the rest of the note is taken as that field: `/note talk to /room officer` gives the note
  `talk to` and the room `officer`.
* Enter the name first. `/phone`, `/room`, `/tag` and `/note` can then be given in any order.
  `/phone`, `/room` and `/note` cannot be repeated.
* Repeat `/tag` once per tag, up to five tags. Spaces inside a tag are allowed, so `/tag Resident Fellow` is one tag.
* Tags may contain letters and digits in any language (e.g., `Café`, `宿舍`), spaces and hyphens (`-`),
  up to 50 characters. They are not case-sensitive, and are displayed with the first letter of each word
  capitalized and the rest in lower case, so `RA`, `ra` and `Ra` all appear as `Ra`.
  Tags in an edited data file are displayed the same way.
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

### Listing active contacts: `list`

Shows all active (not archived) contacts in numbered order, including their available details.
This restores the active list after a search or archived view and does not change stored contacts.

Format: `list`

* Only the `archived`, `emerg` and `fav` subcommands are supported after `list`. To show only contacts with a given tag, use [`find /tag`](#locating-persons-by-name-tag-or-phone-find).

* The command keyword is case-sensitive: use lowercase `list`. `LIST` and `LiSt` are rejected as unknown commands.
* Leading and trailing spaces are ignored.
* Use `list emerg` for emergency-tagged contacts or `list fav` for favourite contacts, as described below.
  To search by any tag, use [`find /tag`](#locating-persons-by-name-tag-or-phone-find).
* Unsupported arguments, such as `list abc` or the retired `list /filter Emergency`, are rejected with
  `Invalid command format. Usage: list`. The displayed list stays unchanged when the command is rejected.
* When there are no active contacts, the list is empty and the result message is `No contacts found.`.
* Otherwise, the result message is `Listed all persons.`.

### Listing emergency contacts: `list emerg`

Shows only active contacts carrying the complete tag `emergency`, in numbered order with their available details.
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
* Run `list` to return to all active contacts.

Example using the existing tag syntax:

1. `add Hall Security /phone 98765432 /tag Emergency`
1. `list emerg` shows Hall Security and any other emergency contacts.
1. `list` restores all active contacts.

### Marking a contact as a favourite: `fav`

Marks a frequently used contact for quick access. Favourite status is separate from tags:
adding a tag named `fav` or `favourite` does not mark a contact as a favourite.

Format: `fav INDEX`

* `INDEX` must be a positive integer identifying a contact in the currently displayed list.
  For example, after a search, `fav 1` marks the 1st search result.
* The contact card shows a **★ Favourite** badge. Its other details and the current filter are preserved.
* On success, the message starts with `Marked as favourite:` and includes the contact's details.
* Repeating the command leaves the contact unchanged and shows `Contact is already a favourite:`
  followed by its details. The command does not toggle the status.
* Ordinary edits, including replacing or clearing tags, preserve favourite status.
* Archived contacts must be unarchived before using `fav` or `unfav`. Otherwise, the command reports
  `Cannot change favourite status for an archived contact. Unarchive it first.` and leaves data unchanged.
* The command word is case-sensitive. Leading and trailing spaces are ignored; extra arguments are rejected.
* Missing, zero, negative, non-integer or overflowing indices show an invalid command format message with
  the `fav` usage. A positive index outside the displayed list shows `The person index provided is invalid.`.
  Rejected commands leave contact data and the displayed list unchanged.

Example: `fav 2` marks the 2nd currently displayed contact.

### Removing favourite status: `unfav`

Removes a contact's favourite status while retaining the contact and all its other details.

Format: `unfav INDEX`

* The index and argument rules are the same as for `fav`, using the currently displayed list.
* The badge disappears and the message starts with `Removed favourite status:` followed by the contact's details.
* If the contact is not a favourite, it stays unchanged and the message starts with `Contact is not a favourite:`.
* The current filter is preserved. In the `list fav` view, the contact disappears immediately and the
  remaining displayed contacts are renumbered. Check the displayed numbers before the next command.

Example: `unfav 1` removes favourite status from the 1st currently displayed contact.

### Listing favourite contacts: `list fav`

Shows active favourite contacts from across the address book, including those hidden by an earlier search.

Format: `list fav`

* Contacts keep their relative order from the main list, rather than the order in which they were marked.
  The displayed results are numbered consecutively from 1.
* For example, if the main list contains Alice at #1, Bob at #2 and Charlie at #3, marking Alice and Charlie
  shows Alice at #1 and Charlie at #2 in `list fav`. In that view, `unfav 2` removes Charlie's favourite status.
* Listing favourites does not change stored contact data.
* Archived contacts are hidden from this view but retain their favourite status. Restoring an archived
  favourite makes it appear here again. Run `list archived` and `unarchive INDEX` to restore it.
* Both words must be lowercase. Extra spaces or tabs between them are accepted.
* Extra arguments such as `list fav 1` are rejected with `Invalid command format. Usage: list fav`.
  The displayed list stays unchanged.
* With no favourites, the list is empty and the message is `No favourite contacts found.`.
  Otherwise, the message is `Listed all favourite contacts.`.
* Run `list` to show all active contacts again.

### Editing a person: `edit`

Changes one or more details of an active contact. Archived contacts must be unarchived before editing.

Format: `edit INDEX [/name NAME] [/phone PHONE] [/room ROOM] [/tag TAG]…​ [/note NOTE]`

* `INDEX` is the number shown beside the contact in the current list, and must be a positive integer.
* At least one field must be given, in any order. Fields you leave out stay as they are.
* Each field follows the same rules as in `add`, so `/name`, `/phone`, `/room` and `/note` cannot be repeated.
* `/tag` replaces all of the contact's existing tags with the tags given. Repeat `/tag` once per tag.
* Favourite status is preserved, including when tags are replaced or cleared.
* An optional field given with nothing after it is removed: `/room` on its own removes the room,
  `/note` on its own removes the note, and `/tag` on its own removes all tags. `/name` and `/phone` are required, so they cannot be removed.

Examples:
* `edit 2 /phone 9123 4567` changes the phone number of the 2nd contact.
* `edit 1 /room B-204 /tag Resident /tag Floor 3` sets the room of the 1st contact and replaces their tags.
* `edit 3 /room` removes the room of the 3rd contact.
* `edit 1 /note Prefers WhatsApp over calls` sets or replaces the note of the 1st contact.
* `edit 1 /note` removes the note of the 1st contact.


### Assigning an emergency contact: `emergency`

Stores emergency details for a person without adding another entry to the contact list.

Format: `emergency INDEX /name NAME /phone PHONE [/relation RELATION]`

* `INDEX` identifies the person in the currently displayed list, as with `edit`.
* Name and phone are required and follow the same rules as `add`. Fields may appear in any order.
* Relation is optional free text, up to 50 characters. Extra whitespace is collapsed.
* Each prefix may appear only once. Invalid input leaves the contact unchanged.
* A person can have one emergency contact. Running the command again replaces all previous emergency details.
  Name and phone are required each time; omitting relation replaces the previous relation with an empty value.
* Emergency details are a separate person field. Replacing or clearing tags does not remove them.
  Clearing emergency details without providing a replacement is not supported yet.
* Details appear on the person's card and are saved automatically. Ordinary edits preserve them.
* The same emergency details may be assigned to several people independently.
* This does not change `list emerg`, which lists the app's tagged emergency resources.

Example: `emergency 1 /name Bob Choo /phone 9123 4567 /relation Brother`

### Locating persons by name, tag or phone: `find`

Finds active persons whose name is close to the given name, even when run from the archived view. Also finds who have the given tag or phone number.

Format: `find /name NAME`, `find /tag TAG` or `find /phone NUMBER`

* Exactly one of `/name`, `/tag` and `/phone` must be given, and nothing may be typed before it.
* Name search ignores capital letters and forgives small typos; for example, `find /name alx yeo` finds `Alex Yeoh`.
* Tag search ignores case and extra spaces; for example, `find /tag Friends` matches the tag `friends`.
* Spaces before and after `NAME`, `TAG` or `NUMBER` are ignored.
* If no person matches, the message `No contacts found` is shown and the list is empty. Otherwise, the number of persons listed is shown.
* A prefix must be followed by a space, so `find /tagfriends` is not recognised as a tag search.
* If the prefix is missing or not followed by a space (such as `/tagfriends`), multiple search prefixes are given, or `NAME`, `TAG` or `NUMBER` is empty, an invalid command format message is shown.
* If `/name`, `/tag` or `/phone` is repeated, a message listing the repeated prefix is shown.

Finding by name (`/name`):
* You do not need the full name. Each word you type must match one of the words in the contact's name,
  in any order; for example, `find /name Yeoh` and `find /name Yeoh Alex` both find `Alex Yeoh`.
* Every word you type must match. For example, `find /name Alex Tan` does not find `Alex Yeoh`, because no part of
  the name is close to `Tan`.
* A word you type matches a word of the name if it is:
  * the same word, ignoring capital letters,
  * the start of the word, typed exactly and at least 3 letters long; for example, `find /name Bala` finds
    `Roy Balakrishnan`, or
  * the same word with a few typos. One typo is one wrong, missing or extra letter, or two neighboring letters
    swapped; for example, `Yoeh` is one typo away from `Yeoh`. The number of typos allowed depends on the
    **shorter** of the two words:

    | Letters in the shorter word | Typos allowed |
    |-----------------------------|---------------|
    | 1–2                         | 0             |
    | 3–5                         | 1             |
    | 6–9                         | 2             |
    | 10 or more                  | 3             |

    So `find /name Jonathn` (1 typo) finds `Jonathan Chua`, but `find /name Jnthn` (3 typos in a 5-letter word) does not.
    Short words must be typed exactly, so `find /name Al` does not find `Alex Yeoh`, and `find /name Ong` does not
    find `Ng Kai`.
* Because small typos are allowed, a search may also list contacts with similar names; for example,
  `find /name Tan` may also list a contact named `Tay`.
* The closest matches are listed first: contacts whose name contains the exact words you typed, then contacts
  whose name words start with what you typed, then contacts with typos, fewest typos first. Contacts that match
  equally well keep their usual order. For example, `find /name Tan` lists `Tan Wei Ming` before `Tay Li Hua`,
  so `edit 1` changes `Tan Wei Ming`.
* Extra spaces between words are ignored, including the non-breaking spaces that names pasted from a chat app
  or web page may contain.

Finding by phone (`/phone`):
* Searches all active contacts for an exact phone number, including contacts hidden by an earlier search.
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
* `find /name alex` returns `Alex Yeoh`
* `find /name Bernise` returns `Bernice Yu`, despite the typo
* `find /name Bala` returns `Roy Balakrishnan`
* `find /tag friends` returns all persons who have the tag `friends`

### Archiving contacts: `archive`

Format: `archive INDEX` or `archive /tag TAG`

* `INDEX` is a positive integer from the currently displayed list, including search results.
* `/tag TAG` archives **all active contacts** with that tag across the address book,
  including contacts outside the displayed results. Matching ignores case and extra whitespace.
* Selection is internal: the command runs immediately without a preview or confirmation.
* The current view/filter stays in place. Archived contacts disappear from active results;
  in the archived view, newly archived contacts appear.
* The result reports how many contacts were archived. Already archived contacts are skipped
  for tag selection; selecting one by index reports that it is already archived.
* A missing tag, repeated `/tag`, mixed index/tag arguments, unknown prefix, or invalid index is rejected.
  An unmatched tag reports that no contacts were archived.
* Archiving preserves contact details. A same-name contact cannot be added while the original is archived;
  restore the existing contact instead.

Examples: `archive 1`, `archive /tag Study Group`.

### Listing archived contacts: `list archived`

Shows only archived contacts with their displayed index numbers. If none exist, the result is
`No archived contacts found.`. Extra arguments are rejected.

Use `list`, `find`, or `list emerg` to return to active contacts. Archived contacts remain
stored and are hidden at startup and in these normal lists/searches.

### Restoring archived contacts: `unarchive`

Format: `unarchive INDEX` or `unarchive /tag TAG`

* Run `list archived` before restoring by index. Use the index currently displayed.
* Tag selection restores all archived contacts matching the tag across the address book,
  ignoring case and extra whitespace. Active contacts are unchanged.
* The current view/filter stays in place: restored contacts disappear from the archived view.
  Use `list` to see them among active contacts.
* The result reports the number restored. An active contact selected by index or a tag with
  no archived matches produces an explanatory message without changing contacts.
* Argument validation follows the same rules as `archive`.

Example: `list archived`, then `unarchive 1`; or `unarchive /tag Study Group`.

### Deleting a person: `delete`

Permanently deletes the specified person from the address book. From `list archived`,
`delete INDEX` can delete an archived contact; it cannot then be unarchived.

Format: `delete INDEX` or `delete /name NAME`

* Deletes the person at the specified `INDEX`.
* The index refers to the index number shown in the displayed person list.
* The index **must be a positive integer** 1, 2, 3, …​
* Name deletion searches **all active contacts**, even when the displayed list is filtered.
* `/name NAME` must be the contact's exact full name, with matching capital letters. Unlike [`find /name`](#locating-persons-by-name-tag-or-phone-find), typos and partial names are not accepted, so a typo cannot delete a different contact.
* If `/name` is missing or repeated, `NAME` is empty or there is text before `/name`, an invalid command format message is shown.
* Exactly one match is deleted immediately. With no matches, nothing is deleted.
* If more than one contact matches, nothing is deleted. The matching contacts are displayed with their indexes; continue with `delete INDEX`.
* The search results remain displayed; deleting the only match leaves an empty list. Use `list` to show all active contacts.
* Purely numeric input is treated as an index, so `delete 1` can never be a name search.


Examples:
* `list` followed by `delete 2` deletes the 2nd person in the address book.
* `find /name Betsy Crower` followed by `delete 1` deletes the 1st person in the results of the `find` command.

### Clearing all entries: `clear`

Permanently clears all active and archived entries, regardless of the current view.

Format: `clear`

### Exiting the program: `exit`

Exits the program.

Format: `exit`

### Saving the data

AddressBook automatically saves data after every command. You do not need to save manually. Archive state is stored as the boolean `archive`: `true`
means archived, and `false` means active. Older files without this field load contacts as active.

If saving fails, an error is shown and the in-memory change remains visible. It has not been
confirmed saved to disk; resolve the storage problem before relying on it surviving a restart.

Favourite status is saved with each contact and remains after restarting, including when the status is removed.
Older data files without favourite status still load; their contacts start unfavourited.

### Editing the data file

AddressBook data is saved automatically as a JSON file `[JAR file location]/data/addressbook.json`. Advanced users are welcome to update data directly by editing that data file.

Each contact's `isFavourite` property is a JSON boolean, `true` or `false`. If the property is absent, it defaults to `false`.

<div markdown="span" class="alert alert-warning">:exclamation: **Caution:**
If your changes make the data file invalid, AddressBook starts with an empty address book at the next run. The invalid file remains on disk until you run a command (AddressBook saves after every command). Still, we recommend backing up the file before editing it.<br>
A contact with more than 5 distinct tags also makes the data file invalid, as does a note longer than 200 characters or with a word starting with `/` (e.g., `see /docs`; a link such as `https://nus.edu.sg` is fine). See [`add`](#adding-a-contact-add) for the rules of each field.<br>
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
**Archive** | `archive INDEX` or `archive /tag TAG`
**List archived** | `list archived`
**Unarchive** | `unarchive INDEX` or `unarchive /tag TAG`
**Clear** | `clear`
**Delete** | `delete INDEX` or `delete /name NAME`<br> e.g., `delete 3`, `delete /name Alice Tan`
**Edit (suspended)** | Temporarily unavailable. Displays `Edit not currently supported`.
**Find** | `find /name NAME`, `find /tag TAG` or `find /phone NUMBER`<br> e.g., `find /name alx yeo`, `find /tag friends`, `find /phone 87438807`
**Edit** | `edit INDEX [/name NAME] [/phone PHONE] [/room ROOM] [/tag TAG]…​`<br> e.g., `edit 2 /phone 9123 4567`, `edit 1 /tag`
**List** | `list`
**List emergency contacts** | `list emerg`
**Mark favourite** | `fav INDEX`<br> e.g., `fav 2`
**Remove favourite status** | `unfav INDEX`<br> e.g., `unfav 1`
**List favourite contacts** | `list fav`
**Assign emergency contact** | `emergency INDEX /name NAME /phone PHONE [/relation RELATION]`
**Help** | `help`
**Recall commands** | ↑ for older commands; ↓ for newer commands or the saved unfinished input. Press Enter to execute. The command box must be focused.
