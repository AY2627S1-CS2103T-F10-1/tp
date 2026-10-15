package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/** An optional room number supplied when creating a contact. */
public class Room {
    public static final int MAX_LENGTH = 20;
    public static final String MESSAGE_EMPTY = "Room number cannot be empty.";
    public static final String MESSAGE_TOO_LONG = "Room number too long, please shorten it and try again.";
    public static final String MESSAGE_INVALID_CHARACTERS = "Room number contains invalid characters.";

    public final String value;

    /** Creates a normalized room; an empty value represents no room. */
    public Room(String room) {
        requireNonNull(room);
        String normalized = StringUtil.collapseWhitespace(room);
        checkArgument(isValidRoom(normalized), MESSAGE_INVALID_CHARACTERS);
        value = normalized;
    }

    /** Empty denotes a contact without a room. */
    public static boolean isValidRoom(String room) {
        return room.length() <= MAX_LENGTH && (room.isEmpty() || room.matches("[\\p{Alnum} -]+"));
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Room otherRoom && value.equals(otherRoom.value);
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
