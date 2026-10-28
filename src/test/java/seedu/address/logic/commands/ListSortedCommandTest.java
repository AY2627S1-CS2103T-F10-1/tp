package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
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

public class ListSortedCommandTest {
    private static final Person ZARA = new PersonBuilder().withName("Zara Tan").build();
    private static final Person AMY = new PersonBuilder().withName("Amy Bee").build();
    private static final Person LOWERCASE_AMY = new PersonBuilder().withName("amy Bee").build();
    private static final Person ARCHIVED = new PersonBuilder().withName("Aaron Tan").build().withArchive(true);

    private Model model;

    @BeforeEach
    public void setUp() {
        model = new ModelManager();
        for (Person person : List.of(ZARA, AMY, LOWERCASE_AMY, ARCHIVED)) {
            model.addPerson(person);
        }
    }

    @Test
    public void execute_fromArchivedView_sortsAllActiveContactsWithoutChangingStorage() {
        AddressBook before = new AddressBook(model.getAddressBook());
        ObservableList<Person> displayed = model.getFilteredPersonList();
        model.updateFilteredPersonList(Person::isArchived);

        CommandResult result = new ListSortedCommand().execute(model);

        assertEquals(ListSortedCommand.MESSAGE_SUCCESS, result.getFeedbackToUser());
        assertEquals(List.of(AMY, ZARA, LOWERCASE_AMY), displayed);
        assertSame(displayed, model.getFilteredPersonList());
        assertEquals(before, model.getAddressBook());
    }

    @Test
    public void execute_noActiveContacts_showsEmptyMessage() {
        model = new ModelManager();
        model.addPerson(ARCHIVED);
        assertEquals(ListCommand.MESSAGE_EMPTY, new ListSortedCommand().execute(model).getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedCommand_keepsSameOrder() {
        new ListSortedCommand().execute(model);
        new ListSortedCommand().execute(model);
        assertEquals(List.of(AMY, ZARA, LOWERCASE_AMY), model.getFilteredPersonList());
    }

    @Test
    public void execute_listAfterSorted_restoresStoredOrder() {
        new ListSortedCommand().execute(model);
        new ListCommand().execute(model);
        assertEquals(List.of(ZARA, AMY, LOWERCASE_AMY), model.getFilteredPersonList());
    }

    @Test
    public void execute_findAfterSorted_restoresStoredOrder() {
        new ListSortedCommand().execute(model);
        new FindCommand(person -> true).execute(model);
        assertEquals(List.of(ZARA, AMY, LOWERCASE_AMY), model.getFilteredPersonList());
    }

    @Test
    public void execute_deleteAfterSorted_usesDisplayedIndex() throws Exception {
        new ListSortedCommand().execute(model);
        new DeleteCommand(INDEX_FIRST_PERSON).execute(model);
        assertEquals(List.of(ZARA, LOWERCASE_AMY, ARCHIVED), model.getAddressBook().getPersonList());
        assertEquals(List.of(ZARA, LOWERCASE_AMY), model.getFilteredPersonList());
    }

    @Test
    public void execute_archiveAfterSorted_usesDisplayedIndexAndReindexes() throws Exception {
        new ListSortedCommand().execute(model);
        new ArchiveCommand(INDEX_FIRST_PERSON).execute(model);
        assertEquals(List.of(ZARA, AMY.withArchive(true), LOWERCASE_AMY, ARCHIVED),
                model.getAddressBook().getPersonList());
        assertEquals(List.of(ZARA, LOWERCASE_AMY), model.getFilteredPersonList());
    }

    @Test
    public void execute_updatedName_reordersLiveView() {
        new ListSortedCommand().execute(model);
        Person renamed = new PersonBuilder(ZARA).withName("Adam Tan").build();
        model.setPerson(ZARA, renamed);
        assertEquals(List.of(renamed, AMY, LOWERCASE_AMY), model.getFilteredPersonList());
    }

    @Test
    public void execute_nullModel_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ListSortedCommand().execute(null));
    }

    @Test
    public void equalsAndHashCode() {
        ListSortedCommand command = new ListSortedCommand();
        assertEquals(command, new ListSortedCommand());
        assertEquals(command.hashCode(), new ListSortedCommand().hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ListCommand()));
    }
}
