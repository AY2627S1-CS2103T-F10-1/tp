package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_TOO_LONG;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_RESIDENT_FELLOW;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PersonHasTagPredicateTest {

    private static final Person RESIDENT_FELLOW_PERSON =
            new PersonBuilder().withTags("RA", VALID_TAG_RESIDENT_FELLOW).build();

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new PersonHasTagPredicate(null));
    }

    @Test
    public void test_personHasTag_returnsTrue() {
        assertTrue(new PersonHasTagPredicate(VALID_TAG_RESIDENT_FELLOW).test(RESIDENT_FELLOW_PERSON));

        // different case
        assertTrue(new PersonHasTagPredicate("ra").test(RESIDENT_FELLOW_PERSON));

        // extra whitespace
        assertTrue(new PersonHasTagPredicate("  resident    FELLOW ").test(RESIDENT_FELLOW_PERSON));
    }

    @Test
    public void test_personDoesNotHaveTag_returnsFalse() {
        // person has no tags
        assertFalse(new PersonHasTagPredicate("RA").test(new PersonBuilder().build()));

        // partial tag name is not a match
        assertFalse(new PersonHasTagPredicate("Resident").test(RESIDENT_FELLOW_PERSON));

        // values that cannot be tag names never match
        assertFalse(new PersonHasTagPredicate("RA/Staff").test(RESIDENT_FELLOW_PERSON));
        assertFalse(new PersonHasTagPredicate(INVALID_TAG_TOO_LONG).test(RESIDENT_FELLOW_PERSON));
        assertFalse(new PersonHasTagPredicate("").test(RESIDENT_FELLOW_PERSON));
    }

    @Test
    public void getTagName_extraWhitespace_returnsCollapsedName() {
        assertEquals("resident fellow", new PersonHasTagPredicate("  resident   fellow ").getTagName());
    }

    @Test
    public void equals() {
        PersonHasTagPredicate predicate = new PersonHasTagPredicate("RA");

        // same object -> returns true
        assertTrue(predicate.equals(predicate));

        // same tag name ignoring case and spacing -> returns true
        assertTrue(predicate.equals(new PersonHasTagPredicate(" ra ")));

        // different types -> returns false
        assertFalse(predicate.equals(1));

        // null -> returns false
        assertFalse(predicate.equals(null));

        // different tag name -> returns false
        assertFalse(predicate.equals(new PersonHasTagPredicate("Resident")));
    }

    @Test
    public void hashCode_sameTagNameIgnoringCaseAndSpacing_sameHashCode() {
        assertEquals(new PersonHasTagPredicate("RA").hashCode(), new PersonHasTagPredicate(" ra ").hashCode());
    }

    @Test
    public void toStringMethod() {
        PersonHasTagPredicate predicate = new PersonHasTagPredicate("RA");
        String expected = PersonHasTagPredicate.class.getCanonicalName() + "{tagName=RA}";
        assertEquals(expected, predicate.toString());
    }
}
