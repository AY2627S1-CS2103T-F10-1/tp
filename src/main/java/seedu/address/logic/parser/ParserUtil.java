package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
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
     * such as a misspelt or wrongly capitalised {@code /phone}. No field value contains {@code /}, so such a word
     * is always a mistake rather than part of a value.
     */
    public static boolean hasUnknownPrefix(String args, Prefix... knownPrefixes) {
        Set<String> known = Stream.of(knownPrefixes).map(Prefix::getPrefix).collect(Collectors.toSet());
        return Stream.of(args.trim().split("\\s+"))
                .anyMatch(word -> word.startsWith(PREFIX_MARKER) && !known.contains(word));
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
        if (trimmedName.isEmpty()) {
            throw new ParseException(Name.MESSAGE_EMPTY);
        }
        if (trimmedName.length() > Name.MAX_LENGTH) {
            throw new ParseException(Name.MESSAGE_TOO_LONG);
        }
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
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
        if (trimmedPhone.isEmpty()) {
            throw new ParseException(Phone.MESSAGE_EMPTY);
        }
        if (trimmedPhone.length() > Phone.MAX_LENGTH) {
            throw new ParseException(Phone.MESSAGE_TOO_LONG);
        }
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_INVALID_CHARACTERS);
        }
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
        if (normalizedRoom.isEmpty()) {
            throw new ParseException(Room.MESSAGE_EMPTY);
        }
        if (normalizedRoom.length() > Room.MAX_LENGTH) {
            throw new ParseException(Room.MESSAGE_TOO_LONG);
        }
        if (!Room.isValidRoom(normalizedRoom)) {
            throw new ParseException(Room.MESSAGE_INVALID_CHARACTERS);
        }
        return new Room(normalizedRoom);
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
        if (normalizedTag.isEmpty()) {
            throw new ParseException(Tag.MESSAGE_EMPTY);
        }
        if (normalizedTag.length() > Tag.MAX_LENGTH) {
            throw new ParseException(Tag.MESSAGE_TOO_LONG);
        }
        if (!Tag.isValidTagName(normalizedTag)) {
            throw new ParseException(Tag.MESSAGE_INVALID_CHARACTERS);
        }
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
}
