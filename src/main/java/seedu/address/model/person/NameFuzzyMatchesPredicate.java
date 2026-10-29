package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.OptionalInt;
import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;

/**
 * Tests that a {@code Person}'s {@code Name} closely matches the given search text, tolerating small typos.
 * Case and extra whitespace are ignored. Every search word must be the exact start of, or within the
 * allowed number of typos ({@link StringUtil#typoDistance}) of, at least one word in the person's name.
 * Longer words allow more typos, so that short words do not match too many unrelated names.
 * {@link #getRankingComparator()} orders matching persons from the closest match to the loosest.
 */
public class NameFuzzyMatchesPredicate implements Predicate<Person> {

    private static final String MESSAGE_BLANK_SEARCH_TEXT = "Search text cannot be blank";
    private static final String WORD_SEPARATOR = " ";
    /** Minimum length of a search word that also matches the start of a name word. */
    private static final int MIN_PREFIX_LENGTH = 3;
    /** Ranking cost of a search word that is a whole word of the name. */
    private static final int EXACT_MATCH_COST = 0;
    /** Ranking cost of a search word that starts a word of the name. A typo match costs this plus its typos. */
    private static final int PREFIX_MATCH_COST = 1;
    /** Ranking cost of a name that does not match, so that it is listed last. */
    private static final int NO_MATCH_COST = Integer.MAX_VALUE;

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

    /**
     * Orders persons by the ranking cost of their names under {@code predicate}, cheapest first.
     */
    private record RankingComparator(NameFuzzyMatchesPredicate predicate) implements Comparator<Person> {
        private RankingComparator {
            requireNonNull(predicate);
        }

        @Override
        public int compare(Person first, Person second) {
            requireNonNull(first);
            requireNonNull(second);
            return Integer.compare(getRankingCost(first), getRankingCost(second));
        }

        private int getRankingCost(Person person) {
            return predicate.getMatchCost(person).orElse(NO_MATCH_COST);
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
        return getMatchCost(person).isPresent();
    }

    /**
     * Returns a comparator that lists persons whose names match the search text more closely first:
     * whole words before starts of words, and starts of words before typos, with fewer typos first.
     * Persons whose names do not match are listed last.
     */
    public Comparator<Person> getRankingComparator() {
        return new RankingComparator(this);
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
     * Returns the ranking cost of {@code person}'s name: the sum of the cheapest match cost of each search
     * word, or an empty {@code OptionalInt} if some search word matches no word of the name.
     */
    private OptionalInt getMatchCost(Person person) {
        List<String> nameWords = toWords(person.getName().fullName);
        int totalCost = 0;
        for (String searchWord : searchWords) {
            OptionalInt wordCost = getCheapestWordMatchCost(searchWord, nameWords);
            if (wordCost.isEmpty()) {
                return OptionalInt.empty();
            }
            totalCost += wordCost.getAsInt();
        }
        assert totalCost >= EXACT_MATCH_COST : "Match cost cannot be below that of an exact match";
        return OptionalInt.of(totalCost);
    }

    /**
     * Returns the cheapest match cost of {@code searchWord} with any of {@code nameWords},
     * or an empty {@code OptionalInt} if it matches none of them.
     */
    private static OptionalInt getCheapestWordMatchCost(String searchWord, List<String> nameWords) {
        return nameWords.stream()
                .map(nameWord -> getWordMatchCost(searchWord, nameWord))
                .flatMapToInt(OptionalInt::stream)
                .min();
    }

    /**
     * Returns the match cost of {@code searchWord} with {@code nameWord}, or an empty {@code OptionalInt}
     * if {@code searchWord} is neither the start of {@code nameWord} nor within the allowed number of typos
     * of it. The shorter of the two words decides how many typos are allowed, so that a short name word
     * such as {@code ng} must be typed exactly even when the search word is longer.
     */
    private static OptionalInt getWordMatchCost(String searchWord, String nameWord) {
        assert !searchWord.isEmpty() && !nameWord.isEmpty() : "Words should be non-empty";
        assert isNormalized(searchWord) && isNormalized(nameWord) : "Words should be lowercase without whitespace";
        if (searchWord.equals(nameWord)) {
            return OptionalInt.of(EXACT_MATCH_COST);
        }
        if (isPrefixMatch(searchWord, nameWord)) {
            return OptionalInt.of(PREFIX_MATCH_COST);
        }

        int typos = StringUtil.typoDistance(searchWord, nameWord);
        int allowedTypos = getAllowedTypos(Math.min(searchWord.length(), nameWord.length()));
        if (typos > allowedTypos) {
            return OptionalInt.empty();
        }
        return OptionalInt.of(PREFIX_MATCH_COST + typos);
    }

    /**
     * Returns true if {@code nameWord} starts with {@code searchWord}, typed exactly.
     * Search words shorter than {@code MIN_PREFIX_LENGTH} are not treated as prefixes, as they would match
     * too many names.
     */
    private static boolean isPrefixMatch(String searchWord, String nameWord) {
        boolean isLongEnoughForPrefix = searchWord.length() >= MIN_PREFIX_LENGTH;
        return isLongEnoughForPrefix && nameWord.startsWith(searchWord);
    }

    /**
     * Returns true if {@code word} is in the form {@link #toWords} produces: lowercase, without whitespace.
     */
    private static boolean isNormalized(String word) {
        boolean isLowercase = word.equals(word.toLowerCase(Locale.ROOT));
        boolean isSingleWord = !word.contains(WORD_SEPARATOR);
        return isLowercase && isSingleWord;
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
