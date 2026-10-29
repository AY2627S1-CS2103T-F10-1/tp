package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_LENGTHENED_PAST_MAX_LENGTH;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GREEK_LOWER_CASE;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_GREEK_UPPER_CASE;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROOM_COMMAND;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.AddCommand;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Room;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

public class AddCommandParserTest {
    private static final String VALID_NAME = "Sarah Tan";
    private static final String VALID_PHONE = "9123 4567";
    private static final String VALID_NAME_AND_PHONE = VALID_NAME + " /phone " + VALID_PHONE;
    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

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
        assertParseFailure(parser, "/phone 91234567", Name.MESSAGE_EMPTY);
        assertParseFailure(parser, VALID_NAME, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME + " /phone", Phone.MESSAGE_EMPTY);
        assertParseFailure(parser, VALID_NAME + " /tag RA", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_nameLength() {
        String longestName = "A".repeat(Name.MAX_LENGTH);
        assertParseSuccess(parser, longestName + " /phone " + VALID_PHONE, new AddCommand(new PersonBuilder()
                .withName(longestName).withPhone(VALID_PHONE).withEmail("").withAddress("").build()));
        assertParseFailure(parser, longestName + "A /phone " + VALID_PHONE, Name.MESSAGE_TOO_LONG);
    }

    @Test
    public void parse_invalidName_failure() {
        assertParseFailure(parser, "Sarah& Tan /phone 91234567", Name.MESSAGE_CONSTRAINTS);
    }

    @Test
    public void parse_phoneAtMaxLength_success() {
        String longestPhone = "1".repeat(Phone.MAX_LENGTH);
        assertParseSuccess(parser, VALID_NAME + " /phone " + longestPhone, new AddCommand(new PersonBuilder()
                .withName(VALID_NAME).withPhone(longestPhone).withEmail("").withAddress("").build()));
    }

    @Test
    public void parse_invalidPhone_failure() {
        assertParseFailure(parser, VALID_NAME + " /phone 912a4567", Phone.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, VALID_NAME + " /phone " + "1".repeat(Phone.MAX_LENGTH + 1),
                Phone.MESSAGE_TOO_LONG);
    }

    @Test
    public void parse_wrongPrefix_failure() {
        assertParseFailure(parser, "Sarah Tan p/91234567", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, "n/Sarah Tan /phone 91234567", Name.MESSAGE_CONSTRAINTS);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " t/Resident", Phone.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /unknown value", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_keywordInWrongCase_failure() {
        assertParseFailure(parser, VALID_NAME + " /PHONE " + VALID_PHONE, MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /Tag RA", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_repeatedPhoneOrRoom_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /phone 87654321",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE_COMMAND));
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /room B1 /room B2",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_ROOM_COMMAND));

        // the values are not checked, so an invalid one does not change the message
        assertParseFailure(parser, VALID_NAME + " /phone 911a /phone 87654321",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_PHONE_COMMAND));
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
    public void parse_note_success() {
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /note Allergic to peanuts",
                addCommandWithNote("Allergic to peanuts"));
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /note  Allergic   to peanuts ",
                addCommandWithNote("Allergic to peanuts"));
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /note Call mum: +65 9123-4567 (after 6pm)!",
                addCommandWithNote("Call mum: +65 9123-4567 (after 6pm)!"));
        assertParseSuccess(parser, " " + VALID_NAME + " /note Allergic to peanuts /phone " + VALID_PHONE,
                addCommandWithNote("Allergic to peanuts"));
    }

    @Test
    public void parse_noteWithSlashInsideWord_success() {
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /note call w/ parent 24/7",
                addCommandWithNote("call w/ parent 24/7"));
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /note see https://nus.edu.sg",
                addCommandWithNote("see https://nus.edu.sg"));
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /note ends with/", addCommandWithNote("ends with/"));
    }

    @Test
    public void parse_noteLength() {
        String longestNote = "a".repeat(Note.MAX_LENGTH);
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /note " + longestNote, addCommandWithNote(longestNote));
        assertParseSuccess(parser, VALID_NAME_AND_PHONE + " /note a", addCommandWithNote("a"));
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /note " + longestNote + "a", Note.MESSAGE_TOO_LONG);
    }

    @Test
    public void parse_invalidNote_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /note", Note.MESSAGE_EMPTY);
    }

    @Test
    public void parse_noteWithWordStartingWithSlash_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /note see /docs", Note.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /note ends with /", Note.MESSAGE_INVALID_CHARACTERS);
        assertParseFailure(parser, " " + VALID_NAME + " /note see /docs /phone " + VALID_PHONE,
                Note.MESSAGE_INVALID_CHARACTERS);

        // an unknown prefix outside the note is still a format error
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /docs /note hi", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_repeatedNote_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /note a /note b",
                Messages.getErrorMessageForDuplicatePrefixes(PREFIX_NOTE_COMMAND));
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
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA /tag RA", Tag.MESSAGE_DUPLICATE);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag RA /tag ra", Tag.MESSAGE_DUPLICATE);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag Resident Fellow /tag resident  fellow",
                Tag.MESSAGE_DUPLICATE);
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag " + VALID_TAG_GREEK_UPPER_CASE
                + " /tag " + VALID_TAG_GREEK_LOWER_CASE, Tag.MESSAGE_DUPLICATE);
    }

    @Test
    public void parse_repeatedTagAtLimit_failure() {
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag T1 /tag T2 /tag T3 /tag T4 /tag T5 /tag t1",
                Tag.MESSAGE_DUPLICATE);
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
        assertParseFailure(parser, VALID_NAME_AND_PHONE + " /tag " + INVALID_TAG_LENGTHENED_PAST_MAX_LENGTH,
                Tag.MESSAGE_TOO_LONG);
    }

    /** Returns the {@code AddCommand} expected for {@link #VALID_NAME_AND_PHONE} with only the given {@code note}. */
    private static AddCommand addCommandWithNote(String note) {
        return new AddCommand(validPersonBuilder().withNote(note).build());
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
        return new AddCommand(validPersonBuilder().withRoom(room).withTags(tags).build());
    }

    /** Returns a builder for the person {@code add} creates from {@link #VALID_NAME_AND_PHONE} alone. */
    private static PersonBuilder validPersonBuilder() {
        return new PersonBuilder().withName(VALID_NAME).withPhone(VALID_PHONE).withEmail("").withAddress("");
    }
}
