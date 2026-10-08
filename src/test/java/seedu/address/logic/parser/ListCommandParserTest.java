package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static seedu.address.logic.commands.CommandTestUtil.PREAMBLE_WHITESPACE;
import static seedu.address.logic.parser.CliSyntax.KEYWORD_FILTER;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ListCommand;
import seedu.address.model.person.PersonHasTagPredicate;
import seedu.address.model.tag.Tag;

public class ListCommandParserTest {

    private final ListCommandParser parser = new ListCommandParser();

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> parser.parse(null));
    }

    @Test
    public void parse_noArgs_returnsListAllCommand() {
        assertParseSuccess(parser, "", new ListCommand());
        assertParseSuccess(parser, PREAMBLE_WHITESPACE, new ListCommand());
    }

    @Test
    public void parse_filterArgs_returnsFilteredListCommand() {
        assertParseSuccess(parser, " " + KEYWORD_FILTER + " Resident",
                new ListCommand(new PersonHasTagPredicate("Resident")));
    }

    @Test
    public void parse_invalidArgs_throwsParseException() {
        assertParseFailure(parser, " 3", TagFilterParser.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " Resident", TagFilterParser.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " /unknown value", TagFilterParser.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " " + KEYWORD_FILTER, Tag.MESSAGE_EMPTY);
    }
}
