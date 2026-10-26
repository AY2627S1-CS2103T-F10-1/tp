package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_COMMAND;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Room;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {

    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    private static final String PREFIX_MARKER = "/";

    private static final Logger logger = LogsCenter.getLogger(ParserUtil.class);

    private static final TextRules NAME_RULES = new TextRules(Name.MAX_LENGTH, Name::isValidName,
            Name.MESSAGE_EMPTY, Name.MESSAGE_TOO_LONG, Name.MESSAGE_CONSTRAINTS);
    private static final TextRules PHONE_RULES = new TextRules(Phone.MAX_LENGTH, Phone::isValidPhone,
            Phone.MESSAGE_EMPTY, Phone.MESSAGE_TOO_LONG, Phone.MESSAGE_INVALID_CHARACTERS);
    private static final TextRules ROOM_RULES = new TextRules(Room.MAX_LENGTH, Room::isValidRoom,
            Room.MESSAGE_EMPTY, Room.MESSAGE_TOO_LONG, Room.MESSAGE_INVALID_CHARACTERS);
    private static final TextRules NOTE_RULES = new TextRules(Note.MAX_LENGTH, Note::isValidNote,
            Note.MESSAGE_EMPTY, Note.MESSAGE_TOO_LONG, Note.MESSAGE_INVALID_CHARACTERS);
    private static final TextRules TAG_RULES = new TextRules(Tag.MAX_LENGTH, Tag::isValidTagName,
            Tag.MESSAGE_EMPTY, Tag.MESSAGE_TOO_LONG, Tag.MESSAGE_INVALID_CHARACTERS);

    /** Normalizes and tokenizes fields following an index, rejecting unknown prefixes and invalid indices. */
    public static ArgumentMultimap parseIndexedArguments(String args, String usage, Prefix... prefixes)
            throws ParseException {
        requireNonNull(args);
        String normalized = " " + StringUtil.collapseWhitespace(args);
        ArgumentMultimap arguments = ArgumentTokenizer.tokenize(normalized, prefixes);
        verifyNoUnknownPrefix(normalized, arguments, usage, prefixes);
        try {
            parseIndex(arguments.getPreamble());
        } catch (ParseException exception) {
            throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage), exception);
        }
        return arguments;
    }

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Returns true if {@code args} has a word that starts with {@code /} but is not one of {@code knownPrefixes},
     * such as a misspelt or wrongly capitalised {@code /phone}. No word of a field value starts with {@code /}, so
     * such a word is always a mistake rather than part of a value.
     */
    public static boolean hasUnknownPrefix(String args, Prefix... knownPrefixes) {
        Set<String> known = Stream.of(knownPrefixes).map(Prefix::getPrefix).collect(Collectors.toSet());
        return Stream.of(args.trim().split("\\s+"))
                .anyMatch(word -> word.startsWith(PREFIX_MARKER) && !known.contains(word));
    }

    /**
     * Checks that {@code args} has no unknown prefix. A note is free text, so an unknown prefix inside it is more
     * likely part of the note than a misspelt prefix, and is reported with the note's own rule instead.
     *
     * @param args Normalized arguments that {@code arguments} was tokenized from.
     * @param arguments Values of {@code knownPrefixes} in {@code args}.
     * @param usage Usage message of the command being parsed.
     * @param knownPrefixes Prefixes that the command accepts.
     * @throws ParseException if {@code args} has an unknown prefix.
     */
    public static void verifyNoUnknownPrefix(String args, ArgumentMultimap arguments, String usage,
            Prefix... knownPrefixes) throws ParseException {
        requireAllNonNull(args, arguments, usage, knownPrefixes);
        if (!hasUnknownPrefix(args, knownPrefixes)) {
            return;
        }
        boolean isInNote = arguments.getAllValues(PREFIX_NOTE_COMMAND).stream()
                .anyMatch(note -> hasUnknownPrefix(note, knownPrefixes));
        if (isInNote) {
            logger.fine("Rejecting a note with a word starting with \"/\": " + args);
            throw new ParseException(Note.MESSAGE_INVALID_CHARACTERS);
        }
        throw new ParseException(String.format(MESSAGE_INVALID_COMMAND_FORMAT, usage));
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is empty, too long or invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        checkText(trimmedName, NAME_RULES);
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is empty, too long or contains invalid characters.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        checkText(trimmedPhone, PHONE_RULES);
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String room} into a {@code Room}.
     * Leading and trailing whitespaces will be trimmed and inner runs of whitespace collapsed.
     *
     * @throws ParseException if the given {@code room} is empty, too long or contains invalid characters.
     */
    public static Room parseRoom(String room) throws ParseException {
        requireNonNull(room);
        String normalizedRoom = StringUtil.collapseWhitespace(room);
        checkText(normalizedRoom, ROOM_RULES);
        return new Room(normalizedRoom);
    }

    /**
     * Parses a {@code String note} into a {@code Note}.
     * Leading and trailing whitespaces will be trimmed and inner runs of whitespace collapsed.
     *
     * @throws ParseException if the given {@code note} is empty, too long or contains {@code /}.
     */
    public static Note parseNote(String note) throws ParseException {
        requireNonNull(note);
        String normalizedNote = StringUtil.collapseWhitespace(note);
        checkText(normalizedNote, NOTE_RULES);
        return new Note(normalizedNote);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (trimmedAddress.isEmpty() || !Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (trimmedEmail.isEmpty() || !Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed and inner runs of whitespace collapsed.
     *
     * @throws ParseException if the given {@code tag} is empty, too long or contains invalid characters.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String normalizedTag = StringUtil.collapseWhitespace(tag);
        checkText(normalizedTag, TAG_RULES);
        return new Tag(normalizedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}, keeping the order in which they were given.
     *
     * @throws ParseException if any tag is invalid, is repeated (ignoring case and spacing),
     *         or there are more than {@link Person#MAX_TAGS} tags.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new LinkedHashSet<>();
        for (String tagName : tags) {
            boolean isNewTag = tagSet.add(parseTag(tagName));
            if (!isNewTag) {
                throw new ParseException(Tag.MESSAGE_DUPLICATE);
            }
        }
        if (tagSet.size() > Person.MAX_TAGS) {
            throw new ParseException(Person.MESSAGE_TAG_LIMIT_REACHED);
        }
        return Collections.unmodifiableSet(tagSet);
    }

    /**
     * Checks {@code value}, which has already been trimmed or collapsed, against {@code rules}.
     * The checks run in a fixed order so the user sees the most basic problem first.
     *
     * @throws ParseException if {@code value} is empty, too long or otherwise invalid.
     */
    private static void checkText(String value, TextRules rules) throws ParseException {
        assert value != null && rules != null : "callers check their input for null before normalizing it";
        assert value.equals(value.trim()) : "value should be trimmed before it is checked";
        if (value.isEmpty()) {
            throw new ParseException(rules.emptyMessage());
        }
        if (value.length() > rules.maxLength()) {
            throw new ParseException(rules.tooLongMessage());
        }
        if (!rules.isValid().test(value)) {
            throw new ParseException(rules.invalidMessage());
        }
    }

    /** The limits and error messages for a text field that must be non-empty. */
    private record TextRules(int maxLength, Predicate<String> isValid, String emptyMessage, String tooLongMessage,
            String invalidMessage) {
        private TextRules {
            requireAllNonNull(isValid, emptyMessage, tooLongMessage, invalidMessage);
            assert maxLength > 0 : "a field must allow at least one character";
        }
    }
}
