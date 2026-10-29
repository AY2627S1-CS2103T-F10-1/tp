package seedu.address.model.tag;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Arrays;
import java.util.Locale;
import java.util.stream.Collectors;

import seedu.address.commons.util.StringUtil;

/**
 * Represents a Tag in the address book.
 * Tags are compared case-insensitively and are displayed in title case (the first letter of each word in
 * upper case and the rest in lower case), so the same category looks identical no matter how it was typed.
 * Guarantees: immutable; name is valid as declared in {@link #isValidTagName(String)}
 */
public class Tag {

    public static final int MAX_LENGTH = 50;

    public static final String MESSAGE_CONSTRAINTS = "Tags should only contain letters, digits, spaces and hyphens, "
            + "and be at most " + MAX_LENGTH + " characters long.";
    public static final String MESSAGE_EMPTY = "Tag cannot be empty.";
    public static final String MESSAGE_TOO_LONG = "Tag too long, please shorten it and try again.";
    public static final String MESSAGE_INVALID_CHARACTERS = "Tag contains invalid characters.";
    public static final String MESSAGE_DUPLICATE = "Contact already has this tag.";

    /**
     * One or more words of letters, digits and hyphens, separated by single spaces.
     * Letters and digits may be from any language; accent marks ({@code \p{M}}) are allowed so that letters
     * typed as a base letter followed by a combining mark are accepted too.
     */
    public static final String VALIDATION_REGEX = "[\\p{L}\\p{M}\\p{N}-]+( [\\p{L}\\p{M}\\p{N}-]+)*";

    private static final String WORD_SEPARATOR = " ";

    public final String tagName;

    /**
     * Constructs a {@code Tag}.
     * Extra whitespace in {@code tagName} is collapsed and each word is put in title case.
     *
     * @param tagName A valid tag name.
     */
    public Tag(String tagName) {
        requireNonNull(tagName);
        checkArgument(isValidTagName(tagName), MESSAGE_CONSTRAINTS);
        this.tagName = toTitleCase(StringUtil.collapseWhitespace(tagName));
    }

    /**
     * Returns true if a given string is a valid tag name once extra whitespace is collapsed.
     * The length limit applies both to the name as typed and to its title-case form, since a few letters,
     * such as "İ", become longer in lower case and the title-case form is what gets saved.
     */
    public static boolean isValidTagName(String test) {
        String normalizedTest = StringUtil.collapseWhitespace(test);
        boolean isTypedNameWithinLimit = normalizedTest.length() <= MAX_LENGTH;
        return isTypedNameWithinLimit && hasValidCharacters(normalizedTest)
                && toTitleCase(normalizedTest).length() <= MAX_LENGTH;
    }

    /**
     * Returns true if a given string, once extra whitespace is collapsed, consists only of characters allowed
     * in a tag name, regardless of its length.
     */
    public static boolean hasValidCharacters(String test) {
        return StringUtil.collapseWhitespace(test).matches(VALIDATION_REGEX);
    }

    /**
     * Returns {@code words} with the first letter of each word in upper case and the rest in lower case,
     * so that names differing only in case, such as "RA" and "ra", are displayed the same way.
     */
    private static String toTitleCase(String words) {
        return Arrays.stream(words.split(WORD_SEPARATOR))
                .map(Tag::toTitleCaseWord)
                .collect(Collectors.joining(WORD_SEPARATOR));
    }

    /**
     * Returns {@code word} with its first character in title case and the rest in lower case.
     * The whole word is lowercased before its first character is title-cased, because some letters, such as
     * the Greek final sigma, lowercase differently depending on the letters around them. This keeps the
     * result consistent with {@link #toComparisonKey(String)}, which lowercases whole names.
     * The first character is handled as a code point so that letters outside the Basic Multilingual Plane
     * are not split.
     */
    private static String toTitleCaseWord(String word) {
        assert !word.isEmpty() : "words of a valid tag name are never empty";
        String lowerCaseWord = word.toLowerCase(Locale.ROOT);
        int firstCodePoint = lowerCaseWord.codePointAt(0);
        String rest = lowerCaseWord.substring(Character.charCount(firstCodePoint));
        return Character.toString(Character.toTitleCase(firstCodePoint)) + rest;
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
