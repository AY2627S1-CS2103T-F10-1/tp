package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameFuzzyMatchesPredicateTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NameFuzzyMatchesPredicate(null));
    }

    @Test
    public void constructor_blankSearchText_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new NameFuzzyMatchesPredicate(""));
        assertThrows(IllegalArgumentException.class, () -> new NameFuzzyMatchesPredicate(" \t "));
    }

    @Test
    public void test_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new NameFuzzyMatchesPredicate("Alex").test(null));
    }

    @Test
    public void equals() {
        NameFuzzyMatchesPredicate firstPredicate = new NameFuzzyMatchesPredicate("first");
        NameFuzzyMatchesPredicate secondPredicate = new NameFuzzyMatchesPredicate("second");

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        assertTrue(firstPredicate.equals(new NameFuzzyMatchesPredicate("first")));

        // same values after ignoring case and extra whitespace -> returns true
        assertTrue(new NameFuzzyMatchesPredicate("Alex Yeoh").equals(new NameFuzzyMatchesPredicate(" alex  YEOH ")));
        assertEquals(new NameFuzzyMatchesPredicate("Alex Yeoh").hashCode(),
                new NameFuzzyMatchesPredicate(" alex  YEOH ").hashCode());

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different search text -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void getAllowedTypos_eachLengthTier_correctTypos() {
        // EP: 1-2 chars allow no typos
        assertEquals(0, NameFuzzyMatchesPredicate.getAllowedTypos(1));
        assertEquals(0, NameFuzzyMatchesPredicate.getAllowedTypos(2)); // Boundary value

        // EP: 3-5 chars allow 1 typo
        assertEquals(1, NameFuzzyMatchesPredicate.getAllowedTypos(3)); // Boundary value
        assertEquals(1, NameFuzzyMatchesPredicate.getAllowedTypos(5)); // Boundary value

        // EP: 6-9 chars allow 2 typos
        assertEquals(2, NameFuzzyMatchesPredicate.getAllowedTypos(6)); // Boundary value
        assertEquals(2, NameFuzzyMatchesPredicate.getAllowedTypos(9)); // Boundary value

        // EP: 10+ chars allow 3 typos
        assertEquals(3, NameFuzzyMatchesPredicate.getAllowedTypos(10)); // Boundary value
        assertEquals(3, NameFuzzyMatchesPredicate.getAllowedTypos(50));
    }

    @Test
    public void test_closeMatch_returnsTrue() {
        // exact full name
        assertMatches("Alex Yeoh", "Alex Yeoh");

        // different case and extra whitespace
        assertMatches("  alex   YEOH ", "Alex Yeoh");

        // non-breaking space between words, as in text pasted from a chat app
        assertMatches("Alex Yeoh", "Alex Yeoh");

        // one word of a multi-word name, in any order
        assertMatches("Yeoh", "Alex Yeoh");
        assertMatches("Yeoh Alex", "Alex Yeoh");

        // typos at the limit of each tier
        assertMatches("Al", "Al Tan"); // 2 chars, exact
        assertMatches("Alx", "Alex Yeoh"); // 3 chars, 1 typo
        assertMatches("Bernse", "Bernice Yu"); // 6 chars, 2 typos
        assertMatches("Belakrishnonn", "Roy Balakrishnan"); // 12+ chars, 3 typos

        // start of a name word, at least 3 letters
        assertMatches("Bala", "Roy Balakrishnan");
        assertMatches("Jon", "Jonathan Chua"); // Boundary value
        assertMatches("char", "Charlotte Oliveiro");
        assertMatches("Chu", "Jonathan Chua"); // start of a later name word

        // the shorter word decides the typos allowed
        assertMatches("Christopher", "Kristopher Lim"); // 10-char name word, 2 typos
        assertMatches("Kristopher", "Christopher Lim"); // 10-char search word, 2 typos

        // two adjacent letters swapped count as 1 typo
        assertMatches("Yoeh", "Alex Yeoh");
        assertMatches("Aelx", "Alex Yeoh");

        // every search word close to some name word
        assertMatches("Alex Yoh", "Alex Yeoh");

        // special characters count as typos
        assertMatches("O'Brien", "Sean OBrien");

        // very long name
        String longName = "Alexandria".repeat(10);
        assertMatches(longName + "xyz", longName);
    }

    @Test
    public void test_notCloseMatch_returnsFalse() {
        // typos beyond the limit of each tier
        assertDoesNotMatch("Ab", "Al Tan"); // 2 chars, 1 typo
        assertDoesNotMatch("Akx", "Alex Yeoh"); // 3 chars, 2 typos
        assertDoesNotMatch("Bxxnce", "Bernice Yu"); // 6 chars, 3 typos
        assertDoesNotMatch("Belakrishnonnn", "Roy Balakrishnan"); // 12+ chars, 4 typos

        // the shorter word decides the typos allowed
        assertDoesNotMatch("Ong", "Ng Kai"); // 2-char name word, 1 typo
        assertDoesNotMatch("Lim", "Tay Li Hua"); // 2-char name word, 1 typo
        assertDoesNotMatch("Christopher", "Kristophe Lim"); // 9-char name word, 3 typos

        // one search word matches, another does not
        assertDoesNotMatch("Alex Tan", "Alex Yeoh");

        // start of a name word, but too short
        assertDoesNotMatch("Al", "Alex Yeoh"); // Boundary value

        // start of a name word with a typo
        assertDoesNotMatch("Blaa", "Roy Balakrishnan");

        // middle or end of a name word
        assertDoesNotMatch("Krish", "Roy Balakrishnan");
        assertDoesNotMatch("shnan", "Roy Balakrishnan");

        // longer than the name word it starts with
        assertDoesNotMatch("Jonathans", "Jon Chua");

        // matches phone, email and address, but not name
        assertFalse(new NameFuzzyMatchesPredicate("12345").test(new PersonBuilder().withName("Alice")
                .withPhone("12345").withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void getRankingComparator_wholeWordThenStartThenTypo_sortedByMatchQuality() {
        Person wholeWord = personNamed("Lim Jun Jie");
        Person startOfWord = personNamed("Limah Tan");
        Person oneTypo = personNamed("Lin Xiao");
        Person noMatch = personNamed("Tay Li Hua");

        assertRankedOrder("Lim", List.of(noMatch, oneTypo, startOfWord, wholeWord),
                List.of(wholeWord, startOfWord, oneTypo, noMatch));
    }

    @Test
    public void getRankingComparator_severalSearchWords_sortedByTotalMatchQuality() {
        Person allWholeWords = personNamed("Alex Yeoh");
        Person oneStartOfWord = personNamed("Alexa Yeoh");
        Person oneTypo = personNamed("Alex Yeo");
        Person twoTypos = personNamed("Alix Yeo");

        assertRankedOrder("Alex Yeoh", List.of(twoTypos, oneTypo, oneStartOfWord, allWholeWords),
                List.of(allWholeWords, oneStartOfWord, oneTypo, twoTypos));
    }

    @Test
    public void getRankingComparator_equallyCloseMatches_keepOrder() {
        Person firstOneTypo = personNamed("Benson Meier");
        Person secondOneTypo = personNamed("Daniel Meier");

        assertRankedOrder("Meyer", List.of(firstOneTypo, secondOneTypo), List.of(firstOneTypo, secondOneTypo));
        assertRankedOrder("Meyer", List.of(secondOneTypo, firstOneTypo), List.of(secondOneTypo, firstOneTypo));
    }

    @Test
    public void getRankingComparator_nullPerson_throwsNullPointerException() {
        Comparator<Person> ranking = new NameFuzzyMatchesPredicate("Alex").getRankingComparator();
        assertThrows(NullPointerException.class, () -> ranking.compare(null, personNamed("Alex Yeoh")));
        assertThrows(NullPointerException.class, () -> ranking.compare(personNamed("Alex Yeoh"), null));
    }

    @Test
    public void getRankingComparator_equals() {
        NameFuzzyMatchesPredicate predicate = new NameFuzzyMatchesPredicate("Alex");

        // same search words -> returns true
        assertEquals(predicate.getRankingComparator(), new NameFuzzyMatchesPredicate(" ALEX ").getRankingComparator());

        // different search words -> returns false
        assertNotEquals(predicate.getRankingComparator(), new NameFuzzyMatchesPredicate("Bob").getRankingComparator());
    }

    @Test
    public void toStringMethod() {
        NameFuzzyMatchesPredicate predicate = new NameFuzzyMatchesPredicate("Alex  Yeoh");

        String expected = NameFuzzyMatchesPredicate.class.getCanonicalName() + "{searchWords=[alex, yeoh]}";
        assertEquals(expected, predicate.toString());
    }

    private static void assertMatches(String searchText, String name) {
        assertTrue(new NameFuzzyMatchesPredicate(searchText).test(personNamed(name)));
    }

    private static void assertDoesNotMatch(String searchText, String name) {
        assertFalse(new NameFuzzyMatchesPredicate(searchText).test(personNamed(name)));
    }

    /**
     * Asserts that sorting {@code persons} by the ranking of {@code searchText} gives {@code expectedOrder}.
     */
    private static void assertRankedOrder(String searchText, List<Person> persons, List<Person> expectedOrder) {
        List<Person> sortedPersons = new ArrayList<>(persons);
        sortedPersons.sort(new NameFuzzyMatchesPredicate(searchText).getRankingComparator());
        assertEquals(expectedOrder, sortedPersons);
    }

    private static Person personNamed(String name) {
        return new PersonBuilder().withName(name).build();
    }
}
