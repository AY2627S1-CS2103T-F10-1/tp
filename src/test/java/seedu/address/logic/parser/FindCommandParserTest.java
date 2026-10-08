package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FindCommand;
import seedu.address.model.person.NameEqualsPredicate;

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
    }

    @Test
    public void parse_validArgs_returnsFindCommand() {
        // full name is kept as a single string, not split into keywords
        FindCommand expectedFindCommand = new FindCommand(new NameEqualsPredicate("Alice Bob"));
        assertParseSuccess(parser, " /name Alice Bob", expectedFindCommand);

        // leading and trailing whitespaces around the name are trimmed
        assertParseSuccess(parser, " /name   Alice Bob  \t", expectedFindCommand);

        // case is preserved
        assertParseSuccess(parser, " /name alice bob", new FindCommand(new NameEqualsPredicate("alice bob")));
    }

}
