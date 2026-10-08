package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.DeleteCommandParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for
 * {@code DeleteCommand}.
 */
public class DeleteCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_validIndexUnfilteredList_success() {
        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        ModelManager expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexUnfilteredList_throwsCommandException() {
        Index outOfBoundIndex = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_validIndexFilteredList_success() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Person personToDelete = model.getFilteredPersonList().get(INDEX_FIRST_PERSON.getZeroBased());
        DeleteCommand deleteCommand = new DeleteCommand(INDEX_FIRST_PERSON);

        String expectedMessage = String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS,
                Messages.format(personToDelete));

        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.deletePerson(personToDelete);
        showNoPerson(expectedModel);

        assertCommandSuccess(deleteCommand, model, expectedMessage, expectedModel);
    }

    @Test
    public void execute_invalidIndexFilteredList_throwsCommandException() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);

        Index outOfBoundIndex = INDEX_SECOND_PERSON;
        // ensures that outOfBoundIndex is still in bounds of address book list
        assertTrue(outOfBoundIndex.getZeroBased() < model.getAddressBook().getPersonList().size());

        DeleteCommand deleteCommand = new DeleteCommand(outOfBoundIndex);

        assertCommandFailure(deleteCommand, model, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        DeleteCommand deleteFirstCommand = new DeleteCommand(INDEX_FIRST_PERSON);
        DeleteCommand deleteSecondCommand = new DeleteCommand(INDEX_SECOND_PERSON);

        // same object -> returns true
        assertTrue(deleteFirstCommand.equals(deleteFirstCommand));

        // same values -> returns true
        DeleteCommand deleteFirstCommandCopy = new DeleteCommand(INDEX_FIRST_PERSON);
        assertTrue(deleteFirstCommand.equals(deleteFirstCommandCopy));

        // different types -> returns false
        assertFalse(deleteFirstCommand.equals(1));

        // null -> returns false
        assertFalse(deleteFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(deleteFirstCommand.equals(deleteSecondCommand));
    }

    @Test
    public void toStringMethod() {
        Index targetIndex = Index.fromOneBased(1);
        DeleteCommand deleteCommand = new DeleteCommand(targetIndex);
        String expected = DeleteCommand.class.getCanonicalName() + "{targetIndex=" + targetIndex + "}";
        assertEquals(expected, deleteCommand.toString());
    }

    @Test
    public void execute_nameSearch_handlesAmbiguityAndFollowUp() throws Exception {
        Person aliceTan = new PersonBuilder().withName("Alice Tan").build();
        Person aliceLee = new PersonBuilder().withName("Alice Lee").build();
        Person bob = new PersonBuilder().withName("Bob Smith").build();
        model = new ModelManager(new AddressBook(), new UserPrefs());
        model.addPerson(aliceTan);
        model.addPerson(aliceLee);
        model.addPerson(bob);
        model.updateFilteredPersonList(person -> person.equals(bob));
        DeleteCommandParser parser = new DeleteCommandParser();

        // Search all contacts with the same OR matching as find, despite the previous filter.
        CommandResult result = parser.parse("aLiCe Tan").execute(model);
        assertEquals(String.format(DeleteCommand.MESSAGE_MULTIPLE_MATCHES, 2), result.getFeedbackToUser());
        assertEquals(List.of(aliceTan, aliceLee), model.getFilteredPersonList());
        assertEquals(List.of(aliceTan, aliceLee, bob), model.getAddressBook().getPersonList());

        parser.parse("1").execute(model);
        assertEquals(List.of(aliceLee, bob), model.getAddressBook().getPersonList());
        parser.parse("Smith").execute(model);
        assertEquals(List.of(aliceLee), model.getAddressBook().getPersonList());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_uniqueKeywordAfterAmbiguity_deletesOnlyMatch() throws Exception {
        Person aliceTan = new PersonBuilder().withName("Alice Tan").build();
        Person aliceLee = new PersonBuilder().withName("Alice Lee").build();
        model = new ModelManager(new AddressBook(), new UserPrefs());
        model.addPerson(aliceTan);
        model.addPerson(aliceLee);
        DeleteCommandParser parser = new DeleteCommandParser();
        parser.parse("Alice").execute(model);

        CommandResult result = parser.parse("tan").execute(model);
        assertEquals(String.format(DeleteCommand.MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(aliceTan)),
                result.getFeedbackToUser());
        assertEquals(List.of(aliceLee), model.getAddressBook().getPersonList());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_noWholeWordMatch_preservesContacts() throws Exception {
        Person alice = new PersonBuilder().withName("Alice Tan").build();
        model = new ModelManager(new AddressBook(), new UserPrefs());
        model.addPerson(alice);

        CommandResult result = new DeleteCommandParser().parse("Ali").execute(model);
        assertEquals(DeleteCommand.MESSAGE_NO_MATCHES, result.getFeedbackToUser());
        assertEquals(List.of(alice), model.getAddressBook().getPersonList());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void equalsAndToString_nameTargets() throws Exception {
        DeleteCommandParser parser = new DeleteCommandParser();
        DeleteCommand alice = parser.parse("Alice");
        assertEquals(alice, parser.parse("Alice"));
        assertFalse(alice.equals(parser.parse("Bob")));
        assertFalse(alice.equals(parser.parse("1")));
        assertFalse(parser.parse("1").equals(alice));
        assertTrue(alice.toString().contains("nameSearch="));
    }

    /**
     * Updates {@code model}'s filtered list to show no one.
     */
    private void showNoPerson(Model model) {
        model.updateFilteredPersonList(p -> false);

        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
