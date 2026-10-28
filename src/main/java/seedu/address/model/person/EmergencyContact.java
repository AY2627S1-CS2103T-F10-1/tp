package seedu.address.model.person;

import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;

import seedu.address.commons.util.StringUtil;

/** Immutable emergency details belonging to a person, independent of the main contact list. */
public final class EmergencyContact {
    public static final int MAX_RELATION_LENGTH = 50;
    public static final String MESSAGE_RELATION_TOO_LONG = "Relation must be at most 50 characters.";

    private final Name name;
    private final Phone phone;
    private final String relation;

    /** Creates emergency details with a required name and phone, and an optional relation. */
    public EmergencyContact(Name name, Phone phone, String relation) {
        requireAllNonNull(name, phone, relation);
        String normalized = StringUtil.collapseWhitespace(relation);
        checkArgument(normalized.length() <= MAX_RELATION_LENGTH, MESSAGE_RELATION_TOO_LONG);
        this.name = name;
        this.phone = phone;
        this.relation = normalized;
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public String getRelation() {
        return relation;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        return other instanceof EmergencyContact contact
                && name.equals(contact.name)
                && phone.equals(contact.phone)
                && relation.equals(contact.relation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phone, relation);
    }

    @Override
    public String toString() {
        return name + (relation.isEmpty() ? "" : "(" + relation + ")") + " " + phone;
    }
}
