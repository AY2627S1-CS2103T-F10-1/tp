package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Person;

/** Sets or replaces emergency details for a person in the displayed list. */
public class EmergencyCommand extends Command {
    public static final String COMMAND_WORD = "emergency";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sets or replaces a person's emergency contact.\n"
            + "Parameters: INDEX /name NAME /phone PHONE [/relation RELATION]\n"
            + "Example: emergency 1 /name Bob Choo /phone 9123 4567 /relation Brother";
    public static final String MESSAGE_SUCCESS = "Emergency contact set for %1$s.\nemergency: %2$s";

    private final Index index;
    private final EmergencyContact emergencyContact;

    /** Creates an assignment for the given index in the displayed list. */
    public EmergencyCommand(Index index, EmergencyContact emergencyContact) {
        requireAllNonNull(index, emergencyContact);
        this.index = index;
        this.emergencyContact = emergencyContact;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        List<Person> persons = model.getFilteredPersonList();
        if (index.getZeroBased() >= persons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person person = persons.get(index.getZeroBased());
        model.setPerson(person, person.withEmergencyContact(emergencyContact));
        return new CommandResult(String.format(MESSAGE_SUCCESS, person.getName(), emergencyContact));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof EmergencyCommand command
                && index.equals(command.index) && emergencyContact.equals(command.emergencyContact);
    }
}
