package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_TOO_LONG;
import static seedu.address.logic.commands.CommandTestUtil.VALID_NAME_AMY;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_AT_MAX_LENGTH;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_RESIDENT_FELLOW;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.TagCommand;
import seedu.address.model.tag.Tag;

public class TagCommandParserTest {

    private static final String KEYWORD = " " + TagCommand.COMMAND_KEYWORD + " ";
    private static final int VERY_LONG_NAME_REPEATS = 500;

    private final TagCommandParser parser = new TagCommandParser();

    @Test
    public void isTagCommand() {
        assertThrows(NullPointerException.class, () -> TagCommandParser.isTagCommand(null));

        assertTrue(TagCommandParser.isTagCommand(VALID_NAME_AMY + KEYWORD + "RA"));
        assertTrue(TagCommandParser.isTagCommand(TagCommand.COMMAND_KEYWORD + " RA")); // missing identifier
        assertTrue(TagCommandParser.isTagCommand(VALID_NAME_AMY + " " + TagCommand.COMMAND_KEYWORD)); // no tag

        assertFalse(TagCommandParser.isTagCommand(VALID_NAME_AMY + " RA")); // no keyword
        assertFalse(TagCommandParser.isTagCommand(VALID_NAME_AMY + TagCommand.COMMAND_KEYWORD + " RA")); // glued
        assertFalse(TagCommandParser.isTagCommand(VALID_NAME_AMY + " /tagged RA")); // longer word
        assertFalse(TagCommandParser.isTagCommand(VALID_NAME_AMY + " /TAG RA")); // keyword is case-sensitive
    }

    @Test
    public void parse_validArgs_returnsTagCommand() {
        TagCommand expectedCommand = new TagCommand(VALID_NAME_AMY, new Tag(VALID_TAG_RESIDENT_FELLOW));

        assertParseSuccess(parser, VALID_NAME_AMY + KEYWORD + VALID_TAG_RESIDENT_FELLOW, expectedCommand);

        // extra whitespace around and within both parts
        assertParseSuccess(parser, "  Amy   Bee \t " + TagCommand.COMMAND_KEYWORD + "   Resident    Fellow  ",
                expectedCommand);

        // different case in tag
        assertParseSuccess(parser, VALID_NAME_AMY + KEYWORD + "resident FELLOW", expectedCommand);
    }

    @Test
    public void parse_missingParts_throwsParseException() {
        // missing keyword
        assertParseFailure(parser, VALID_NAME_AMY + " RA", TagCommand.MESSAGE_INVALID_FORMAT);

        // missing identifier
        assertParseFailure(parser, TagCommand.COMMAND_KEYWORD + " RA", TagCommand.MESSAGE_INVALID_FORMAT);

        // missing tag value
        assertParseFailure(parser, VALID_NAME_AMY + " " + TagCommand.COMMAND_KEYWORD,
                TagCommand.MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME_AMY + KEYWORD + "   ", TagCommand.MESSAGE_INVALID_FORMAT);

        // empty input
        assertParseFailure(parser, "", TagCommand.MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        // boundary: one character over the limit
        assertParseFailure(parser, VALID_NAME_AMY + KEYWORD + INVALID_TAG_TOO_LONG, Tag.MESSAGE_TOO_LONG);

        // command delimiter inside the tag
        assertParseFailure(parser, VALID_NAME_AMY + KEYWORD + "Resident/Fellow", Tag.MESSAGE_INVALID_CHARACTERS);

        // more than one tag per command
        assertParseFailure(parser, VALID_NAME_AMY + KEYWORD + "RA" + KEYWORD + "Resident",
                Tag.MESSAGE_INVALID_CHARACTERS);

        // other special characters
        assertParseFailure(parser, VALID_NAME_AMY + KEYWORD + "hubby*", Tag.MESSAGE_INVALID_CHARACTERS);
    }

    @Test
    public void parse_tagAtMaxLength_returnsTagCommand() {
        assertParseSuccess(parser, VALID_NAME_AMY + KEYWORD + VALID_TAG_AT_MAX_LENGTH,
                new TagCommand(VALID_NAME_AMY, new Tag(VALID_TAG_AT_MAX_LENGTH)));
    }

    @Test
    public void parse_veryLongIdentifier_returnsTagCommand() {
        String veryLongName = "Amy ".repeat(VERY_LONG_NAME_REPEATS).trim();
        assertParseSuccess(parser, veryLongName + KEYWORD + "RA", new TagCommand(veryLongName, new Tag("RA")));
    }
}
