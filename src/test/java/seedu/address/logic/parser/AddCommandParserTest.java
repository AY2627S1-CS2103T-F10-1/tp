package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Room;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
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
        assertParseFailure(parser, "/phone 91234567", AddCommandParser.MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, "Sarah Tan", expected);
        assertParseFailure(parser, "Sarah Tan /phone", AddCommandParser.MESSAGE_EMPTY_PHONE);
    }

    @Test
    public void parse_invalidPhone_failure() {
        assertParseFailure(parser, "Sarah Tan /phone 912a4567", AddCommandParser.MESSAGE_INVALID_PHONE);
        assertParseFailure(parser, "Sarah Tan /phone 123456789012345678901", AddCommandParser.MESSAGE_LONG_PHONE);
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
        assertParseFailure(parser, "Sarah Tan /phone 91234567 t/Resident", AddCommandParser.MESSAGE_INVALID_PHONE);
        assertParseFailure(parser, "Sarah Tan /phone 91234567 /phone 87654321",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
    @Test
    public void parse_optionalRoomAndTags_success() {
        Person base = new PersonBuilder().withName("Sarah Tan").withPhone("9123 4567")
                .withEmail("").withAddress("").withTags("Resident", "BlockB").build();
        Person expected = new Person(base.getName(), base.getPhone(), base.getEmail(), base.getAddress(),
                base.getTags(), new Room("B-204"));
        assertParseSuccess(parser, " Sarah   Tan /phone 9123   4567 /room B-204 /tag resident /tag BlockB",
                new AddCommand(expected));
        Person roomOnly = new Person(expected.getName(), expected.getPhone(), expected.getEmail(),
                expected.getAddress(), java.util.Set.of(), expected.getRoom());
        assertParseSuccess(parser, "Sarah Tan /phone 9123 4567 /room B-204", new AddCommand(roomOnly));
        assertParseSuccess(parser, "Sarah Tan /phone 9123 4567 /tag Resident /tag BlockB", new AddCommand(base));
    }

    @Test
    public void parse_invalidOptionalValues_failure() {
        String command = "Sarah Tan /phone 91234567";
        assertParseFailure(parser, command + " /room", Room.MESSAGE_EMPTY);
        assertParseFailure(parser, command + " /room " + "B".repeat(21), Room.MESSAGE_TOO_LONG);
        assertParseFailure(parser, command + " /room B#204", Room.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, command + " /tag", seedu.address.model.tag.Tag.MESSAGE_EMPTY);
        assertParseFailure(parser, command + " /tag Resident /tag resident", AddCommandParser.MESSAGE_DUPLICATE_TAG);
        assertParseFailure(parser, command + " /tag A /tag B /tag C /tag D /tag E /tag F",
                Person.MESSAGE_TAG_LIMIT_REACHED);
        assertParseFailure(parser, command + " /room B1 /room B2",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        assertParseFailure(parser, "A".repeat(101) + " /phone 91234567", AddCommandParser.MESSAGE_LONG_NAME);
    }

}
