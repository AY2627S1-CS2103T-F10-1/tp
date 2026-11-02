package seedu.address.model.person;

import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Phone} equals the given number, ignoring whitespace.
 */
public class PhoneEqualsPredicate implements Predicate<Person> {
    private final String phone;

    public PhoneEqualsPredicate(String phone) {
        this.phone = removeWhitespace(phone);
    }

    @Override
    public boolean test(Person person) {
        return removeWhitespace(person.getPhone().value).equals(phone);
    }

    /** Removes supported whitespace for comparison without changing stored phone formatting. */
    private static String removeWhitespace(String phone) {
        return StringUtil.collapseWhitespace(phone).replace(" ", "");
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
