package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

public class EmergencyContactTest {
    private final Name name = new Name("Bob Choo");
    private final Phone phone = new Phone("91234567");

    @Test
    public void constructor_nullDetails_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new EmergencyContact(null, phone, ""));
        assertThrows(NullPointerException.class, () -> new EmergencyContact(name, null, ""));
        assertThrows(NullPointerException.class, () -> new EmergencyContact(name, phone, null));
    }

    @Test
    public void constructor_relationTooLong_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> new EmergencyContact(name, phone, "a".repeat(51)));
    }

    @Test
    public void constructor_relationWhitespace_normalized() {
        assertEquals("Family friend", new EmergencyContact(name, phone, " Family   friend ").getRelation());
    }

    @Test
    public void equals_sameDetails_returnsTrue() {
        EmergencyContact contact = new EmergencyContact(name, phone, "Brother");
        EmergencyContact copy = new EmergencyContact(name, phone, "Brother");
        assertEquals(contact, copy);
        assertEquals(contact.hashCode(), copy.hashCode());
    }

    @Test
    public void equals_differentDetails_returnsFalse() {
        EmergencyContact contact = new EmergencyContact(name, phone, "Brother");
        assertFalse(contact.equals(null));
        assertFalse(contact.equals(new EmergencyContact(new Name("Other"), phone, "Brother")));
        assertFalse(contact.equals(new EmergencyContact(name, new Phone("87654321"), "Brother")));
        assertFalse(contact.equals(new EmergencyContact(name, phone, "Sister")));
    }

    @Test
    public void toString_withRelation_showsRelationInParentheses() {
        EmergencyContact contact = new EmergencyContact(name, phone, "Brother");
        assertEquals("Bob Choo(Brother) 91234567", contact.toString());
    }

    @Test
    public void toString_withoutRelation_omitsParentheses() {
        assertEquals("Bob Choo 91234567", new EmergencyContact(name, phone, "").toString());
    }
}
