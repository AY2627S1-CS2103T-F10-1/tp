package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FIND_NAME;
import static seedu.address.logic.parser.CliSyntax.PREFIX_FIND_TAG;

import java.util.Optional;

import seedu.address.logic.commands.FindCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.NameEqualsPredicate;
import seedu.address.model.person.PersonHasTagPredicate;

/**
 * Parses input arguments and creates a new FindCommand object
 */
public class FindCommandParser implements Parser<FindCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the FindCommand
     * and returns a FindCommand object for execution.
     * Exactly one of the {@code /name} and {@code /tag} prefixes must be given.
     * Any non-empty tag value is accepted, since a value no contact has simply matches nobody.
     *
     * @throws ParseException if the user input does not conform to the expected format
     */
    public FindCommand parse(String args) throws ParseException {
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(args, PREFIX_FIND_NAME, PREFIX_FIND_TAG);

        Optional<String> name = argMultimap.getValue(PREFIX_FIND_NAME);
        Optional<String> tag = argMultimap.getValue(PREFIX_FIND_TAG);

        boolean hasExactlyOnePrefix = name.isPresent() != tag.isPresent();
        if (!argMultimap.getPreamble().isEmpty() || !hasExactlyOnePrefix
                || name.orElse(tag.orElse("")).isEmpty()) {
            throw new ParseException(
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        }

        if (name.isPresent()) {
            return new FindCommand(new NameEqualsPredicate(name.get()));
        }
        return new FindCommand(new PersonHasTagPredicate(tag.get()));
    }

}
