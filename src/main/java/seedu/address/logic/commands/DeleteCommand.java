package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Deletes a person identified by displayed index or uniquely matching name keywords.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes a person by displayed index or uniquely matching name keywords.\n"
            + "Parameters: INDEX (positive integer) or KEYWORD [MORE_KEYWORDS]...\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";

    public static final String MESSAGE_NO_MATCHES = "No contacts found matching that name. No contacts deleted.";
    public static final String MESSAGE_MULTIPLE_MATCHES = "%1$d contacts found matching that name. "
            + "No contacts deleted. Use delete INDEX from the displayed list or delete with a unique name keyword.";

    private final Index targetIndex;
    private final FindCommand nameSearch;

    /**
     * Creates a command that deletes the person at the given displayed index.
     */
    public DeleteCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
        this.nameSearch = null;
    }

    /**
     * Creates a command that deletes only when the given name search finds exactly one person.
     */
    public DeleteCommand(FindCommand nameSearch) {
        this.targetIndex = null;
        this.nameSearch = requireNonNull(nameSearch);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (nameSearch != null) {
            nameSearch.execute(model);
            int matchCount = model.getFilteredPersonList().size();
            if (matchCount == 1) {
                return new DeleteCommand(Index.fromOneBased(1)).execute(model);
            }
            return new CommandResult(matchCount == 0 ? MESSAGE_NO_MATCHES
                    : String.format(MESSAGE_MULTIPLE_MATCHES, matchCount));
        }
        List<Person> lastShownList = model.getFilteredPersonList();

        if (targetIndex.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToDelete = lastShownList.get(targetIndex.getZeroBased());
        model.deletePerson(personToDelete);
        return new CommandResult(String.format(MESSAGE_DELETE_PERSON_SUCCESS, Messages.format(personToDelete)));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof DeleteCommand otherDeleteCommand)) {
            return false;
        }

        return Objects.equals(targetIndex, otherDeleteCommand.targetIndex)
                && Objects.equals(nameSearch, otherDeleteCommand.nameSearch);
    }

    @Override
    public String toString() {
        if (nameSearch != null) {
            return new ToStringBuilder(this).add("nameSearch", nameSearch).toString();
        }
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
