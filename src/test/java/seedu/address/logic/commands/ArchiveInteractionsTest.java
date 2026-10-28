package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BOB;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.AddressBookParser;
import seedu.address.model.AddressBook;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

/** Checks existing commands against a mix of active and archived contacts. */
public class ArchiveInteractionsTest {
    private final AddressBookParser parser = new AddressBookParser();
    private final Person archived = BOB.withArchive(true);
    private Model model;

    @BeforeEach
    public void setUp() {
        AddressBook book = new AddressBook();
        book.addPerson(ALICE);
        book.addPerson(archived);
        model = new ModelManager(book, new UserPrefs());
    }

    @Test
    public void edit_archivedContact_rejectedWithoutChangingDataOrView() throws Exception {
        parser.parseCommand("list archived").execute(model);
        assertThrows(CommandException.class, "Cannot edit an archived contact. Unarchive it first.", () ->
                parser.parseCommand("edit 1 /phone 91234567").execute(model));
        assertEquals(List.of(archived), model.getFilteredPersonList());
        assertEquals(List.of(ALICE, archived), model.getAddressBook().getPersonList());
    }

    @Test
    public void edit_activeContact_stillWorksAndKeepsArchiveHidden() throws Exception {
        parser.parseCommand("edit 1 /phone 91234567").execute(model);
        Person edited = new PersonBuilder(ALICE).withPhone("91234567").build();
        assertEquals(List.of(edited), model.getFilteredPersonList());
        assertEquals(List.of(edited, archived), model.getAddressBook().getPersonList());
    }

    @Test
    public void delete_archivedIndex_permanentlyRemovesDisplayedContact() throws Exception {
        parser.parseCommand("list archived").execute(model);
        parser.parseCommand("delete 1").execute(model);
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(ALICE), model.getAddressBook().getPersonList());
        assertFalse(model.hasPerson(BOB));
    }

    @Test
    public void delete_archivedName_doesNotDeleteHiddenContact() throws Exception {
        parser.parseCommand("list archived").execute(model);
        CommandResult result = parser.parseCommand("delete /name " + BOB.getName().fullName).execute(model);
        assertEquals(DeleteCommand.MESSAGE_NO_MATCHES, result.getFeedbackToUser());
        assertEquals(List.of(), model.getFilteredPersonList());
        assertEquals(List.of(ALICE, archived), model.getAddressBook().getPersonList());
    }

    @Test
    public void delete_activeNameFromArchivedView_deletesActiveMatch() throws Exception {
        parser.parseCommand("list archived").execute(model);
        parser.parseCommand("delete /name " + ALICE.getName().fullName).execute(model);
        assertEquals(List.of(archived), model.getAddressBook().getPersonList());
        assertEquals(List.of(), model.getFilteredPersonList());
    }

    @Test
    public void add_archivedDuplicate_rejectedWithRestoreGuidance() throws Exception {
        parser.parseCommand("list archived").execute(model);
        assertThrows(CommandException.class,
                "Contact already exists in the archive. Use list archived and unarchive INDEX to restore it.", () ->
                parser.parseCommand("add " + BOB.getName().fullName + " /phone 91234567").execute(model));
        assertEquals(List.of(ALICE, archived), model.getAddressBook().getPersonList());
        assertEquals(List.of(archived), model.getFilteredPersonList());
    }

    @Test
    public void add_activeDuplicate_keepsExistingMessage() {
        assertThrows(CommandException.class, AddCommand.MESSAGE_DUPLICATE_PERSON, () ->
                parser.parseCommand("add " + ALICE.getName().fullName + " /phone 91234567").execute(model));
    }

    @Test
    public void edit_nameConflictsWithArchivedContact_stillRejected() {
        assertThrows(CommandException.class, EditCommand.MESSAGE_DUPLICATE_PERSON, () ->
                parser.parseCommand("edit 1 /name " + BOB.getName().fullName).execute(model));
        assertEquals(List.of(ALICE, archived), model.getAddressBook().getPersonList());
    }

    @Test
    public void clear_fromEitherView_removesActiveAndArchivedContacts() throws Exception {
        for (String view : List.of("list", "list archived")) {
            setUp();
            parser.parseCommand(view).execute(model);
            parser.parseCommand("clear").execute(model);
            assertEquals(List.of(), model.getFilteredPersonList());
            assertEquals(List.of(), model.getAddressBook().getPersonList());
        }
    }
}
