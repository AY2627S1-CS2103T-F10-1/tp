package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a Tag in the address book.
 * Tags are compared case-insensitively and are displayed with the first letter of each word capitalized,
 * so the same category looks identical no matter how it was typed.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public class Tag {

    public static final int MAX_LENGTH = 50;

    public static final String MESSAGE_CONSTRAINTS = "Tags should only contain letters, digits, spaces and hyphens, "
            + "and be at most " + MAX_LENGTH + " characters long.";
    public static final String MESSAGE_EMPTY = "Tag cannot be empty.";
    public static final String MESSAGE_TOO_LONG = "Tag too long, please shorten it and try again.";
    public static final String MESSAGE_INVALID_CHARACTERS = "Tag contains invalid characters.";

    /** One or more words of letters, digits and hyphens, separated by single spaces. */
    public static final String VALIDATION_REGEX = "[\\p{Alnum}-]+( [\\p{Alnum}-]+)*";

    private static final String WORD_SEPARATOR = " ";

    public final String tagName;

    /**
     * Constructs a {@code Tag}.
     * Extra whitespace in {@code tagName} is collapsed and the first letter of each word is capitalized.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_CONSTRAINTS);
        this.tagName = capitalizeWords(StringUtil.collapseWhitespace(tagName));
    }

    /**
     * Returns true if a given string is a valid tag name once extra whitespace is collapsed.
     */
    public static boolean isValidTagName(String test) {
        String normalizedTest = StringUtil.collapseWhitespace(test);
        return normalizedTest.length() <= MAX_LENGTH && normalizedTest.matches(VALIDATION_REGEX);
    }

    /**
     * Returns {@code words} with the first letter of each word in upper case and the rest left as typed,
     * so acronyms such as "RA" are preserved.
     */
    private static String capitalizeWords(String words) {
        return Arrays.stream(words.split(WORD_SEPARATOR))
                .map(word -> word.substring(0, 1).toUpperCase(Locale.ROOT) + word.substring(1))
                .collect(Collectors.joining(WORD_SEPARATOR));
    }

    /**
     * Returns true if {@code name} refers to this tag, ignoring case and extra whitespace.
     * Unlike constructing a {@code Tag}, any string is accepted; one that is not a valid tag name never matches.
     */
    public boolean matchesName(String name) {
        requireNonNull(name);
        return getComparisonKey().equals(toComparisonKey(name));
    }

    /**
     * Returns the form of {@code name} used to compare tag names, ignoring case and extra whitespace.
     * Two names refer to the same tag exactly when their comparison keys are equal.
     */
    public static String toComparisonKey(String name) {
        requireNonNull(name);
        return StringUtil.collapseWhitespace(name).toLowerCase(Locale.ROOT);
    }

    private String getComparisonKey() {
        return toComparisonKey(tagName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Tag otherTag)) {
            return false;
        }

        return getComparisonKey().equals(otherTag.getComparisonKey());
    }

    @Override
    public int hashCode() {
        return getComparisonKey().hashCode();
    }

    /**
     * Formats state as text for viewing.
     */
    public String toString() {
        return '[' + tagName + ']';
    }

}
