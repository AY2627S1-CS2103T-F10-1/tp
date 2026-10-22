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
 * Removes favourite status from a contact identified by its displayed index.
 */
public class UnfavCommand extends Command {
    public static final String COMMAND_WORD = "unfav";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Removes favourite status from the contact at the displayed index.\n"
            + "Parameters: INDEX (positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS = "Removed favourite status: %1$s";
    public static final String MESSAGE_NOT_FAVOURITE = "Contact is not a favourite: %1$s";

    private final Index targetIndex;

    /**
     * Creates a command that removes favourite status from the contact at the given displayed index.
     */
    public UnfavCommand(Index targetIndex) {
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
        if (!target.isFavourite()) {
            return new CommandResult(String.format(MESSAGE_NOT_FAVOURITE, Messages.format(target)));
        }

        model.setPerson(target, target.withFavourite(false));
        return new CommandResult(String.format(MESSAGE_SUCCESS, Messages.format(target)));
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof UnfavCommand otherCommand
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
