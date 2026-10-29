package seedu.address.model.person;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ADDRESS_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_EMAIL_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;
import static seedu.address.testutil.TypicalPersons.JANE;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class PersonTest {

    @Test
    public void constructor_withoutFavouriteStatus_defaultsToFalse() {
        assertFalse(new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                ALICE.getTags()).isFavourite());
        assertFalse(new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                ALICE.getTags(), new Room("B-204")).isFavourite());
        assertFalse(new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                ALICE.getTags(), new Room("B-204"), new Note(VALID_NOTE_BOB)).isFavourite());
    }

    @Test
    public void withFavourite_changesStatus_preservesOtherDetails() {
        Person original = new PersonBuilder(ALICE).withRoom("B-204").withNote(VALID_NOTE_BOB)
                .withTags("Staff", "RA", "Block4").build();

        Person favourite = original.withFavourite(true);

        assertTrue(favourite.isFavourite());
        assertFalse(original.isFavourite());
        assertEquals(original.getName(), favourite.getName());
        assertEquals(original.getPhone(), favourite.getPhone());
        assertEquals(original.getEmail(), favourite.getEmail());
        assertEquals(original.getAddress(), favourite.getAddress());
        assertEquals(original.getRoom(), favourite.getRoom());
        assertEquals(original.getNote(), favourite.getNote());
        assertEquals(List.copyOf(original.getTags()), List.copyOf(favourite.getTags()));
        assertEquals(original, favourite.withFavourite(false));
        assertTrue(favourite.isFavourite());
    }

    @Test
    public void withFavourite_sameStatus_returnsEqualPerson() {
        assertEquals(ALICE, ALICE.withFavourite(false));
        Person favourite = ALICE.withFavourite(true);
        assertEquals(favourite, favourite.withFavourite(true));
    }

    @Test
    public void withEmergencyContact_archivedPerson_preservesArchiveAndNote() {
        Person original = new PersonBuilder(ALICE).withNote("Call after six").withFavourite(true).build()
                .withArchive(true);
        EmergencyContact contact = new EmergencyContact(new Name("Jane Tan"), new Phone("91234567"), "Sister");
        Person updated = original.withEmergencyContact(contact);
        assertTrue(updated.isArchived());
        assertTrue(updated.isFavourite());
        assertEquals(original.getNote(), updated.getNote());
        assertEquals(contact, updated.getEmergencyContact().orElseThrow());
    }

    @Test
    public void builderCopy_archivedPerson_preservesAllFields() {
        Person original = new PersonBuilder(ALICE).withNote("Call after six").withFavourite(true).build()
                .withEmergencyContact(new EmergencyContact(new Name("Jane Tan"), new Phone("91234567"), "Sister"))
                .withArchive(true);
        assertEquals(original, new PersonBuilder(original).build());
    }

    @Test
    public void constructor_archiveOverloads_preserveRequestedState() {
        Person withRoom = new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags(), new Room("B-204"), true);
        Person withNote = new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), ALICE.getTags(), new Room("B-204"), new Note("Call after six"), true);
        assertTrue(withRoom.isArchived());
        assertTrue(withNote.isArchived());
        assertTrue(withRoom.getEmergencyContact().isEmpty());
        assertTrue(withNote.getEmergencyContact().isEmpty());
        assertEquals(new Note("Call after six"), withNote.getNote());
    }

    @Test
    public void archiveState_defaultsToFalse() {
        assertFalse(ALICE.isArchived());
        assertFalse(new PersonBuilder().withRoom("B-204").build().isArchived());
    }

    @Test
    public void withArchive_preservesDetailsAndIdentity() {
        Person original = new PersonBuilder(ALICE).withRoom("B-204").withNote("Call after six")
                .withFavourite(true).build()
                .withEmergencyContact(new EmergencyContact(new Name("Jane Tan"), new Phone("91234567"), "Sister"));
        Person archived = original.withArchive(true);
        assertTrue(archived.isArchived());
        assertTrue(archived.isFavourite());
        assertEquals(original.getNote(), archived.getNote());
        assertEquals(original.getEmergencyContact(), archived.getEmergencyContact());
        assertFalse(original.isArchived());
        assertTrue(original.isSamePerson(archived));
        assertTrue(archived.isSamePerson(original));
        assertFalse(original.equals(archived));
        assertEquals(original, archived.withArchive(false));
        assertEquals(original.hashCode(), archived.withArchive(false).hashCode());
        assertEquals(archived, original.withArchive(true));
        assertEquals(archived.hashCode(), original.withArchive(true).hashCode());
        assertEquals(List.copyOf(original.getTags()), List.copyOf(archived.getTags()));
    }

    @Test
    public void asObservableList_modifyList_throwsUnsupportedOperationException() {
        Person person = new PersonBuilder().build();
        assertThrows(UnsupportedOperationException.class, () -> person.getTags().remove(0));
    }

    @Test
    public void constructor_overTagLimit_throwsIllegalArgumentException() {
        Set<Tag> tagsOverLimit = new LinkedHashSet<>(JANE.getTags());
        tagsOverLimit.add(new Tag("Staff"));

        assertThrows(IllegalArgumentException.class, () -> new Person(JANE.getName(), JANE.getPhone(),
                JANE.getEmail(), JANE.getAddress(), tagsOverLimit));
    }

    @Test
    public void constructor_exactlyTagLimit_success() {
        Set<Tag> tagsAtLimit = createTags(Person.MAX_TAGS);

        Person person = new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                tagsAtLimit);
        assertEquals(Person.MAX_TAGS, person.getTags().size());
        assertEquals(tagsAtLimit, person.getTags());

        // the constructor that also takes a room has the same limit
        Person personWithRoom = new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(),
                ALICE.getAddress(), tagsAtLimit, ALICE.getRoom());
        assertEquals(Person.MAX_TAGS, personWithRoom.getTags().size());
    }

    @Test
    public void constructor_oneOverTagLimit_throwsIllegalArgumentException() {
        Set<Tag> tagsOverLimit = createTags(Person.MAX_TAGS + 1);

        assertThrows(IllegalArgumentException.class, Person.MESSAGE_TAG_LIMIT_REACHED, () -> new Person(
                ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(), tagsOverLimit));
        assertThrows(IllegalArgumentException.class, Person.MESSAGE_TAG_LIMIT_REACHED, () -> new Person(
                ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(), tagsOverLimit,
                ALICE.getRoom()));
    }

    @Test
    public void constructor_tags_keepsGivenOrder() {
        List<Tag> tagsInGivenOrder = List.of(new Tag("Staff"), new Tag("RA"), new Tag("Block4"));
        Person person = new Person(ALICE.getName(), ALICE.getPhone(), ALICE.getEmail(), ALICE.getAddress(),
                new LinkedHashSet<>(tagsInGivenOrder));

        assertEquals(tagsInGivenOrder, List.copyOf(person.getTags()));
    }

    @Test
    public void isSamePerson() {
        // same object -> returns true
        assertTrue(ALICE.isSamePerson(ALICE));

        // favourite status does not change identity
        assertTrue(ALICE.isSamePerson(ALICE.withFavourite(true)));

        // null -> returns false
        assertFalse(ALICE.isSamePerson(null));

        // same name, all other attributes different -> returns true
        Person editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB)
                .withAddress(VALID_ADDRESS_BOB).withTags(VALID_TAG_HUSBAND).build();
        assertTrue(ALICE.isSamePerson(editedAlice));

        // different name, all other attributes same -> returns false
        editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.isSamePerson(editedAlice));

        // name differs in case, all other attributes same -> returns false
        Person editedBob = new PersonBuilder(BOB).withName(VALID_NAME_BOB.toLowerCase()).build();
        assertFalse(BOB.isSamePerson(editedBob));

        // name has trailing spaces, all other attributes same -> returns false
        String nameWithTrailingSpaces = VALID_NAME_BOB + " ";
        editedBob = new PersonBuilder(BOB).withName(nameWithTrailingSpaces).build();
        assertFalse(BOB.isSamePerson(editedBob));
    }

    @Test
    public void equals() {
        // same values -> returns true
        Person aliceCopy = new PersonBuilder(ALICE).build();
        assertTrue(ALICE.equals(aliceCopy));

        // same object -> returns true
        assertTrue(ALICE.equals(ALICE));

        // null -> returns false
        assertFalse(ALICE.equals(null));

        // different type -> returns false
        assertFalse(ALICE.equals(5));

        // different person -> returns false
        assertFalse(ALICE.equals(BOB));

        // different name -> returns false
        Person editedAlice = new PersonBuilder(ALICE).withName(VALID_NAME_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different phone -> returns false
        editedAlice = new PersonBuilder(ALICE).withPhone(VALID_PHONE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different email -> returns false
        editedAlice = new PersonBuilder(ALICE).withEmail(VALID_EMAIL_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different address -> returns false
        editedAlice = new PersonBuilder(ALICE).withAddress(VALID_ADDRESS_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different tags -> returns false
        editedAlice = new PersonBuilder(ALICE).withTags(VALID_TAG_HUSBAND).build();
        assertFalse(ALICE.equals(editedAlice));

        // different room -> returns false
        editedAlice = new PersonBuilder(ALICE).withRoom("B-204").build();
        assertFalse(ALICE.equals(editedAlice));

        // different note -> returns false
        editedAlice = new PersonBuilder(ALICE).withNote(VALID_NOTE_BOB).build();
        assertFalse(ALICE.equals(editedAlice));

        // different favourite status -> returns false
        assertFalse(ALICE.equals(ALICE.withFavourite(true)));
        assertEquals(ALICE.withFavourite(true), new PersonBuilder(ALICE).withFavourite(true).build());
    }

    @Test
    public void hashCode_differentFavouriteStatus_differs() {
        Person favourite = ALICE.withFavourite(true);
        Person copy = new PersonBuilder(favourite).build();
        assertNotEquals(ALICE.hashCode(), favourite.hashCode());
        assertTrue(copy.isFavourite());
        assertEquals(favourite, copy);
        assertEquals(favourite.hashCode(), copy.hashCode());
    }

    @Test
    public void hashCode_differentNote_differs() {
        Person aliceWithNote = new PersonBuilder(ALICE).withNote(VALID_NOTE_BOB).build();
        assertNotEquals(ALICE.hashCode(), aliceWithNote.hashCode());
        assertEquals(aliceWithNote.hashCode(), new PersonBuilder(aliceWithNote).build().hashCode());
    }

    @Test
    public void withEmergencyContact_preservesOtherFieldsAndOriginalPerson() {
        Person original = new PersonBuilder(ALICE).withRoom("B-204").withNote("Allergic to peanuts").build();
        EmergencyContact contact = new EmergencyContact(new Name("Bob Choo"), new Phone("91234567"), "Brother");
        Person updated = original.withEmergencyContact(contact);
        assertTrue(original.getEmergencyContact().isEmpty());
        assertTrue(original.isSamePerson(updated));
        assertEquals(original.getNote(), updated.getNote());
        assertEquals(original.getRoom(), updated.getRoom());
        assertEquals(original.getPhone(), updated.getPhone());
        assertEquals(original.getEmail(), updated.getEmail());
        assertEquals(original.getAddress(), updated.getAddress());
        assertEquals(original.getTags(), updated.getTags());
        assertEquals(contact, updated.getEmergencyContact().orElseThrow());
    }

    @Test
    public void equals_emergencyDetailsIncludedInEqualityAndHashCode() {
        EmergencyContact contact = new EmergencyContact(new Name("Bob Choo"), new Phone("91234567"), "Brother");
        Person updated = ALICE.withEmergencyContact(contact);
        Person copy = new PersonBuilder(updated).build();
        assertFalse(ALICE.equals(updated));
        assertEquals(updated, copy);
        assertEquals(updated.hashCode(), copy.hashCode());
    }

    @Test
    public void toString_withEmergencyContact_includesEmergencyDetails() {
        EmergencyContact contact = new EmergencyContact(new Name("Bob Choo"), new Phone("91234567"), "Brother");
        assertTrue(ALICE.withEmergencyContact(contact).toString().contains("emergencyContact="));
    }

    private static Set<Tag> createTags(int count) {
        Set<Tag> tags = new LinkedHashSet<>();
        for (int i = 1; i <= count; i++) {
            tags.add(new Tag("Tag" + i));
        }
        return tags;
    }

    @Test
    public void toStringMethod() {
        String expected = Person.class.getCanonicalName() + "{name=" + ALICE.getName() + ", phone=" + ALICE.getPhone()
                + ", email=" + ALICE.getEmail() + ", address=" + ALICE.getAddress() + ", tags=" + ALICE.getTags() + "}";
        assertEquals(expected, ALICE.toString());

        Person aliceWithRoom = new PersonBuilder(ALICE).withRoom("B-204").build();
        assertTrue(aliceWithRoom.toString().contains("room=B-204"));

        Person aliceWithNote = new PersonBuilder(ALICE).withNote(VALID_NOTE_BOB).build();
        assertTrue(aliceWithNote.toString().contains("note=" + VALID_NOTE_BOB));

        assertTrue(ALICE.withFavourite(true).toString().contains("isFavourite=true"));
    }
}
