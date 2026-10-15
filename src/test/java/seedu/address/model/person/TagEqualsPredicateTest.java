package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class TagEqualsPredicateTest {

    @Test
    public void equals() {
        TagEqualsPredicate firstPredicate = new TagEqualsPredicate(new Tag("first"));
        TagEqualsPredicate secondPredicate = new TagEqualsPredicate(new Tag("second"));

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        assertTrue(firstPredicate.equals(new TagEqualsPredicate(new Tag("first"))));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different tag -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_personHasTag_returnsTrue() {
        TagEqualsPredicate predicate = new TagEqualsPredicate(new Tag("friends"));

        // Only tag
        assertTrue(predicate.test(new PersonBuilder().withTags("friends").build()));

        // One of several tags
        assertTrue(predicate.test(new PersonBuilder().withTags("owesMoney", "friends").build()));

        // Different case
        assertTrue(predicate.test(new PersonBuilder().withTags("Friends").build()));
        assertTrue(new TagEqualsPredicate(new Tag("FRIENDS"))
                .test(new PersonBuilder().withTags("friends").build()));
    }

    @Test
    public void test_personDoesNotHaveTag_returnsFalse() {
        TagEqualsPredicate predicate = new TagEqualsPredicate(new Tag("friends"));

        // No tags
        assertFalse(predicate.test(new PersonBuilder().withTags().build()));

        // Different tag
        assertFalse(predicate.test(new PersonBuilder().withTags("family").build()));

        // Tag is only a part of a longer tag
        assertFalse(predicate.test(new PersonBuilder().withTags("friends2").build()));

        // Matches name but not tag
        assertFalse(predicate.test(new PersonBuilder().withName("friends").withTags("family").build()));
    }

    @Test
    public void toStringMethod() {
        TagEqualsPredicate predicate = new TagEqualsPredicate(new Tag("friends"));

        String expected = TagEqualsPredicate.class.getCanonicalName() + "{tag=[friends]}";
        assertEquals(expected, predicate.toString());
    }
}
