package seedu.address.logic.parser;

/**
 * A prefix that marks the beginning of an argument in an arguments string.
 * E.g. 't/' in 'add James t/ friend'.
 */
public class Prefix {
    private final String prefix;
    private final boolean isKeyword;

    /**
     * Creates a prefix that may be directly followed by its value, such as {@code n/} in {@code n/John}.
     */
    public Prefix(String prefix) {
        this(prefix, false);
    }

    /**
     * Creates a prefix. A keyword prefix, such as {@code /name} in {@code find /name John}, is only recognised
     * when it is a standalone word, so {@code /nameJohn} is not treated as the prefix {@code /name}.
     */
    public Prefix(String prefix, boolean isKeyword) {
        this.prefix = prefix;
        this.isKeyword = isKeyword;
    }

    public String getPrefix() {
        return prefix;
    }

    /**
     * Returns true if this prefix must be followed by whitespace or the end of the input to be recognised.
     */
    public boolean isKeyword() {
        return isKeyword;
    }

    @Override
    public String toString() {
        return getPrefix();
    }

    @Override
    public int hashCode() {
        return prefix == null ? 0 : prefix.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Prefix otherPrefix)) {
            return false;
        }

        return prefix.equals(otherPrefix.prefix);
    }
}
