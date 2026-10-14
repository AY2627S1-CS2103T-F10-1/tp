package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String VALID_NAME_AND_PHONE = "Sarah Tan /phone 9123 4567";

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_validCommand_success() {
        assertParseSuccess(parser, " Sarah Tan /phone 9123 4567",
                new AddCommand(new PersonBuilder().withName("Sarah Tan").withPhone("9123 4567")
                        .withEmail("").withAddress("").build()));
    }

    @Test
    public void parse_phoneCharacters_success() {
        assertParseSuccess(parser, "Daniel Lim /phone +65 (8123)-4567",
                new AddCommand(new PersonBuilder().withName("Daniel Lim")
                        .withPhone("+65 (8123)-4567").withEmail("").withAddress("").build()));
    }

    @Test
    public void parse_missingNameOrPhone_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "/phone 91234567", expected);
        assertParseFailure(parser, "Sarah Tan", expected);
        assertParseFailure(parser, "Sarah Tan /phone", expected);
    }

    @Test
    public void parse_invalidPhone_failure() {
        assertParseFailure(parser, "Sarah Tan /phone 912a4567", Phone.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, "Sarah Tan /phone 123456789012345678901", Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_wrongPrefix_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "Sarah Tan p/91234567", expected);
        assertParseFailure(parser, "n/Sarah Tan /phone 91234567", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidName_failure() {
        assertParseFailure(parser, "Sarah& Tan /phone 91234567", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_extraFields_failure() {
        assertParseFailure(parser, "Sarah Tan /phone 91234567 t/Resident", Phone.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_duplicateKeywords_failure() {
        assertParseFailure(parser, "Sarah Tan /phone 91234567 /phone 87654321",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE_COMMAND));
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA /tag Staff",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_TAG_COMMAND));
    }

    @Test
    public void parse_tags_success() {
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag RA", addCommandWithTags("RA"));
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag RA,Resident Fellow",
                addCommandWithTags("RA", "Resident Fellow"));
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag   RA  ,   Resident   Fellow  ",
                addCommandWithTags("RA", "Resident Fellow"));
    }

    @Test
    public void parse_tagBeforePhone_success() {
        assertParseSuccess(parser, "Sarah Tan /tag RA, Staff /phone 9123 4567", addCommandWithTags("RA", "Staff"));
    }

    @Test
    public void parse_repeatedTag_mergedIntoOne() {
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag RA, ra,  RA ", addCommandWithTags("RA"));
    }

    @Test
    public void parse_tagLimit() {
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag T1, T2, T3, T4, T5",
                addCommandWithTags("T1", "T2", "T3", "T4", "T5"));
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag T1, T2, T3, T4, T5, T6",
                Person.MESSAGE_TAG_LIMIT_REACHED);
    }

    @Test
    public void parse_emptyTag_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag   ", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA,,Staff", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA,", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag ,RA", Tag.MESSAGE_EMPTY);
    }

    @Test
    public void parse_invalidTag_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA/Staff", Tag.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA, St@ff", Tag.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag " + "a".repeat(Tag.MAX_LENGTH + 1),
                Tag.MESSAGE_TOO_LONG);
    }

    @Test
    public void parse_tagsWithoutNameOrPhone_failure() {
        String expected = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " /phone 91234567 /tag RA", expected);
        assertParseFailure(parser, "Sarah Tan /tag RA", expected);
    }

    /**
     * Returns the {@code AddCommand} expected for {@link #VALID_NAME_AND_PHONE} with the given {@code tags}.
     */
    private static AddCommand addCommandWithTags(String... tags) {
        return new AddCommand(new PersonBuilder().withName("Sarah Tan").withPhone("9123 4567")
                .withEmail("").withAddress("").withTags(tags).build());
    }
}
