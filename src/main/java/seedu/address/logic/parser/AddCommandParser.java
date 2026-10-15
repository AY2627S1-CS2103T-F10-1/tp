package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROOM_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_PHONE_LENGTH = 20;

    private static final String PREFIX_MARKER = "/";
    private static final String WORD_SEPARATOR = " ";
    private static final Set<String> KNOWN_PREFIXES = Stream.of(PREFIX_PHONE_COMMAND, PREFIX_ROOM_COMMAND,
            PREFIX_TAG_COMMAND).map(Prefix::getPrefix).collect(Collectors.toUnmodifiableSet());

    /**
     * Parses the given {@code String} of arguments in the context of the AddCommand
     * and returns an AddCommand object for execution.
     * {@code /phone}, {@code /room} and {@code /tag} may follow the name in any order.
     *
     * @throws ParseException if the user input does not conform to the expected format.
     */
    @Override
    public AddCommand parse(String args) throws ParseException {
        requireNonNull(args);
        String normalized = WORD_SEPARATOR + StringUtil.collapseWhitespace(args);
        ArgumentMultimap argMultimap = ArgumentTokenizer.tokenize(normalized,
                PREFIX_PHONE_COMMAND, PREFIX_ROOM_COMMAND, PREFIX_TAG_COMMAND);
        verifyFormat(normalized, argMultimap);
        assert argMultimap.getValue(PREFIX_PHONE_COMMAND).isPresent() : "verifyFormat should ensure /phone is given";

        Name name = parseName(argMultimap.getPreamble());
        Phone phone = parsePhone(argMultimap.getValue(PREFIX_PHONE_COMMAND).orElseThrow());
        Room room = parseRoom(argMultimap.getValue(PREFIX_ROOM_COMMAND));
        Set<Tag> tags = parseTags(argMultimap.getAllValues(PREFIX_TAG_COMMAND));

        // Email and address remain legacy model fields until the edit workflow is redesigned.
        return new AddCommand(new Person(name, phone, new Email(""), new Address(""), tags, room));
    }

    /**
     * Checks that {@code /phone} is given exactly once, {@code /room} at most once, and no unknown prefix is used.
     */
    private static void verifyFormat(String normalized, ArgumentMultimap argMultimap) throws ParseException {
        boolean isPhoneGivenOnce = argMultimap.getAllValues(PREFIX_PHONE_COMMAND).size() == 1;
        boolean isRoomRepeated = argMultimap.getAllValues(PREFIX_ROOM_COMMAND).size() > 1;
        if (!isPhoneGivenOnce || isRoomRepeated || hasUnknownPrefix(normalized)) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE));
        }
    }

    private static boolean hasUnknownPrefix(String normalized) {
        return Stream.of(normalized.trim().split(WORD_SEPARATOR))
                .anyMatch(word -> word.startsWith(PREFIX_MARKER) && !KNOWN_PREFIXES.contains(word));
    }

    private static Name parseName(String nameValue) throws ParseException {
        if (nameValue.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_NAME);
        }
        if (nameValue.length() > MAX_NAME_LENGTH) {
            throw new ParseException(MESSAGE_LONG_NAME);
        }
        return ParserUtil.parseName(nameValue);
    }

    private static Phone parsePhone(String phoneValue) throws ParseException {
        if (phoneValue.isEmpty()) {
            throw new ParseException(MESSAGE_EMPTY_PHONE);
        }
        if (phoneValue.length() > MAX_PHONE_LENGTH) {
            throw new ParseException(MESSAGE_LONG_PHONE);
        }
        if (!Phone.isValidPhone(phoneValue)) {
            throw new ParseException(MESSAGE_INVALID_PHONE);
        }
        return ParserUtil.parsePhone(phoneValue);
    }

    /**
     * Parses the optional room value; an absent {@code /room} gives an empty room.
     */
    private static Room parseRoom(Optional<String> roomValue) throws ParseException {
        if (roomValue.isEmpty()) {
            return new Room("");
        }
        String room = roomValue.get();
        if (room.isEmpty()) {
            throw new ParseException(Room.MESSAGE_EMPTY);
        }
        if (room.length() > Room.MAX_LENGTH) {
            throw new ParseException(Room.MESSAGE_TOO_LONG);
        }
        if (!Room.isValidRoom(room)) {
            throw new ParseException(Room.MESSAGE_INVALID_CHARACTERS);
        }
        return new Room(room);
    }

    /**
     * Parses each {@code /tag} value into a tag, keeping the order in which they were given.
     *
     * @throws ParseException if any tag is invalid, repeated, or there are more than {@link Person#MAX_TAGS} tags.
     */
    private static Set<Tag> parseTags(List<String> tagValues) throws ParseException {
        Set<Tag> tags = new LinkedHashSet<>();
        for (String tagValue : tagValues) {
            boolean isNewTag = tags.add(ParserUtil.parseTag(tagValue));
            if (!isNewTag) {
                throw new ParseException(MESSAGE_DUPLICATE_TAG);
            }
        }
        if (tags.size() > Person.MAX_TAGS) {
            throw new ParseException(Person.MESSAGE_TAG_LIMIT_REACHED);
        }
        return tags;
    }
}
