package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;

import java.util.Comparator;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/** Lists active contacts in ascending, case-sensitive lexicographical order by full name. */
public class ListSortedCommand extends Command {
    public static final String SORTED_KEYWORD = "sorted";
    public static final String MESSAGE_USAGE = ListCommand.COMMAND_WORD + " " + SORTED_KEYWORD;
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Listed active contacts sorted by name.";

    private static final Comparator<Person> BY_NAME = Comparator.comparing(person -> person.getName().fullName);

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS);
        model.sortFilteredPersonList(BY_NAME);
        return new CommandResult(model.getFilteredPersonList().isEmpty()
                ? ListCommand.MESSAGE_EMPTY : MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof ListSortedCommand;
    }

    @Override
    public int hashCode() {
        return ListSortedCommand.class.hashCode();
    }
}
