package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;

import java.util.function.Predicate;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in the address book whose name closely matches the given name,
 * or who have the given tag or phone number. Name matching ignores case and tolerates small typos;
 * tag matching ignores case and extra spaces; phone matching ignores extra spaces.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose name closely matches "
            + "the specified name (ignoring case and small typos), who have the specified tag (ignoring case), "
            + "or whose phone number is the specified number (ignoring extra spaces), "
            + "and displays them as a list with index numbers.\n"
            + "Parameters: " + PREFIX_NAME_COMMAND + " NAME or " + PREFIX_TAG_COMMAND + " TAG or "
            + PREFIX_PHONE_COMMAND + " NUMBER\n"
            + "Examples: " + COMMAND_WORD + " " + PREFIX_NAME_COMMAND + " Sarah Tan, "
            + COMMAND_WORD + " " + PREFIX_TAG_COMMAND + " friends, "
            + COMMAND_WORD + " " + PREFIX_PHONE_COMMAND + " 91234567";

    private static final Logger logger = LogsCenter.getLogger(FindCommand.class);

    private final Predicate<Person> predicate;

    /**
     * Creates a FindCommand that lists the persons matching {@code predicate}.
     */
    public FindCommand(Predicate<Person> predicate) {
        requireNonNull(predicate);
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS.and(predicate));
        int count = model.getFilteredPersonList().size();
        logger.fine("Find listed " + count + " person(s) matching " + predicate);
        if (count == 0) {
            return new CommandResult(Messages.MESSAGE_NO_PERSONS_FOUND);
        }
        return new CommandResult(String.format(Messages.MESSAGE_PERSONS_LISTED_OVERVIEW, count));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof FindCommand otherFindCommand)) {
            return false;
        }

        return predicate.equals(otherFindCommand.predicate);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("predicate", predicate)
                .toString();
    }
}
