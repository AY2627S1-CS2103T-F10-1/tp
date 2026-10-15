package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FIND_NAME;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameEqualsPredicate;

/**
 * Parses input arguments and creates a new DeleteCommand object
 */
public class DeleteCommandParser implements Parser<DeleteCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the DeleteCommand
     * and returns a DeleteCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public DeleteCommand parse(String args) throws ParseException {
        try {
            String trimmedArgs = args.trim();
            // Signed integers must remain index inputs even when they are invalid or overflow.
            if (!trimmedArgs.isEmpty() && !trimmedArgs.matches("[+-]?[0-9]+")) {
                return new DeleteCommand(parseName(trimmedArgs));
            }
            Index index = ParserUtil.parseIndex(args);
            return new DeleteCommand(index);
        } catch (ParseException pe) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE), pe);
        }
    }

    /**
     * Parses {@code args} of the form {@code /name NAME} into a {@code NameEqualsPredicate}.
     *
     * @throws ParseException if the {@code /name} prefix is missing, the name is empty or there is text before it.
     */
    private static NameEqualsPredicate parseName(String args) throws ParseException {
        // The tokenizer only recognises a prefix that follows whitespace, so restore the leading space.
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(" " + args, PREFIX_FIND_NAME);
        if (!argMultimap.getPreamble().isEmpty()
                || argMultimap.getValue(PREFIX_FIND_NAME).isEmpty()
                || argMultimap.getValue(PREFIX_FIND_NAME).get().isEmpty()) {
            throw new ParseException(MESSAGE_INVALID_COMMAND_FORMAT);
        }
        return new NameEqualsPredicate(argMultimap.getValue(PREFIX_FIND_NAME).get());
    }

}
