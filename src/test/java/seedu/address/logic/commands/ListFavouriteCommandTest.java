package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests listing favourites and updating the displayed list when favourite status changes.
 */
public class ListFavouriteCommandTest {
    private static final Person FIRST_FAVOURITE = new PersonBuilder().withName("Zara Tan").withFavourite(true)
            .withRoom("B-204").withNote("Call after 6 pm").build();
    private static final Person SECOND_FAVOURITE = new PersonBuilder().withName("Amy Tan").withFavourite(true)
            .withTags("Emergency").build();
    private static final Person OTHER_CONTACT = new PersonBuilder().withName("Other Contact")
            .withTags("fav", "favourite").build();

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        model.addPerson(FIRST_FAVOURITE);
        model.addPerson(OTHER_CONTACT);
        model.addPerson(SECOND_FAVOURITE);
    }

    @Test
    public void execute_unfilteredList_showsOnlyFavouritesInStoredOrder() {
        AddressBook before = new AddressBook(model.getAddressBook());
        ObservableList<Person> displayed = model.getFilteredPersonList();

        CommandResult result = new ListFavouriteCommand().execute(model);

        assertEquals(ListFavouriteCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(List.of(FIRST_FAVOURITE, SECOND_FAVOURITE), displayed);
        assertSame(displayed, model.getFilteredPersonList());
        assertEquals(before, model.getAddressBook());
        assertFalse(result.isShowHelp());
        assertFalse(result.isExit());
    }

    @Test
    public void execute_filteredList_searchesAllStoredContacts() {
        model.updateFilteredPersonList(person -> person.equals(OTHER_CONTACT));

        new ListFavouriteCommand().execute(model);

        assertEquals(List.of(FIRST_FAVOURITE, SECOND_FAVOURITE), model.getFilteredPersonList());
    }

    @Test
    public void execute_emptyFilteredList_searchesAllStoredContacts() {
        model.updateFilteredPersonList(person -> false);

        new ListFavouriteCommand().execute(model);

        assertEquals(List.of(FIRST_FAVOURITE, SECOND_FAVOURITE), model.getFilteredPersonList());
    }

    @Test
    public void execute_noFavourites_showsEmptyListAndMessage() {
        model = new ModelManager();
        model.addPerson(OTHER_CONTACT);

        CommandResult result = new ListFavouriteCommand().execute(model);

        assertEquals(ListFavouriteCommand.MESSAGE_EMPTY, result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(OTHER_CONTACT), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_emptyAddressBook_showsEmptyListAndMessage() {
        model = new ModelManager();

        CommandResult result = new ListFavouriteCommand().execute(model);

        assertEquals(ListFavouriteCommand.MESSAGE_EMPTY, result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedCommand_preservesListAndData() {
        new ListFavouriteCommand().execute(model);
        AddressBook before = new AddressBook(model.getAddressBook());

        CommandResult result = new ListFavouriteCommand().execute(model);

        assertEquals(ListFavouriteCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(List.of(FIRST_FAVOURITE, SECOND_FAVOURITE), model.getFilteredPersonList());
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void execute_listAfterFavourites_restoresAllContacts() {
        new ListFavouriteCommand().execute(model);

        new ListCommand().execute(model);

        assertEquals(List.of(FIRST_FAVOURITE, OTHER_CONTACT, SECOND_FAVOURITE), model.getFilteredPersonList());
    }

    @Test
    public void execute_statusChanges_updatesFavouriteListInStoredOrder() {
        new ListFavouriteCommand().execute(model);
        Person newFavourite = OTHER_CONTACT.withFavourite(true);

        model.setPerson(OTHER_CONTACT, newFavourite);
        assertEquals(List.of(FIRST_FAVOURITE, newFavourite, SECOND_FAVOURITE), model.getFilteredPersonList());

        model.setPerson(FIRST_FAVOURITE, FIRST_FAVOURITE.withFavourite(false));
        assertEquals(List.of(newFavourite, SECOND_FAVOURITE), model.getFilteredPersonList());
    }

    @Test
    public void execute_unfav_reindexesFavouritesAndPreservesFilter() throws Exception {
        new ListFavouriteCommand().execute(model);

        new UnfavCommand(INDEX_FIRST_PERSON).execute(model);
        assertEquals(List.of(SECOND_FAVOURITE), model.getFilteredPersonList());

        new UnfavCommand(INDEX_FIRST_PERSON).execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(FIRST_FAVOURITE.withFavourite(false), OTHER_CONTACT,
                SECOND_FAVOURITE.withFavourite(false)), model.getAddressBook().getPersonList());
        assertCommandFailure(new UnfavCommand(INDEX_FIRST_PERSON), model, MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListFavouriteCommand().execute(null));
    }

    @Test
    public void equalsAndHashCode() {
        ListFavouriteCommand command = new ListFavouriteCommand();
        assertEquals(command, command);
        assertEquals(command, new ListFavouriteCommand());
        assertEquals(command.hashCode(), new ListFavouriteCommand().hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
        assertFalse(command.equals(new ListEmergencyCommand()));
    }
}
