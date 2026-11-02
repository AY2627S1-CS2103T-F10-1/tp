package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.UnarchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonHasTagPredicate;

/** Parses either a displayed index or a single tag selector for restoring archived contacts. */
public class UnarchiveCommandParser implements Parser<UnarchiveCommand> {
    @Override
    public UnarchiveCommand parse(String args) throws ParseException {
        try {
            if (ParserUtil.hasUnknownPrefix(args, PREFIX_TAG_COMMAND)) {
                throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
            }
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_TAG_COMMAND);
            arguments.verifyNoDuplicatePrefixesFor(PREFIX_TAG_COMMAND);
            if (arguments.getValue(PREFIX_TAG_COMMAND).isPresent()) {
                String tag = StringUtil.collapseWhitespace(arguments.getValue(PREFIX_TAG_COMMAND).orElseThrow());
                if (!arguments.getPreamble().isEmpty() || tag.isEmpty()) {
                    throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
                }
                return new UnarchiveCommand(new PersonHasTagPredicate(tag));
            }
            return new UnarchiveCommand(ParserUtil.parseIndex(args));
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnarchiveCommand.MESSAGE_USAGE), pe);
        }
    }
}
