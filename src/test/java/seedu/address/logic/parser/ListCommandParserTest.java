package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_noArguments_returnsListCommand() throws Exception {
        assertInstanceOf(ListCommand.class, parser.parse(""));
        assertInstanceOf(ListCommand.class, parser.parse(" \t "));
    }

    @Test
    public void parse_extraArguments_throwsParseException() {
        assertParseFailure(parser, " abc", ListCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1", ListCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /unknown value", ListCommand.MESSAGE_INVALID_FORMAT);
    }
}
