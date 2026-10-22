package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.AddressBook;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.PersonBuilder;

public class JsonAdaptedEmergencyContactTest {
    @TempDir
    public Path testFolder;

    @Test
    public void saveAndLoad_emergencyDetails_roundTrip() throws Exception {
        EmergencyContact contact = new EmergencyContact(new Name("Bob Choo"), new Phone("91234567"), "Brother");
        Person person = new PersonBuilder(ALICE).withNote("Allergic to peanuts").build().withEmergencyContact(contact);
        AddressBook book = new AddressBook();
        book.addPerson(person);
        JsonAddressBookStorage storage = new JsonAddressBookStorage(testFolder.resolve("contacts.json"));
        storage.saveAddressBook(book);
        Person loaded = storage.readAddressBook().orElseThrow().getPersonList().getFirst();
        assertEquals(person, loaded);
        assertEquals(contact, loaded.getEmergencyContact().orElseThrow());
    }

    @Test
    public void toModelType_missingEmergencyField_loadsExistingPerson() throws Exception {
        String json = JsonUtil.toJsonString(new JsonAdaptedPerson(ALICE))
                .replaceAll(",?\\s*\"emergencyContact\"\\s*:\\s*null", "");
        assertEquals(ALICE, JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType());
        assertTrue(ALICE.getEmergencyContact().isEmpty());
    }

    @Test
    public void toModelType_missingRelation_defaultsToEmpty() throws Exception {
        JsonAdaptedEmergencyContact adapted = JsonUtil.fromJsonString(
                "{\"name\":\"Bob Choo\",\"phone\":\"91234567\"}", JsonAdaptedEmergencyContact.class);
        assertEquals("", adapted.toModelType().getRelation());
    }

    @Test
    public void toModelType_olderEmergencyRecordWithoutNote_preservesEmergencyDetails() throws Exception {
        String json = "{\"name\":\"Amy Bee\",\"phone\":\"85355255\",\"email\":\"\",\"address\":\"\",\"tags\":[],"
                + "\"emergencyContact\":{\"name\":\"Bob Choo\",\"phone\":\"91234567\",\"relation\":\"Brother\"}}";
        Person loaded = JsonUtil.fromJsonString(json, JsonAdaptedPerson.class).toModelType();
        assertEquals("", loaded.getNote().value);
        assertEquals("Bob Choo(Brother) 91234567", loaded.getEmergencyContact().orElseThrow().toString());
    }

    @Test
    public void toModelType_invalidOrIncompleteEmergencyDetails_rejected() {
        for (JsonAdaptedEmergencyContact invalid : new JsonAdaptedEmergencyContact[]{
            new JsonAdaptedEmergencyContact(null, "91234567", ""),
            new JsonAdaptedEmergencyContact("B@b", "91234567", ""),
            new JsonAdaptedEmergencyContact("Bob", null, ""),
            new JsonAdaptedEmergencyContact("Bob", "abc", ""),
            new JsonAdaptedEmergencyContact("Bob", "91234567", "a".repeat(51))}) {
            assertThrows(IllegalValueException.class, invalid::toModelType);
            JsonAdaptedPerson person = new JsonAdaptedPerson(ALICE.getName().fullName, ALICE.getPhone().value,
                    ALICE.getEmail().value, ALICE.getAddress().value, java.util.List.of(), "", "", invalid);
            assertThrows(IllegalValueException.class, person::toModelType);
        }
    }
}
