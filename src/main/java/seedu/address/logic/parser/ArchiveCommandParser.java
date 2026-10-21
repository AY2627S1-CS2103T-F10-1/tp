package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import seedu.address.logic.commands.ArchiveCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonHasTagPredicate;

/** Parses either a displayed index or a single tag selector for archiving. */
public class ArchiveCommandParser implements Parser<ArchiveCommand> {
    @Override
    public ArchiveCommand parse(String args) throws ParseException {
        try {
            if (ParserUtil.hasUnknownPrefix(args, PREFIX_TAG_COMMAND)) {
                throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
            }
            ArgumentMultimap arguments = ArgumentTokenizer.tokenize(" " + args.trim(), PREFIX_TAG_COMMAND);
            arguments.verifyNoDuplicatePrefixesFor(PREFIX_TAG_COMMAND);
            if (arguments.getValue(PREFIX_TAG_COMMAND).isPresent()) {
                String tag = arguments.getValue(PREFIX_TAG_COMMAND).orElseThrow();
                if (!arguments.getPreamble().isEmpty() || tag.isEmpty()) {
                    throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
                }
                return new ArchiveCommand(new PersonHasTagPredicate(tag));
            }
            return new ArchiveCommand(ParserUtil.parseIndex(args));
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, ArchiveCommand.MESSAGE_USAGE), pe);
        }
    }
}
