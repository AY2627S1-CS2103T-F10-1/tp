package seedu.address.logic.parser;

import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROOM_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Room;
import seedu.address.model.tag.Tag;

/**
 * A field of a {@link Person} that the user can set from the command line.
 * This is the single place where a field's prefix, validation and accessor are defined, and both
 * {@code add} and {@code edit} are built from {@link #values()}. To make a new field available to both
 * commands, add one constant here and include it in {@link #values()}.
 *
 * @param <T> the type of the field's value
 */
public final class PersonField<T> {
    public static final String MESSAGE_EMPTY_NAME = "Contact name cannot be empty.";
    public static final String MESSAGE_LONG_NAME = "Contact name too long, please shorten it and try again.";
    public static final String MESSAGE_EMPTY_PHONE = "Phone number cannot be empty.";
    public static final String MESSAGE_LONG_PHONE = "Phone number too long, please shorten it and try again.";
    public static final String MESSAGE_INVALID_PHONE = "Phone number contains invalid characters.";
    public static final String MESSAGE_DUPLICATE_TAG = "Contact already has this tag.";

    public static final int MAX_NAME_LENGTH = 100;
    public static final int MAX_PHONE_LENGTH = 20;

    public static final PersonField<Name> NAME = required("name", PREFIX_NAME_COMMAND, "NAME",
            single(PersonField::parseName), Person::getName);
    public static final PersonField<Phone> PHONE = required("phone", PREFIX_PHONE_COMMAND, "PHONE",
            single(PersonField::parsePhone), Person::getPhone);
    public static final PersonField<Room> ROOM = optional("room", PREFIX_ROOM_COMMAND, "ROOM", false,
            single(PersonField::parseRoom), Person::getRoom, () -> new Room(""));
    public static final PersonField<Set<Tag>> TAGS = optional("tags", PREFIX_TAG_COMMAND, "TAG", true,
            PersonField::parseTags, Person::getTags, Collections::emptySet);

    private static final List<PersonField<?>> VALUES = List.of(NAME, PHONE, ROOM, TAGS);

    private final String name;
    private final Prefix prefix;
    private final String placeholder;
    private final boolean isRepeatable;
    private final ValuesParser<T> parser;
    private final Function<Person, T> getter;
    private final Supplier<T> defaultValue;

    private PersonField(String name, Prefix prefix, String placeholder, boolean isRepeatable,
                        ValuesParser<T> parser, Function<Person, T> getter, Supplier<T> defaultValue) {
        this.name = name;
        this.prefix = prefix;
        this.placeholder = placeholder;
        this.isRepeatable = isRepeatable;
        this.parser = parser;
        this.getter = getter;
        this.defaultValue = defaultValue;
    }

    private static <T> PersonField<T> required(String name, Prefix prefix, String placeholder,
                                               ValuesParser<T> parser, Function<Person, T> getter) {
        return new PersonField<>(name, prefix, placeholder, false, parser, getter, null);
    }

    private static <T> PersonField<T> optional(String name, Prefix prefix, String placeholder, boolean isRepeatable,
                                               ValuesParser<T> parser, Function<Person, T> getter,
                                               Supplier<T> defaultValue) {
        return new PersonField<>(name, prefix, placeholder, isRepeatable, parser, getter, defaultValue);
    }

    /** Returns every field a person has, in the order they are shown to the user. */
    public static List<PersonField<?>> values() {
        return VALUES;
    }

    /** Returns the name of this field, which matches the name of the corresponding field in {@link Person}. */
    public String getName() {
        return name;
    }

    public Prefix getPrefix() {
        return prefix;
    }

    /** Returns true if this field may be given more than once in a single command. */
    public boolean isRepeatable() {
        return isRepeatable;
    }

    /** Returns true if {@code add} must be given a value for this field. */
    public boolean isRequired() {
        return defaultValue == null;
    }

    /** Returns the value used when {@code add} is not given this field; empty if the field is required. */
    public Optional<T> getDefaultValue() {
        return Optional.ofNullable(defaultValue).map(Supplier::get);
    }

    /**
     * Returns the part of a command's usage message that describes this field, e.g. {@code [/tag TAG]...}.
     * The field is shown as optional if {@code isOptional} or if {@code add} does not require it.
     */
    public String getUsage(boolean isOptional) {
        String usage = prefix + " " + placeholder;
        return isRequired() && !isOptional ? usage : "[" + usage + "]" + (isRepeatable ? "..." : "");
    }

    /** Returns the value of this field in {@code person}. */
    public T getFrom(Person person) {
        return getter.apply(person);
    }

    /**
     * Parses {@code values}, the values given for this field's prefix. A field that is not repeatable
     * must be given exactly one value.
     *
     * @throws ParseException if the values are invalid.
     */
    public T parse(List<String> values) throws ParseException {
        return parser.parse(values);
    }

    /**
     * Parses the values given for this field in {@code argMultimap}, or returns empty if there are none.
     * If {@code isClearAllowed} is true, a repeatable field given a single empty value is reset to its default
     * value, so that for example {@code /tag} on its own removes all tags.
     *
     * @throws ParseException if the values are invalid.
     */
    public Optional<T> parseFrom(ArgumentMultimap argMultimap, boolean isClearAllowed) throws ParseException {
        List<String> values = argMultimap.getAllValues(prefix);
        if (values.isEmpty()) {
            return Optional.empty();
        }
        if (isClearAllowed && isRepeatable && values.equals(List.of(""))) {
            return getDefaultValue();
        }
        return Optional.of(parse(values));
    }

    @Override
    public String toString() {
        return name;
    }

    @FunctionalInterface
    private interface ValuesParser<T> {
        T parse(List<String> values) throws ParseException;
    }

    @FunctionalInterface
    private interface ValueParser<T> {
        T parse(String value) throws ParseException;
    }

    private static <T> ValuesParser<T> single(ValueParser<T> valueParser) {
        return values -> {
            assert values.size() == 1 : "a field that is not repeatable must be given exactly one value";
            return valueParser.parse(values.getFirst());
        };
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

    private static Room parseRoom(String roomValue) throws ParseException {
        String room = StringUtil.collapseWhitespace(roomValue);
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
     * Parses each value into a tag, keeping the order in which they were given.
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
        return Collections.unmodifiableSet(tags);
    }
}
