package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.logic.commands.ListEmergencyCommand;

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

    @Test
    public void parse_emergKeyword_returnsListEmergencyCommand() throws Exception {
        assertInstanceOf(ListEmergencyCommand.class, parser.parse("emerg"));
        assertInstanceOf(ListEmergencyCommand.class, parser.parse("  EMERG  "));
        assertInstanceOf(ListEmergencyCommand.class, parser.parse("\tEmErG\t"));
    }

    @Test
    public void parse_emergWithExtraArguments_throwsParseException() {
        assertParseFailure(parser, "emerg friend", ListEmergencyCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "EMERG 1", ListEmergencyCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "emerg\temerg", ListEmergencyCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "emerg /filterByTag staff", ListEmergencyCommand.MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unknownKeyword_throwsParseException() {
        assertParseFailure(parser, "emergency", ListCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "emergfriend", ListCommand.MESSAGE_INVALID_FORMAT);
    }
}
