package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class NameTest {

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Name(null));
    }

    @Test
    public void constructor_invalidName_throwsIllegalArgumentException() {
        String invalidName = "";
        assertThrows(IllegalArgumentException.class, () -> new Name(invalidName));
    }

    @Test
    public void constructor_extraWhitespace_collapsed() {
        assertEquals("Sarah Tan", new Name("  Sarah   Tan ").fullName);
        assertEquals(new Name("Sarah Tan"), new Name("Sarah  Tan"));
    }

    @Test
    public void isValidName() {
        // null name
        assertThrows(NullPointerException.class, () -> Name.isValidName(null));

        // invalid name
        assertFalse(Name.isValidName("")); // empty string
        assertFalse(Name.isValidName(" ")); // spaces only
        assertFalse(Name.isValidName("^")); // only non-alphanumeric characters
        assertFalse(Name.isValidName("peter*")); // contains non-alphanumeric characters
        assertFalse(Name.isValidName("R@chel")); // symbol that is not allowed
        assertFalse(Name.isValidName("Tan, Wei")); // comma
        assertFalse(Name.isValidName("O’Brien")); // curly apostrophe
        assertFalse(Name.isValidName("/x")); // word starts with a slash
        assertFalse(Name.isValidName("Kumar /o")); // later word starts with a slash
        assertFalse(Name.isValidName("-Lee")); // word starts with a hyphen
        assertFalse(Name.isValidName("'Lee")); // word starts with an apostrophe
        assertFalse(Name.isValidName(".")); // boundary: only a symbol
        assertFalse(Name.isValidName("\u0301")); // only a combining accent mark

        // valid name
        assertTrue(Name.isValidName("peter jack")); // alphabets only
        assertTrue(Name.isValidName("12345")); // numbers only
        assertTrue(Name.isValidName("peter the 2nd")); // alphanumeric characters
        assertTrue(Name.isValidName("Capital Tan")); // with capital letters
        assertTrue(Name.isValidName("David Roger Jackson Ray Jr 2nd")); // long names
        assertTrue(Name.isValidName("Kumar s/o Rajan")); // slash inside a word
        assertTrue(Name.isValidName("O'Brien")); // apostrophe
        assertTrue(Name.isValidName("Mary-Jane Lee")); // hyphen
        assertTrue(Name.isValidName("Tan Jr.")); // period at the end of a word
        assertTrue(Name.isValidName("José García")); // accented letters
        assertTrue(Name.isValidName("Jose\u0301")); // letter followed by a combining accent mark
        assertTrue(Name.isValidName("李小明")); // non-Latin letters
        assertTrue(Name.isValidName("A")); // boundary: single letter
        assertTrue(Name.isValidName("  Sarah   Tan  ")); // extra spaces are collapsed

        // length boundary
        assertTrue(Name.isValidName("A".repeat(Name.MAX_LENGTH)));
        assertFalse(Name.isValidName("A".repeat(Name.MAX_LENGTH + 1)));
        assertTrue(Name.isValidName(" " + "A".repeat(Name.MAX_LENGTH) + " ")); // surrounding spaces do not count
    }

    @Test
    public void equals() {
        Name name = new Name("Valid Name");

        // same values -> returns true
        assertTrue(name.equals(new Name("Valid Name")));

        // same object -> returns true
        assertTrue(name.equals(name));

        // null -> returns false
        assertFalse(name.equals(null));

        // different types -> returns false
        assertFalse(name.equals(5.0f));

        // different values -> returns false
        assertFalse(name.equals(new Name("Other Valid Name")));
    }
}
