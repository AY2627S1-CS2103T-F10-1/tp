package seedu.address.model.person;

import static java.util.Objects.requireNonNull;

import java.util.function.Predicate;

import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Tests that a {@code Person} has a tag with the given name, ignoring case and extra whitespace.
 * Any name is accepted, since tags only exist on contacts; a name no contact has simply matches nobody.
 */
public class PersonHasTagPredicate implements Predicate<Person> {
    private final String tagName;

    /**
     * Creates a predicate matching persons who have a tag named {@code tagName}.
     *
     * @param tagName The tag name to look for; extra whitespace is collapsed.
     */
    public PersonHasTagPredicate(String tagName) {
        requireNonNull(tagName);
        this.tagName = StringUtil.collapseWhitespace(tagName);
    }

    public String getTagName() {
        return tagName;
    }

    @Override
    public boolean test(Person person) {
        return person.getTags().stream()
                .anyMatch(tag -> tag.matchesName(tagName));
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof PersonHasTagPredicate otherPersonHasTagPredicate)) {
            return false;
        }

        return Tag.toComparisonKey(tagName).equals(Tag.toComparisonKey(otherPersonHasTagPredicate.tagName));
    }

    @Override
    public int hashCode() {
        return Tag.toComparisonKey(tagName).hashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).add("tagName", tagName).toString();
    }
}
