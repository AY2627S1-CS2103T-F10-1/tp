package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class Name {

    public static final int MAX_LENGTH = 100;
    public static final String MESSAGE_EMPTY = "Contact name cannot be empty.";
    public static final String MESSAGE_TOO_LONG = "Contact name too long, please shorten it and try again.";
    public static final String MESSAGE_CONSTRAINTS =
            "Names should only contain letters, digits, spaces and the symbols ' - . /, and each word should "
            + "start with a letter or digit.";

    /**
     * A word starts with a letter or digit in any language, so " " is invalid and a word cannot be mistaken for a
     * {@code /} prefix. Accent marks ({@code \p{M}}) are allowed for letters typed as a base letter and a combining
     * mark, and the symbols are for names such as "s/o", "O'Brien", "Mary-Jane" and "Jr.".
     */
    private static final String WORD_REGEX = "[\\p{L}\\p{N}][\\p{L}\\p{M}\\p{N}'./-]*";

    public static final String VALIDATION_REGEX = WORD_REGEX + "( " + WORD_REGEX + ")*";

    public final String fullName;

    /**
     * Constructs a {@code Name}, collapsing extra whitespace.
     *
     * @param name A valid name.
     */
    public Name(String name) {
        requireNonNull(name);
        checkArgument(isValidName(name), MESSAGE_CONSTRAINTS);
        fullName = StringUtil.collapseWhitespace(name);
    }

    /**
     * Returns true if a given string is a valid name once extra whitespace is collapsed.
     */
    public static boolean isValidName(String test) {
        String normalizedTest = StringUtil.collapseWhitespace(test);
        return normalizedTest.length() <= MAX_LENGTH && normalizedTest.matches(VALIDATION_REGEX);
    }


    @Override
    public String toString() {
        return fullName;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Name otherName)) {
            return false;
        }

        return fullName.equals(otherName.fullName);
    }

    @Override
    public int hashCode() {
        return fullName.hashCode();
    }

}
