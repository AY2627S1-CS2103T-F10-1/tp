package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ACTIVE_PERSONS;

import seedu.address.model.Model;
import seedu.address.model.person.Person;

/**
 * Lists active favourite contacts in their stored order.
 */
public class ListFavouriteCommand extends Command {
    public static final String FAVOURITE_KEYWORD = "fav";
    public static final String MESSAGE_USAGE = ListCommand.COMMAND_WORD + " " + FAVOURITE_KEYWORD;
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Listed all favourite contacts.";
    public static final String MESSAGE_EMPTY = "No favourite contacts found.";

    @Override
    public CommandResult execute(Model model) {
        requireNonNull(model);
        model.updateFilteredPersonList(PREDICATE_SHOW_ACTIVE_PERSONS.and(Person::isFavourite));
        return new CommandResult(model.getFilteredPersonList().isEmpty() ? MESSAGE_EMPTY : MESSAGE_SUCCESS);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || other instanceof ListFavouriteCommand;
    }

    @Override
    public int hashCode() {
        return ListFavouriteCommand.class.hashCode();
    }
}
