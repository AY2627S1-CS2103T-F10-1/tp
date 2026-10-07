package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;

/**
 * Parses input arguments and creates a new AddCommand object
 */
public class AddCommandParser implements Parser<AddCommand> {

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * @throws ParseException if the user input does not conform to the expected format
     */
    public AddCommand parse(String args) throws ParseException {
        int phonePrefixIndex = args.indexOf(PREFIX_PHONE_COMMAND.getPrefix());
        if (phonePrefixIndex <= 0) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        String nameValue = args.substring(0, phonePrefixIndex).trim();
        String phoneValue = args.substring(phonePrefixIndex + PREFIX_PHONE_COMMAND.getPrefix().length()).trim();
        if (nameValue.isEmpty() || phoneValue.isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }

        Name name = ParserUtil.parseName(nameValue);
        Phone phone = ParserUtil.parsePhone(phoneValue);

        // Email and address remain legacy model fields until the edit workflow is redesigned.
        Person person = new Person(name, phone, new Email(""), new Address(""), java.util.Set.of());

        return new AddCommand(person);
    }

}
