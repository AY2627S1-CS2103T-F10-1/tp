package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.VALID_TAG_RESIDENT_FELLOW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.JANE;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.tag.Tag;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) and unit tests for {@code TagCommand}.
 */
public class TagCommandTest {

    private static final Tag RESIDENT_FELLOW = new Tag(VALID_TAG_RESIDENT_FELLOW);

    private final Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    /**
     * Returns a copy of {@code model} in which {@code target} has {@code tag} added.
     */
    private Model createExpectedModel(Person target, Tag tag) {
        Model expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
        expectedModel.setPerson(target, target.withAddedTag(tag));
        return expectedModel;
    }

    /**
     * Asserts that tagging the person named {@code identifier} adds {@code tag} to {@code target},
     * leaving {@code model} equal to {@code expectedModel}.
     */
    private void assertTagSuccess(String identifier, Person target, Tag tag, Model expectedModel) {
        String expectedMessage = String.format(TagCommand.MESSAGE_SUCCESS, target.getName());
        assertCommandSuccess(new TagCommand(identifier, tag), model, expectedMessage, expectedModel);
    }

    private void assertTagSuccess(String identifier, Person target, Tag tag) {
        assertTagSuccess(identifier, target, tag, createExpectedModel(target, tag));
    }

    @Test
    public void execute_exactName_success() {
        assertTagSuccess(BENSON.getName().fullName, BENSON, RESIDENT_FELLOW);
    }

    @Test
    public void execute_nameWithDifferentCaseAndSpacing_success() {
        assertTagSuccess("  benson    MEIER ", BENSON, RESIDENT_FELLOW);
    }

    @Test
    public void execute_personNotInFilteredList_success() {
        Model expectedModel = createExpectedModel(BENSON, RESIDENT_FELLOW);
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        showPersonAtIndex(expectedModel, INDEX_FIRST_PERSON);
        assertFalse(model.getFilteredPersonList().contains(BENSON));

        assertTagSuccess(BENSON.getName().fullName, BENSON, RESIDENT_FELLOW, expectedModel);
    }

    @Test
    public void execute_oneBelowTagLimit_success() {
        Person personWithFourTags = new PersonBuilder().withTags("A", "B", "C", "D").build();
        model.addPerson(personWithFourTags);

        assertTagSuccess(personWithFourTags.getName().fullName, personWithFourTags, RESIDENT_FELLOW);
    }

    @Test
    public void execute_newTag_appendedAfterExistingTags() throws Exception {
        new TagCommand(BENSON.getName().fullName, RESIDENT_FELLOW).execute(model);

        Person taggedBenson = model.getAddressBook().getPersonList().stream()
                .filter(BENSON::isSamePerson)
                .findFirst()
                .orElseThrow();
        List<Tag> tags = List.copyOf(taggedBenson.getTags());
        assertEquals(RESIDENT_FELLOW, tags.get(tags.size() - 1));
    }

    @Test
    public void execute_exactCaseMatchPreferred_tagsExactMatch() {
        Person lowerCaseBenson = new PersonBuilder(BENSON).withName("benson meier").build();
        model.addPerson(lowerCaseBenson);

        assertTagSuccess("benson meier", lowerCaseBenson, RESIDENT_FELLOW);
    }

    @Test
    public void execute_nonExistentName_throwsCommandException() {
        assertCommandFailure(new TagCommand("Nobody Here", RESIDENT_FELLOW), model,
                Messages.MESSAGE_CONTACT_NOT_FOUND);

        // partial name is not a match
        assertCommandFailure(new TagCommand("Benson", RESIDENT_FELLOW), model, Messages.MESSAGE_CONTACT_NOT_FOUND);
    }

    @Test
    public void execute_duplicateTag_throwsCommandException() {
        Tag existingTagDifferentCase = new Tag("OWESMONEY"); // BENSON has "owesMoney"
        assertCommandFailure(new TagCommand(BENSON.getName().fullName, existingTagDifferentCase), model,
                TagCommand.MESSAGE_DUPLICATE_TAG);
    }

    @Test
    public void execute_tagLimitReached_throwsCommandException() {
        model.addPerson(JANE);
        assertCommandFailure(new TagCommand(JANE.getName().fullName, new Tag("Staff")), model,
                Person.MESSAGE_TAG_LIMIT_REACHED);
    }

    @Test
    public void execute_duplicateTagAtLimit_reportsDuplicate() {
        model.addPerson(JANE);
        assertCommandFailure(new TagCommand(JANE.getName().fullName, new Tag("ra")), model,
                TagCommand.MESSAGE_DUPLICATE_TAG);
    }

    @Test
    public void equals() {
        TagCommand tagBenson = new TagCommand("Benson Meier", RESIDENT_FELLOW);

        // same object -> returns true
        assertTrue(tagBenson.equals(tagBenson));

        // same values, after normalizing spaces -> returns true
        assertTrue(tagBenson.equals(new TagCommand(" Benson   Meier ", RESIDENT_FELLOW)));

        // different types -> returns false
        assertFalse(tagBenson.equals(1));

        // null -> returns false
        assertFalse(tagBenson.equals(null));

        // different name -> returns false
        assertFalse(tagBenson.equals(new TagCommand("Alice Pauline", RESIDENT_FELLOW)));

        // different tag -> returns false
        assertFalse(tagBenson.equals(new TagCommand("Benson Meier", new Tag("RA"))));
    }

    @Test
    public void toStringMethod() {
        TagCommand tagCommand = new TagCommand("Benson Meier", RESIDENT_FELLOW);
        String expected = TagCommand.class.getCanonicalName() + "{targetName=Benson Meier, tag="
                + RESIDENT_FELLOW + "}";
        assertEquals(expected, tagCommand.toString());
    }
}
