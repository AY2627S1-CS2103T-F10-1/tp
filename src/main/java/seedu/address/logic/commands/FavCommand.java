package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Marks a contact identified by its displayed index as a favourite.
 */
public class FavCommand extends Command {
    public static final String COMMAND_WORD = "fav";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Marks the contact at the displayed index as a favourite.\n"
            + "Parameters: INDEX (positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS = "Marked as favourite: %1$s";
    public static final String MESSAGE_ALREADY_FAVOURITE = "Contact is already a favourite: %1$s";
    public static final String MESSAGE_ARCHIVED_PERSON =
            "Cannot change favourite status for an archived contact. Unarchive it first.";

    private final Index targetIndex;

    /**
     * Creates a command that marks the contact at the given displayed index as a favourite.
     */
    public FavCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> displayed = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= displayed.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person target = displayed.get(targetIndex.getZeroBased());
        if (target.isArchived()) {
            throw new CommandException(MESSAGE_ARCHIVED_PERSON);
        }
        if (target.isFavourite()) {
            return new CommandResult(String.format(MESSAGE_ALREADY_FAVOURITE, Messages.format(target)));
        }

        model.setPerson(target, target.withFavourite(true));
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(target)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof FavCommand otherCommand
                && targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public int hashCode() {
        return targetIndex.getZeroBased();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
