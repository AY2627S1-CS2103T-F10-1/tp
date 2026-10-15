package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeleteCommand;
import seedu.address.model.person.NameEqualsPredicate;

/**
 * Tests index and name parsing for deletion.
 */
public class DeleteCommandParserTest {

    private DeleteCommandParser parser = new DeleteCommandParser();

    @Test
    public void parse_validArgs_returnsDeleteCommand() {
        assertParseSuccess(parser, "1", new DeleteCommand(INDEX_FIRST_PERSON));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        String[] invalidInputs = {"", "   ", "0", "-1", "+1", "999999999999999999999",
            "Alice", "/name", "/name   ", "Alice /name Tan", "/tag friends"};
        for (String input : invalidInputs) {
            assertParseFailure(parser, input,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE));
        }
    }
    @Test
    public void parse_nameArgs_returnsNameDeleteCommand() {
        assertParseSuccess(parser, "/name Alice", new DeleteCommand(new NameEqualsPredicate("Alice")));

        // surrounding whitespace is trimmed, case is preserved
        assertParseSuccess(parser, " /name   Alice Tan  ", new DeleteCommand(new NameEqualsPredicate("Alice Tan")));
        assertParseSuccess(parser, "/name alice tan", new DeleteCommand(new NameEqualsPredicate("alice tan")));
    }
}
