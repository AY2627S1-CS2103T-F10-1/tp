package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NAME_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_NOTE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_PHONE_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_ROOM_COMMAND;
import static seedu.address.logic.parser.CliSyntax.PREFIX_TAG_COMMAND;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Room;
import seedu.address.model.tag.Tag;

/**
 * A field of a {@link Person} that the user can set from the command line.
 * This is the single place where a field's prefix, parser and accessor are defined. {@code add} is built from
 * {@link #values()} and {@code edit} from {@link #editableValues()}. To make a new field available to both
 * commands, add one constant here and include it in both lists. Tags are not editable, as they are changed
 * with {@code toggle}.
 *
 * @param <T> the type of the field's value
 */
public final class PersonField<T> {
    public static final PersonField<Name> NAME = required("name", PREFIX_NAME_COMMAND, "NAME",
            single(ParserUtil::parseName), Person::getName);
    public static final PersonField<Phone> PHONE = required("phone", PREFIX_PHONE_COMMAND, "PHONE",
            single(ParserUtil::parsePhone), Person::getPhone);
    public static final PersonField<Room> ROOM = optional("room", PREFIX_ROOM_COMMAND, "ROOM", false,
            single(ParserUtil::parseRoom), Person::getRoom, () -> new Room(""));
    public static final PersonField<Set<Tag>> TAGS = optional("tags", PREFIX_TAG_COMMAND, "TAG", true,
            ParserUtil::parseTags, Person::getTags, Collections::emptySet);
    public static final PersonField<Note> NOTE = optional("note", PREFIX_NOTE_COMMAND, "NOTE", false,
            single(ParserUtil::parseNote), Person::getNote, () -> new Note(""));

    private static final List<PersonField<?>> VALUES = List.of(NAME, PHONE, ROOM, TAGS, NOTE);
    private static final List<PersonField<?>> EDITABLE_VALUES = List.of(NAME, PHONE, ROOM, NOTE);
    private static final Logger logger = LogsCenter.getLogger(PersonField.class);

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

    /** Returns the fields that {@code edit} can change, which are all fields except tags. */
    public static List<PersonField<?>> editableValues() {
        return EDITABLE_VALUES;
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
     * If {@code isClearAllowed} is true, an optional field given a single empty value is reset to its default
     * value, so that for example {@code /room} on its own removes the room.
     *
     * @throws ParseException if the values are invalid.
     */
    public Optional<T> parseFrom(ArgumentMultimap argMultimap, boolean isClearAllowed) throws ParseException {
        requireNonNull(argMultimap);
        List<String> values = argMultimap.getAllValues(prefix);
        if (values.isEmpty()) {
            return Optional.empty();
        }
        boolean isClearRequested = isClearAllowed && !isRequired() && values.equals(List.of(""));
        if (isClearRequested) {
            assert getDefaultValue().isPresent() : "an optional field always has a default value";
            logger.fine("Clearing field " + name + " given with no value");
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
}
