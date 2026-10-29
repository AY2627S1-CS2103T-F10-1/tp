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

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

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
    public void execute_roomSearchAfterRankedNameSearch_restoresStoredOrder() {
        Person first = new PersonBuilder(BENSON).withRoom("B-204").build();
        Person second = new PersonBuilder(ELLE).withRoom("B-204").build();
        model.setPerson(BENSON, first);
        model.setPerson(ELLE, second);
        NameFuzzyMatchesPredicate namePredicate = new NameFuzzyMatchesPredicate("Meyer");
        new FindCommand(namePredicate, namePredicate.getRankingComparator()).execute(model);
        assertEquals(second, model.getFilteredPersonList().getFirst());

        new FindCommand(new RoomEqualsPredicate("B-204")).execute(model);

        assertEquals(List.of(first, second), model.getFilteredPersonList());
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

        // same predicate and ranking -> returns true
        FindCommand rankedFirstCommand = new FindCommand(firstPredicate, firstPredicate.getRankingComparator());
        assertTrue(rankedFirstCommand.equals(
                new FindCommand(firstPredicate, firstPredicate.getRankingComparator())));

        // same predicate, ranked and unranked -> returns false
        assertFalse(rankedFirstCommand.equals(findFirstCommand));

        // same predicate, different ranking -> returns false
        assertFalse(rankedFirstCommand.equals(new FindCommand(firstPredicate, secondPredicate.getRankingComparator())));
    }

    @Test
    public void constructor_nullArguments_throwsNullPointerException() {
        NameFuzzyMatchesPredicate predicate = new NameFuzzyMatchesPredicate("Alice");
        assertThrows(NullPointerException.class, () -> new FindCommand(null));
        assertThrows(NullPointerException.class, () -> new FindCommand(null, predicate.getRankingComparator()));
        assertThrows(NullPointerException.class, () -> new FindCommand(predicate, null));
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
    public void execute_exactAndTypoMatches_exactMatchListedFirst() {
        // Elle Meyer matches exactly; the Meiers are 1 typo away and keep their order
        assertNameSearchFinds("Meyer", ELLE, BENSON, DANIEL);
    }

    @Test
    public void execute_prefixAndTypoMatches_prefixListedBeforeTypo() {
        Person limJunJie = new PersonBuilder().withName("Lim Jun Jie").withPhone("91110004").build();
        Person linXiao = new PersonBuilder().withName("Lin Xiao").withPhone("91110005").build();
        Person limahTan = new PersonBuilder().withName("Limah Tan").withPhone("91110006").build();
        for (Person person : List.of(linXiao, limahTan, limJunJie)) {
            model.addPerson(person);
            expectedModel.addPerson(person);
        }
        assertNameSearchFinds("Lim", limJunJie, limahTan, linXiao);
    }

    @Test
    public void execute_listAfterRankedFind_originalOrderRestored() {
        assertNameSearchFinds("Meyer", ELLE, BENSON, DANIEL);
        new ListCommand().execute(model);
        assertEquals(getTypicalAddressBook().getPersonList(), model.getFilteredPersonList());
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
     * Asserts that a ranked search by {@code searchText} lists exactly {@code expectedPersons}, in order,
     * with the matching status message.
     */
    private void assertNameSearchFinds(String searchText, Person... expectedPersons) {
        boolean isNoneExpected = expectedPersons.length == 0;
        String expectedMessage = isNoneExpected
                ? MESSAGE_NO_PERSONS_FOUND
                : String.format(MESSAGE_PERSONS_LISTED_OVERVIEW, expectedPersons.length);
        NameFuzzyMatchesPredicate predicate = new NameFuzzyMatchesPredicate(searchText);
        Comparator<Person> ranking = predicate.getRankingComparator();
        expectedModel.updateFilteredPersonList(predicate, ranking);
        assertCommandSuccess(new FindCommand(predicate, ranking), model, expectedMessage, expectedModel);
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
        assertEquals(getExpectedString(predicate, Optional.empty()), new FindCommand(predicate).toString());

        Comparator<Person> ranking = predicate.getRankingComparator();
        assertEquals(getExpectedString(predicate, Optional.of(ranking)),
                new FindCommand(predicate, ranking).toString());
    }

    /**
     * Returns the expected {@code toString()} of a {@code FindCommand} with {@code predicate} and {@code ranking}.
     */
    private static String getExpectedString(Predicate<Person> predicate, Optional<Comparator<Person>> ranking) {
        return FindCommand.class.getCanonicalName() + "{predicate=" + predicate + ", ranking=" + ranking + "}";
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
