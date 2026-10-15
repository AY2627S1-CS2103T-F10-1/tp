package seedu.address.model.person;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} is exactly equal (case-sensitive) to the name given.
 */
public class NameEqualsPredicate implements Predicate<Person> {
    private final String name;

    public NameEqualsPredicate(String name) {
        this.name = name;
    }

    @Override
    public boolean test(Person person) {
        return person.getName().fullName.equals(name);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameEqualsPredicate otherNameEqualsPredicate)) {
            return false;
        }

        return name.equals(otherNameEqualsPredicate.name);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("name", name).toString();
    }
}
