package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import java.util.function.Predicate;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Finds and lists all persons in the address book whose full name is exactly equal to the given name,
 * or who have the given tag. Name matching is case sensitive; tag matching ignores case and extra spaces.
 */
public class FindCommand extends Command {

    public static final String COMMAND_WORD = "find";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Finds all persons whose full name is exactly "
            + "the specified name (case-sensitive), or who have the specified tag (ignoring case), "
            + "and displays them as a list with index numbers.\n"
            + "Parameters: " + PREFIX_NAME_COMMAND + " NAME or " + PREFIX_TAG_COMMAND + " TAG\n"
            + "Examples: " + COMMAND_WORD + " " + PREFIX_NAME_COMMAND + " Sarah Tan, "
            + COMMAND_WORD + " " + PREFIX_TAG_COMMAND + " friends";

    private final Predicate<Person> predicate;

    public FindCommand(Predicate<Person> predicate) {
        this.predicate = predicate;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(predicate);
        int count = model.getFilteredPersonList().size();
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
