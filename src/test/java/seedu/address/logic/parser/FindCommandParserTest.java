package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.getErrorMessageForDuplicatePrefixes;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameFuzzyMatchesPredicate;
import seedu.address.model.person.PersonHasTagPredicate;
import seedu.address.model.person.PhoneEqualsPredicate;

public class FindCommandParserTest {

    private FindCommandParser parser = new FindCommandParser();

    @Test
    public void parse_emptyArg_throwsParseException() {
        assertParseFailure(parser, "     ", String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_missingPrefixOrName_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);

        // no prefix
        assertParseFailure(parser, "Alice Bob", expectedMessage);

        // prefix with no name
        assertParseFailure(parser, " /name", expectedMessage);
        assertParseFailure(parser, " /name   ", expectedMessage);

        // text before prefix
        assertParseFailure(parser, " Alice /name Bob", expectedMessage);

        // prefix /tag with no tag
        assertParseFailure(parser, " /tag", expectedMessage);
        assertParseFailure(parser, " /tag   ", expectedMessage);

        // text before /tag
        assertParseFailure(parser, " Alice /tag friends", expectedMessage);

        // prefix directly followed by the value
        assertParseFailure(parser, " /tagfriends", expectedMessage);
        assertParseFailure(parser, " /tags friends", expectedMessage);
        assertParseFailure(parser, " /nameAlice", expectedMessage);

        // more than one name
        String duplicateNameMessage = getErrorMessageForDuplicatePrefixes(PREFIX_NAME_COMMAND);
        assertParseFailure(parser, " /name Alice /name Bob", duplicateNameMessage);
        assertParseFailure(parser, " /name Alice /name Alice", duplicateNameMessage);

        // more than one tag
        String duplicateTagMessage = getErrorMessageForDuplicatePrefixes(PREFIX_TAG_COMMAND);
        assertParseFailure(parser, " /tag friends /tag family", duplicateTagMessage);
        assertParseFailure(parser, " /tag friends /tag friends", duplicateTagMessage);

        // both prefixes
        assertParseFailure(parser, " /name Alice /tag friends", expectedMessage);
        assertParseFailure(parser, " /tag friends /name Alice", expectedMessage);
    }

    @Test
    public void parse_validTag_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new PersonHasTagPredicate("friends"));
        assertParseSuccess(parser, " /tag friends", expectedFindCommand);

        // leading and trailing whitespaces around the tag are trimmed
        assertParseSuccess(parser, " /tag   friends  \t", expectedFindCommand);

        // multi-word tag
        assertParseSuccess(parser, " /tag best   friends",
                new FindCommand(new PersonHasTagPredicate("best friends")));

        // a value that is not a valid tag is accepted and simply matches nobody
        assertParseSuccess(parser, " /tag fr!ends", new FindCommand(new PersonHasTagPredicate("fr!ends")));

        // case is ignored when comparing tags
        assertParseSuccess(parser, " /tag Friends", new FindCommand(new PersonHasTagPredicate("Friends")));
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        FindCommand expectedFindCommand = new FindCommand(new NameFuzzyMatchesPredicate("Alice Bob"));
        assertParseSuccess(parser, " /name Alice Bob", expectedFindCommand);

        // extra whitespace around and between words is ignored
        assertParseSuccess(parser, " /name   Alice    Bob  \t", expectedFindCommand);

        // case is ignored
        assertParseSuccess(parser, " /name alice bob", expectedFindCommand);
    }

    @Test
    public void parse_phone_returnsFindCommand() {
        assertParseSuccess(parser, " /phone 91234567", new FindCommand(new PhoneEqualsPredicate("91234567")));
        assertParseSuccess(parser, " /phone  +65  9123 4567  ",
                new FindCommand(new PhoneEqualsPredicate("+65 9123 4567")));
        assertParseSuccess(parser, " /phone unknown", new FindCommand(new PhoneEqualsPredicate("unknown")));
    }

    @Test
    public void parse_invalidPhonePrefixes_throwsParseException() {
        String message = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        for (String input : new String[]{" /phone", " /phone   ", " /phone91234567", " text /phone 91234567",
            " /phone 91234567 /name Alice", " /tag friends /phone 91234567",
            " /name Alice /phone 91234567 /tag friends"}) {
            assertParseFailure(parser, input, message);
        }
        assertParseFailure(parser, " /phone 123 /phone 456",
                getErrorMessageForDuplicatePrefixes(CliSyntax.PREFIX_PHONE_COMMAND));
    }

}
