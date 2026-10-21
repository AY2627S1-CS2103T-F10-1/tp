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

/** Archives a displayed contact or all active contacts matching a tag. */
public class ArchiveCommand extends Command {
    public static final String COMMAND_WORD = "archive";
    public static final String MESSAGE_USAGE = COMMAND_WORD
            + ": Archives a contact by displayed index or all active contacts with a tag.\n"
            + "Parameters: INDEX (positive integer) or /tag TAG\n"
            + "Examples: " + COMMAND_WORD + " 1, " + COMMAND_WORD + " /tag friends";
    public static final String MESSAGE_SUCCESS = "Archived 1 contact.";
    public static final String MESSAGE_ALREADY_ARCHIVED = "Contact is already archived. No contacts archived.";

    public static final String MESSAGE_NO_MATCHES =
            "No active contacts found with that tag. No contacts archived.";

    private final Index targetIndex;
    private final PersonHasTagPredicate tagSearch;

    /** Selects a contact by its displayed index. */
    public ArchiveCommand(Index targetIndex) {
        this.targetIndex = requireNonNull(targetIndex);
        this.tagSearch = null;
    }

    /** Selects active contacts across the whole address book using the existing tag matching rules. */
    public ArchiveCommand(PersonHasTagPredicate tagSearch) {
        this.targetIndex = null;
        this.tagSearch = requireNonNull(tagSearch);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        if (tagSearch != null) {
            return archiveByTag(model);
        }
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

    private CommandResult archiveByTag(Model model) {
        List<Person> targets = model.getAddressBook().getPersonList().stream()
                .filter(person -> !person.isArchived())
                .filter(tagSearch)
                .toList();
        if (targets.isEmpty()) {
            return new CommandResult(MESSAGE_NO_MATCHES);
        }

        List<Person> replacements = targets.stream().map(person -> person.withArchive(true)).toList();
        for (int i = 0; i < targets.size(); i++) {
            model.setPerson(targets.get(i), replacements.get(i));
        }
        return new CommandResult(targets.size() == 1 ? MESSAGE_SUCCESS
                : "Archived " + targets.size() + " contacts.");
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ArchiveCommand otherCommand
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
