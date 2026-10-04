package seedu.address.model.person;

import java.util.Objects;

/** Represents an optional remark for a person. */
public class Remark {
    public final String value;

    public Remark(String value) {
        this.value = Objects.requireNonNull(value);
    }

    @Override
    public boolean equals(Object other) {
        return other == this || (other instanceof Remark && value.equals(((Remark) other).value));
    }

    @Override
    public int hashCode() { return value.hashCode(); }

    @Override
    public String toString() { return value; }
}
