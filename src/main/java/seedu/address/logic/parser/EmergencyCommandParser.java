package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.EmergencyCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/** Parses emergency assignment or removal, reusing the name and phone rules from add and edit. */
public class EmergencyCommandParser implements Parser<EmergencyCommand> {
    private static final Prefix NAME = PREFIX_NAME_COMMAND;
    private static final Prefix PHONE = PREFIX_PHONE_COMMAND;
    private static final Prefix RELATION = new Prefix("/relation", true);
    private static final Prefix CLEAR = new Prefix("/clear", true);

    @Override
    public EmergencyCommand parse(String args) throws ParseException {
        ArgumentMultimap arguments = ParserUtil.parseIndexedArguments(
                args, EmergencyCommand.MESSAGE_USAGE, NAME, PHONE, RELATION, CLEAR);
        arguments.verifyNoDuplicatePrefixesFor(NAME, PHONE, RELATION, CLEAR);
        Index index = ParserUtil.parseIndex(arguments.getPreamble());
        if (arguments.getValue(CLEAR).isPresent()) {
            boolean hasAssignmentFields = arguments.getValue(NAME).isPresent()
                    || arguments.getValue(PHONE).isPresent() || arguments.getValue(RELATION).isPresent();
            if (hasAssignmentFields || !arguments.getValue(CLEAR).orElseThrow().isEmpty()) {
                throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmergencyCommand.MESSAGE_USAGE));
            }
            return new EmergencyCommand(index);
        }
        if (arguments.getValue(NAME).isEmpty() || arguments.getValue(PHONE).isEmpty()) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmergencyCommand.MESSAGE_USAGE));
        }
        Name name = ParserUtil.parseName(arguments.getValue(NAME).orElseThrow());
        Phone phone = ParserUtil.parsePhone(arguments.getValue(PHONE).orElseThrow());
        String relation = arguments.getValue(RELATION).orElse("");
        if (relation.length() > EmergencyContact.MAX_RELATION_LENGTH) {
            throw new ParseException(EmergencyContact.MESSAGE_RELATION_TOO_LONG);
        }
        return new EmergencyCommand(index, new EmergencyContact(name, phone, relation));
    }
}
