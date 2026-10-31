package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class EmergencyCommandTest {
    private final EmergencyContact contact =
            new EmergencyContact(new Name("Bob Choo"), new Phone("91234567"), "Brother");
    private final Model model = new ModelManager();

    @Test
    public void execute_filteredList_assignsOnlyDisplayedPersonAndPreservesDetails() throws Exception {
        model.addPerson(ALICE);
        model.addPerson(BOB);
        model.updateFilteredPersonList(person -> person.isSamePerson(BOB));

        CommandResult result = new EmergencyCommand(INDEX_FIRST_PERSON, contact).execute(model);

        assertEquals("Emergency contact set for " + BOB.getName() + ".\nemergency: Bob Choo(Brother) 91234567",
                result.getFeedbackToUser());
        assertEquals(List.of(ALICE, BOB.withEmergencyContact(contact)), model.getAddressBook().getPersonList());
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_existingEmergencyContact_replacesAllDetailsAndPreservesPerson() throws Exception {
        model.addPerson(ALICE.withEmergencyContact(contact));
        EmergencyContact replacement = new EmergencyContact(new Name("Amy Bee"), new Phone("87654321"), "");
        CommandResult result = new EmergencyCommand(INDEX_FIRST_PERSON, replacement).execute(model);
        assertEquals("Emergency contact set for " + ALICE.getName() + ".\nemergency: Amy Bee 87654321",
                result.getFeedbackToUser());
        assertEquals(List.of(ALICE.withEmergencyContact(replacement)), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_clearFilteredPerson_preservesOtherDetailsAndView() throws Exception {
        model.addPerson(ALICE);
        model.addPerson(BOB.withFavourite(true).withEmergencyContact(contact));
        model.updateFilteredPersonList(person -> person.isSamePerson(BOB));

        CommandResult result = new EmergencyCommand(INDEX_FIRST_PERSON).execute(model);

        assertEquals("Emergency contact cleared for " + BOB.getName() + ".", result.getFeedbackToUser());
        assertEquals(List.of(ALICE, BOB.withFavourite(true)), model.getAddressBook().getPersonList());
        assertEquals(List.of(BOB.withFavourite(true)), model.getFilteredPersonList());
        new EmergencyCommand(INDEX_FIRST_PERSON).execute(model);
        assertEquals(List.of(ALICE, BOB.withFavourite(true)), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_invalidIndex_rejectedWithoutChanges() {
        assertCommandFailure(new EmergencyCommand(INDEX_FIRST_PERSON, contact), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        model.addPerson(ALICE);
        assertCommandFailure(new EmergencyCommand(Index.fromOneBased(2), contact), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        assertCommandFailure(new EmergencyCommand(Index.fromOneBased(2)), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals_comparesIndexAndEmergencyDetails() {
        EmergencyCommand command = new EmergencyCommand(INDEX_FIRST_PERSON, contact);
        assertTrue(command.equals(command));
        assertEquals(command, new EmergencyCommand(INDEX_FIRST_PERSON, contact));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
        assertFalse(command.equals(new EmergencyCommand(Index.fromOneBased(2), contact)));
        assertFalse(command.equals(new EmergencyCommand(INDEX_FIRST_PERSON,
                new EmergencyContact(new Name("Amy Bee"), new Phone("87654321"), ""))));
        EmergencyCommand clear = new EmergencyCommand(INDEX_FIRST_PERSON);
        assertEquals(clear, new EmergencyCommand(INDEX_FIRST_PERSON));
        assertFalse(clear.equals(command));
        assertFalse(clear.equals(new EmergencyCommand(Index.fromOneBased(2))));
    }
}
