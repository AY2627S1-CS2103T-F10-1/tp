package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Room;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String VALID_NAME = "Sarah Tan";
    private static final String VALID_PHONE = "9123 4567";
    private static final String VALID_NAME_AND_PHONE = VALID_NAME + " /phone " + VALID_PHONE;
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_PHONE_LENGTH = 20;

    private final AddCommandParser parser = new AddCommandParser();

    @Test
    public void parse_validCommand_success() {
        assertParseSuccess(parser, " " + VALID_NAME_AND_PHONE, addCommandWithTags());
    }

    @Test
    public void parse_extraWhitespace_collapsed() {
        assertParseSuccess(parser, "  Sarah   Tan  /phone 9123   4567 ", addCommandWithTags());
    }

    @Test
    public void parse_phoneCharacters_success() {
        assertParseSuccess(parser, "Daniel Lim /phone +65 (8123)-4567",
                new AddCommand(new PersonBuilder().withName("Daniel Lim")
                        .withPhone("+65 (8123)-4567").withEmail("").withAddress("").build()));
    }

    @Test
    public void parse_emptyArgs_failure() {
        assertParseFailure(parser, "", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "   ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_missingNameOrPhone_failure() {
        assertParseFailure(parser, "/phone 91234567", AddCommandParser.MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, VALID_NAME, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME + " /phone", AddCommandParser.MESSAGE_EMPTY_PHONE);
        assertParseFailure(parser, VALID_NAME + " /tag RA", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_nameLength() {
        String longestName = "A".repeat(MAX_NAME_LENGTH);
        assertParseSuccess(parser, longestName + " /phone " + VALID_PHONE, new AddCommand(new PersonBuilder()
                .withName(longestName).withPhone(VALID_PHONE).withEmail("").withAddress("").build()));
        assertParseFailure(parser, longestName + "A /phone " + VALID_PHONE, AddCommandParser.MESSAGE_LONG_NAME);
    }

    @Test
    public void parse_invalidName_failure() {
        assertParseFailure(parser, "Sarah& Tan /phone 91234567", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_invalidPhone_failure() {
        assertParseFailure(parser, VALID_NAME + " /phone 912a4567", AddCommandParser.MESSAGE_INVALID_PHONE);
        assertParseFailure(parser, VALID_NAME + " /phone " + "1".repeat(MAX_PHONE_LENGTH + 1),
                AddCommandParser.MESSAGE_LONG_PHONE);
    }

    @Test
    public void parse_wrongPrefix_failure() {
        assertParseFailure(parser, "Sarah Tan p/91234567", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "n/Sarah Tan /phone 91234567", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " t/Resident", AddCommandParser.MESSAGE_INVALID_PHONE);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /unknown value", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_repeatedPhoneOrRoom_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /phone 87654321", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /room B1 /room B2", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_room_success() {
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /room B-204", addCommandWithRoomAndTags("B-204"));
        String longestRoom = "B".repeat(Room.MAX_LENGTH);
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /room " + longestRoom,
                addCommandWithRoomAndTags(longestRoom));
    }

    @Test
    public void parse_invalidRoom_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /room", Room.MESSAGE_EMPTY);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /room " + "B".repeat(Room.MAX_LENGTH + 1),
                Room.MESSAGE_TOO_LONG);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /room B#204", Room.MESSAGE_INVALID_CHARACTERS);
    }

    @Test
    public void parse_tags_success() {
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag RA", addCommandWithTags("RA"));
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag RA /tag Resident Fellow",
                addCommandWithTags("RA", "Resident Fellow"));
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag   RA   /tag   Resident   Fellow  ",
                addCommandWithTags("RA", "Resident Fellow"));
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag Hall-Staff /tag Block4",
                addCommandWithTags("Hall-Staff", "Block4"));
    }

    @Test
    public void parse_tagAtMaxLength_success() {
        String longestTag = "a".repeat(Tag.MAX_LENGTH);
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag " + longestTag, addCommandWithTags(longestTag));
    }

    @Test
    public void parse_fieldsInAnyOrder_success() {
        AddCommand expected = addCommandWithRoomAndTags("B-204", "RA", "Staff");
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /room B-204 /tag RA /tag Staff", expected);
        assertParseSuccess(parser, VALID_NAME + " /tag RA /room B-204 /tag Staff /phone " + VALID_PHONE, expected);
        assertParseSuccess(parser, VALID_NAME + " /room B-204 /phone " + VALID_PHONE + " /tag RA /tag Staff",
                expected);
    }

    @Test
    public void parse_repeatedTag_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA /tag RA", AddCommandParser.MESSAGE_DUPLICATE_TAG);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA /tag ra", AddCommandParser.MESSAGE_DUPLICATE_TAG);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag Resident Fellow /tag resident  fellow",
                AddCommandParser.MESSAGE_DUPLICATE_TAG);
    }

    @Test
    public void parse_tagLimit() {
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /tag T1 /tag T2 /tag T3 /tag T4 /tag T5",
                addCommandWithTags("T1", "T2", "T3", "T4", "T5"));
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag T1 /tag T2 /tag T3 /tag T4 /tag T5 /tag T6",
                Person.MESSAGE_TAG_LIMIT_REACHED);
    }

    @Test
    public void parse_emptyTag_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag   ", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA /tag", Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag /tag RA", Tag.MESSAGE_EMPTY);
    }

    @Test
    public void parse_invalidTag_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA/Staff", Tag.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA, Staff", Tag.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA /tag St@ff", Tag.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag " + "a".repeat(Tag.MAX_LENGTH + 1),
                Tag.MESSAGE_TOO_LONG);
    }

    /**
     * Returns the {@code AddCommand} expected for {@link #VALID_NAME_AND_PHONE} with no room and the given
     * {@code tags}.
     */
    private static AddCommand addCommandWithTags(String... tags) {
        return addCommandWithRoomAndTags("", tags);
    }

    /**
     * Returns the {@code AddCommand} expected for {@link #VALID_NAME_AND_PHONE} with the given {@code room}
     * and {@code tags}.
     */
    private static AddCommand addCommandWithRoomAndTags(String room, String... tags) {
        Person person = new PersonBuilder().withName(VALID_NAME).withPhone(VALID_PHONE)
                .withEmail("").withAddress("").withTags(tags).build();
        return new AddCommand(new Person(person.getName(), person.getPhone(), person.getEmail(),
                person.getAddress(), person.getTags(), new Room(room)));
    }
}
