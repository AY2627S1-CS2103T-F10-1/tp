package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/** Clears emergency details for a person in the displayed list. */
public class EmergencyClearCommand extends Command {
    public static final String MESSAGE_SUCCESS = "Emergency contact cleared for %1$s.";
    public static final String MESSAGE_NO_EMERGENCY_CONTACT =
            "No emergency contact assigned to %1$s. No changes made.";

    private final Index index;

    /** Creates a removal of emergency details for the given displayed index. */
    public EmergencyClearCommand(Index index) {
        this.index = requireNonNull(index);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> persons = model.getFilteredPersonList();
        if (index.getZeroBased() >= persons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person person = persons.get(index.getZeroBased());
        if (person.isArchived()) {
            throw new CommandException(EmergencyCommand.MESSAGE_ARCHIVED_PERSON);
        }
        if (person.getEmergencyContact().isEmpty()) {
            return new CommandResult(String.format(MESSAGE_NO_EMERGENCY_CONTACT, person.getName()));
        }
        model.setPerson(person, person.withoutEmergencyContact());
        return new CommandResult(String.format(MESSAGE_SUCCESS, person.getName()));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof EmergencyClearCommand command && index.equals(command.index);
    }
}
