package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.stream.Stream;

import seedu.address.commons.util.StringUtil;

/** An optional free-text note about a contact, such as an allergy or how they prefer to be reached. */
public class Note {
    /** Keeps the note short enough to read at a glance on the contact card. */
    public static final int MAX_LENGTH = 200;
    public static final String MESSAGE_EMPTY = "Note cannot be empty.";
    public static final String MESSAGE_TOO_LONG = "Note too long, please shorten it and try again.";
    public static final String MESSAGE_INVALID_CHARACTERS =
            "No word in a note can start with \"/\", as that starts a new field.";
    public static final String MESSAGE_CONSTRAINTS = "Notes must be at most " + MAX_LENGTH
            + " characters long, and no word in a note can start with \"/\".";

    /** Marks the start of a command prefix, so no word of a note can start with it. */
    private static final String PREFIX_MARKER = "/";
    private static final String WHITESPACE = "\\s+";

    public final String value;

    /** Creates a normalized note; an empty value represents no note. */
    public Note(String note) {
        requireNonNull(note);
        String normalized = StringUtil.collapseWhitespace(note);
        checkArgument(isValidNote(normalized), MESSAGE_CONSTRAINTS);
        value = normalized;
    }

    /** Returns true if {@code note} is a valid note; an empty note means the contact has no note. */
    public static boolean isValidNote(String note) {
        requireNonNull(note);
        return note.length() <= MAX_LENGTH && !hasWordStartingWithPrefixMarker(note);
    }

    /** Returns true if a word of {@code note} starts with "/"; a "/" inside a word, as in "24/7", is allowed. */
    private static boolean hasWordStartingWithPrefixMarker(String note) {
        return Stream.of(note.split(WHITESPACE)).anyMatch(word -> word.startsWith(PREFIX_MARKER));
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
