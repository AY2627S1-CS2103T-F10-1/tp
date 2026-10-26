package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} closely matches the given search text, tolerating small typos.
 * Case and extra whitespace are ignored. Every search word must be within its allowed number of typos
 * ({@link StringUtil#typoDistance}) of at least one word in the person's name. Longer words allow more typos,
 * so that short words do not match too many unrelated names.
 */
public class NameFuzzyMatchesPredicate implements Predicate<Person> {

    private static final String MESSAGE_BLANK_SEARCH_TEXT = "Search text cannot be blank";
    private static final String WORD_SEPARATOR = " ";

    /**
     * Number of typos allowed between two words, by the minimum length of the shorter word.
     * Declared from the longest words down, so the first tier a word is long enough for applies.
     */
    private enum TypoTolerance {
        LONG_WORD(10, 3),
        MEDIUM_WORD(6, 2),
        SHORT_WORD(3, 1),
        VERY_SHORT_WORD(0, 0);

        private final int minWordLength;
        private final int allowedTypos;

        TypoTolerance(int minWordLength, int allowedTypos) {
            this.minWordLength = minWordLength;
            this.allowedTypos = allowedTypos;
        }
    }

    private final List<String> searchWords;

    /**
     * Creates a predicate matching persons whose name closely matches {@code searchText}.
     *
     * @param searchText The name to search for; cannot be null or blank.
     */
    public NameFuzzyMatchesPredicate(String searchText) {
        requireNonNull(searchText);
        this.searchWords = toWords(searchText);
        checkArgument(!searchWords.isEmpty(), MESSAGE_BLANK_SEARCH_TEXT);
    }

    @Override
    public boolean test(Person person) {
        requireNonNull(person);
        List<String> nameWords = toWords(person.getName().fullName);
        return searchWords.stream()
                .allMatch(searchWord -> isCloseToAnyWord(searchWord, nameWords));
    }

    /**
     * Returns the number of typos allowed between two words whose shorter word has length {@code wordLength}.
     */
    static int getAllowedTypos(int wordLength) {
        assert wordLength >= 0 : "Word length cannot be negative";
        return Arrays.stream(TypoTolerance.values())
                .filter(tolerance -> wordLength >= tolerance.minWordLength)
                .findFirst()
                .orElseThrow(() -> new AssertionError("VERY_SHORT_WORD should cover every length"))
                .allowedTypos;
    }

    /**
     * Returns true if {@code searchWord} is within its allowed number of typos of any of {@code nameWords}.
     */
    private static boolean isCloseToAnyWord(String searchWord, List<String> nameWords) {
        return nameWords.stream()
                .anyMatch(nameWord -> isCloseMatch(searchWord, nameWord));
    }

    /**
     * Returns true if {@code searchWord} is within the allowed number of typos of {@code nameWord}.
     * The shorter of the two words decides how many typos are allowed, so that a short name word
     * such as {@code ng} must be typed exactly even when the search word is longer.
     */
    private static boolean isCloseMatch(String searchWord, String nameWord) {
        int allowedTypos = getAllowedTypos(Math.min(searchWord.length(), nameWord.length()));
        return StringUtil.typoDistance(searchWord, nameWord) <= allowedTypos;
    }

    /**
     * Returns the lowercase words of {@code text}, ignoring extra whitespace.
     * Returns an empty list if {@code text} is blank.
     */
    private static List<String> toWords(String text) {
        String normalizedText = StringUtil.collapseWhitespace(text).toLowerCase(Locale.ROOT);
        return Arrays.stream(normalizedText.split(WORD_SEPARATOR))
                .filter(word -> !word.isEmpty())
                .toList();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NameFuzzyMatchesPredicate otherNameFuzzyMatchesPredicate)) {
            return false;
        }

        return searchWords.equals(otherNameFuzzyMatchesPredicate.searchWords);
    }

    @Override
    public int hashCode() {
        return searchWords.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("searchWords", searchWords).toString();
    }
}
