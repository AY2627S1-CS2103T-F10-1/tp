package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/** Archives a contact identified by its index in the currently displayed list. */
public class ArchiveCommand extends Command {
    public static final String COMMAND_WORD = "archive";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Archives a contact by displayed index.\n"
            + "Parameters: INDEX (positive integer)\nExample: " + COMMAND_WORD + " 1";
    public static final String MESSAGE_SUCCESS = "Archived 1 contact.";
    public static final String MESSAGE_ALREADY_ARCHIVED = "Contact is already archived. No contacts archived.";

    private final Index targetIndex;

    public ArchiveCommand(Index targetIndex) {
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
            return new CommandResult(MESSAGE_ALREADY_ARCHIVED);
        }

        // Resolve the target before replacement changes the observable filtered list.
        Person archived = target.withArchive(true);
        model.setPerson(target, archived);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ArchiveCommand otherCommand
                && targetIndex.equals(otherCommand.targetIndex);
    }

    @Override
    public int hashCode() {
        return targetIndex.hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
