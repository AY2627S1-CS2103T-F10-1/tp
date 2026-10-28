package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_NO_PERSONS_FOUND;
import static seedu.address.logic.Messages.MESSAGE_PERSONS_LISTED_OVERVIEW;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.ELLE;
import static seedu.address.testutil.TypicalPersons.FIONA;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.NameFuzzyMatchesPredicate;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonHasTagPredicate;
import seedu.address.model.person.PhoneEqualsPredicate;
import seedu.address.model.person.RoomEqualsPredicate;
import seedu.address.testutil.PersonBuilder;

/**
 * Contains integration tests (interaction with the Model) for {@code FindCommand}.
 */
public class FindCommandTest {
    private Model model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
    private Model expectedModel = new ModelManager(getTypicalAddressBook(), new UserPrefs());

    @Test
    public void execute_roomSearch_findsAllActiveMatchesFromArchivedView() {
        Person first = new PersonBuilder(ALICE).withRoom("B-204").build();
        Person second = new PersonBuilder(BENSON).withRoom("b-204").build();
        Person archived = new PersonBuilder(DANIEL).withRoom("B-204").build().withArchive(true);
        model.setPerson(ALICE, first);
        model.setPerson(BENSON, second);
        model.setPerson(DANIEL, archived);
        List<Person> stored = List.copyOf(model.getAddressBook().getPersonList());
        model.updateFilteredPersonList(Person::isArchived);

        CommandResult result = new FindCommand(new RoomEqualsPredicate("b-204")).execute(model);

        assertEquals(String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), result.getFeedbackToUser());
        assertEquals(List.of(first, second), model.getFilteredPersonList());
        assertEquals(stored, model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_roomNotFound_showsEmptyList() {
        CommandResult result = new FindCommand(new RoomEqualsPredicate("unknown")).execute(model);
        assertEquals(MESSAGE_NO_PERSONS_FOUND, result.getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void equals() {
        NameFuzzyMatchesPredicate firstPredicate = new NameFuzzyMatchesPredicate("first");
        NameFuzzyMatchesPredicate secondPredicate = new NameFuzzyMatchesPredicate("second");

        FindCommand findFirstCommand = new FindCommand(firstPredicate);
        FindCommand findSecondCommand = new FindCommand(secondPredicate);

        // same object -> returns true
        assertTrue(findFirstCommand.equals(findFirstCommand));

        // same values -> returns true
        FindCommand findFirstCommandCopy = new FindCommand(firstPredicate);
        assertTrue(findFirstCommand.equals(findFirstCommandCopy));

        // different types -> returns false
        assertFalse(findFirstCommand.equals(1));

        // null -> returns false
        assertFalse(findFirstCommand.equals(null));

        // different person -> returns false
        assertFalse(findFirstCommand.equals(findSecondCommand));
    }

    @Test
    public void constructor_nullPredicate_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FindCommand(null));
    }

    @Test
    public void execute_nameNotInAddressBook_noPersonFound() {
        assertNameSearchFinds("Nobody Here");
    }

    @Test
    public void execute_partialName_similarNamesFound() {
        // "Meyer" is one typo away from "Meier", so Elle Meyer is listed too
        assertNameSearchFinds("Meier", BENSON, DANIEL, ELLE);
    }

    @Test
    public void execute_exactFullName_onePersonFound() {
        assertNameSearchFinds("Fiona Kunz", FIONA);
    }

    @Test
    public void execute_differentCaseName_onePersonFound() {
        assertNameSearchFinds("benson meier", BENSON);
    }

    @Test
    public void execute_nameWithTypo_onePersonFound() {
        assertNameSearchFinds("Fiona Kuns", FIONA);
    }

    /**
     * Asserts that searching by {@code searchText} lists exactly {@code expectedPersons}, in order,
     * with the matching status message.
     */
    private void assertNameSearchFinds(String searchText, Person... expectedPersons) {
        boolean isNoneExpected = expectedPersons.length == 0;
        String expectedMessage = isNoneExpected
                ? MESSAGE_NO_PERSONS_FOUND
                : String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.length);
        NameFuzzyMatchesPredicate predicate = new NameFuzzyMatchesPredicate(searchText);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(new FindCommand(predicate), model, expectedMessage, expectedModel);
        assertEquals(List.of(expectedPersons), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagInAddressBook_multiplePersonsFound() {
        String expectedMessage = String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 3);
        PersonHasTagPredicate predicate = new PersonHasTagPredicate("friends");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, expectedMessage, expectedModel);
        assertEquals(List.of(ALICE, BENSON, DANIEL), model.getFilteredPersonList());
    }

    @Test
    public void execute_tagNotInAddressBook_noPersonFound() {
        PersonHasTagPredicate predicate = new PersonHasTagPredicate("nobody");
        FindCommand command = new FindCommand(predicate);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(command, model, MESSAGE_NO_PERSONS_FOUND, expectedModel);
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void toStringMethod() {
        NameFuzzyMatchesPredicate predicate = new NameFuzzyMatchesPredicate("Alice Bob");
        FindCommand findCommand = new FindCommand(predicate);
        String expected = FindCommand.class.getCanonicalName() + "{predicate=" + predicate + "}";
        assertEquals(expected, findCommand.toString());
    }
    @Test
    public void execute_phoneSearch_findsAllMatchesDespitePreviousFilter() {
        var sharedPhoneContact = new PersonBuilder().withName("Shared Phone")
                .withPhone(ALICE.getPhone().value).build();
        model.addPerson(sharedPhoneContact);
        expectedModel.addPerson(sharedPhoneContact);
        model.updateFilteredPersonList(person -> false);
        PhoneEqualsPredicate predicate = new PhoneEqualsPredicate(ALICE.getPhone().value);
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(new FindCommand(predicate), model,
                String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, 2), expectedModel);
        assertEquals(List.of(ALICE, sharedPhoneContact), model.getFilteredPersonList());
    }

    @Test
    public void execute_phoneNotFound_showsEmptyList() {
        PhoneEqualsPredicate predicate = new PhoneEqualsPredicate("00000000000000000000");
        expectedModel.updateFilteredPersonList(predicate);
        assertCommandSuccess(new FindCommand(predicate), model, MESSAGE_NO_PERSONS_FOUND, expectedModel);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }
}
