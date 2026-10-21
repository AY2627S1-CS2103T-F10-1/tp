package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/** Lists archived contacts without changing the stored address book. */
public class ListArchivedCommand extends Command {
    public static final String ARCHIVED_KEYWORD = "archived";
    public static final String MESSAGE_USAGE = ListCommand.COMMAND_WORD + " " + ARCHIVED_KEYWORD;
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Listed all archived contacts.";
    public static final String MESSAGE_EMPTY = "No archived contacts found.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(Person::isArchived);
        return new CommandResult(model.getFilteredPersonList().isEmpty() ? MESSAGE_EMPTY : MESSAGE_SUCCESS);
    }
}
