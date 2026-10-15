package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import java.util.LinkedHashSet;
import java.util.Set;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Room;
import seedu.address.model.tag.Tag;

/** Parses the add command, including optional room and tags. */
public class AddCommandParser implements Parser<AddCommand> {
    public static final String MESSAGE_EMPTY_NAME = "Contact name cannot be empty.";
    public static final String MESSAGE_LONG_NAME = "Contact name too long, please shorten it and try again.";
    public static final String MESSAGE_EMPTY_PHONE = "Phone number cannot be empty.";
    public static final String MESSAGE_LONG_PHONE = "Phone number too long, please shorten it and try again.";
    public static final String MESSAGE_INVALID_PHONE = "Phone number contains invalid characters.";
    public static final String MESSAGE_DUPLICATE_TAG = "Contact already has this tag.";

    private static final Prefix PHONE = new Prefix("/phone");
    private static final Prefix ROOM = new Prefix("/room");
    private static final Prefix TAG = new Prefix("/tag");

    @Override
    public AddCommand parse(String args) throws ParseException {
        String normalized = " " + StringUtil.collapseWhitespace(args);
        ArgumentMultimap values = ArgumentTokenizer.tokenize(normalized, PHONE, ROOM, TAG);
        if (values.getValue(PHONE).isEmpty()
                || values.getAllValues(PHONE).size() > 1 || values.getAllValues(ROOM).size() > 1) {
            throw invalidFormat();
        }
        int phoneIndex = normalized.indexOf(" /phone");
        int roomIndex = normalized.indexOf(" /room");
        int tagIndex = normalized.indexOf(" /tag");
        if ((roomIndex >= 0 && roomIndex < phoneIndex) || (tagIndex >= 0 && tagIndex < phoneIndex)) {
            throw invalidFormat();
        }
        for (String token : normalized.trim().split(" ")) {
            if (token.startsWith("/") && !Set.of("/phone", "/room", "/tag").contains(token)) {
                throw invalidFormat();
            }
        }
        String nameValue = values.getPreamble();
        String phoneValue = values.getValue(PHONE).orElseThrow();
        if (nameValue.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_NAME);
        }
        if (nameValue.length() > 100) {
            throw new ParseException(MESSAGE_LONG_NAME);
        }
        if (phoneValue.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_PHONE);
        }
        if (phoneValue.length() > 20) {
            throw new ParseException(MESSAGE_LONG_PHONE);
        }
        if (!Phone.isValidPhone(phoneValue)) {
            throw new ParseException(MESSAGE_INVALID_PHONE);
        }
        Name name = ParserUtil.parseName(nameValue);
        Phone phone = ParserUtil.parsePhone(phoneValue);
        Room room = new Room("");
        if (values.getValue(ROOM).isPresent()) {
            String roomValue = values.getValue(ROOM).orElseThrow();
            if (roomValue.isEmpty()) {
                throw new ParseException(Room.MESSAGE_EMPTY);
            }
            if (roomValue.length() > Room.MAX_LENGTH) {
                throw new ParseException(Room.MESSAGE_TOO_LONG);
            }
            if (!Room.isValidRoom(roomValue)) {
                throw new ParseException(Room.MESSAGE_INVALID_CHARACTERS);
            }
            room = new Room(roomValue);
        }
        Set<Tag> tags = new LinkedHashSet<>();
        for (String tagValue : values.getAllValues(TAG)) {
            if (!tags.add(ParserUtil.parseTag(tagValue))) {
                throw new ParseException(MESSAGE_DUPLICATE_TAG);
            }
            if (tags.size() > Person.MAX_TAGS) {
                throw new ParseException(Person.MESSAGE_TAG_LIMIT_REACHED);
            }
        }
        return new AddCommand(new Person(name, phone, new Email(""), new Address(""), tags, room));
    }

    private ParseException invalidFormat() {
        return new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
    }
}
