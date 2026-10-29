package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/** Matches an exact room number, ignoring case and extra whitespace. */
public class RoomEqualsPredicate implements Predicate<Person> {
    private final String room;

    /** Creates a normalized search value; unknown values simply match no room. */
    public RoomEqualsPredicate(String room) {
        requireNonNull(room);
        this.room = StringUtil.collapseWhitespace(room).toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean test(Person person) {
        return person.getRoom().value.toLowerCase(Locale.ROOT).equals(room);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof RoomEqualsPredicate otherPredicate && room.equals(otherPredicate.room);
    }

    @Override
    public int hashCode() {
        return room.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("room", room).toString();
    }
}
