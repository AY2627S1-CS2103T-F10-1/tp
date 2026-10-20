package seedu.address.model.person;

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
    private final Room room;
    private final Set<Tag> tags = new LinkedHashSet<>();
    private final Note note;

    /**
     * Every field must be present and not null, and there must be at most {@link #MAX_TAGS} tags.
     * Tags keep the iteration order of {@code tags}.
     */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags) {
        this(name, phone, email, address, tags, new Room(""));
    }

    /** Creates a contact with an optional room number. */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Room room) {
        this(name, phone, email, address, tags, room, new Note(""));
    }

    /** Creates a contact with an optional room number and note. */
    public Person(Name name, Phone phone, Email email, Address address, Set<Tag> tags, Room room, Note note) {
        requireAllNonNull(name, phone, email, address, tags, room, note);
        checkArgument(tags.size() <= MAX_TAGS, MESSAGE_TAG_LIMIT_REACHED);
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.room = room;
        this.tags.addAll(tags);
        this.note = note;
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

    public Room getRoom() {
        return room;
    }

    public Address getAddress() {
        return address;
    }

    public Note getNote() {
        return note;
    }

    /**
     * Returns an immutable tag set, which throws {@code UnsupportedOperationException}
     * if modification is attempted.
     */
    public Set<Tag> getTags() {
        return Collections.unmodifiableSet(tags);
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
                && room.equals(otherPerson.room)
                && tags.equals(otherPerson.tags)
                && note.equals(otherPerson.note);
    }

    @Override
    public int hashCode() {
        // use this method for custom fields hashing instead of implementing your own
        return Objects.hash(name, phone, email, address, tags, room, note);
    }

    @Override
    public String toString() {
        ToStringBuilder builder = new ToStringBuilder(this)
                .add("name", name)
                .add("phone", phone)
                .add("email", email)
                .add("address", address)
                .add("tags", tags);
        if (!room.value.isEmpty()) {
            builder.add("room", room);
        }
        if (!note.value.isEmpty()) {
            builder.add("note", note);
        }
        return builder.toString();
    }

}
