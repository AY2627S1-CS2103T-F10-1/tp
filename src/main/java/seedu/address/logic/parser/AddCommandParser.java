package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;

/** Parses the add command: the name, followed by every other {@link PersonField} in any order. */
public class AddCommandParser implements Parser<AddCommand> {
    private static final String PREFIX_MARKER = "/";
    private static final String WORD_SEPARATOR = " ";

    /** The name is the text before the first prefix, so every other field is given with a prefix. */
    private static final List<PersonField<?>> PREFIXED_FIELDS = PersonField.values().stream()
            .filter(field -> field != PersonField.NAME)
            .collect(Collectors.toUnmodifiableList());
    private static final Prefix[] PREFIXES = PREFIXED_FIELDS.stream()
            .map(PersonField::getPrefix)
            .toArray(Prefix[]::new);
    private static final Set<String> KNOWN_PREFIXES = Stream.of(PREFIXES)
            .map(Prefix::getPrefix)
            .collect(Collectors.toUnmodifiableSet());

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
     * Checks that each required field is given exactly once, no other field is repeated unless it may be,
     * and no unknown prefix is used.
     */
    private static void verifyFormat(String normalized, ArgumentMultimap argMultimap) throws ParseException {
        boolean isFieldMisused = PREFIXED_FIELDS.stream().anyMatch(field -> {
            int count = argMultimap.getAllValues(field.getPrefix()).size();
            return (field.isRequired() && count != 1) || (!field.isRepeatable() && count > 1);
        });
        if (isFieldMisused || hasUnknownPrefix(normalized)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
    }

    private static boolean hasUnknownPrefix(String normalized) {
        return Stream.of(normalized.trim().split(WORD_SEPARATOR))
                .anyMatch(word -> word.startsWith(PREFIX_MARKER) && !KNOWN_PREFIXES.contains(word));
    }
}
