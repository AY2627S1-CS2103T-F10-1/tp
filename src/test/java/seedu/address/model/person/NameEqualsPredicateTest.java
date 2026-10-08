package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class NameEqualsPredicateTest {

    @Test
    public void equals() {
        NameEqualsPredicate firstPredicate = new NameEqualsPredicate("first");
        NameEqualsPredicate secondPredicate = new NameEqualsPredicate("second");

        // same object -> returns true
        assertTrue(firstPredicate.equals(firstPredicate));

        // same values -> returns true
        NameEqualsPredicate firstPredicateCopy = new NameEqualsPredicate("first");
        assertTrue(firstPredicate.equals(firstPredicateCopy));

        // different types -> returns false
        assertFalse(firstPredicate.equals(1));

        // null -> returns false
        assertFalse(firstPredicate.equals(null));

        // different name -> returns false
        assertFalse(firstPredicate.equals(secondPredicate));
    }

    @Test
    public void test_nameEqualsFullName_returnsTrue() {
        // Single-word name
        NameEqualsPredicate predicate = new NameEqualsPredicate("Alice");
        assertTrue(predicate.test(new PersonBuilder().withName("Alice").build()));

        // Multi-word name
        predicate = new NameEqualsPredicate("Alice Tan");
        assertTrue(predicate.test(new PersonBuilder().withName("Alice Tan").build()));
    }

    @Test
    public void test_nameDoesNotEqualFullName_returnsFalse() {
        // Different case
        NameEqualsPredicate predicate = new NameEqualsPredicate("alice bob");
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Partial name (single word of a multi-word name)
        predicate = new NameEqualsPredicate("Alice");
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Name is a prefix of the full name
        predicate = new NameEqualsPredicate("Alice B");
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Words in a different order
        predicate = new NameEqualsPredicate("Bob Alice");
        assertFalse(predicate.test(new PersonBuilder().withName("Alice Bob").build()));

        // Matches phone, email and address, but not name
        predicate = new NameEqualsPredicate("12345");
        assertFalse(predicate.test(new PersonBuilder().withName("Alice").withPhone("12345")
                .withEmail("alice@email.com").withAddress("Main Street").build()));
    }

    @Test
    public void toStringMethod() {
        NameEqualsPredicate predicate = new NameEqualsPredicate("Alice Bob");

        String expected = NameEqualsPredicate.class.getCanonicalName() + "{name=Alice Bob}";
        assertEquals(expected, predicate.toString());
    }
}
