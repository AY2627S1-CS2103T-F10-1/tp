package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/** An optional free-text note about a contact, such as an allergy or how they prefer to be reached. */
public class Note {
    /** Keeps the note short enough to read at a glance on the contact card. */
    public static final int MAX_LENGTH = 200;
    public static final String MESSAGE_EMPTY = "Note cannot be empty.";
    public static final String MESSAGE_TOO_LONG = "Note too long, please shorten it and try again.";
    public static final String MESSAGE_INVALID_CHARACTERS = "Note cannot contain \"/\".";
    public static final String MESSAGE_CONSTRAINTS = "Notes must be at most " + MAX_LENGTH
            + " characters long and cannot contain \"/\".";

    /** Marks the start of a command prefix, so it cannot appear in a note. */
    private static final String FORBIDDEN_CHARACTER = "/";

    public final String value;

    /** Creates a normalized note; an empty value represents no note. */
    public Note(String note) {
        requireNonNull(note);
        String normalized = StringUtil.collapseWhitespace(note);
        checkArgument(isValidNote(normalized), MESSAGE_CONSTRAINTS);
        value = normalized;
    }

    /** Empty denotes a contact without a note. */
    public static boolean isValidNote(String note) {
        return note.length() <= MAX_LENGTH && !note.contains(FORBIDDEN_CHARACTER);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Note otherNote && value.equals(otherNote.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public String toString() {
        return value;
    }
}
