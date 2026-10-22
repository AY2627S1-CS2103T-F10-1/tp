package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.showPersonAtIndex;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/**
 * Tests the shared behaviour of marking and unmarking favourite contacts.
 */
public class FavouriteCommandsTest {
    private static final Person FIRST = new PersonBuilder(ALICE).withRoom("B-204").withNote("Call after 6 pm")
            .withTags("Resident", "Floor 3").build();
    private static final Person SECOND = new PersonBuilder(BOB).withRoom("C-101").withNote("Prefers WhatsApp").build();

    @Test
    public void constructor_nullIndex_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new FavCommand(null));
        assertThrows(NullPointerException.class, () -> new UnfavCommand(null));
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        for (boolean isFavourite : new boolean[] {false, true}) {
            assertThrows(NullPointerException.class, () ->
                    createCommand(isFavourite, INDEX_FIRST_PERSON).execute(null));
        }
    }

    @Test
    public void execute_validIndex_changesOnlyTargetFavouriteStatus() throws Exception {
        for (boolean isFavourite : new boolean[] {false, true}) {
            Model model = createModel(!isFavourite);
            Person target = model.getFilteredPersonList().getFirst();
            ObservableList<Person> displayed = model.getFilteredPersonList();

            CommandResult result = createCommand(isFavourite, INDEX_FIRST_PERSON).execute(model);

            assertEquals(String.format(isFavourite ? FavCommand.MESSAGE_SUCCESS : UnfavCommand.MESSAGE_SUCCESS,
                    Messages.format(target)), result.getFeedbackToUser());
            assertEquals(List.of(FIRST.withFavourite(isFavourite), SECOND.withFavourite(!isFavourite)),
                    model.getAddressBook().getPersonList());
            assertEquals(model.getAddressBook().getPersonList(), displayed);
            assertEquals(List.copyOf(target.getTags()), List.copyOf(displayed.getFirst().getTags()));
            assertSame(displayed, model.getFilteredPersonList());
            assertFalse(result.isShowHelp());
            assertFalse(result.isExit());
        }
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndPreservesFilter() throws Exception {
        for (boolean isFavourite : new boolean[] {false, true}) {
            Model model = createModel(!isFavourite);
            showPersonAtIndex(model, INDEX_SECOND_PERSON);
            ObservableList<Person> displayed = model.getFilteredPersonList();

            createCommand(isFavourite, INDEX_FIRST_PERSON).execute(model);

            assertEquals(List.of(FIRST.withFavourite(!isFavourite), SECOND.withFavourite(isFavourite)),
                    model.getAddressBook().getPersonList());
            assertEquals(List.of(SECOND.withFavourite(isFavourite)), displayed);
            assertSame(displayed, model.getFilteredPersonList());
        }
    }

    @Test
    public void execute_statusBasedFilter_updatesListWithoutResettingFilter() throws Exception {
        for (boolean isFavourite : new boolean[] {false, true}) {
            Model model = createModel(!isFavourite);
            model.updateFilteredPersonList(person -> person.isFavourite() != isFavourite);
            ObservableList<Person> displayed = model.getFilteredPersonList();

            createCommand(isFavourite, INDEX_FIRST_PERSON).execute(model);

            assertEquals(List.of(FIRST.withFavourite(isFavourite), SECOND.withFavourite(!isFavourite)),
                    model.getAddressBook().getPersonList());
            assertEquals(List.of(SECOND.withFavourite(!isFavourite)), displayed);
            assertSame(displayed, model.getFilteredPersonList());
        }
    }

    @Test
    public void execute_alreadyInRequestedState_preservesDataAndFilter() throws Exception {
        for (boolean isFavourite : new boolean[] {false, true}) {
            Model model = createModel(isFavourite);
            showPersonAtIndex(model, INDEX_SECOND_PERSON);
            Person target = model.getFilteredPersonList().getFirst();
            AddressBook before = new AddressBook(model.getAddressBook());

            CommandResult result = createCommand(isFavourite, INDEX_FIRST_PERSON).execute(model);

            assertEquals(String.format(isFavourite ? FavCommand.MESSAGE_ALREADY_FAVOURITE
                    : UnfavCommand.MESSAGE_NOT_FAVOURITE, Messages.format(target)), result.getFeedbackToUser());
            assertEquals(before, model.getAddressBook());
            assertEquals(List.of(target), model.getFilteredPersonList());
            assertSame(target, model.getFilteredPersonList().getFirst());
        }
    }

    @Test
    public void execute_repeatedCommand_doesNotToggleStatus() throws Exception {
        for (boolean isFavourite : new boolean[] {false, true}) {
            Model model = createModel(!isFavourite);
            Command command = createCommand(isFavourite, INDEX_FIRST_PERSON);
            command.execute(model);
            Person target = model.getFilteredPersonList().getFirst();

            command.execute(model);

            assertEquals(isFavourite, target.isFavourite());
            assertSame(target, model.getFilteredPersonList().getFirst());
        }
    }

    @Test
    public void execute_invalidIndex_preservesDataAndFilter() {
        for (boolean isFavourite : new boolean[] {false, true}) {
            Model model = createModel(!isFavourite);
            assertCommandFailure(createCommand(isFavourite, Index.fromOneBased(3)), model,
                    MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            assertCommandFailure(createCommand(isFavourite, Index.fromOneBased(Integer.MAX_VALUE)), model,
                    MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            showPersonAtIndex(model, INDEX_SECOND_PERSON);
            assertCommandFailure(createCommand(isFavourite, INDEX_SECOND_PERSON), model,
                    MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
    }

    @Test
    public void execute_emptyList_preservesData() {
        for (boolean isFavourite : new boolean[] {false, true}) {
            assertCommandFailure(createCommand(isFavourite, INDEX_FIRST_PERSON), new ModelManager(),
                    MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
            Model model = createModel(!isFavourite);
            model.updateFilteredPersonList(person -> false);
            assertCommandFailure(createCommand(isFavourite, INDEX_FIRST_PERSON), model,
                    MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        }
    }

    @Test
    public void equalsAndHashCode() {
        for (boolean isFavourite : new boolean[] {false, true}) {
            Command command = createCommand(isFavourite, INDEX_FIRST_PERSON);
            Command equalCommand = createCommand(isFavourite, Index.fromOneBased(1));
            assertEquals(command, command);
            assertEquals(command, equalCommand);
            assertEquals(command.hashCode(), equalCommand.hashCode());
            assertFalse(command.equals(null));
            assertFalse(command.equals("fav"));
            assertFalse(command.equals(createCommand(isFavourite, INDEX_SECOND_PERSON)));
            assertFalse(command.equals(createCommand(!isFavourite, INDEX_FIRST_PERSON)));
        }
    }

    @Test
    public void toStringMethod() {
        assertEquals(FavCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}",
                new FavCommand(INDEX_FIRST_PERSON).toString());
        assertEquals(UnfavCommand.class.getCanonicalName() + "{targetIndex=" + INDEX_FIRST_PERSON + "}",
                new UnfavCommand(INDEX_FIRST_PERSON).toString());
    }

    private Model createModel(boolean isFavourite) {
        AddressBook book = new AddressBook();
        book.addPerson(FIRST.withFavourite(isFavourite));
        book.addPerson(SECOND.withFavourite(isFavourite));
        return new ModelManager(book, new UserPrefs());
    }

    private Command createCommand(boolean isFavourite, Index index) {
        return isFavourite ? new FavCommand(index) : new UnfavCommand(index);
    }
}
