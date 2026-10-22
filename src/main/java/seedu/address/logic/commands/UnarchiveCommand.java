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
import seedu.address.model.person.PersonHasTagPredicate;

/** Restores a displayed contact or all archived contacts matching a tag. */
public class UnarchiveCommand extends Command {
    public static final String COMMAND_WORD = "unarchive";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Restores a contact by displayed index or all archived contacts with a tag.\n"
            + "Parameters: INDEX (positive integer) or /tag TAG\n"
            + "Examples: " + COMMAND_WORD + " 1, " + COMMAND_WORD + " /tag friends";
    public static final String MESSAGE_SUCCESS = "Unarchived 1 contact.";
    public static final String MESSAGE_ALREADY_ACTIVE = "Contact is already active. No contacts unarchived.";

    public static final String MESSAGE_NO_MATCHES =
            "No archived contacts found with that tag. No contacts unarchived.";

    private final Index targetIndex;
    private final PersonHasTagPredicate tagSearch;

    /** Selects a contact by its displayed index. */
    public UnarchiveCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
        this.tagSearch = null;
    }

    /** Selects archived contacts across the whole address book using the existing tag matching rules. */
    public UnarchiveCommand(PersonHasTagPredicate tagSearch) {
        this.targetIndex = null;
        this.tagSearch = requireNonNull(tagSearch);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (tagSearch != null) {
            return unarchiveByTag(model);
        }
        List<Person> displayed = model.getFilteredPersonList();
        if (targetIndex.getZeroBased() >= displayed.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person target = displayed.get(targetIndex.getZeroBased());
        if (!target.isArchived()) {
            return new CommandResult(MESSAGE_ALREADY_ACTIVE);
        }

        // Resolve the target before replacement changes the observable filtered list.
        Person restored = target.withArchive(false);
        model.setPerson(target, restored);
        return new CommandResult(MESSAGE_SUCCESS);
    }

    private CommandResult unarchiveByTag(Model model) {
        List<Person> targets = model.getAddressBook().getPersonList().stream()
                .filter(Person::isArchived)
                .filter(tagSearch)
                .toList();
        if (targets.isEmpty()) {
            return new CommandResult(MESSAGE_NO_MATCHES);
        }

        List<Person> replacements = targets.stream().map(person -> person.withArchive(false)).toList();
        for (int i = 0; i < targets.size(); i++) {
            model.setPerson(targets.get(i), replacements.get(i));
        }
        return new CommandResult(targets.size() == 1 ? MESSAGE_SUCCESS
                : "Unarchived " + targets.size() + " contacts.");
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof UnarchiveCommand otherCommand
                && Objects.equals(targetIndex, otherCommand.targetIndex)
                && Objects.equals(tagSearch, otherCommand.tagSearch);
    }

    @Override
    public int hashCode() {
        return Objects.hash(targetIndex, tagSearch);
    }

    @Override
    public String toString() {
        if (tagSearch != null) {
            return new ToStringBuilder(this).add("tagSearch", tagSearch).toString();
        }
        return new ToStringBuilder(this).add("targetIndex", targetIndex).toString();
    }
}
