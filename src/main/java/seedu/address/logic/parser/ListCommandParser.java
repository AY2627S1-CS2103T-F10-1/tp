package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.Command;
import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListEmergencyCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses arguments for listing all contacts or emergency contacts.
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

        if (ListEmergencyCommand.EMERGENCY_KEYWORD.equals(trimmedArgs)) {
            return new ListEmergencyCommand();
        }

        String firstArgument = trimmedArgs.split("\\s+", 2)[0];
        if (ListEmergencyCommand.EMERGENCY_KEYWORD.equals(firstArgument)) {
            throw new ParseException(ListEmergencyCommand.MESSAGE_INVALID_FORMAT);
        }

        throw new ParseException(ListCommand.MESSAGE_INVALID_FORMAT);
    }
}
