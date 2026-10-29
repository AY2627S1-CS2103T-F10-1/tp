package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;

/** Parses the add command: the name, followed by every other {@link PersonField} in any order. */
public class AddCommandParser implements Parser<AddCommand> {
    private static final String WORD_SEPARATOR = " ";

    /** The name is the text before the first prefix, so every other field is given with a prefix. */
    private static final List<PersonField<?>> PREFIXED_FIELDS = PersonField.values().stream()
            .filter(field -> field != PersonField.NAME)
            .collect(Collectors.toUnmodifiableList());
    private static final Prefix[] PREFIXES = PREFIXED_FIELDS.stream()
            .map(PersonField::getPrefix)
            .toArray(Prefix[]::new);
    private static final Prefix[] SINGLE_VALUED_PREFIXES = PREFIXED_FIELDS.stream()
            .filter(field -> !field.isRepeatable())
            .map(PersonField::getPrefix)
            .toArray(Prefix[]::new);

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * The fields other than the name may follow the name in any order.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    @Override
    public AddCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String normalized = WORD_SEPARATOR + StringUtil.collapseWhitespace(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalized, PREFIXES);
        verifyFormat(normalized, argMultimap);
        argMultimap.verifyNoDuplicatePrefixesFor(SINGLE_VALUED_PREFIXES);

        PersonFieldValues details = new PersonFieldValues();
        details.set(PersonField.NAME, PersonField.NAME.parse(List.of(argMultimap.getPreamble())));
        for (PersonField<?> field : PREFIXED_FIELDS) {
            details.setIfGiven(field, argMultimap, false);
            details.setDefaultIfAbsent(field);
        }

        // Email and address remain legacy model fields until the edit workflow is redesigned.
        return new AddCommand(details.toPerson(new Email(""), new Address("")));
    }

    /**
     * Checks that each required field is given and no unknown prefix is used.
     * Fields that are given more than once are checked afterwards.
     */
    private static void verifyFormat(String normalized, ArgumentMultimap argMultimap) throws ParseException {
        boolean isRequiredFieldMissing = PREFIXED_FIELDS.stream().anyMatch(field ->
                field.isRequired() && argMultimap.getAllValues(field.getPrefix()).isEmpty());
        if (isRequiredFieldMissing) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
        ParserUtil.verifyNoUnknownPrefix(normalized, argMultimap, AddCommand.MESSAGE_USAGE, PREFIXES);
    }
}
