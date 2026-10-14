package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    private static final String TAG_SEPARATOR = ",";

    /** Makes {@code String#split} keep trailing empty entries, so a trailing separator is reported as an empty tag. */
    private static final int SPLIT_KEEP_TRAILING_EMPTY = -1;

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        requireNonNull(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_PHONE_COMMAND, PREFIX_TAG_COMMAND);
        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_PHONE_COMMAND, PREFIX_TAG_COMMAND);

        String nameValue = argMultimap.getPreamble();
        String phoneValue = argMultimap.getValue(PREFIX_PHONE_COMMAND).orElse("");
        if (nameValue.isEmpty() || phoneValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        Name name = ParserUtil.parseName(nameValue);
        Phone phone = ParserUtil.parsePhone(phoneValue);
        Set<Tag> tags = parseTagList(argMultimap.getValue(PREFIX_TAG_COMMAND));

        // Email and address remain legacy model fields until the edit workflow is redesigned.
        Person person = new Person(name, phone, new Email(""), new Address(""), tags);

        return new AddCommand(person);
    }

    /**
     * Parses the comma-separated value of {@code /tag} into a set of tags, or no tags if {@code /tag} was not given.
     *
     * @throws ParseException if any tag is invalid or there are more than {@link Person#MAX_TAGS} tags.
     */
    private static Set<Tag> parseTagList(Optional<String> tagList) throws ParseException {
        if (tagList.isEmpty()) {
            return Set.of();
        }
        return ParserUtil.parseTags(Arrays.asList(tagList.get().split(TAG_SEPARATOR, SPLIT_KEEP_TRAILING_EMPTY)));
    }

}
