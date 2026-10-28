package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.PersonField;
import seedu.address.logic.parser.PersonFieldValues;
import seedu.address.model.Model;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;

/**
 * Toggles tags of an existing person in the address book: tags the person has are removed,
 * and tags the person does not have are added.
 */
public class ToggleCommand extends Command {

    public static final String COMMAND_WORD = "toggle";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ": Toggles tags of the person identified "
            + "by the index number used in the displayed person list. "
            + "A tag the person has is removed, and a tag the person does not have is added.\n"
            + "Parameters: INDEX (must be a positive integer) " + PersonField.TAGS.getPrefix() + " TAG "
            + "[" + PersonField.TAGS.getPrefix() + " TAG]...\n"
            + "Example: " + COMMAND_WORD + " 1 /tag Resident /tag Floor 3";

    public static final String MESSAGE_TOGGLE_SUCCESS = "Toggled tags of person: %1$s";

    private final Index index;
    private final Set<Tag> tagsToToggle;

    /**
     * @param index of the person in the filtered person list to toggle tags of
     * @param tagsToToggle tags to add or remove, which must not be empty
     */
    public ToggleCommand(Index index, Set<Tag> tagsToToggle) {
        requireNonNull(index);
        requireNonNull(tagsToToggle);

        this.index = index;
        this.tagsToToggle = new LinkedHashSet<>(tagsToToggle);
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {
        requireNonNull(model);
        List<Person> lastShownList = model.getFilteredPersonList();

        if (index.getZeroBased() >= lastShownList.size()) {
            throw new CommandException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }

        Person personToEdit = lastShownList.get(index.getZeroBased());
        Set<Tag> toggledTags = toggle(personToEdit.getTags(), tagsToToggle);
        if (toggledTags.size() > Person.MAX_TAGS) {
            throw new CommandException(Person.MESSAGE_TAG_LIMIT_REACHED);
        }

        PersonFieldValues values = PersonFieldValues.of(personToEdit);
        values.set(PersonField.TAGS, toggledTags);
        Person editedPerson = values.toPerson(personToEdit.getEmail(), personToEdit.getAddress());

        model.setPerson(personToEdit, editedPerson);
        model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
        return new CommandResult(String.format(MESSAGE_TOGGLE_SUCCESS, Messages.format(editedPerson)));
    }

    /**
     * Returns the tags in {@code current} that are not in {@code toggled}, followed by the tags
     * in {@code toggled} that are not in {@code current}.
     */
    private static Set<Tag> toggle(Set<Tag> current, Set<Tag> toggled) {
        Set<Tag> result = new LinkedHashSet<>();
        current.stream().filter(tag -> !toggled.contains(tag)).forEach(result::add);
        toggled.stream().filter(tag -> !current.contains(tag)).forEach(result::add);
        return result;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ToggleCommand otherToggleCommand)) {
            return false;
        }

        return index.equals(otherToggleCommand.index)
                && tagsToToggle.equals(otherToggleCommand.tagsToToggle);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("index", index)
                .add("tagsToToggle", tagsToToggle)
                .toString();
    }
}
