package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.testutil.PersonBuilder;

public class EmergencyContactPredicateTest {

    private final EmergencyContactPredicate predicate = new EmergencyContactPredicate();

    @Test
    public void test_emergencyTagIgnoringCase_returnsTrue() {
        assertTrue(predicate.test(new PersonBuilder().withTags("emergency").build()));
        assertTrue(predicate.test(new PersonBuilder().withTags("Emergency").build()));
        assertTrue(predicate.test(new PersonBuilder().withTags("EMERGENCY").build()));
        assertTrue(predicate.test(new PersonBuilder().withTags("staff", "eMeRgEnCy", "resident").build()));
    }

    @Test
    public void test_noEmergencyTag_returnsFalse() {
        assertFalse(predicate.test(new PersonBuilder().build()));
        assertFalse(predicate.test(new PersonBuilder().withTags("staff", "resident").build()));
        assertFalse(predicate.test(new PersonBuilder().withTags("emerg", "emergencyservice").build()));
        assertFalse(predicate.test(new PersonBuilder().withName("Emergency")
                .withEmail("emergency@example.com").withAddress("Emergency Road").build()));
    }

    @Test
    public void test_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> predicate.test(null));
    }
}
