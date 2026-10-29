package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.UnfavCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a displayed index for removing favourite status from a contact.
 */
public class UnfavCommandParser implements Parser<UnfavCommand> {
    @Override
    public UnfavCommand parse(String args) throws ParseException {
        try {
            return new UnfavCommand(ParserUtil.parseIndex(args));
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnfavCommand.MESSAGE_USAGE), pe);
        }
    }
}
