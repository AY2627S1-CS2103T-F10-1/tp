package seedu.address.testutil;

import java.util.List;
import java.util.Map;

import seedu.address.logic.parser.PersonField;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * A valid value, as the user would type it, for each {@link PersonField}.
 * A field that is added to {@code PersonField.values()} must be given a sample here, which makes every test that
 * loops over the fields cover it.
 */
public class PersonFieldSamples {
    private static final Map<PersonField<?>, String> SAMPLE_VALUES = Map.of(
            PersonField.NAME, "Sample Name",
            PersonField.PHONE, "98765432",
            PersonField.ROOM, "Z-999",
            PersonField.TAGS, "Sample Tag");

    /** Returns the sample text for {@code field}, as typed after its prefix. */
    public static String getText(PersonField<?> field) {
        String text = SAMPLE_VALUES.get(field);
        if (text == null) {
            throw new IllegalStateException("Add a sample value for " + field + " to PersonFieldSamples");
        }
        return text;
    }

    /** Returns the sample text for {@code field}, parsed into a value. */
    public static <T> T getValue(PersonField<T> field) throws ParseException {
        return field.parse(List.of(getText(field)));
    }
}
