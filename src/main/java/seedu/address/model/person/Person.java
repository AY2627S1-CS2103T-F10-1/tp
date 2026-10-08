package seedu.address.model.person;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.tag.Tag;

/**
 * Represents a Person in the address book.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Person {

    /** Keeps the contact card scannable; beyond this, tags stop being an at-a-glance identifier. */
    public static final int MAX_TAGS = 5;
    public static final String MESSAGE_TAG_LIMIT_REACHED = "Tag limit reached (max " + MAX_TAGS + ")";

    // Identity fields
    private final Name name;
    private final Phone phone;
    private final Email email;

    // Data fields
    private final Address address;
    private final Set<Tag> tags = new LinkedHashSet<>();

    /**
     * Every field must be present and not null, and there must be at most {@link #MAX_TAGS} tags.
     * Tags keep the iteration order of {@code tags}.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        requireAllNonNull(name, phone, email, address, tags);
        checkArgument(tags.size() <= MAX_TAGS, MESSAGE_TAG_LIMIT_REACHED);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.tags.addAll(tags);
    }

    public Name getName() {
        return name;
    }

    public Phone getPhone() {
        return phone;
    }

    public Email getEmail() {
        return email;
    }

    public Address getAddress() {
        return address;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    /**
     * Returns true if this person already has {@code tag}, ignoring case.
     */
    public boolean hasTag(Tag tag) {
        requireNonNull(tag);
        return tags.contains(tag);
    }

    /**
     * Returns true if this person already has {@link #MAX_TAGS} tags.
     */
    public boolean isTagLimitReached() {
        return tags.size() >= MAX_TAGS;
    }

    /**
     * Returns a copy of this person with {@code tag} added after the existing tags.
     * The caller must ensure the tag is new and the tag limit has not been reached.
     */
    public Person withAddedTag(Tag tag) {
        requireNonNull(tag);
        assert !hasTag(tag) : "Tag should not already be present";
        assert !isTagLimitReached() : "Tag limit should not already be reached";

        Set<Tag> updatedTags = new LinkedHashSet<>(tags);
        updatedTags.add(tag);
        return new Person(name, phone, email, address, updatedTags);
    }

    /**
     * Returns true if both persons have the same name.
     * This defines a weaker notion of equality between two persons.
     */
    public boolean isSamePerson(Person otherPerson) {
        if (otherPerson == this) {
            return true;
        }

        return otherPerson != null
                && otherPerson.getName().equals(getName());
    }

    /**
     * Returns true if both persons have the same identity and data fields.
     * This defines a stronger notion of equality between two persons.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Person otherPerson)) {
            return false;
        }

        return name.equals(otherPerson.name)
                && phone.equals(otherPerson.phone)
                && email.equals(otherPerson.email)
                && address.equals(otherPerson.address)
                && tags.equals(otherPerson.tags);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags)
                .toString();
    }

}
