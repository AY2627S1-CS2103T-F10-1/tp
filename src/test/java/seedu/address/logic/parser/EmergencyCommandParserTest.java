package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EmergencyCommand;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

public class EmergencyCommandParserTest {
    private final EmergencyCommandParser parser = new EmergencyCommandParser();

    @Test
    public void parse_validFields_normalizesWhitespaceAndAcceptsAnyOrder() {
        assertParseSuccess(parser, " 1 /relation  Family   friend \t/phone +65 9123 4567 /name Bob   Choo ",
                command("Family friend"));
        assertParseSuccess(parser, "1 /name Bob Choo /phone +65 9123 4567", command(""));
        assertParseSuccess(parser, "1 /name Bob Choo /phone +65 9123 4567 /relation", command(""));
        assertParseSuccess(parser, "1 /name Bob Choo /phone +65 9123 4567 /relation " + "a".repeat(50),
                command("a".repeat(50)));
    }

    @Test
    public void parse_invalidFormat_failure() {
        String usage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EmergencyCommand.MESSAGE_USAGE);
        for (String input : new String[]{"", "0 /name Bob /phone 91234567", "1 /name Bob", "1 /phone 91234567",
            "1 /name Bob /phone 91234567 /foo value", "1 /name Bob /phone 91234567 /clear",
            "Bob /name Bob /phone 91234567", "1 /name Bob /PHONE 91234567",
            "-1 /name Bob /phone 91234567", "999999999999999999999 /name Bob /phone 91234567"}) {
            assertParseFailure(parser, input, usage);
        }
    }

    @Test
    public void parse_invalidDetails_usesSharedValidation() {
        assertParseFailure(parser, "1 /name /phone 91234567", Name.MESSAGE_EMPTY);
        assertParseFailure(parser, "1 /name Bob /phone", Phone.MESSAGE_EMPTY);
        assertParseFailure(parser, "1 /name B@b /phone 91234567", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "1 /name Bob /phone abc", Phone.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, "1 /name " + "a".repeat(101) + " /phone 91234567", Name.MESSAGE_TOO_LONG);
        assertParseFailure(parser, "1 /name Bob /phone " + "1".repeat(21), Phone.MESSAGE_TOO_LONG);
        assertParseFailure(parser, "1 /name Bob /phone 91234567 /relation " + "a".repeat(51),
                EmergencyContact.MESSAGE_RELATION_TOO_LONG);
    }

    @Test
    public void parse_repeatedFields_failure() {
        for (Prefix prefix : new Prefix[]{PREFIX_NAME_COMMAND, PREFIX_PHONE_COMMAND,
            new Prefix("/relation", true)}) {
            assertParseFailure(parser, "1 /name Bob /phone 91234567 /relation Brother " + prefix + " value",
                    Messages.getErrorMessageForDuplicatePrefixes(prefix));
        }
    }

    private EmergencyCommand command(String relation) {
        return new EmergencyCommand(INDEX_FIRST_PERSON,
                new EmergencyContact(new Name("Bob Choo"), new Phone("+65 9123 4567"), relation));
    }
}
