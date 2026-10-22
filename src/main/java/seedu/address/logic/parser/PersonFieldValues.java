package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;

/**
 * Holds a value for some or all of the {@link PersonField}s, so that commands do not need to
 * know which fields a person has.
 */
public class PersonFieldValues {
    private final Map<PersonField<?>, Object> values = new LinkedHashMap<>();

    public PersonFieldValues() {}

    /** Copy constructor. */
    public PersonFieldValues(PersonFieldValues toCopy) {
        values.putAll(toCopy.values);
    }

    /** Returns values holding every editable field of {@code person}. */
    public static PersonFieldValues of(Person person) {
        requireNonNull(person);
        PersonFieldValues personValues = new PersonFieldValues();
        PersonField.values().forEach(field -> personValues.setFrom(field, person));
        return personValues;
    }

    private <T> void setFrom(PersonField<T> field, Person person) {
        set(field, field.getFrom(person));
    }

    /** Sets the value of {@code field}, or removes it if {@code value} is null. */
    public <T> void set(PersonField<T> field, T value) {
        requireNonNull(field);
        if (value == null) {
            values.remove(field);
        } else {
            values.put(field, value);
        }
    }

    /**
     * Parses the values given for {@code field} in {@code argMultimap} and sets them, if any were given.
     * See {@link PersonField#parseFrom(ArgumentMultimap, boolean)}.
     *
     * @throws ParseException if the values are invalid.
     */
    public <T> void setIfGiven(PersonField<T> field, ArgumentMultimap argMultimap, boolean isClearAllowed)
            throws ParseException {
        field.parseFrom(argMultimap, isClearAllowed).ifPresent(value -> set(field, value));
    }

    /** Sets {@code field} to its default value if it has no value, which a required field never does. */
    public <T> void setDefaultIfAbsent(PersonField<T> field) {
        if (get(field).isEmpty()) {
            set(field, field.getDefaultValue().orElseThrow());
        }
    }

    @SuppressWarnings("unchecked") // set(PersonField<T>, T) guarantees the value of a field has its type
    public <T> Optional<T> get(PersonField<T> field) {
        return Optional.ofNullable((T) values.get(field));
    }

    /** Returns true if no field has a value. */
    public boolean isEmpty() {
        return values.isEmpty();
    }

    /** Replaces the values in this object with those that {@code overrides} has. */
    public void putAll(PersonFieldValues overrides) {
        values.putAll(overrides.values);
    }

    /**
     * Creates a person from these values, which must have a value for every field.
     * The legacy {@code email} and {@code address}, which are not fields, are given separately.
     */
    public Person toPerson(Email email, Address address) {
        requireAllNonNull(email, address);
        return new Person(require(PersonField.NAME), require(PersonField.PHONE), email, address,
                require(PersonField.TAGS), require(PersonField.ROOM), require(PersonField.NOTE));
    }

    private <T> T require(PersonField<T> field) {
        return get(field).orElseThrow(() -> new IllegalStateException("No value for " + field));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonFieldValues otherValues)) {
            return false;
        }

        return values.equals(otherValues.values);
    }

    @Override
    public int hashCode() {
        return values.hashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this);
        PersonField.values().forEach(field -> builder.add(field.getName(), values.get(field)));
        return builder.toString();
    }
}
