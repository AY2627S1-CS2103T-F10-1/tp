package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalPersons.DANIEL;
import static seedu.address.testutil.TypicalPersons.getTypicalAddressBook;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.model.person.PersonHasTagPredicate;

/**
 * Contains integration tests (interaction with the Model) and unit tests for ListCommand.
 */
public class ListCommandTest {

    private static final PersonHasTagPredicate FRIENDS_FILTER = new PersonHasTagPredicate("friends");
    private static final PersonHasTagPredicate UNUSED_TAG_FILTER = new PersonHasTagPredicate("Hall-Staff");
    private static final List<Person> PERSONS_TAGGED_FRIENDS = List.of(ALICE, BENSON, DANIEL);
    private static final String MESSAGE_FRIENDS_LISTED =
            String.format(ListCommand.MESSAGE_FILTER_SUCCESS, PERSONS_TAGGED_FRIENDS.size(),
                    FRIENDS_FILTER.getTagName());

    private Model model;
    private Model expectedModel;

    @BeforeEach
    public void setUp() {
        model = new ModelManager(getTypicalAddressBook(), new UserPrefs());
        expectedModel = new ModelManager(model.getAddressBook(), new UserPrefs());
    }

    @Test
    public void execute_listIsNotFiltered_showsSameList() {
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_listIsFiltered_showsEverything() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        assertCommandSuccess(new ListCommand(), model, ListCommand.MESSAGE_SUCCESS, expectedModel);
    }

    @Test
    public void execute_tagFilter_showsOnlyTaggedPersons() {
        expectedModel.updateFilteredPersonList(FRIENDS_FILTER);

        assertCommandSuccess(new ListCommand(FRIENDS_FILTER), model, MESSAGE_FRIENDS_LISTED, expectedModel);
        assertEquals(PERSONS_TAGGED_FRIENDS, model.getFilteredPersonList());
    }

    @Test
    public void execute_tagFilterOnFilteredList_filtersWholeAddressBook() {
        showPersonAtIndex(model, INDEX_FIRST_PERSON);
        expectedModel.updateFilteredPersonList(FRIENDS_FILTER);

        assertCommandSuccess(new ListCommand(FRIENDS_FILTER), model, MESSAGE_FRIENDS_LISTED, expectedModel);
    }

    @Test
    public void execute_tagFilterNoMatches_showsEmptyList() {
        expectedModel.updateFilteredPersonList(UNUSED_TAG_FILTER);

        assertCommandSuccess(new ListCommand(UNUSED_TAG_FILTER), model, ListCommand.MESSAGE_NO_CONTACTS_WITH_TAG,
                expectedModel);
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void execute_tagFilterOnEmptyAddressBook_showsNoMatchMessage() {
        Model emptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        Model expectedEmptyModel = new ModelManager(new AddressBook(), new UserPrefs());
        expectedEmptyModel.updateFilteredPersonList(FRIENDS_FILTER);

        assertCommandSuccess(new ListCommand(FRIENDS_FILTER), emptyModel, ListCommand.MESSAGE_NO_CONTACTS_WITH_TAG,
                expectedEmptyModel);
    }

    @Test
    public void equals() {
        ListCommand listAll = new ListCommand();
        ListCommand listFriends = new ListCommand(FRIENDS_FILTER);

        // same object -> returns true
        assertTrue(listFriends.equals(listFriends));

        // same values -> returns true
        assertTrue(listAll.equals(new ListCommand()));
        assertTrue(listFriends.equals(new ListCommand(new PersonHasTagPredicate("FRIENDS"))));

        // different types -> returns false
        assertFalse(listAll.equals(1));

        // null -> returns false
        assertFalse(listAll.equals(null));

        // filtered vs unfiltered -> returns false
        assertFalse(listAll.equals(listFriends));

        // different tag -> returns false
        assertFalse(listFriends.equals(new ListCommand(UNUSED_TAG_FILTER)));
    }

    @Test
    public void toStringMethod() {
        assertEquals(ListCommand.class.getCanonicalName() + "{tagFilter=null}", new ListCommand().toString());
        assertEquals(ListCommand.class.getCanonicalName() + "{tagFilter=" + FRIENDS_FILTER + "}",
                new ListCommand(FRIENDS_FILTER).toString());
    }
}
