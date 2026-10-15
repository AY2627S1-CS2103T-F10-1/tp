package seedu.address.model.person;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Tests that a {@code Person} has a {@code Tag} that is equal (ignoring case) to the tag given.
 */
public class TagEqualsPredicate implements Predicate<Person> {
    private final Tag tag;

    public TagEqualsPredicate(Tag tag) {
        this.tag = tag;
    }

    @Override
    public boolean test(Person person) {
        return person.getTags().stream().anyMatch(t -> t.tagName.equalsIgnoreCase(tag.tagName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TagEqualsPredicate otherTagEqualsPredicate)) {
            return false;
        }

        return tag.equals(otherTagEqualsPredicate.tag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("tag", tag).toString();
    }
}
