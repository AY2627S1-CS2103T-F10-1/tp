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
 * Deletes a person identified using its displayed index from the address book.
 */
public class DeleteCommand extends Command {

    public static final String COMMAND_WORD = "delete";

    // TODO(delete-by-name): Extend usage to describe delete INDEX and delete KEYWORD [MORE_KEYWORDS].
    // Add messages for zero matches and multiple matches, explicitly stating that nothing was deleted.
    // For multiple matches, include the count and suggest a displayed index or a unique name keyword.
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Deletes the person identified by the index number used in the displayed person list.\n"
            + "Parameters: INDEX (must be a positive integer)\n"
            + "Example: " + COMMAND_WORD + " 1";

    public static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted person: %1$s";

    private final Index targetIndex;

    // TODO(delete-by-name): Add a constructor/state for the FindCommand prepared by the parser.
    // Keep index-based construction; update equals and toString to account for the two target forms.
    public DeleteCommand(Index targetIndex) {
        this.targetIndex = targetIndex;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        // TODO(delete-by-name): For a name target, execute the prepared FindCommand against this model.
        // This searches all contacts, replacing any previous filter with the matching contacts.
        // If exactly one result remains, execute new DeleteCommand(Index.fromOneBased(1)) and
        // return its result. Otherwise return the zero/multiple-match message without deleting.
        // Leave matches displayed with their existing indexes so the next delete INDEX works.
        // A later delete NAME searches all contacts again. Do not change find's OR matching:
        // Alice Tan still matches Alice Lee; users need a unique keyword or a displayed index.
        // Keep the resulting search filter, including the empty list after deleting a unique match.
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

        return targetIndex.equals(otherDeleteCommand.targetIndex);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetIndex", targetIndex)
                .toString();
    }
}
