package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses input arguments and creates a new ListCommand object.
 */
public class ListCommandParser implements Parser<ListCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the ListCommand
     * and returns a ListCommand object for execution.
     * No arguments lists every person; {@code /filter TAG} lists only persons with that tag.
     *
     * @throws ParseException if the arguments are not empty and not a valid {@code /filter TAG}.
     */
    @Override
    public ListCommand parse(String args) throws ParseException {
        requireNonNull(args);
        if (args.isBlank()) {
            return new ListCommand();
        }
        return new ListCommand(TagFilterParser.parse(args));
    }
}
