package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
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
        assertParseFailure(parser, "Sarah Tan /phone 91234567 /phone 87654321", Phone.MESSAGE_CONSTRAINTS);
    }
}
