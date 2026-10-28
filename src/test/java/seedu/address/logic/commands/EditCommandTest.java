package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.DESC_AMY;
import static seedu.address.logic.commands.CommandTestUtil.DESC_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NOTE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_PHONE_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_ROOM_BOB;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_FRIEND;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_HUSBAND;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.function.UnaryOperator;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.logic.parser.EditCommandParser;
import seedu.address.logic.parser.PersonField;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.ReadOnlyAddressBook;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.testutil.EditPersonDescriptorBuilder;
import seedu.address.testutil.PersonBuilder;
import seedu.address.testutil.PersonFieldSamples;

/**
 * Contains integration tests (interaction with the Model) and unit tests for EditCommand.
 */
public class EditCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_editNoteAndClearTags_preservesEmergencyDetails() throws Exception {
        Person original = model.getFilteredPersonList().getFirst();
        EmergencyContact contact = new EmergencyContact(new Name("Bob Choo"), new Phone("91234567"), "Brother");
        model.setPerson(original, original.withEmergencyContact(contact));
        new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder().withNote("Call after 6pm")
                .withRoom("B-204").withTags().build()).execute(model);
        Person stored = model.getAddressBook().getPersonList().getFirst();
        assertEquals(contact, stored.getEmergencyContact().orElseThrow());
        assertEquals("Call after 6pm", stored.getNote().value);
        assertEquals("B-204", stored.getRoom().value);
        assertTrue(stored.getTags().isEmpty());
        assertEquals(original.getPhone(), stored.getPhone());
    }

    @Test
    public void execute_allFieldsSpecifiedUnfilteredList_success() {
        Person originalPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(originalPerson).withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withRoom(VALID_ROOM_BOB).withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(editedPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_someFieldsSpecifiedUnfilteredList_success() {
        Index indexLastPerson = Index.fromOneBased(model.getFilteredPersonList().size());
        Person lastPerson = model.getFilteredPersonList().get(indexLastPerson.getZeroBased());

        PersonBuilder personInList = new PersonBuilder(lastPerson);
        Person editedPerson = personInList.withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withTags(VALID_TAG_HUSBAND).build();

        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withTags(VALID_TAG_HUSBAND).build();
        EditCommand editCommand = new EditCommand(indexLastPerson, descriptor);

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(lastPerson, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_clearRoom_removesRoom() {
        Person withRoom = replaceFirstPerson(person -> person.withRoom(VALID_ROOM_BOB));
        assertFirstPersonEdited(withRoom, new PersonBuilder(withRoom).withRoom("").build(),
                new EditPersonDescriptorBuilder().withRoom("").build());
    }

    @Test
    public void execute_setNote_addsNote() {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        assertFirstPersonEdited(original, new PersonBuilder(original).withNote(VALID_NOTE_AMY).build(),
                new EditPersonDescriptorBuilder().withNote(VALID_NOTE_AMY).build());
    }

    @Test
    public void execute_replaceNote_replacesNote() {
        Person withNote = replaceFirstPerson(person -> person.withNote(VALID_NOTE_AMY));
        assertFirstPersonEdited(withNote, new PersonBuilder(withNote).withNote(VALID_NOTE_BOB).build(),
                new EditPersonDescriptorBuilder().withNote(VALID_NOTE_BOB).build());
    }

    @Test
    public void execute_clearNote_removesNote() {
        Person withNote = replaceFirstPerson(person -> person.withNote(VALID_NOTE_AMY));
        assertFirstPersonEdited(withNote, new PersonBuilder(withNote).withNote("").build(),
                new EditPersonDescriptorBuilder().withNote("").build());
    }

    /** Replaces the first person in {@code model} with a copy changed by {@code change}, and returns the copy. */
    private Person replaceFirstPerson(UnaryOperator<PersonBuilder> change) {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person replacement = change.apply(new PersonBuilder(original)).build();
        model.setPerson(original, replacement);
        return replacement;
    }

    /** Asserts that editing the first person, {@code original}, with {@code descriptor} gives {@code edited}. */
    private void assertFirstPersonEdited(Person original, Person edited, EditPersonDescriptor descriptor) {
        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(edited));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, edited);

        assertCommandSuccess(new EditCommand(INDEX_FIRST_PERSON, descriptor), model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_editedContact_canBeReadBackFromModel() throws Exception {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(original).withName(VALID_NAME_BOB).withPhone(VALID_PHONE_BOB)
                .withRoom(VALID_ROOM_BOB).withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).build();

        new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder(editedPerson).build()).execute(model);

        // read the contact back, rather than comparing with a model that went through the same code
        assertEquals(editedPerson, model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased()));
    }

    @Test
    public void execute_roomEdited_messageShowsNewRoom() throws Exception {
        CommandResult result = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withRoom(VALID_ROOM_BOB).build()).execute(model);

        assertTrue(result.getFeedbackToUser().contains("Room: " + VALID_ROOM_BOB), result.getFeedbackToUser());
    }

    @Test
    public void execute_oneFieldAtATime_changesOnlyThatFieldAndPreservesFavourite() throws Exception {
        replaceFirstPerson(person -> person.withFavourite(true));
        for (PersonField<?> field : PersonField.values()) {
            assertOnlyFieldChanged(field);
        }
    }

    /**
     * Edits only {@code field} and checks, from the person that the command gives to the model,
     * that this field has its new value and every other field is as before.
     */
    private <T> void assertOnlyFieldChanged(PersonField<T> field) throws Exception {
        Person target = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        T newValue = PersonFieldSamples.getValue(field);
        assertNotEquals(newValue, field.getFrom(target), "the sample for " + field + " must differ from the target");
        EditPersonDescriptor descriptor = new EditPersonDescriptor();
        descriptor.set(field, newValue);

        SetPersonSpy spy = new SetPersonSpy(model.getAddressBook());
        new EditCommand(INDEX_FIRST_PERSON, descriptor).execute(spy);

        assertEquals(target, spy.target);
        for (PersonField<?> other : PersonField.values()) {
            Object expected = other == field ? newValue : other.getFrom(target);
            assertEquals(expected, other.getFrom(spy.editedPerson), "field " + other + " after editing " + field);
        }
        assertEquals(target.getEmail(), spy.editedPerson.getEmail());
        assertEquals(target.getAddress(), spy.editedPerson.getAddress());
        assertTrue(spy.editedPerson.isFavourite(), "favourite status after editing " + field);
    }

    @Test
    public void execute_replaceTags_preservesFavouriteAndOtherDetails() throws Exception {
        Person original = replaceFirstPerson(person -> person.withFavourite(true).withRoom(VALID_ROOM_BOB)
                .withNote(VALID_NOTE_AMY));

        new EditCommandParser().parse(" 1 /tag " + VALID_TAG_HUSBAND).execute(model);

        Person edited = model.getFilteredPersonList().getFirst();
        assertTrue(edited.isFavourite());
        assertEquals(new PersonBuilder(original).withTags(VALID_TAG_HUSBAND).build(), edited);
    }

    @Test
    public void execute_clearTags_preservesFavouriteAndOtherDetails() throws Exception {
        Person original = replaceFirstPerson(person -> person.withFavourite(true).withRoom(VALID_ROOM_BOB)
                .withNote(VALID_NOTE_AMY));

        new EditCommandParser().parse(" 1 /tag").execute(model);

        Person edited = model.getFilteredPersonList().getFirst();
        assertTrue(edited.isFavourite());
        assertEquals(new PersonBuilder(original).withTags().build(), edited);
    }

    /** A model that records the replacement given to {@code setPerson} instead of storing it. */
    private static class SetPersonSpy extends ModelManager {
        private Person target;
        private Person editedPerson;

        SetPersonSpy(ReadOnlyAddressBook addressBook) {
            super(addressBook, new UserPrefs());
        }

        @Override
        public void setPerson(Person target, Person editedPerson) {
            this.target = target;
            this.editedPerson = editedPerson;
        }
    }

    @Test
    public void execute_roomAndTagsOnly_keepsOtherFields() {
        Person original = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(original).withRoom(VALID_ROOM_BOB).withTags().build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withRoom(VALID_ROOM_BOB).withTags().build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_noFieldSpecifiedUnfilteredList_success() {
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptor());
        Person editedPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_filteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personInFilteredList = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        Person editedPerson = new PersonBuilder(personInFilteredList).withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        String expectedMessage = String.format(EditCommand.MESSAGE_EDIT_PERSON_SUCCESS, Messages.format(editedPerson));

        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(model.getFilteredPersonList().get(0), editedPerson);

        assertCommandSuccess(editCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_duplicatePersonUnfilteredList_failure() {
        Person firstPerson = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder(firstPerson).build();
        EditCommand editCommand = new EditCommand(INDEX_SECOND_PERSON, descriptor);

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_duplicatePersonFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        // edit person in filtered list into a duplicate in address book
        Person personInList = model.getAddressBook().getPersonList().get(INDEX_SECOND_PERSON.getZeroBased());
        EditCommand editCommand = new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder(personInList).build());

        assertCommandFailure(editCommand, model, EditCommand.MESSAGE_DUPLICATE_PERSON);
    }

    @Test
    public void execute_invalidPersonIndexUnfilteredList_failure() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build();
        EditCommand editCommand = new EditCommand(outOfBoundIndex, descriptor);

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    /**
     * Edit filtered list where index is larger than size of filtered list,
     * but smaller than size of address book
     */
    @Test
    public void execute_invalidPersonIndexFilteredList_failure() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        EditCommand editCommand = new EditCommand(outOfBoundIndex,
                new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB).build());

        assertCommandFailure(editCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        final EditCommand standardCommand = new EditCommand(INDEX_FIRST_PERSON, DESC_AMY);

        // same values -> returns true
        EditPersonDescriptor copyDescriptor = new EditPersonDescriptor(DESC_AMY);
        EditCommand commandWithSameValues = new EditCommand(INDEX_FIRST_PERSON, copyDescriptor);
        assertTrue(standardCommand.equals(commandWithSameValues));

        // same object -> returns true
        assertTrue(standardCommand.equals(standardCommand));

        // null -> returns false
        assertFalse(standardCommand.equals(null));

        // different types -> returns false
        assertFalse(standardCommand.equals(new ClearCommand()));

        // different index -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_SECOND_PERSON, DESC_AMY)));

        // different descriptor -> returns false
        assertFalse(standardCommand.equals(new EditCommand(INDEX_FIRST_PERSON, DESC_BOB)));
    }

    @Test
    public void toStringMethod() {
        Index index = Index.fromOneBased(1);
        EditPersonDescriptor editPersonDescriptor = new EditPersonDescriptor();
        EditCommand editCommand = new EditCommand(index, editPersonDescriptor);
        String expected = EditCommand.class.getCanonicalName() + "{index=" + index + ", editPersonDescriptor="
                + editPersonDescriptor + "}";
        assertEquals(expected, editCommand.toString());
    }

}
