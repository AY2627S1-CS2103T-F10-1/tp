package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
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
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

public class EmergencyClearCommandTest {
    private final EmergencyContact contact =
            new EmergencyContact(new Name("Bob Choo"), new Phone("91234567"), "Brother");
    private final Model model = new ModelManager();

    @Test
    public void execute_clearFilteredPerson_preservesOtherDetailsAndView() throws Exception {
        model.addPerson(ALICE);
        model.addPerson(BOB.withFavourite(true).withEmergencyContact(contact));
        model.updateFilteredPersonList(person -> person.isSamePerson(BOB));

        CommandResult result = new EmergencyClearCommand(INDEX_FIRST_PERSON).execute(model);

        assertEquals("Emergency contact cleared for " + BOB.getName() + ".", result.getFeedbackToUser());
        assertEquals(List.of(ALICE, BOB.withFavourite(true)), model.getAddressBook().getPersonList());
        assertEquals(List.of(BOB.withFavourite(true)), model.getFilteredPersonList());
        Person cleared = model.getFilteredPersonList().getFirst();
        CommandResult repeated = new EmergencyClearCommand(INDEX_FIRST_PERSON).execute(model);
        assertEquals("No emergency contact assigned to " + BOB.getName() + ". No changes made.",
                repeated.getFeedbackToUser());
        assertSame(cleared, model.getFilteredPersonList().getFirst());
        assertEquals(List.of(ALICE, BOB.withFavourite(true)), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_invalidIndex_rejectedWithoutChanges() {
        assertCommandFailure(new EmergencyClearCommand(INDEX_FIRST_PERSON), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        model.addPerson(ALICE);
        assertCommandFailure(new EmergencyClearCommand(Index.fromOneBased(2)), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_archivedPerson_rejectedWithoutChanges() {
        model.addPerson(ALICE.withArchive(true).withEmergencyContact(contact));
        model.updateFilteredPersonList(Person::isArchived);
        assertCommandFailure(new EmergencyClearCommand(INDEX_FIRST_PERSON), model,
                EmergencyCommand.MESSAGE_ARCHIVED_PERSON);
    }

    @Test
    public void equals_comparesIndex() {
        EmergencyClearCommand command = new EmergencyClearCommand(INDEX_FIRST_PERSON);
        assertTrue(command.equals(command));
        assertEquals(command, new EmergencyClearCommand(INDEX_FIRST_PERSON));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new EmergencyCommand(INDEX_FIRST_PERSON, contact)));
        assertFalse(command.equals(new EmergencyClearCommand(Index.fromOneBased(2))));
    }
}
