package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
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
import seedu.address.testutil.PersonFieldSamples;
import seedu.address.testutil.TypicalPersons;

/**
 * Checks that {@code add} and {@code edit} accept exactly the same fields, so a field added to
 * {@link Person} cannot be left out of one of them.
 */
public class PersonFieldTest {
    /** Person fields managed outside add/edit arguments. */
    private static final Set<String> NON_EDITABLE_PERSON_FIELDS =
            Set.of("email", "address", "archive", "emergencyContact", "isFavourite");

    @Test
    public void everyPersonFieldIsRegistered() {
        Set<String> registered = PersonField.values().stream().map(PersonField::getName)
                .collect(Collectors.toSet());
        Set<String> personFields = Stream.of(Person.class.getDeclaredFields())
                .filter(field -> !Modifier.isStatic(field.getModifiers()))
                .map(Field::getName)
                .filter(name -> !NON_EDITABLE_PERSON_FIELDS.contains(name))
                .collect(Collectors.toSet());

        assertEquals(personFields, registered,
                "Register new Person fields in PersonField.values() so that add and edit support them");
    }

    @Test
    public void everyFieldHasSampleValue() {
        PersonField.values().forEach(PersonFieldSamples::getText);
    }

    @Test
    public void addAcceptsEveryField() throws Exception {
        StringBuilder args = new StringBuilder(" " + PersonFieldSamples.getText(PersonField.NAME));
        PersonFieldValues expectedValues = new PersonFieldValues();
        for (PersonField<?> field : PersonField.values()) {
            setSample(expectedValues, field);
            if (field != PersonField.NAME) {
                args.append(" ").append(field.getPrefix()).append(" ").append(PersonFieldSamples.getText(field));
            }
        }

        AddCommand expected = new AddCommand(expectedValues.toPerson(new Email(""), new Address("")));
        assertEquals(expected, new AddCommandParser().parse(args.toString()));
    }

    @Test
    public void editAcceptsEveryField() throws Exception {
        for (PersonField<?> field : PersonField.values()) {
            EditCommand command = new EditCommandParser().parse(" 1 " + field.getPrefix() + " "
                    + PersonFieldSamples.getText(field));
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

    @Test
    public void parseFrom_nullArgMultimap_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> PersonField.NOTE.parseFrom(null, true));
    }

    @Test
    public void toPerson_nullLegacyField_throwsNullPointerException() {
        PersonFieldValues values = PersonFieldValues.of(TypicalPersons.ALICE);
        assertThrows(NullPointerException.class, () -> values.toPerson(null, new Address("")));
        assertThrows(NullPointerException.class, () -> values.toPerson(new Email(""), null));
    }

    private static <T> void setSample(PersonFieldValues values, PersonField<T> field) throws ParseException {
        values.set(field, field.parse(List.of(PersonFieldSamples.getText(field))));
    }
}
