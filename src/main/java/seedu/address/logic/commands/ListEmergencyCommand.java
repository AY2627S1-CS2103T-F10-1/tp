package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;

import seedu.address.model.Model;
import seedu.address.model.person.EmergencyContactPredicate;

/**
 * Lists all contacts tagged as emergency to the user.
 */
public class ListEmergencyCommand extends Command {

    public static final String EMERGENCY_KEYWORD = "emerg";
    public static final String MESSAGE_USAGE = ListCommand.COMMAND_WORD + " " + EMERGENCY_KEYWORD;
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Listed all emergency contacts.";
    public static final String MESSAGE_EMPTY = "No emergency contacts found.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS.and(new EmergencyContactPredicate()));
        return new CommandResult(model.getFilteredPersonList().isEmpty() ? MESSAGE_EMPTY : MESSAGE_SUCCESS);
    }
}
