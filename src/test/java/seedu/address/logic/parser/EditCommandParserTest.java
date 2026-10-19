package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROOM_COMMAND;
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
import seedu.address.model.person.Person;
import seedu.address.model.person.Room;
import seedu.address.model.tag.Tag;
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
        assertParseFailure(parser, " 1 /email amy@example.com", MESSAGE_INVALID_FORMAT);

        // the old prefixes are no longer recognised
        assertParseFailure(parser, " 1 p/91234567", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_invalidValue_failure() {
        assertParseFailure(parser, " 1 /name James&", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, " 1 /name", PersonField.MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, " 1 /phone 911a", PersonField.MESSAGE_INVALID_PHONE);
        assertParseFailure(parser, " 1 /phone", PersonField.MESSAGE_EMPTY_PHONE);
        assertParseFailure(parser, " 1 /room", Room.MESSAGE_EMPTY);
        assertParseFailure(parser, " 1 /room B#1", Room.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, " 1 /tag hubby*", Tag.MESSAGE_INVALID_CHARACTERS);

        // /tag on its own resets the tags, but together with a tag it is an error
        assertParseFailure(parser, " 1 /tag friend /tag", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, " 1 /tag /tag friend", Tag.MESSAGE_EMPTY);

        // same rules as add
        assertParseFailure(parser, " 1 /tag RA /tag ra", PersonField.MESSAGE_DUPLICATE_TAG);
        assertParseFailure(parser, " 1 /tag a /tag b /tag c /tag d /tag e /tag f", Person.MESSAGE_TAG_LIMIT_REACHED);

        // only the first invalid value is reported
        assertParseFailure(parser, " 1 /name James& /phone 911a", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_allFieldsSpecified_success() {
        EditPersonDescriptor descriptor = new EditPersonDescriptorBuilder().withName("Amy Bee")
                .withPhone("22222222").withRoom("B-204").withTags("Husband", "Friend").build();

        assertParseSuccess(parser, " 2 /tag Husband /phone 22222222 /name Amy Bee /tag Friend /room B-204",
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
        assertParseSuccess(parser, " 3 /tag friend", new EditCommand(INDEX_THIRD_PERSON,
                new EditPersonDescriptorBuilder().withTags("friend").build()));
    }

    @Test
    public void parse_multipleTags_replacesAllInOrder() {
        assertParseSuccess(parser, " 1 /tag friend /tag Resident Fellow", new EditCommand(INDEX_FIRST_PERSON,
                new EditPersonDescriptorBuilder().withTags("friend", "Resident Fellow").build()));
    }

    @Test
    public void parse_repeatedSingleValuedFields_failure() {
        assertParseFailure(parser, " 1 /phone 911a /phone 22222222",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE_COMMAND));
        assertParseFailure(parser, " 1 /room B1 /room B2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ROOM_COMMAND));
    }

    @Test
    public void parse_resetTags_success() {
        assertParseSuccess(parser, " 3 /tag", new EditCommand(INDEX_THIRD_PERSON,
                new EditPersonDescriptorBuilder().withTags().build()));
    }
}
