package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.RoomCommand;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.RoomNumber;
import seedu.address.testutil.PersonBuilder;

public class RoomCommandParserTest {
    private final RoomCommandParser parser = new RoomCommandParser();

    @Test
    public void parse_residentFirstCommand_assignsRoom() throws Exception {
        Model model = new ModelManager();
        model.addPerson(new PersonBuilder().withName("Sarah Tan").withTags("Resident").build());
        new AddressBookParser().parseCommand("  Sarah Tan /room B-204  ").execute(model);
        assertEquals(Optional.of(new RoomNumber("B-204")),
                model.getAddressBook().getPersonList().get(0).getRoomNumber());
    }

    @Test
    public void parse_invalidFormat_throwsParseException() {
        assertParseFailure(parser, "Sarah Tan B-204", RoomCommand.MESSAGE_USAGE);
        assertParseFailure(parser, " /room B-204", RoomCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "Sarah Tan /room B-204 /room B-205", RoomCommand.MESSAGE_USAGE);
    }

    @Test
    public void parse_invalidRoom_throwsParseException() {
        assertParseFailure(parser, "Sarah Tan /room", "Room number cannot be empty.");
        assertParseFailure(parser, "Sarah Tan /room B/204", "Room number contains invalid characters.");
        assertParseFailure(parser, "Sarah Tan /room 123456789012345678901",
                "Room number too long, please shorten it and try again.");
    }
}
