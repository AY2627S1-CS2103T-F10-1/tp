package seedu.address.model.person;

import java.util.function.Predicate;

/**
 * Tests whether a contact has the emergency tag, ignoring case.
 */
public class EmergencyContactPredicate implements Predicate<Person> {

    private static final String EMERGENCY_TAG_NAME = "emergency";

    @Override
    public boolean test(Person person) {
        return person.getTags().stream()
                .anyMatch(tag -> EMERGENCY_TAG_NAME.equalsIgnoreCase(tag.tagName));
    }
}
