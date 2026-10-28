package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

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

        // one word of a multi-word name, in any order
        assertMatches("Yeoh", "Alex Yeoh");
        assertMatches("Yeoh Alex", "Alex Yeoh");

        // typos at the limit of each tier
        assertMatches("Al", "Al Tan"); // 2 chars, exact
        assertMatches("Alx", "Alex Yeoh"); // 3 chars, 1 typo
        assertMatches("Bernse", "Bernice Yu"); // 6 chars, 2 typos
        assertMatches("Christopher", "Kristophe Lim"); // 11 chars, 3 typos

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
        assertDoesNotMatch("Christopher", "Kristofer Lim"); // 11 chars, 4 typos

        // one search word matches, another does not
        assertDoesNotMatch("Alex Tan", "Alex Yeoh");

        // short prefix of a word
        assertDoesNotMatch("Al", "Alex Yeoh");

        // matches phone, email and address, but not name
        assertFalse(new NameFuzzyMatchesPredicate("12345").test(new PersonBuilder().withName("Alice")
                .withPhone("12345").withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void toStringMethod() {
        NameFuzzyMatchesPredicate predicate = new NameFuzzyMatchesPredicate("Alex  Yeoh");

        String expected = NameFuzzyMatchesPredicate.class.getCanonicalName() + "{searchWords=[alex, yeoh]}";
        assertEquals(expected, predicate.toString());
    }

    private static void assertMatches(String searchText, String name) {
        assertTrue(new NameFuzzyMatchesPredicate(searchText).test(new PersonBuilder().withName(name).build()));
    }

    private static void assertDoesNotMatch(String searchText, String name) {
        assertFalse(new NameFuzzyMatchesPredicate(searchText).test(new PersonBuilder().withName(name).build()));
    }
}
