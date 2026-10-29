package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROOM_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_THIRD_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.commands.EditCommand.EditPersonDescriptor;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Room;
import seedu.address.testutil.EditPersonDescriptorBuilder;

public class EditCommandParserTest {
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);

    private final EditCommandParser parser = new EditCommandParser();

    @Test
    public void parse_missingParts_failure() {
        // no index specified
        assertParseFailure(parser, " /name Amy Bee", MESSAGE_INVALID_FORMAT);

        // no field specified
        assertParseFailure(parser, " 1", EditCommand.MESSAGE_NOT_EDITED);

        // no index and no field specified
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidPreamble_failure() {
        assertParseFailure(parser, " -5 /name Amy Bee", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 0 /name Amy Bee", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 some random string", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /unknown string", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_unknownOrWrongCasePrefix_failure() {
        assertParseFailure(parser, " 1 /phone 91234567 /foo", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /room B1 /PHONE 91234567", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /name Amy Bee /Tag friend", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /name Amy Bee /Room B1", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /email amy@example.com", MESSAGE_INVALID_FORMAT);

        // the old prefixes are no longer recognised
        assertParseFailure(parser, " 1 p/91234567", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, " 1 /name James&", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 /phone 911a", Phone.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, " 1 /room B#1", Room.MESSAGE_INVALID_CHARACTERS);

        // only the first invalid value is reported
        assertParseFailure(parser, " 1 /name James& /phone 911a", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("Amy Bee")
                .withPhone("22222222").withRoom("B-204").withNote("Prefers WhatsApp").build();

        assertParseSuccess(parser, " 2 /note Prefers WhatsApp /phone 22222222 /name Amy Bee /room B-204",
                new EditCommand(INDEX_SECOND_PERSON, descriptor));
    }

    @Test
    public void parse_oneFieldSpecified_success() {
        assertParseSuccess(parser, " 3 /name Amy Bee", new EditCommand(INDEX_THIRD_PERSON,
                new EditPersonDescriptorBuilder().withName("Amy Bee").build()));
        assertParseSuccess(parser, " 3 /phone 9123 4567", new EditCommand(INDEX_THIRD_PERSON,
                new EditPersonDescriptorBuilder().withPhone("9123 4567").build()));
        assertParseSuccess(parser, " 3 /room B-204", new EditCommand(INDEX_THIRD_PERSON,
                new EditPersonDescriptorBuilder().withRoom("B-204").build()));
    }

    @Test
    public void parse_extraWhitespace_collapsed() {
        EditCommand expected = new EditCommand(INDEX_FIRST_PERSON, new EditPersonDescriptorBuilder()
                .withName("Amy Bee").withPhone("9123 4567").withRoom("B-204").build());

        assertParseSuccess(parser, "  1  /name  Amy   Bee \t/phone 9123   4567\n/room B-204 ", expected);
    }

    @Test
    public void parse_tagPrefix_failure() {
        // tags are changed with toggle, so edit no longer recognises /tag in any form
        assertParseFailure(parser, " 1 /tag friend", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /tag", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /tag friend /tag Resident Fellow", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /room B-204 /tag", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " 1 /name Amy Bee /tag friend", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void usageMessage_doesNotMentionTags() {
        assertFalse(EditCommand.MESSAGE_USAGE.contains(PREFIX_TAG_COMMAND.getPrefix()));
    }

    @Test
    public void parse_repeatedSingleValuedFields_failure() {
        assertParseFailure(parser, " 1 /phone 911a /phone 22222222",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE_COMMAND));
        assertParseFailure(parser, " 1 /room B1 /room B2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ROOM_COMMAND));
    }

    @Test
    public void parse_emptyOptionalField_clearsField() {
        assertParseSuccess(parser, " 3 /room", new EditCommand(INDEX_THIRD_PERSON,
                new EditPersonDescriptorBuilder().withRoom("").build()));
        assertParseSuccess(parser, " 3 /room /note", new EditCommand(INDEX_THIRD_PERSON,
                new EditPersonDescriptorBuilder().withRoom("").withNote("").build()));
        assertParseSuccess(parser, " 3 /note", new EditCommand(INDEX_THIRD_PERSON,
                new EditPersonDescriptorBuilder().withNote("").build()));
    }

    @Test
    public void parse_note_success() {
        assertParseSuccess(parser, " 1 /note  Prefers   WhatsApp ", new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withNote("Prefers WhatsApp").build()));
        assertParseSuccess(parser, " 1 /note Allergic to peanuts /phone 91234567", new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withPhone("91234567").withNote("Allergic to peanuts").build()));
    }

    @Test
    public void parse_invalidNote_failure() {
        assertParseFailure(parser, " 1 /note " + "a".repeat(Note.MAX_LENGTH + 1), Note.MESSAGE_TOO_LONG);
        assertParseFailure(parser, " 1 /note a /note b",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NOTE_COMMAND));
    }

    @Test
    public void parse_noteWithWordStartingWithSlash_failure() {
        assertParseFailure(parser, " 1 /note see /docs", Note.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, " 1 /note ends with /", Note.MESSAGE_INVALID_CHARACTERS);
    }

    @Test
    public void parse_noteWithSlashInsideWord_success() {
        assertParseSuccess(parser, " 1 /note call w/ parent 24/7", new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withNote("call w/ parent 24/7").build()));
    }

    @Test
    public void parse_emptyRequiredField_failure() {
        assertParseFailure(parser, " 1 /name", Name.MESSAGE_EMPTY);
        assertParseFailure(parser, " 1 /phone", Phone.MESSAGE_EMPTY);
    }
}
