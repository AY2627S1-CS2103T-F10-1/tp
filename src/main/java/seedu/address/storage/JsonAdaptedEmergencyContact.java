package seedu.address.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;

/** Jackson-friendly emergency details; absent relation values default to empty. */
class JsonAdaptedEmergencyContact {
    private final String name;
    private final String phone;
    private final String relation;

    @JsonCreator
    public JsonAdaptedEmergencyContact(@JsonProperty("name") String name, @JsonProperty("phone") String phone,
            @JsonProperty("relation") String relation) {
        this.name = name;
        this.phone = phone;
        this.relation = relation;
    }

    public JsonAdaptedEmergencyContact(EmergencyContact source) {
        this(source.getName().fullName, source.getPhone().value, source.getRelation());
    }

    /** Converts stored details, reporting invalid or incomplete records as invalid data. */
    public EmergencyContact toModelType() throws IllegalValueException {
        if (name == null || !Name.isValidName(name)) {
            throw new IllegalValueException(Name.MESSAGE_CONSTRAINTS);
        }
        if (phone == null || !Phone.isValidPhone(phone)) {
            throw new IllegalValueException(Phone.MESSAGE_CONSTRAINTS);
        }
        try {
            return new EmergencyContact(new Name(name), new Phone(phone), relation == null ? "" : relation);
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(exception.getMessage(), exception);
        }
    }
}
