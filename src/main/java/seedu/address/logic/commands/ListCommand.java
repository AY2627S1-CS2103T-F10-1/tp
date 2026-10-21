package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;

import seedu.address.model.Model;

/**
 * Lists all contacts in the address book to the user.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_USAGE = COMMAND_WORD;
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_EMPTY = "No contacts found.";
    public static final String MESSAGE_SUCCESS = "Listed all persons.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        return new CommandResult(
                model.getFilteredPersonList().isEmpty() ? MESSAGE_EMPTY : MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        // instanceof handles nulls
        return other == this || other instanceof ListCommand;
    }

    @Override
    public int hashCode() {
        return ListCommand.class.hashCode();
    }
}
