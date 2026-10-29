package seedu.address.model.tag;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_LENGTHENED_PAST_MAX_LENGTH;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_TOO_LONG;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_AT_MAX_LENGTH;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GREEK_LOWER_CASE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GREEK_UPPER_CASE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_LENGTHENED_TO_MAX_LENGTH;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class TagTest {

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
        assertFalse(Tag.isValidTagName("Café!")); // non-ASCII letter with a symbol
        assertFalse(Tag.isValidTagName("🚨")); // emoji is not a letter
        assertFalse(Tag.isValidTagName(INVALID_TAG_TOO_LONG)); // boundary: one character too long

        // valid tag names
        assertTrue(Tag.isValidTagName("RA")); // letters only
        assertTrue(Tag.isValidTagName("4")); // boundary: single digit
        assertTrue(Tag.isValidTagName("Block4")); // letters and digits
        assertTrue(Tag.isValidTagName("Hall-Staff")); // hyphen
        assertTrue(Tag.isValidTagName("Resident Fellow")); // inner space
        assertTrue(Tag.isValidTagName("  Resident    Fellow  ")); // extra spaces are collapsed
        assertTrue(Tag.isValidTagName(VALID_TAG_AT_MAX_LENGTH)); // boundary: exactly at the limit
        assertTrue(Tag.isValidTagName(" " + VALID_TAG_AT_MAX_LENGTH + " ")); // surrounding spaces do not count
        assertTrue(Tag.isValidTagName("Café Vendor")); // accented letter
        assertTrue(Tag.isValidTagName("Cafe\u0301")); // letter followed by a combining accent mark
        assertTrue(Tag.isValidTagName("宿舍")); // non-Latin letters
        assertTrue(Tag.isValidTagName("விடுதி")); // letters with vowel signs
        assertTrue(Tag.isValidTagName("٣")); // non-ASCII digit

        // non-ASCII letters at the length boundary
        assertTrue(Tag.isValidTagName("宿".repeat(Tag.MAX_LENGTH)));
        assertFalse(Tag.isValidTagName("宿".repeat(Tag.MAX_LENGTH + 1)));

        // letters that become longer in title case, at the length boundary
        assertTrue(Tag.isValidTagName(VALID_TAG_LENGTHENED_TO_MAX_LENGTH));
        assertFalse(Tag.isValidTagName(INVALID_TAG_LENGTHENED_PAST_MAX_LENGTH));
    }

    @Test
    public void hasValidCharacters() {
        assertThrows(NullPointerException.class, () -> Tag.hasValidCharacters(null));

        assertTrue(Tag.hasValidCharacters("  Resident   Fellow ")); // extra spaces are collapsed
        assertTrue(Tag.hasValidCharacters(INVALID_TAG_TOO_LONG)); // length is not checked
        assertTrue(Tag.hasValidCharacters(INVALID_TAG_LENGTHENED_PAST_MAX_LENGTH));

        assertFalse(Tag.hasValidCharacters("")); // empty string
        assertFalse(Tag.hasValidCharacters("RA/Staff")); // command delimiter
    }

    @Test
    public void constructor_lengthenedToMaxLength_savedNameIsValid() {
        String tagName = new Tag(VALID_TAG_LENGTHENED_TO_MAX_LENGTH).tagName;
        assertEquals(Tag.MAX_LENGTH, tagName.length());
        assertTrue(Tag.isValidTagName(tagName)); // a saved tag can be loaded again
        assertEquals(tagName, new Tag(tagName).tagName); // and is unchanged when loaded
    }

    @Test
    public void constructor_lengthenedPastMaxLength_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new Tag(INVALID_TAG_LENGTHENED_PAST_MAX_LENGTH));
    }

    @Test
    public void constructor_validTagName_displaysInTitleCase() {
        assertEquals("Ra", new Tag("RA").tagName);
        assertEquals("Ra", new Tag("ra").tagName);
        assertEquals("Ra", new Tag("rA").tagName);
        assertEquals("Resident Fellow", new Tag("resident fellow").tagName);
        assertEquals("Resident Fellow", new Tag("RESIDENT FELLOW").tagName);
        assertEquals("Resident Fellow", new Tag("  resident    Fellow ").tagName);
        assertEquals("Block4-a", new Tag("BLOCK4-A").tagName); // only the start of a word is capitalized
        assertEquals("4th Floor", new Tag("4TH floor").tagName); // digits are left unchanged
        assertEquals("Élève", new Tag("éLÈVE").tagName); // accented letters
        assertEquals("宿舍", new Tag("宿舍").tagName); // letters without case are left unchanged
        // a sigma ending the word is a final sigma however it was typed
        assertEquals("Ος", new Tag(VALID_TAG_GREEK_UPPER_CASE).tagName);
        assertEquals("Ος", new Tag(VALID_TAG_GREEK_LOWER_CASE).tagName);
        // Deseret letters lie outside the Basic Multilingual Plane, so each takes two chars
        assertEquals("\uD801\uDC00\uD801\uDC2F", new Tag("\uD801\uDC28\uD801\uDC07").tagName);
    }

    @Test
    public void constructor_sameNameInDifferentCase_sameDisplayName() {
        assertEquals(new Tag("RESIDENT").tagName, new Tag("resident").tagName);
        assertEquals(new Tag("Resident").tagName, new Tag("rEsIdEnT").tagName);
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
        assertTrue(new Tag(VALID_TAG_GREEK_UPPER_CASE).equals(new Tag(VALID_TAG_GREEK_LOWER_CASE)));

        // different value -> returns false
        assertFalse(tag.equals(new Tag("Resident")));
    }

    @Test
    public void matchesName() {
        Tag tag = new Tag("Resident Fellow");

        assertThrows(NullPointerException.class, () -> tag.matchesName(null));

        assertTrue(tag.matchesName("Resident Fellow"));
        assertTrue(tag.matchesName("  resident   FELLOW ")); // different case and spacing
        assertTrue(new Tag(VALID_TAG_GREEK_UPPER_CASE).matchesName(VALID_TAG_GREEK_UPPER_CASE));
        assertTrue(new Tag(VALID_TAG_GREEK_UPPER_CASE).matchesName(VALID_TAG_GREEK_LOWER_CASE));

        assertFalse(tag.matchesName("Resident")); // partial name
        assertFalse(tag.matchesName("")); // empty string
        assertFalse(tag.matchesName("Resident/Fellow")); // cannot be a tag name
        assertFalse(tag.matchesName(INVALID_TAG_TOO_LONG));
    }

    @Test
    public void toComparisonKey() {
        assertThrows(NullPointerException.class, () -> Tag.toComparisonKey(null));

        assertEquals("resident fellow", Tag.toComparisonKey("  Resident   FELLOW "));
        assertEquals("", Tag.toComparisonKey("   ")); // whitespace only
        assertEquals("ra/staff", Tag.toComparisonKey("RA/Staff")); // accepts values that are not valid tags
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
