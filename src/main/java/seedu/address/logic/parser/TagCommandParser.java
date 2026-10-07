package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.commands.TagCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.tag.Tag;

/**
 * Parses user input of the form {@code <contact identifier> /tag <tag>} into a {@code TagCommand}.
 * Unlike other commands, the input has no leading command word; it is recognized by the {@code /tag} keyword.
 */
public class TagCommandParser implements Parser<TagCommand> {

    /** Matches the keyword as a standalone word, so names or tags merely containing "/tag" are not affected. */
    private static final Pattern KEYWORD_PATTERN =
            Pattern.compile("(^|\\s)" + Pattern.quote(TagCommand.COMMAND_KEYWORD) + "(\\s|$)");

    /** Splits the input at the first keyword; any later "/tag" becomes part of the tag and is rejected there. */
    private static final Pattern TAG_COMMAND_FORMAT = Pattern.compile(
            "(?<identifier>.*?)\\s+" + Pattern.quote(TagCommand.COMMAND_KEYWORD) + "(?<tag>(\\s.*)?)");

    /**
     * Returns true if {@code userInput} contains the {@code /tag} keyword and should be parsed by this parser.
     */
    public static boolean isTagCommand(String userInput) {
        requireNonNull(userInput);
        return KEYWORD_PATTERN.matcher(userInput).find();
    }

    /**
     * Parses the given {@code String} of user input in the context of the TagCommand
     * and returns a TagCommand object for execution.
     *
     * @throws ParseException if the identifier or tag value is missing, or the tag is invalid.
     */
    @Override
    public TagCommand parse(String userInput) throws ParseException {
        requireNonNull(userInput);
        Matcher matcher = TAG_COMMAND_FORMAT.matcher(userInput.trim());
        if (!matcher.matches()) {
            throw new ParseException(TagCommand.MESSAGE_INVALID_FORMAT);
        }

        String identifier = matcher.group("identifier");
        String tagValue = matcher.group("tag").trim();
        assert !identifier.isBlank() : "Trimmed input cannot start with whitespace, so the identifier is non-blank";
        if (tagValue.isEmpty()) {
            throw new ParseException(TagCommand.MESSAGE_INVALID_FORMAT);
        }

        Tag tag = ParserUtil.parseTag(tagValue);
        return new TagCommand(identifier, tag);
    }
}
