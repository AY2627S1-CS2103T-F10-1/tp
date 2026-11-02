package seedu.address.model.person;

import java.util.function.Predicate;

/**
 * Tests whether a contact has the emergency tag using the shared tag-matching rules.
 */
public class EmergencyTagPredicate implements Predicate<Person> {

    private static final String EMERGENCY_TAG_NAME = "emergency";

    @Override
    public boolean test(Person person) {
        return person.getTags().stream()
                .anyMatch(tag -> tag.matchesName(EMERGENCY_TAG_NAME));
    }
}
