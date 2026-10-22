package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/** Exercises archive visibility through the real command parser and model. */
public class ArchiveVisibilityTest {
    private final AddressBookParser parser = new AddressBookParser();
    private final Person active = new PersonBuilder(ALICE).withTags("emergency").build();
    private final Person archived = new PersonBuilder(BOB).withTags("emergency").build().withArchive(true);
    private Model model;

    @BeforeEach
    public void setUp() {
        AddressBook book = new AddressBook();
        book.addPerson(active);
        book.addPerson(archived);
        model = new ModelManager(book, new UserPrefs());
    }

    @Test
    public void startup_onlyActiveContactsVisible() {
        assertEquals(List.of(active), model.getFilteredPersonList());
    }

    @Test
    public void listArchived_thenList_switchesViewsWithoutRemovingData() throws Exception {
        parser.parseCommand("list archived").execute(model);
        assertEquals(List.of(archived), model.getFilteredPersonList());
        parser.parseCommand("list").execute(model);
        assertEquals(List.of(active), model.getFilteredPersonList());
        assertEquals(List.of(active, archived), model.getAddressBook().getPersonList());
    }

    @Test
    public void find_allSearchTypes_excludeArchivedContacts() throws Exception {
        for (String query : List.of("/name " + archived.getName().fullName,
                "/phone " + archived.getPhone().value, "/tag emergency")) {
            parser.parseCommand("find " + query).execute(model);
            assertTrue(model.getFilteredPersonList().stream().noneMatch(Person::isArchived));
        }
        assertEquals(List.of(active), model.getFilteredPersonList());
    }

    @Test
    public void find_fromArchivedView_searchesActiveContacts() throws Exception {
        parser.parseCommand("list archived").execute(model);
        parser.parseCommand("find /tag emergency").execute(model);
        assertEquals(List.of(active), model.getFilteredPersonList());
    }

    @Test
    public void emergencyList_excludesArchivedContacts() throws Exception {
        parser.parseCommand("list emerg").execute(model);
        assertEquals(List.of(active), model.getFilteredPersonList());
    }

    @Test
    public void archivedList_emptyAndWhitespaceHandled() throws Exception {
        model = new ModelManager();
        CommandResult result = parser.parseCommand("list   archived  ").execute(model);
        assertEquals("No archived contacts found.", result.getFeedbackToUser());
        assertTrue(model.getFilteredPersonList().isEmpty());
    }

    @Test
    public void archivedList_invalidArgumentsRejected() {
        for (String command : List.of("list archived 1", "list archived /tag emergency", "list Archived")) {
            assertThrows(ParseException.class, () -> parser.parseCommand(command));
        }
    }

    @Test
    public void activeList_onlyArchivedContacts_reportsEmpty() throws Exception {
        model.deletePerson(active);
        CommandResult result = parser.parseCommand("list").execute(model);
        assertEquals(ListCommand.MESSAGE_EMPTY, result.getFeedbackToUser());
        assertEquals(List.of(archived), model.getAddressBook().getPersonList());
    }
}
