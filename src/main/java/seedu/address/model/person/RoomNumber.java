package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

/** Represents a resident's room number. */
public class RoomNumber {
    public static final String MESSAGE_CONSTRAINTS =
            "Room number must be 20 characters or fewer and contain only letters, numbers, spaces, or hyphens";
    public static final String VALIDATION_REGEX = "[A-Za-z0-9 -]{1,20}";

    public final String value;

    /**
     * Creates a validated room number with surrounding whitespace trimmed and extra whitespace collapsed.
     *
     * @param roomNumber A valid room number.
     */
    public RoomNumber(String roomNumber) {
        requireNonNull(roomNumber);
        String normalizedRoomNumber = roomNumber.trim().replaceAll("\\s+", " ");
        checkArgument(isValidRoomNumber(normalizedRoomNumber), MESSAGE_CONSTRAINTS);
        value = normalizedRoomNumber;
    }

    public static boolean isValidRoomNumber(String roomNumber) {
        return roomNumber != null && roomNumber.trim().matches(VALIDATION_REGEX);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof RoomNumber otherRoomNumber
                && value.equals(otherRoomNumber.value);
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
