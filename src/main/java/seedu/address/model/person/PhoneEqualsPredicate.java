package seedu.address.model.person;

import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Phone} equals the given number, ignoring extra spaces.
 */
public class PhoneEqualsPredicate implements Predicate<Person> {
    private final String phone;

    public PhoneEqualsPredicate(String phone) {
        this.phone = StringUtil.collapseWhitespace(phone);
    }

    @Override
    public boolean test(Person person) {
        return person.getPhone().value.equals(phone);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PhoneEqualsPredicate otherPhoneEqualsPredicate)) {
            return false;
        }

        return phone.equals(otherPhoneEqualsPredicate.phone);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("phone", phone).toString();
    }
}
