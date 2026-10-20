package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;

public class EmergencyContactTest {
    private final Name name = new Name("Bob Choo");
    private final Phone phone = new Phone("91234567");

    @Test
    public void constructor_validatesRelationAndRequiresDetails() {
        assertThrows(NullPointerException.class, () -> new EmergencyContact(null, phone, ""));
        assertThrows(NullPointerException.class, () -> new EmergencyContact(name, null, ""));
        assertThrows(NullPointerException.class, () -> new EmergencyContact(name, phone, null));
        assertThrows(IllegalArgumentException.class, () -> new EmergencyContact(name, phone, "a".repeat(51)));
        assertEquals("Family friend", new EmergencyContact(name, phone, " Family   friend ").getRelation());
    }

    @Test
    public void equality_comparesStoredDetailsWithoutChangingPersonIdentity() {
        EmergencyContact contact = new EmergencyContact(name, phone, "Brother");
        EmergencyContact copy = new EmergencyContact(name, phone, "Brother");
        assertEquals(contact, copy);
        assertEquals(contact.hashCode(), copy.hashCode());
        assertFalse(contact.equals(null));
        assertFalse(contact.equals(new EmergencyContact(new Name("Other"), phone, "Brother")));
        assertFalse(contact.equals(new EmergencyContact(name, new Phone("87654321"), "Brother")));
        assertFalse(contact.equals(new EmergencyContact(name, phone, "Sister")));
        Person updated = ALICE.withEmergencyContact(contact);
        assertTrue(ALICE.isSamePerson(updated));
        assertFalse(ALICE.equals(updated));
        assertEquals(updated.hashCode(), ALICE.withEmergencyContact(copy).hashCode());
        assertTrue(ALICE.getEmergencyContact().isEmpty());
        assertEquals("Bob Choo(Brother) 91234567", contact.toString());
        assertEquals("Bob Choo 91234567", new EmergencyContact(name, phone, "").toString());
        assertTrue(updated.toString().contains("emergencyContact="));
    }
}
