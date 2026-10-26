package seedu.address.commons.util;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Helper functions for handling strings.
 */
public class StringUtil {

    /** Matches runs of ASCII whitespace and Unicode spaces, such as the non-breaking spaces of pasted text. */
    private static final String WHITESPACE_RUN_REGEX = "[\\s\\p{Z}]+";
    private static final String SINGLE_SPACE = " ";
    /** Cost of one insertion, deletion, substitution or swap when computing the typo distance. */
    private static final int EDIT_COST = 1;

    /**
     * Returns true if the {@code sentence} contains the {@code word}.
     *   Ignores case, but a full word match is required.
     *   <br>examples:<pre>
     *       containsWordIgnoreCase("ABc def", "abc") == true
     *       containsWordIgnoreCase("ABc def", "DEF") == true
     *       containsWordIgnoreCase("ABc def", "AB") == false //not a full word match
     *       </pre>
     * @param sentence cannot be null
     * @param word cannot be null, cannot be empty, must be a single word
     */
    public static boolean containsWordIgnoreCase(String sentence, String word) {
        requireNonNull(sentence);
        requireNonNull(word);

        String preppedWord = word.trim();
        checkArgument(!preppedWord.isEmpty(), "Word parameter cannot be empty");
        checkArgument(preppedWord.split("\\s+").length == 1, "Word parameter should be a single word");

        String preppedSentence = sentence;
        String[] wordsInPreppedSentence = preppedSentence.split("\\s+");

        return Arrays.stream(wordsInPreppedSentence)
                .anyMatch(preppedWord::equalsIgnoreCase);
    }

    /**
     * Returns {@code s} with leading and trailing whitespace removed and every inner run of whitespace
     * replaced by a single space, e.g. {@code "  Resident   Fellow "} becomes {@code "Resident Fellow"}.
     * Unicode spaces, such as the non-breaking space (U+00A0), count as whitespace.
     *
     * @param s The string to normalize; cannot be null.
     */
    public static String collapseWhitespace(String s) {
        requireNonNull(s);
        // Collapse before trimming, as trim() only removes ASCII whitespace
        String collapsed = s.replaceAll(WHITESPACE_RUN_REGEX, SINGLE_SPACE).trim();
        assert !collapsed.contains(SINGLE_SPACE + SINGLE_SPACE) : "Whitespace runs should be collapsed";
        assert collapsed.equals(collapsed.strip()) : "Leading and trailing whitespace should be removed";
        return collapsed;
    }

    /**
     * Returns the typo distance between {@code first} and {@code second}: the minimum number of
     * single-character insertions, deletions, substitutions or swaps of two adjacent characters needed
     * to turn one into the other, where no character is edited more than once (the optimal string
     * alignment distance). The comparison is case-sensitive.
     *
     * @param first The first string; cannot be null.
     * @param second The second string; cannot be null.
     */
    public static int typoDistance(String first, String second) {
        requireNonNull(first);
        requireNonNull(second);

        // costs[i][j] is the distance between the first i characters of first and the first j of second
        int[][] costs = new int[first.length() + 1][];
        // Turning an empty prefix of first into each prefix of second takes one insertion per character
        costs[0] = IntStream.rangeClosed(0, second.length()).toArray();

        for (int i = 1; i <= first.length(); i++) {
            costs[i] = new int[second.length() + 1];
            costs[i][0] = i;
            for (int j = 1; j <= second.length(); j++) {
                costs[i][j] = cheapestEdit(first, second, costs, i, j);
            }
        }

        int distance = costs[first.length()][second.length()];
        assert distance >= 0 && distance <= Math.max(first.length(), second.length())
                : "Typo distance must be between 0 and the length of the longer string";
        return distance;
    }

    /**
     * Returns the cost of the cheapest edit for cell ({@code i}, {@code j}) of the typo distance table,
     * given that the costs of all earlier cells are already filled in.
     */
    private static int cheapestEdit(String first, String second, int[][] costs, int i, int j) {
        assert i >= 1 && j >= 1 : "Row 0 and column 0 of the typo distance table are filled in directly";
        boolean isSameChar = first.charAt(i - 1) == second.charAt(j - 1);
        int substitutionCost = isSameChar ? costs[i - 1][j - 1] : costs[i - 1][j - 1] + EDIT_COST;
        int deletionCost = costs[i - 1][j] + EDIT_COST;
        int insertionCost = costs[i][j - 1] + EDIT_COST;
        int cheapestCost = Math.min(substitutionCost, Math.min(deletionCost, insertionCost));

        if (!isAdjacentSwap(first, second, i, j)) {
            return cheapestCost;
        }
        return Math.min(cheapestCost, costs[i - 2][j - 2] + EDIT_COST);
    }

    /**
     * Returns true if the last two of the first {@code i} characters of {@code first} are the last two of
     * the first {@code j} characters of {@code second}, swapped.
     */
    private static boolean isAdjacentSwap(String first, String second, int i, int j) {
        assert i <= first.length() && j <= second.length() : "Cell must lie within the typo distance table";
        boolean hasTwoCharsEach = i >= 2 && j >= 2;
        return hasTwoCharsEach
                && first.charAt(i - 1) == second.charAt(j - 2)
                && first.charAt(i - 2) == second.charAt(j - 1);
    }

    /**
     * Returns a detailed message of {@code t}, including the stack trace.
     */
    public static String getDetails(Throwable t) {
        requireNonNull(t);
        StringWriter sw = new StringWriter();
        t.printStackTrace(new PrintWriter(sw));
        return t.getMessage() + "\n" + sw.toString();
    }

    /**
     * Returns true if {@code s} represents a non-zero unsigned integer
     * e.g. 1, 2, 3, ..., {@code Integer.MAX_VALUE} <br>
     * Will return false for any other non-null string input
     * e.g. empty string, "-1", "0", "+1", and " 2 " (untrimmed), "3 0" (contains whitespace), "1 a" (contains letters)
     * @throws NullPointerException if {@code s} is null.
     */
    public static boolean isNonZeroUnsignedInteger(String s) {
        requireNonNull(s);

        try {
            int value = Integer.parseInt(s);
            return value > 0 && !s.startsWith("+"); // "+1" is successfully parsed by Integer#parseInt(String)
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
