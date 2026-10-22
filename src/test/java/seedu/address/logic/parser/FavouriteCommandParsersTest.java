package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.FavCommand;
import seedu.address.logic.commands.UnfavCommand;

/**
 * Tests index validation for marking and unmarking favourite contacts.
 */
public class FavouriteCommandParsersTest {
    private final FavCommandParser favParser = new FavCommandParser();
    private final UnfavCommandParser unfavParser = new UnfavCommandParser();

    @Test
    public void parse_validIndex_success() {
        for (String args : List.of("1", "  1  ", "\t1\t")) {
            assertParseSuccess(favParser, args, new FavCommand(INDEX_FIRST_PERSON));
            assertParseSuccess(unfavParser, args, new UnfavCommand(INDEX_FIRST_PERSON));
        }
        assertParseSuccess(favParser, "2147483647", new FavCommand(Index.fromOneBased(Integer.MAX_VALUE)));
        assertParseSuccess(unfavParser, "2147483647", new UnfavCommand(Index.fromOneBased(Integer.MAX_VALUE)));
    }

    @Test
    public void parse_invalidArguments_failure() {
        for (String args : List.of("", "  ", "0", "-1", "+1", "1.5", "2147483648", "one", "1 2",
                "1\t2", "1 extra", "/name Alice", "1 /tag friends")) {
            assertParseFailure(favParser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, FavCommand.MESSAGE_USAGE));
            assertParseFailure(unfavParser, args,
                    String.format(MESSAGE_INVALID_COMMAND_FORMAT, UnfavCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_nullArguments_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> favParser.parse(null));
        assertThrows(NullPointerException.class, () -> unfavParser.parse(null));
    }
}
