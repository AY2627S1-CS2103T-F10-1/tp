package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.commands.CommandTestUtil.INVALID_TAG_TOO_LONG;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_AT_MAX_LENGTH;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROOM_COMMAND;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.JANE;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Room;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+65#1234";
    private static final String INVALID_ADDRESS = " ";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_ADDRESS = "123 Main Street #0505";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_ROOM = "B-204 Annex";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";
    private static final List<String> TAGS_AT_LIMIT = JANE.getTags().stream().map(tag -> tag.tagName).toList();

    private static final String WHITESPACE = " \t\r\n";
    private static final String USAGE = "usage";
    private static final Prefix[] NOTE_AND_ROOM = {PREFIX_NOTE_COMMAND, PREFIX_ROOM_COMMAND};

    /** Tokenizes {@code args} with the note and room prefixes and checks it for unknown prefixes. */
    private static void verifyNoUnknownPrefix(String args) throws ParseException {
        ArgumentMultimap arguments = args == null ? null : ArgumentTokenizer.tokenize(args, NOTE_AND_ROOM);
        ParserUtil.verifyNoUnknownPrefix(args, arguments, USAGE, NOTE_AND_ROOM);
    }

    @Test
    public void verifyNoUnknownPrefix_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> verifyNoUnknownPrefix(null));
    }

    @Test
    public void verifyNoUnknownPrefix_onlyKnownPrefixes_success() throws Exception {
        verifyNoUnknownPrefix(" /room C-310 /note call w/ parent 24/7");
        verifyNoUnknownPrefix("");
    }

    @Test
    public void verifyNoUnknownPrefix_unknownPrefixInNote_throwsNoteError() {
        assertThrows(ParseException.class, Note.MESSAGE_INVALID_CHARACTERS, () ->
                verifyNoUnknownPrefix(" /room C-310 /note see /docs"));
        assertThrows(ParseException.class, Note.MESSAGE_INVALID_CHARACTERS, () ->
                verifyNoUnknownPrefix(" /note ends with /"));
    }

    @Test
    public void verifyNoUnknownPrefix_unknownPrefixOutsideNote_throwsFormatError() {
        String formatError = String.format(MESSAGE_INVALID_COMMAND_FORMAT, USAGE);
        assertThrows(ParseException.class, formatError, () -> verifyNoUnknownPrefix(" /rom C-310"));
        assertThrows(ParseException.class, formatError, () -> verifyNoUnknownPrefix(" /room C-310 /docs"));
    }

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parseName_emptyValue_throwsParseException() {
        assertThrows(ParseException.class, Name.MESSAGE_EMPTY, () -> ParserUtil.parseName(""));
        assertThrows(ParseException.class, Name.MESSAGE_EMPTY, () -> ParserUtil.parseName(WHITESPACE));
    }

    @Test
    public void parseName_lengthBoundary() throws Exception {
        String longestName = "A".repeat(Name.MAX_LENGTH);
        assertEquals(new Name(longestName), ParserUtil.parseName(longestName));
        assertThrows(ParseException.class, Name.MESSAGE_TOO_LONG, () -> ParserUtil.parseName(longestName + "A"));
    }

    @Test
    public void parsePhone_emptyValue_throwsParseException() {
        assertThrows(ParseException.class, Phone.MESSAGE_EMPTY, () -> ParserUtil.parsePhone(""));
        assertThrows(ParseException.class, Phone.MESSAGE_EMPTY, () -> ParserUtil.parsePhone(WHITESPACE));
    }

    @Test
    public void parsePhone_lengthBoundary() throws Exception {
        String longestPhone = "1".repeat(Phone.MAX_LENGTH);
        assertEquals(new Phone(longestPhone), ParserUtil.parsePhone(longestPhone));
        assertThrows(ParseException.class, Phone.MESSAGE_TOO_LONG, () -> ParserUtil.parsePhone(longestPhone + "1"));
    }

    @Test
    public void parsePhone_invalidCharacters_throwsParseException() {
        assertThrows(ParseException.class, Phone.MESSAGE_INVALID_CHARACTERS, () ->
                ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parseRoom_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseRoom(null));
    }

    @Test
    public void parseRoom_validValueWithExtraWhitespace_returnsCollapsedRoom() throws Exception {
        assertEquals(new Room(VALID_ROOM), ParserUtil.parseRoom(WHITESPACE + "B-204   Annex" + WHITESPACE));
    }

    @Test
    public void parseRoom_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, Room.MESSAGE_EMPTY, () -> ParserUtil.parseRoom(WHITESPACE));
        assertThrows(ParseException.class, Room.MESSAGE_TOO_LONG, () ->
                ParserUtil.parseRoom("A".repeat(Room.MAX_LENGTH + 1)));
        assertThrows(ParseException.class, Room.MESSAGE_INVALID_CHARACTERS, () -> ParserUtil.parseRoom("B/204"));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parseAddress_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseAddress((String) null));
    }

    @Test
    public void parseAddress_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseAddress(INVALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithoutWhitespace_returnsAddress() throws Exception {
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(VALID_ADDRESS));
    }

    @Test
    public void parseAddress_validValueWithWhitespace_returnsTrimmedAddress() throws Exception {
        String addressWithWhitespace = WHITESPACE + VALID_ADDRESS + WHITESPACE;
        Address expectedAddress = new Address(VALID_ADDRESS);
        assertEquals(expectedAddress, ParserUtil.parseAddress(addressWithWhitespace));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTag_emptyValue_throwsParseException() {
        assertThrows(ParseException.class, Tag.MESSAGE_EMPTY, () -> ParserUtil.parseTag(""));
        assertThrows(ParseException.class, Tag.MESSAGE_EMPTY, () -> ParserUtil.parseTag(WHITESPACE));
    }

    @Test
    public void parseTag_tooLong_throwsParseException() {
        assertThrows(ParseException.class, Tag.MESSAGE_TOO_LONG, () -> ParserUtil.parseTag(INVALID_TAG_TOO_LONG));
    }

    @Test
    public void parseTag_invalidCharacters_throwsParseException() {
        assertThrows(ParseException.class, Tag.MESSAGE_INVALID_CHARACTERS, () -> ParserUtil.parseTag(INVALID_TAG));
        assertThrows(ParseException.class, Tag.MESSAGE_INVALID_CHARACTERS, () -> ParserUtil.parseTag("RA/Staff"));
    }

    @Test
    public void parseTag_atMaxLengthWithSurroundingWhitespace_returnsTag() throws Exception {
        assertEquals(new Tag(VALID_TAG_AT_MAX_LENGTH),
                ParserUtil.parseTag(WHITESPACE + VALID_TAG_AT_MAX_LENGTH + WHITESPACE));
    }

    @Test
    public void parseTag_multiWordWithExtraSpaces_returnsCollapsedTag() throws Exception {
        assertEquals("Resident Fellow", ParserUtil.parseTag("  resident    fellow ").tagName);
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }

    @Test
    public void parseTags_atTagLimit_returnsTagsInGivenOrder() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(TAGS_AT_LIMIT);
        List<String> actualTagNames = actualTagSet.stream().map(tag -> tag.tagName).toList();

        assertEquals(TAGS_AT_LIMIT, actualTagNames);
    }

    @Test
    public void parseTags_overTagLimit_throwsParseException() {
        List<String> tagsOverLimit = new ArrayList<>(TAGS_AT_LIMIT);
        tagsOverLimit.add(VALID_TAG_1);

        assertThrows(ParseException.class, Person.MESSAGE_TAG_LIMIT_REACHED, () -> ParserUtil.parseTags(tagsOverLimit));
    }

    @Test
    public void parseTags_duplicatesDifferingInCase_throwsParseException() {
        List<String> tagsWithDuplicate = List.of(VALID_TAG_1, VALID_TAG_1.toUpperCase());

        assertThrows(ParseException.class, Tag.MESSAGE_DUPLICATE, () -> ParserUtil.parseTags(tagsWithDuplicate));
    }
}
