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
    public void archiveState_defaultsToFalse() {
        assertFalse(ALICE.isArchived());
        assertFalse(new PersonBuilder().withRoom("B-204").build().isArchived());
    }

    @Test
    public void withArchive_preservesDetailsAndIdentity() {
        Person original = new PersonBuilder(ALICE).withRoom("B-204").build();
        Person archived = original.withArchive(true);
        assertTrue(archived.isArchived());
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
    }

    @Test
    public void hashCode_differentNote_differs() {
        Person aliceWithNote = new PersonBuilder(ALICE).withNote(VALID_NOTE_BOB).build();
        assertNotEquals(ALICE.hashCode(), aliceWithNote.hashCode());
        assertEquals(aliceWithNote.hashCode(), new PersonBuilder(aliceWithNote).build().hashCode());
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
    }
}
