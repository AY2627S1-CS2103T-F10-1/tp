package seedu.address.logic.commands;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Optional;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Person;

/** Sets, replaces or clears emergency details for a person in the displayed list. */
public class EmergencyCommand extends Command {
    public static final String COMMAND_WORD = "emergency";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Sets, replaces or clears emergency details.\n"
            + "Parameters: INDEX /name NAME /phone PHONE [/relation RELATION] or INDEX /clear\n"
            + "Examples: emergency 1 /name Bob Choo /phone 9123 4567 /relation Brother; emergency 1 /clear";
    public static final String MESSAGE_SUCCESS = "Emergency contact set for %1$s.\nemergency: %2$s";
    public static final String MESSAGE_CLEAR_SUCCESS = "Emergency contact cleared for %1$s.";
    public static final String MESSAGE_ARCHIVED_PERSON =
            "Cannot change emergency details of an archived contact. Unarchive it first.";

    private final Index index;
    private final Optional<EmergencyContact> emergencyContact;

    /** Creates an assignment for the given index in the displayed list. */
    public EmergencyCommand(Index index, EmergencyContact emergencyContact) {
        requireAllNonNull(index, emergencyContact);
        this.index = index;
        this.emergencyContact = Optional.of(emergencyContact);
    }

    /** Creates a removal of emergency details for the given displayed index. */
    public EmergencyCommand(Index index) {
        requireAllNonNull(index);
        this.index = index;
        this.emergencyContact = Optional.empty();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireAllNonNull(model);
        List<Person> persons = model.getFilteredPersonList();
        if (index.getZeroBased() >= persons.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
        Person person = persons.get(index.getZeroBased());
        if (person.isArchived()) {
            throw new CommandException(MESSAGE_ARCHIVED_PERSON);
        }
        if (emergencyContact.isEmpty()) {
            model.setPerson(person, person.withoutEmergencyContact());
            return new CommandResult(String.format(MESSAGE_CLEAR_SUCCESS, person.getName()));
        }
        EmergencyContact assignedContact = emergencyContact.orElseThrow();
        model.setPerson(person, person.withEmergencyContact(assignedContact));
        return new CommandResult(String.format(MESSAGE_SUCCESS, person.getName(), assignedContact));
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
