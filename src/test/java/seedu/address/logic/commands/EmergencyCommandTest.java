package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
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

        CommandResult result = new AddressBookParser().parseCommand(
                "emergency 1 /name Bob Choo /phone 91234567 /relation Brother").execute(model);

        assertEquals(String.format(EmergencyCommand.MESSAGE_SUCCESS, BOB.getName()), result.getFeedbackToUser());
        assertEquals(List.of(ALICE, BOB.withEmergencyContact(contact)), model.getAddressBook().getPersonList());
        assertEquals(1, model.getFilteredPersonList().size());
    }

    @Test
    public void execute_existingEmergencyContact_replacesAllDetailsAndPreservesPerson() throws Exception {
        model.addPerson(ALICE.withEmergencyContact(contact));
        EmergencyContact replacement = new EmergencyContact(new Name("Amy Bee"), new Phone("87654321"), "");
        CommandResult result = new AddressBookParser().parseCommand(
                "emergency 1 /name Amy Bee /phone 87654321").execute(model);
        assertEquals(String.format(EmergencyCommand.MESSAGE_SUCCESS, ALICE.getName()), result.getFeedbackToUser());
        assertEquals(List.of(ALICE.withEmergencyContact(replacement)), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_editTagsAfterAssignment_preservesEmergency() throws Exception {
        model.addPerson(ALICE.withEmergencyContact(contact));
        new AddressBookParser().parseCommand("edit 1 /tag").execute(model);
        Person stored = model.getAddressBook().getPersonList().getFirst();
        assertTrue(stored.getTags().isEmpty());
        assertEquals(contact, stored.getEmergencyContact().orElseThrow());
    }

    @Test
    public void execute_invalidIndex_rejectedWithoutChanges() {
        assertCommandFailure(new EmergencyCommand(INDEX_FIRST_PERSON, contact), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        model.addPerson(ALICE);
        assertCommandFailure(new EmergencyCommand(Index.fromOneBased(2), contact), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_editAfterAssignment_preservesEmergencyAndUpdatesRoom() throws Exception {
        model.addPerson(ALICE.withEmergencyContact(contact));
        new AddressBookParser().parseCommand("edit 1 /phone 87654321 /room B-204").execute(model);

        Person stored = model.getAddressBook().getPersonList().getFirst();
        assertEquals(contact, stored.getEmergencyContact().orElseThrow());
        assertEquals("B-204", stored.getRoom().value);
        assertEquals("87654321", stored.getPhone().value);
        assertEquals(ALICE.getTags(), stored.getTags());
        assertTrue(ALICE.isSamePerson(stored));
    }
}
