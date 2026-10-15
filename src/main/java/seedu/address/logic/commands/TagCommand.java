package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.util.StringUtil;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Adds a tag to an existing person identified by their full name, ignoring case and extra spaces.
 */
public class TagCommand extends Command {

    public static final String COMMAND_KEYWORD = "/tag";

    public static final String MESSAGE_USAGE = "<contact identifier> " + COMMAND_KEYWORD + " <tag>";
    public static final String MESSAGE_INVALID_FORMAT = "Invalid command format. Usage: " + MESSAGE_USAGE;
    public static final String MESSAGE_SUCCESS = "Tag added to %1$s.";
    public static final String MESSAGE_DUPLICATE_TAG = "Contact already has this tag.";

    private static final Logger logger = LogsCenter.getLogger(TagCommand.class);

    private final String targetName;
    private final Tag tag;

    /**
     * Creates a TagCommand to add {@code tag} to the person whose full name matches {@code targetName}.
     *
     * @param targetName The full name of the person to tag.
     * @param tag The tag to add.
     */
    public TagCommand(String targetName, Tag tag) {
        requireAllNonNull(targetName, tag);
        this.targetName = StringUtil.collapseWhitespace(targetName);
        this.tag = tag;
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        Person personToTag = findTarget(model.getAddressBook().getPersonList())
                .orElseThrow(() -> new CommandException(Messages.MESSAGE_CONTACT_NOT_FOUND));

        if (personToTag.hasTag(tag)) {
            throw new CommandException(MESSAGE_DUPLICATE_TAG);
        }
        if (personToTag.isTagLimitReached()) {
            throw new CommandException(Person.MESSAGE_TAG_LIMIT_REACHED);
        }

        model.setPerson(personToTag, personToTag.withAddedTag(tag));
        logger.fine("Added " + tag + " to " + personToTag.getName());
        return new CommandResult(String.format(MESSAGE_SUCCESS, personToTag.getName()));
    }

    /**
     * Returns the person whose name matches {@code targetName} exactly, or failing that, ignoring case.
     * Searches all persons, not just those currently displayed, since the person is identified by name.
     */
    private Optional<Person> findTarget(List<Person> persons) {
        Optional<Person> exactMatch = persons.stream()
                .filter(person -> getNormalizedName(person).equals(targetName))
                .findFirst();
        if (exactMatch.isPresent()) {
            return exactMatch;
        }
        return persons.stream()
                .filter(person -> getNormalizedName(person).equalsIgnoreCase(targetName))
                .findFirst();
    }

    private static String getNormalizedName(Person person) {
        return StringUtil.collapseWhitespace(person.getName().fullName);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof TagCommand otherTagCommand)) {
            return false;
        }

        return targetName.equals(otherTagCommand.targetName)
                && tag.equals(otherTagCommand.tag);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("targetName", targetName)
                .add("tag", tag)
                .toString();
    }
}
