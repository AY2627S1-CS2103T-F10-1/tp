package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.EmergencyContact;
import seedu.address.model.person.Name;
import seedu.address.model.person.Person;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ToggleCommand.
 */
public class ToggleCommandTest {

    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_addAndRemoveTags_success() {
        Person person = new PersonBuilder().withName("Zed").withTags("a", "b").build();
        model.addPerson(person);
        Index index = Index.fromOneBased(model.getFilteredPersonList().size());

        // "a" is removed, "c" is added after the remaining tags
        Person expected = new PersonBuilder(person).withTags("b", "c").build();
        assertToggleSuccess(index, person, expected, "Added: C. Removed: A.", "a", "c");
    }

    @Test
    public void execute_onlyAdded_messageListsOnlyAdded() {
        Person person = new PersonBuilder().withName("Zed").withTags().build();
        model.addPerson(person);
        Index index = Index.fromOneBased(model.getFilteredPersonList().size());

        Person expected = new PersonBuilder(person).withTags("Floor 3", "RA").build();
        assertToggleSuccess(index, person, expected, "Added: Floor 3, RA.", "floor  3", "RA");
    }

    @Test
    public void execute_tagsAreCaseInsensitive_success() {
        Person person = new PersonBuilder().withName("Zed").withTags("friends").build();
        model.addPerson(person);
        Index index = Index.fromOneBased(model.getFilteredPersonList().size());

        Person expected = new PersonBuilder(person).withTags().build();
        // the removed tag is reported as it is stored, not as it was typed
        assertToggleSuccess(index, person, expected, "Removed: Friends.", "FRIENDS");
    }

    @Test
    public void execute_emergencyCountsTowardsLimit_failure() {
        Person person = new PersonBuilder().withName("Zed").withTags("emergency", "a", "b", "c", "d").build();
        model.addPerson(person);
        Index index = Index.fromOneBased(model.getFilteredPersonList().size());

        assertCommandFailure(new ToggleCommand(index, tags("e")), model, Person.MESSAGE_TAG_LIMIT_REACHED);
    }

    @Test
    public void execute_removalAtLimit_success() {
        Person person = new PersonBuilder().withName("Zed").withTags("a", "b", "c", "d", "e").build();
        model.addPerson(person);
        Index index = Index.fromOneBased(model.getFilteredPersonList().size());

        // one tag removed and one added keeps the person within the limit
        Person expected = new PersonBuilder(person).withTags("b", "c", "d", "e", "f").build();
        assertToggleSuccess(index, person, expected, "Added: F. Removed: A.", "a", "f");
    }

    @Test
    public void execute_resultExactlyAtLimit_success() {
        Person person = new PersonBuilder().withName("Zed").withTags("a", "b", "c").build();
        model.addPerson(person);
        Index index = Index.fromOneBased(model.getFilteredPersonList().size());

        // 3 tags + 2 added = exactly Person.MAX_TAGS
        Person expected = new PersonBuilder(person).withTags("a", "b", "c", "d", "e").build();
        assertToggleSuccess(index, person, expected, "Added: D, E.", "d", "e");
        assertEquals(Person.MAX_TAGS, model.getFilteredPersonList().get(index.getZeroBased()).getTags().size());

        // one more tag would take the result over the limit, so it is rejected and the tags are unchanged
        assertCommandFailure(new ToggleCommand(index, tags("f")), model, Person.MESSAGE_TAG_LIMIT_REACHED);
        assertEquals(Person.MAX_TAGS, model.getFilteredPersonList().get(index.getZeroBased()).getTags().size());

        // toggling a tag the contact already has still works at the limit
        Person atLimit = model.getFilteredPersonList().get(index.getZeroBased());
        Person expectedAfterRemoval = new PersonBuilder(atLimit).withTags("a", "b", "c", "d").build();
        assertToggleSuccess(index, atLimit, expectedAfterRemoval, "Removed: E.", "e");
    }

    @Test
    public void execute_removeAllAndAddOneAtLimit_success() {
        Person person = new PersonBuilder().withName("Zed").withTags("a", "b", "c", "d", "e").build();
        model.addPerson(person);
        Index index = Index.fromOneBased(model.getFilteredPersonList().size());

        // six tags are given, but only one is left after the toggle
        Person expected = new PersonBuilder(person).withTags("f").build();
        assertToggleSuccess(index, person, expected, "Added: F. Removed: A, B, C, D, E.",
                "a", "b", "c", "d", "e", "f");
    }

    @Test
    public void execute_tooManyTagsAdded_failure() {
        Index index = Index.fromOneBased(1);
        assertCommandFailure(new ToggleCommand(index, tags("a", "b", "c", "d", "e", "f")), model,
                Person.MESSAGE_TAG_LIMIT_REACHED);
    }

    @Test
    public void execute_preservesFavouriteNoteAndEmergencyContact_success() throws Exception {
        Person base = new PersonBuilder().withName("Zed").withTags("a").withNote("Prefers WhatsApp")
                .withFavourite(true).build();
        EmergencyContact emergencyContact = new EmergencyContact(new Name("Bob Choo"), new Phone("91234567"), "Dad");
        Person person = new Person(base.getName(), base.getPhone(), base.getEmail(), base.getAddress(),
                base.getTags(), base.getRoom(), base.getNote(), Optional.of(emergencyContact), false, true);
        model.addPerson(person);
        Index index = Index.fromOneBased(model.getFilteredPersonList().size());

        new ToggleCommand(index, tags("b")).execute(model);

        Person result = model.getFilteredPersonList().get(index.getZeroBased());
        assertTrue(result.isFavourite());
        assertFalse(result.isArchived());
        assertEquals(Optional.of(emergencyContact), result.getEmergencyContact());
        assertEquals("Prefers WhatsApp", result.getNote().value);
    }

    @Test
    public void execute_archivedPerson_failure() {
        Person base = new PersonBuilder().withName("Zed").withTags("a").build();
        Person archived = new Person(base.getName(), base.getPhone(), base.getEmail(), base.getAddress(),
                base.getTags(), base.getRoom(), base.getNote(), Optional.empty(), true, false);
        model.addPerson(archived);
        model.updateFilteredPersonList(Model.PREDICATE_SHOW_ALL_PERSONS);
        Index index = Index.fromOneBased(model.getFilteredPersonList().size());

        assertCommandFailure(new ToggleCommand(index, tags("b")), model, ToggleCommand.MESSAGE_ARCHIVED_PERSON);
    }

    @Test
    public void execute_invalidIndex_failure() {
        Index outOfBounds = Index.fromOneBased(model.getFilteredPersonList().size() + 1);
        assertCommandFailure(new ToggleCommand(outOfBounds, tags("a")), model,
                Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void equals() {
        ToggleCommand command = new ToggleCommand(INDEX_FIRST_PERSON, tags("a"));

        assertTrue(command.equals(new ToggleCommand(INDEX_FIRST_PERSON, tags("a"))));
        assertTrue(command.equals(command));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
        assertNotEquals(command, new ToggleCommand(INDEX_SECOND_PERSON, tags("a")));
        assertNotEquals(command, new ToggleCommand(INDEX_FIRST_PERSON, tags("b")));
    }

    @Test
    public void toStringMethod() {
        ToggleCommand command = new ToggleCommand(INDEX_FIRST_PERSON, tags("a"));
        String expected = ToggleCommand.class.getCanonicalName() + "{index=" + INDEX_FIRST_PERSON
                + ", tagsToToggle=" + tags("a") + "}";
        assertEquals(expected, command.toString());
    }

    private void assertToggleSuccess(Index index, Person original, Person expected, String expectedChanges,
                                     String... tagNames) {
        ToggleCommand command = new ToggleCommand(index, tags(tagNames));
        String expectedMessage = String.format(ToggleCommand.MESSAGE_TOGGLE_SUCCESS, Messages.format(expected),
                expectedChanges);
        Model expectedModel = new ModelManager(new AddressBook(model.getAddressBook()), new UserPrefs());
        expectedModel.setPerson(original, expected);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.copyOf(expected.getTags()), List.copyOf(
                model.getFilteredPersonList().get(index.getZeroBased()).getTags()));
    }

    private static Set<Tag> tags(String... names) {
        Set<Tag> tags = new LinkedHashSet<>();
        for (String name : names) {
            tags.add(new Tag(name));
        }
        return tags;
    }
}
