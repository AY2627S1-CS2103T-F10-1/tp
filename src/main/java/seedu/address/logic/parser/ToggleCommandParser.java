package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.ToggleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new ToggleCommand object.
 */
public class ToggleCommandParser implements Parser<ToggleCommand> {
    private static final String WORD_SEPARATOR = " ";

    /**
     * Parses the given {@code String} of arguments in the context of the ToggleCommand
     * and returns a ToggleCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public ToggleCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String normalized = WORD_SEPARATOR + StringUtil.collapseWhitespace(args);
        if (ParserUtil.hasUnknownPrefix(normalized, PREFIX_TAG_COMMAND)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ToggleCommand.MESSAGE_USAGE));
        }
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalized, PREFIX_TAG_COMMAND);

        Index index;
        try {
            index = ParserUtil.parseIndex(argMultimap.getPreamble());
        } catch (ParseException pe) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ToggleCommand.MESSAGE_USAGE), pe);
        }

        List<String> tagValues = argMultimap.getAllValues(PREFIX_TAG_COMMAND);
        if (tagValues.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, ToggleCommand.MESSAGE_USAGE));
        }

        Set<Tag> tags = PersonField.TAGS.parse(tagValues);
        return new ToggleCommand(index, tags);
    }
}
