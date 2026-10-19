package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.AddCommand;
import seedu.address.logic.commands.EditCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Person;
import seedu.address.testutil.TypicalPersons;

/**
 * Checks that {@code add} and {@code edit} accept exactly the same fields, so a field added to
 * {@link Person} cannot be left out of one of them.
 */
public class PersonFieldTest {
    /** Person fields that are not set from the command line. */
    private static final Set<String> LEGACY_PERSON_FIELDS = Set.of("email", "address");

    /** A valid value for each field. A new field must be given one here. */
    private static final Map<PersonField<?>, String> SAMPLE_VALUES = Map.of(
            PersonField.NAME, "Sample Name",
            PersonField.PHONE, "98765432",
            PersonField.ROOM, "Z-999",
            PersonField.TAGS, "Sample Tag");

    @Test
    public void everyPersonFieldIsRegistered() {
        Set<String> registered = PersonField.values().stream().map(PersonField::getName)
                .collect(Collectors.toSet());
        Set<String> personFields = Stream.of(Person.class.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(Field::getName)
                .filter(name -> !LEGACY_PERSON_FIELDS.contains(name))
                .collect(Collectors.toSet());

        assertEquals(personFields, registered,
                "Register new Person fields in PersonField.values() so that add and edit support them");
    }

    @Test
    public void everyFieldHasSampleValue() {
        assertEquals(Set.copyOf(PersonField.values()), SAMPLE_VALUES.keySet());
    }

    @Test
    public void addAcceptsEveryField() throws Exception {
        StringBuilder args = new StringBuilder(" " + SAMPLE_VALUES.get(PersonField.NAME));
        PersonFieldValues expectedValues = new PersonFieldValues();
        for (PersonField<?> field : PersonField.values()) {
            setSample(expectedValues, field);
            if (field != PersonField.NAME) {
                args.append(" ").append(field.getPrefix()).append(" ").append(SAMPLE_VALUES.get(field));
            }
        }

        AddCommand expected = new AddCommand(expectedValues.toPerson(new Email(""), new Address("")));
        assertEquals(expected, new AddCommandParser().parse(args.toString()));
    }

    @Test
    public void editAcceptsEveryField() throws Exception {
        for (PersonField<?> field : PersonField.values()) {
            EditCommand command = new EditCommandParser().parse(" 1 " + field.getPrefix() + " "
                    + SAMPLE_VALUES.get(field));
            assertTrue(command.toString().contains(field.getName() + "="),
                    "edit should accept " + field.getPrefix());
        }
    }

    @Test
    public void editMessageUsageMentionsEveryField() {
        for (PersonField<?> field : PersonField.values()) {
            assertTrue(EditCommand.MESSAGE_USAGE.contains(field.getPrefix().getPrefix()));
        }
    }

    @Test
    public void valuesRoundTripThroughPerson() {
        for (Person person : TypicalPersons.getTypicalPersons()) {
            assertEquals(person, PersonFieldValues.of(person).toPerson(person.getEmail(), person.getAddress()));
        }
    }

    private static <T> void setSample(PersonFieldValues values, PersonField<T> field) throws ParseException {
        values.set(field, field.parse(List.of(SAMPLE_VALUES.get(field))));
    }
}
