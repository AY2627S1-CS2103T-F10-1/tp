package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

    private static final String TAG_AT_MAX_LENGTH = "a".repeat(Tag.MAX_LENGTH);
    private static final String TAG_OVER_MAX_LENGTH = "a".repeat(Tag.MAX_LENGTH + 1);

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new Tag(null));
    }

    @Test
    public void constructor_invalidTagName_throwsIllegalArgumentException() {
        String invalidTagName = "";
        assertThrows(IllegalArgumentException.class, () -> new Tag(invalidTagName));
    }

    @Test
    public void isValidTagName() {
        // null tag name
        assertThrows(NullPointerException.class, () -> Tag.isValidTagName(null));

        // invalid tag names
        assertFalse(Tag.isValidTagName("")); // empty string
        assertFalse(Tag.isValidTagName("   ")); // spaces only
        assertFalse(Tag.isValidTagName("Resident/Fellow")); // command delimiter
        assertFalse(Tag.isValidTagName("hubby*")); // other symbols
        assertFalse(Tag.isValidTagName("Block_4")); // underscore
        assertFalse(Tag.isValidTagName(TAG_OVER_MAX_LENGTH)); // boundary: one character too long

        // valid tag names
        assertTrue(Tag.isValidTagName("RA")); // letters only
        assertTrue(Tag.isValidTagName("4")); // boundary: single digit
        assertTrue(Tag.isValidTagName("Block4")); // letters and digits
        assertTrue(Tag.isValidTagName("Hall-Staff")); // hyphen
        assertTrue(Tag.isValidTagName("Resident Fellow")); // inner space
        assertTrue(Tag.isValidTagName("  Resident    Fellow  ")); // extra spaces are collapsed
        assertTrue(Tag.isValidTagName(TAG_AT_MAX_LENGTH)); // boundary: exactly at the limit
        assertTrue(Tag.isValidTagName(" " + TAG_AT_MAX_LENGTH + " ")); // surrounding spaces do not count
    }

    @Test
    public void constructor_validTagName_normalizesDisplayName() {
        assertEquals("RA", new Tag("RA").tagName); // acronym kept as typed
        assertEquals("Ra", new Tag("ra").tagName);
        assertEquals("Resident Fellow", new Tag("resident fellow").tagName);
        assertEquals("Resident Fellow", new Tag("  resident    Fellow ").tagName);
        assertEquals("Block4-a", new Tag("block4-a").tagName);
        assertEquals("4th Floor", new Tag("4th floor").tagName); // digits are left unchanged
    }

    @Test
    public void equals() {
        Tag tag = new Tag("Resident Fellow");

        // same values -> returns true
        assertTrue(tag.equals(new Tag("Resident Fellow")));

        // same object -> returns true
        assertTrue(tag.equals(tag));

        // null -> returns false
        assertFalse(tag.equals(null));

        // different type -> returns false
        assertFalse(tag.equals(5.0f));

        // different case or spacing -> returns true
        assertTrue(tag.equals(new Tag("RESIDENT fellow")));
        assertTrue(tag.equals(new Tag(" Resident   Fellow ")));

        // different value -> returns false
        assertFalse(tag.equals(new Tag("Resident")));
    }

    @Test
    public void hashCode_differentCase_sameHashCode() {
        assertEquals(new Tag("RA").hashCode(), new Tag("ra").hashCode());
        assertNotEquals(new Tag("RA").hashCode(), new Tag("Resident").hashCode());
    }

    @Test
    public void toStringMethod() {
        assertEquals("[Resident Fellow]", new Tag("resident fellow").toString());
    }

}
