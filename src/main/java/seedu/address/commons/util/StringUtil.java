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

    private static final String WHITESPACE_RUN_REGEX = "\\s+";
    private static final String SINGLE_SPACE = " ";
    /** Cost of one insertion, deletion or substitution when computing the Levenshtein distance. */
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
     *
     * @param s The string to normalize; cannot be null.
     */
    public static String collapseWhitespace(String s) {
        requireNonNull(s);
        return s.trim().replaceAll(WHITESPACE_RUN_REGEX, SINGLE_SPACE);
    }

    /**
     * Returns the Levenshtein distance between {@code first} and {@code second}: the minimum number of
     * single-character insertions, deletions or substitutions needed to turn one into the other.
     * The comparison is case-sensitive.
     *
     * @param first The first string; cannot be null.
     * @param second The second string; cannot be null.
     */
    public static int levenshteinDistance(String first, String second) {
        requireNonNull(first);
        requireNonNull(second);

        // Turning an empty prefix of first into each prefix of second takes one insertion per character
        int[] previousRow = IntStream.rangeClosed(0, second.length()).toArray();

        for (int i = 1; i <= first.length(); i++) {
            int[] currentRow = new int[second.length() + 1];
            currentRow[0] = i;
            for (int j = 1; j <= second.length(); j++) {
                boolean isSameChar = first.charAt(i - 1) == second.charAt(j - 1);
                currentRow[j] = cheapestEdit(isSameChar, previousRow[j - 1], previousRow[j], currentRow[j - 1]);
            }
            previousRow = currentRow;
        }

        int distance = previousRow[second.length()];
        assert distance >= 0 && distance <= Math.max(first.length(), second.length())
                : "Levenshtein distance must be between 0 and the length of the longer string";
        return distance;
    }

    /**
     * Returns the cost of the cheapest edit for one cell of the Levenshtein table, given the costs of
     * its diagonal, upper and left neighbors.
     */
    private static int cheapestEdit(boolean isSameChar, int diagonalCost, int aboveCost, int leftCost) {
        int substitutionCost = isSameChar ? diagonalCost : diagonalCost + EDIT_COST;
        int deletionCost = aboveCost + EDIT_COST;
        int insertionCost = leftCost + EDIT_COST;
        return Math.min(substitutionCost, Math.min(deletionCost, insertionCost));
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
