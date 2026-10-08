package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.Objects;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.Model;
import seedu.address.model.person.PersonHasTagPredicate;

/**
 * Lists all persons in the address book to the user, or only those with a given tag.
 */
public class ListCommand extends Command {

    public static final String COMMAND_WORD = "list";

    public static final String MESSAGE_SUCCESS = "Listed all persons.";
    public static final String MESSAGE_FILTER_SUCCESS = "%1$d contact(s) tagged %2$s listed.";
    public static final String MESSAGE_NO_CONTACTS_WITH_TAG = "No contacts found with this tag.";

    /** Tag to filter by, or null to list every person. */
    private final PersonHasTagPredicate tagFilter;

    /**
     * Creates a ListCommand that lists every person.
     */
    public ListCommand() {
        this.tagFilter = null;
    }

    /**
     * Creates a ListCommand that lists only persons matching {@code tagFilter}.
     */
    public ListCommand(PersonHasTagPredicate tagFilter) {
        requireNonNull(tagFilter);
        this.tagFilter = tagFilter;
    }

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        if (tagFilter == null) {
            model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
            return new CommandResult(MESSAGE_SUCCESS);
        }

        model.updateFilteredPersonList(tagFilter);
        int matchCount = model.getFilteredPersonList().size();
        if (matchCount == 0) {
            return new CommandResult(MESSAGE_NO_CONTACTS_WITH_TAG);
        }
        return new CommandResult(String.format(MESSAGE_FILTER_SUCCESS, matchCount, tagFilter.getTagName()));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ListCommand otherListCommand)) {
            return false;
        }

        return Objects.equals(tagFilter, otherListCommand.tagFilter);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("tagFilter", tagFilter)
                .toString();
    }
}
