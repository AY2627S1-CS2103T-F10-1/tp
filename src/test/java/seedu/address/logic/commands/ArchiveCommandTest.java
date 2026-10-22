package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.collections.ObservableList;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;

/** Tests single-contact archiving through the parser and observable model. */
public class ArchiveCommandTest {
    private final AddressBookParser parser = new AddressBookParser();
    private Model model;

    @BeforeEach
    public void setUp() {
        AddressBook book = new AddressBook();
        book.addPerson(ALICE);
        book.addPerson(BOB);
        model = new ModelManager(book, new UserPrefs());
    }

    @Test
    public void execute_validIndex_archivesOnlySelectedContactAndUpdatesExistingList() throws Exception {
        ObservableList<Person> displayed = model.getFilteredPersonList();
        CommandResult result = parser.parseCommand("archive 1").execute(model);
        assertEquals("Archived 1 contact.", result.getFeedbackToUser());
        assertSame(displayed, model.getFilteredPersonList());
        assertEquals(List.of(BOB), displayed);
        assertEquals(List.of(ALICE.withArchive(true), BOB), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_filteredList_usesDisplayedIndexAndPreservesFilter() throws Exception {
        parser.parseCommand("find /name " + BOB.getName().fullName).execute(model);
        parser.parseCommand("archive 1").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(ALICE, BOB.withArchive(true)), model.getAddressBook().getPersonList());
        parser.parseCommand("list archived").execute(model);
        assertEquals(List.of(BOB.withArchive(true)), model.getFilteredPersonList());
    }

    @Test
    public void execute_repeatedIndex_usesUpdatedDisplayedList() throws Exception {
        parser.parseCommand("archive 1").execute(model);
        parser.parseCommand("archive 1").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(ALICE.withArchive(true), BOB.withArchive(true)),
                model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_invalidDisplayedIndex_doesNotChangeDataOrView() throws Exception {
        parser.parseCommand("find /name " + BOB.getName().fullName).execute(model);
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                parser.parseCommand("archive 2").execute(model));
        assertEquals(List.of(BOB), model.getFilteredPersonList());
        assertEquals(List.of(ALICE, BOB), model.getAddressBook().getPersonList());
    }

    @Test
    public void execute_emptyList_rejectsIndex() {
        model = new ModelManager();
        assertThrows(CommandException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                parser.parseCommand("archive 1").execute(model));
    }

    @Test
    public void execute_alreadyArchived_explainsWithoutChangingDataOrView() throws Exception {
        model.setPerson(ALICE, ALICE.withArchive(true));
        parser.parseCommand("list archived").execute(model);
        CommandResult result = parser.parseCommand("archive 1").execute(model);
        assertEquals("Contact is already archived. No contacts archived.", result.getFeedbackToUser());
        assertEquals(List.of(ALICE.withArchive(true)), model.getFilteredPersonList());
        assertEquals(List.of(ALICE.withArchive(true), BOB), model.getAddressBook().getPersonList());
    }

    @Test
    public void parse_whitespace_acceptsIndex() throws Exception {
        parser.parseCommand("  archive   2  ").execute(model);
        assertEquals(List.of(ALICE, BOB.withArchive(true)), model.getAddressBook().getPersonList());
    }

    @Test
    public void parse_invalidArguments_doesNotChangeData() {
        for (String args : List.of("", "0", "-1", "+1", "1.5", "2147483648", "one", "1 2", "1 /tag friends")) {
            assertThrows(ParseException.class, () -> parser.parseCommand("archive " + args));
            assertEquals(List.of(ALICE, BOB), model.getAddressBook().getPersonList());
            assertEquals(List.of(ALICE, BOB), model.getFilteredPersonList());
        }
    }
}
