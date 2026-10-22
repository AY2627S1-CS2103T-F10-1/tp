package seedu.address.storage;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.StringUtil;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Note;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.person.Room;
import seedu.address.model.tag.Tag;

/**
 * Jackson-friendly version of {@link Person}.
 */
class JsonAdaptedPerson {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Person's %s field is missing!";

    private final String name;
    private final String phone;
    private final String email;
    private final String address;
    private final String room;
    private final boolean archive;
    private final List<JsonAdaptedTag> tags = new ArrayList<>();
    private final String note;
    private final JsonAdaptedEmergencyContact emergencyContact;

    /**
     * Constructs a {@code JsonAdaptedPerson} with the given person details.
     */
    public JsonAdaptedPerson(String name, String phone, String email, String address, List<JsonAdaptedTag> tags) {
        this(name, phone, email, address, tags, null, null);
    }

    /** Creates an adapter without a note, emergency contact, or archive state. */
    public JsonAdaptedPerson(String name, String phone, String email, String address,
            List<JsonAdaptedTag> tags, String room) {
        this(name, phone, email, address, tags, room, null, null, false);
    }

    /** Creates an adapter with archive state. */
    public JsonAdaptedPerson(String name, String phone, String email, String address,
            List<JsonAdaptedTag> tags, String room, boolean archive) {
        this(name, phone, email, address, tags, room, null, null, archive);
    }

    /** Creates an adapter with a note. */
    public JsonAdaptedPerson(String name, String phone, String email, String address,
            List<JsonAdaptedTag> tags, String room, String note) {
        this(name, phone, email, address, tags, room, note, null, false);
    }

    /** Creates an adapter with a note and archive state. */
    public JsonAdaptedPerson(String name, String phone, String email, String address,
            List<JsonAdaptedTag> tags, String room, String note, boolean archive) {
        this(name, phone, email, address, tags, room, note, null, archive);
    }

    /** Creates an adapter with a note and emergency contact. */
    public JsonAdaptedPerson(String name, String phone, String email, String address,
            List<JsonAdaptedTag> tags, String room, String note,
            JsonAdaptedEmergencyContact emergencyContact) {
        this(name, phone, email, address, tags, room, note, emergencyContact, false);
    }

    @JsonCreator
    public JsonAdaptedPerson(@JsonProperty("name") String name,
            @JsonProperty("phone") String phone,
            @JsonProperty("email") String email,
            @JsonProperty("address") String address,
            @JsonProperty("tags") List<JsonAdaptedTag> tags,
            @JsonProperty("room") String room,
            @JsonProperty("note") String note,
            @JsonProperty("emergencyContact") JsonAdaptedEmergencyContact emergencyContact,
            @JsonProperty("archive") boolean archive) {
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.room = room;
        this.note = note;
        this.emergencyContact = emergencyContact;
        this.archive = archive;
        if (tags != null) {
            this.tags.addAll(tags);
        }
    }

    /**
     * Converts a given {@code Person} into this class for Jackson use.
     */
    public JsonAdaptedPerson(Person source) {
        name = source.getName().fullName;
        phone = source.getPhone().value;
        email = source.getEmail().value;
        address = source.getAddress().value;
        room = source.getRoom().value;
        archive = source.isArchived();
        note = source.getNote().value;
        emergencyContact = source.getEmergencyContact().map(JsonAdaptedEmergencyContact::new).orElse(null);
        tags.addAll(source.getTags().stream()
                .map(JsonAdaptedTag::new)
                .collect(Collectors.toList()));
    }

    /**
     * Converts this Jackson-friendly adapted person object into the model's {@code Person} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted person.
     */
    public Person toModelType() throws IllegalValueException {
        final List<Tag> personTags = new ArrayList<>();
        for (JsonAdaptedTag tag : tags) {
            personTags.add(tag.toModelType());
        }

        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Name.class.getSimpleName()));
        }
        if (!Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        final Name modelName = new Name(name);

        if (phone == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Phone.class.getSimpleName()));
        }
        if (!Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        final Phone modelPhone = new Phone(phone);

        if (email == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Email.class.getSimpleName()));
        }
        if (!Email.isValidEmail(email)) {
            throw new IllegalValueException(Email.MESSAGE_CONSTRAINTS);
        }
        final Email modelEmail = new Email(email);

        if (address == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, Address.class.getSimpleName()));
        }
        if (!Address.isValidAddress(address)) {
            throw new IllegalValueException(Address.MESSAGE_CONSTRAINTS);
        }
        final Address modelAddress = new Address(address);

        final Set<Tag> modelTags = new LinkedHashSet<>(personTags);
        if (modelTags.size() > Person.MAX_TAGS) {
            throw new IllegalValueException(Person.MESSAGE_TAG_LIMIT_REACHED);
        }
        Optional<EmergencyContact> modelEmergencyContact = emergencyContact == null
                ? Optional.empty() : Optional.of(emergencyContact.toModelType());

        return new Person(modelName, modelPhone, modelEmail, modelAddress,
                modelTags, toModelRoom(), toModelNote(), modelEmergencyContact, archive);
    }

    /** Returns the stored room; a file without one gives a contact without a room. */
    private Room toModelRoom() throws IllegalValueException {
        String roomValue = normalizeOptional(room);
        if (!Room.isValidRoom(roomValue)) {
            throw new IllegalValueException(Room.MESSAGE_INVALID_CHARACTERS);
        }
        return new Room(roomValue);
    }

    /** Returns the stored note; a file without one gives a contact without a note. */
    private Note toModelNote() throws IllegalValueException {
        String noteValue = normalizeOptional(note);
        if (!Note.isValidNote(noteValue)) {
            throw new IllegalValueException(Note.MESSAGE_CONSTRAINTS);
        }
        return new Note(noteValue);
    }

    private static String normalizeOptional(String value) {
        return value == null ? "" : StringUtil.collapseWhitespace(value);
    }

}
