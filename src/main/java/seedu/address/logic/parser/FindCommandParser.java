package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import java.util.Optional;
import java.util.stream.Stream;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameFuzzyMatchesPredicate;
import seedu.address.model.person.PersonHasTagPredicate;
import seedu.address.model.person.PhoneEqualsPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * Exactly one of {@code /name}, {@code /tag} and {@code /phone} must be given.
     * Only one search value is allowed; repeated prefixes are rejected.
     * Any non-empty search value is accepted; a value no contact has matches nobody.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(
                args, PREFIX_NAME_COMMAND, PREFIX_TAG_COMMAND, PREFIX_PHONE_COMMAND);

        argMultimap.verifyNoDuplicatePrefixesFor(PREFIX_NAME_COMMAND, PREFIX_TAG_COMMAND, PREFIX_PHONE_COMMAND);

        Optional<String> name = argMultimap.getValue(PREFIX_NAME_COMMAND);
        Optional<String> tag = argMultimap.getValue(PREFIX_TAG_COMMAND);

        Optional<String> phone = argMultimap.getValue(PREFIX_PHONE_COMMAND);
        boolean hasExactlyOnePrefix = Stream.of(name, tag, phone).filter(Optional::isPresent).count() == 1;
        if (!argMultimap.getPreamble().isEmpty() || !hasExactlyOnePrefix
                || name.orElse(tag.orElse(phone.orElse(""))).isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        if (name.isPresent()) {
            NameFuzzyMatchesPredicate namePredicate = new NameFuzzyMatchesPredicate(name.get());
            return new FindCommand(namePredicate, namePredicate.getRankingComparator());
        }
        if (phone.isPresent()) {
            return new FindCommand(new PhoneEqualsPredicate(phone.get()));
        }
        return new FindCommand(new PersonHasTagPredicate(tag.get()));
    }

}
