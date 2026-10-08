package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.KEYWORD_FILTER;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonHasTagPredicate;
import seedu.address.model.tag.Tag;

/**
 * Parses the {@code /filter TAG} arguments shared by output commands, such as {@code list}.
 * To let another output command filter by tag, call {@link #parse(String)} from its parser and pass the
 * resulting predicate to {@code Model#updateFilteredPersonList}.
 */
public class TagFilterParser {

    public static final String MESSAGE_INVALID_FORMAT =
            "Invalid command format. Usage: <output command> " + KEYWORD_FILTER + " <tagName>";

    private static final String GROUP_TAG = "tag";

    private static final Pattern FILTER_FORMAT =
            Pattern.compile(Pattern.quote(KEYWORD_FILTER) + "(?<" + GROUP_TAG + ">(\\s.*)?)");

    private TagFilterParser() {} // prevents instantiation

    /**
     * Parses {@code args} of the form {@code /filter TAG} into a {@code PersonHasTagPredicate}.
     * Any non-empty tag value is accepted, since a value no contact has simply matches nobody.
     *
     * @param args The command arguments, with or without surrounding whitespace.
     * @return A predicate matching persons who have the given tag.
     * @throws ParseException if the {@code /filter} keyword is missing or the tag value is empty.
     */
    public static PersonHasTagPredicate parse(String args) throws ParseException {
        requireNonNull(args);
        Matcher matcher = FILTER_FORMAT.matcher(args.trim());
        if (!matcher.matches()) {
            throw new ParseException(MESSAGE_INVALID_FORMAT);
        }

        String tagValue = matcher.group(GROUP_TAG).trim();
        if (tagValue.isEmpty()) {
            throw new ParseException(Tag.MESSAGE_EMPTY);
        }
        return new PersonHasTagPredicate(tagValue);
    }
}
