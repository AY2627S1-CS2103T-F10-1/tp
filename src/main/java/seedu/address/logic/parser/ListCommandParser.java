package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ListArchivedCommand;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListEmergencyCommand;
import seedu.address.logic.commands.ListFavouriteCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for listing active, archived, emergency or favourite contacts.
 */
public class ListCommandParser implements Parser<Command> {

    /**
     * Parses the given arguments and returns a list command for execution.
     *
     * @throws ParseException if the arguments do not match a supported list format.
     */
    @Override
    public Command parse(String args) throws ParseException {
        requireNonNull(args);
        String trimmedArgs = args.trim();

        if (trimmedArgs.isEmpty()) {
            return new ListCommand();
        }

        if (ListArchivedCommand.ARCHIVED_KEYWORD.equals(trimmedArgs)) {
            return new ListArchivedCommand();
        }

        if (ListEmergencyCommand.EMERGENCY_KEYWORD.equals(trimmedArgs)) {
            return new ListEmergencyCommand();
        }

        if (ListFavouriteCommand.FAVOURITE_KEYWORD.equals(trimmedArgs)) {
            return new ListFavouriteCommand();
        }

        String firstArgument = trimmedArgs.split("\\s+", 2)[0];
        if (ListArchivedCommand.ARCHIVED_KEYWORD.equals(firstArgument)) {
            throw new ParseException(ListArchivedCommand.MESSAGE_INVALID_FORMAT);
        }
        if (ListEmergencyCommand.EMERGENCY_KEYWORD.equals(firstArgument)) {
            throw new ParseException(ListEmergencyCommand.MESSAGE_INVALID_FORMAT);
        }
        if (ListFavouriteCommand.FAVOURITE_KEYWORD.equals(firstArgument)) {
            throw new ParseException(ListFavouriteCommand.MESSAGE_INVALID_FORMAT);
        }

        throw new ParseException(ListCommand.MESSAGE_INVALID_FORMAT);
    }
}
