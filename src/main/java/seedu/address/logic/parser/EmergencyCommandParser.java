package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.address.logic.commands.EmergencyCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/** Parses an emergency assignment using the same name and phone rules as add and edit. */
public class EmergencyCommandParser implements Parser<EmergencyCommand> {
    private static final Prefix NAME = PersonField.NAME.getPrefix();
    private static final Prefix PHONE = PersonField.PHONE.getPrefix();
    private static final Prefix RELATION = new Prefix("/relation", true);

    @Override
    public EmergencyCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = ParserUtil.parseIndexedArguments(
                args, EmergencyCommand.MESSAGE_USAGE, NAME, PHONE, RELATION);
        arguments.verifyNoDuplicatePrefixesFor(NAME, PHONE, RELATION);
        if (arguments.getValue(NAME).isEmpty() || arguments.getValue(PHONE).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmergencyCommand.MESSAGE_USAGE));
        }
        Name name = PersonField.NAME.parse(arguments.getAllValues(NAME));
        Phone phone = PersonField.PHONE.parse(arguments.getAllValues(PHONE));
        String relation = arguments.getValue(RELATION).orElse("");
        if (relation.length() > EmergencyContact.MAX_RELATION_LENGTH) {
            throw new ParseException(EmergencyContact.MESSAGE_RELATION_TOO_LONG);
        }
        return new EmergencyCommand(ParserUtil.parseIndex(arguments.getPreamble()),
                new EmergencyContact(name, phone, relation));
    }
}
