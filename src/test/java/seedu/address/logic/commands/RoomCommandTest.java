package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.model.person.RoomNumber;
import seedu.address.testutil.PersonBuilder;

public class RoomCommandTest {
    @Test
    public void execute_resident_assignsAndReplacesRoom() throws Exception {
        Model model = new ModelManager();
        Person resident = new PersonBuilder().withName("Sarah Tan").withTags("resident").build();
        model.addPerson(resident);
        assertEquals("Room number added to Sarah Tan.",
                new RoomCommand("sarah tan", new RoomNumber("B-204")).execute(model).getFeedbackToUser());
        Person updated = model.getAddressBook().getPersonList().get(0);
        assertEquals(Optional.of(new RoomNumber("B-204")), updated.getRoomNumber());
        assertEquals(resident.getPhone(), updated.getPhone());
        assertEquals(resident.getTags(), updated.getTags());
        new RoomCommand("Sarah Tan", new RoomNumber("1204")).execute(model);
        assertEquals(Optional.of(new RoomNumber("1204")),
                model.getAddressBook().getPersonList().get(0).getRoomNumber());
    }

    @Test
    public void execute_nonResident_doesNotModifyContact() {
        Model model = new ModelManager();
        Person contact = new PersonBuilder().build();
        model.addPerson(contact);
        assertThrows(CommandException.class, RoomCommand.MESSAGE_NOT_RESIDENT, () ->
                new RoomCommand(contact.getName().fullName, new RoomNumber("B-204")).execute(model));
        assertEquals(contact, model.getAddressBook().getPersonList().get(0));
    }

    @Test
    public void execute_missingContact_throwsCommandException() {
        Model model = new ModelManager();
        assertThrows(CommandException.class, RoomCommand.MESSAGE_NOT_FOUND, () ->
                new RoomCommand("Sarah Tan", new RoomNumber("B-204")).execute(model));
    }
}
