package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class PhoneEqualsPredicateTest {
    @Test
    public void test_exactNumber_matchesOnlyPhone() {
        Person person = new PersonBuilder().withName("12345").withPhone("+65 9123 4567").build();
        assertTrue(new PhoneEqualsPredicate(" +65  9123 4567 ").test(person));
        assertFalse(new PhoneEqualsPredicate("9123").test(person));
        assertFalse(new PhoneEqualsPredicate("+6591234567").test(person));
        assertFalse(new PhoneEqualsPredicate("12345").test(person));
        assertFalse(new PhoneEqualsPredicate("unknown").test(person));
    }

    @Test
    public void equals() {
        PhoneEqualsPredicate predicate = new PhoneEqualsPredicate("9123 4567");
        assertTrue(predicate.equals(predicate));
        assertEquals(predicate, new PhoneEqualsPredicate(" 9123  4567 "));
        assertFalse(predicate.equals(new PhoneEqualsPredicate("12345")));
        assertFalse(predicate.equals(null));
        assertFalse(predicate.equals("9123 4567"));
    }

    @Test
    public void toStringMethod() {
        assertEquals(PhoneEqualsPredicate.class.getCanonicalName() + "{phone=91234567}",
                new PhoneEqualsPredicate("91234567").toString());
    }
}
