package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.ToggleCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new ToggleCommand object.
 */
public class ToggleCommandParser implements Parser<ToggleCommand> {
    public static final String MESSAGE_REPEATED_TAG = "Repeated tag: %1$s. Each tag can only be given once.";

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

        return new ToggleCommand(index, parseTags(tagValues));
    }

    /**
     * Parses each value into a tag, keeping the order in which they were given.
     * Unlike {@code add}, a tag given twice is reported as repeated, since the contact is not what has it twice.
     *
     * @throws ParseException if any tag is invalid or repeated, or there are more than {@link Person#MAX_TAGS} tags.
     */
    private static Set<Tag> parseTags(List<String> tagValues) throws ParseException {
        Set<Tag> tags = new LinkedHashSet<>();
        for (String tagValue : tagValues) {
            Tag tag = ParserUtil.parseTag(tagValue);
            if (!tags.add(tag)) {
                throw new ParseException(String.format(MESSAGE_REPEATED_TAG, tag.tagName));
            }
        }
        if (tags.size() > Person.MAX_TAGS) {
            throw new ParseException(Person.MESSAGE_TAG_LIMIT_REACHED);
        }
        return tags;
    }
}
