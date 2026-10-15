package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_TOO_LONG;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_RESIDENT_FELLOW;
import static seedu.address.logic.parser.CliSyntax.KEYWORD_FILTER;
import static seedu.address.testutil.Assert.assertThrows;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.PersonHasTagPredicate;
import seedu.address.model.tag.Tag;

public class TagFilterParserTest {

    private static final String FILTER = " " + KEYWORD_FILTER + " ";

    private static void assertParseSuccess(String args, String expectedTagName) throws Exception {
        assertEquals(new PersonHasTagPredicate(expectedTagName), TagFilterParser.parse(args));
    }

    private static void assertParseFailure(String args, String expectedMessage) {
        assertThrows(ParseException.class, expectedMessage, () -> TagFilterParser.parse(args));
    }

    @Test
    public void parse_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> TagFilterParser.parse(null));
    }

    @Test
    public void parse_validArgs_returnsPredicate() throws Exception {
        assertParseSuccess(FILTER + VALID_TAG_RESIDENT_FELLOW, VALID_TAG_RESIDENT_FELLOW);

        // extra whitespace around the keyword and within the tag
        assertParseSuccess(" \t " + KEYWORD_FILTER + "   Resident    Fellow  ", VALID_TAG_RESIDENT_FELLOW);
    }

    @Test
    public void parse_valueThatCannotBeTag_returnsPredicate() throws Exception {
        // any value is a valid filter; it simply matches no contact
        assertParseSuccess(FILTER + "RA/Staff", "RA/Staff");
        assertParseSuccess(FILTER + INVALID_TAG_TOO_LONG, INVALID_TAG_TOO_LONG);
        assertParseSuccess(FILTER + "RA" + FILTER + "Staff", "RA" + FILTER + "Staff"); // repeated keyword
    }

    @Test
    public void parse_emptyTagValue_throwsParseException() {
        assertParseFailure(KEYWORD_FILTER, Tag.MESSAGE_EMPTY);
        assertParseFailure(FILTER + "   ", Tag.MESSAGE_EMPTY);
    }

    @Test
    public void parse_missingKeyword_throwsParseException() {
        assertParseFailure("", TagFilterParser.MESSAGE_INVALID_FORMAT); // empty input
        assertParseFailure("Resident", TagFilterParser.MESSAGE_INVALID_FORMAT); // tag without keyword
        assertParseFailure("3", TagFilterParser.MESSAGE_INVALID_FORMAT);
        assertParseFailure(KEYWORD_FILTER + "Resident", TagFilterParser.MESSAGE_INVALID_FORMAT); // glued
        assertParseFailure("/FILTER Resident", TagFilterParser.MESSAGE_INVALID_FORMAT); // keyword is case-sensitive
        assertParseFailure("Resident" + FILTER + "RA", TagFilterParser.MESSAGE_INVALID_FORMAT); // text before
    }
}
